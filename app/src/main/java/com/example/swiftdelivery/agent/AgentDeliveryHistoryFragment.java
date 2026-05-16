package com.example.swiftdelivery.agent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AgentDeliveryHistoryFragment extends Fragment {

    private RecyclerView deliveryHistoryRecyclerView;
    private DatabaseReference reference;
    private FirebaseUser currentAgent;
    private AgentDeliveryHistoryAdapter adapter;
    private List<AgentDeliveryHistory> deliveryHistoryList = new ArrayList<>();
    private String agentId, deliveryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_delivery_history, container, false);

        currentAgent = FirebaseAuth.getInstance().getCurrentUser();
        if (currentAgent != null)
        {
            agentId = currentAgent.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference("Deliveries");

        deliveryHistoryRecyclerView = view.findViewById(R.id.agentDeliveryHistoryRecyclerView);
        deliveryHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AgentDeliveryHistoryAdapter(deliveryHistoryList);

        deliveryHistoryRecyclerView.setAdapter(adapter);

        fetchAvailableDeliveries();

        return view;
    }
    private void fetchAvailableDeliveries()
    {
        reference.orderByChild("AssignedAgent").equalTo(agentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryHistoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    deliveryId = snapshot.getKey();
                    String status = snapshot.child("Status").getValue(String.class);
                    Log.d("FirebaseSnapshot", "Data: " + snapshot.getValue());
                    AgentDeliveryHistory delivery = snapshot.getValue(AgentDeliveryHistory.class);
                    if (deliveryId != null)
                    {
                        delivery.setDeliveryID(deliveryId);
                    }
                    if (status != null)
                    {
                        delivery.setStatus(status);
                    }
                    if (delivery != null)
                    {
                        deliveryHistoryList.add(delivery);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Failed to retrieve delivery with null deliveryId", Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.updateDeliveries(deliveryHistoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load available deliveries.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}