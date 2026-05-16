package com.example.swiftdelivery.agent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;
import com.example.swiftdelivery.agent.AgentActiveDeliveryAdapter;
import com.example.swiftdelivery.agent.AgentAvailableDelivery;
import com.example.swiftdelivery.agent.AgentDeliveryNavigationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AgentActiveDeliveryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AgentActiveDeliveryAdapter adapter;
    private List<AgentAvailableDelivery> activeList = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseUser currentAgent;
    private TextView tvNoActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_available_delivery, container, false);

        TextView title = view.findViewById(R.id.tv_availableDeliveries);
        title.setText("Active Deliveries");
        
        tvNoActive = view.findViewById(R.id.tv_noDeliveries);
        tvNoActive.setText("No active deliveries.");

        currentAgent = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Deliveries");

        recyclerView = view.findViewById(R.id.availableDeliveryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AgentActiveDeliveryAdapter(activeList, delivery -> {
            AgentDeliveryNavigationFragment navFragment = new AgentDeliveryNavigationFragment();
            Bundle bundle = new Bundle();
            bundle.putString("DELIVERY_ID", delivery.getDeliveryID());
            navFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_agent, navFragment)
                    .addToBackStack(null).commit();
        });

        recyclerView.setAdapter(adapter);
        fetchActiveDeliveries();

        return view;
    }

    private void fetchActiveDeliveries() {
        if (currentAgent == null) return;
        String agentId = currentAgent.getUid();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String status = snapshot.child("Status").getValue(String.class);
                    String assignedAgent = snapshot.child("AssignedAgent").getValue(String.class);

                    if ("accepted".equalsIgnoreCase(status) && agentId.equals(assignedAgent)) {
                        try {
                            AgentAvailableDelivery delivery = snapshot.getValue(AgentAvailableDelivery.class);
                            if (delivery != null) {
                                delivery.setDeliveryID(snapshot.getKey());
                                activeList.add(delivery);
                            }
                        } catch (Exception e) {
                            Log.e("ActiveDeliveries", "Error parsing", e);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                tvNoActive.setVisibility(activeList.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(activeList.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ActiveDeliveries", error.getMessage());
            }
        });
    }
}
