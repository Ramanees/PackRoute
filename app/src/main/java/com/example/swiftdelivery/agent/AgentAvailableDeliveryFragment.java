package com.example.swiftdelivery.agent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentAvailableDeliveryFragment extends Fragment {

    TextView tv_heading, tv_noDeliveries;
    private RecyclerView deliveryRecyclerView;
    private DatabaseReference reference, agentDetailsReference;
    private FirebaseUser currentAgent;
    private AgentAvailableDeliveryAdapter adapter;
    private List<AgentAvailableDelivery> deliveryList = new ArrayList<>();
    private String agentName, agentMobile, agentVehicleType, agentVehicleReg, deliveryId;
    private String delv_ID;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agent_available_delivery, container, false);

        currentAgent = FirebaseAuth.getInstance().getCurrentUser();
        if (currentAgent == null) {
            Toast.makeText(getContext(), "You are not logged in. Please log in to continue.", Toast.LENGTH_SHORT).show();
            Intent intent_goBack = new Intent(requireContext(), AgentLoginActivity.class);
            startActivity(intent_goBack);
        }

        reference = FirebaseDatabase.getInstance().getReference("Deliveries");

        if (currentAgent != null) {
            String agentId = currentAgent.getUid();
            agentDetailsReference = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(agentId);
            agentDetailsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        agentName = snapshot.child("name").getValue(String.class);
                        agentMobile = snapshot.child("phone").getValue(String.class);
                        agentVehicleType = snapshot.child("vehicle").getValue(String.class);
                        agentVehicleReg = snapshot.child("registration").getValue(String.class);
                    } else {
                        Toast.makeText(getContext(), "Agent details not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Agent does not exist.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        tv_heading = view.findViewById(R.id.tv_availableDeliveries);
        tv_noDeliveries = view.findViewById(R.id.tv_noDeliveries);


        deliveryRecyclerView = view.findViewById(R.id.availableDeliveryRecyclerView);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AgentAvailableDeliveryAdapter(deliveryList, new AgentAvailableDeliveryAdapter.OnDeliveryClickListener() {
            @Override
            public void onDeliveryClick(AgentAvailableDelivery delivery) {
                acceptDelivery(delivery);
            }
        });

        deliveryRecyclerView.setAdapter(adapter);
        fetchAvailableDeliveries();

        return view;
    }

    private void fetchAvailableDeliveries()
    {
        // Fetch all deliveries and filter manually to ensure compatibility
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryList.clear();
                Log.d("AgentAvailable", "Refreshing deliveries... Total nodes: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String status = snapshot.child("Status").getValue(String.class);
                    
                    // Only show deliveries that are "pending"
                    if ("pending".equalsIgnoreCase(status)) {
                        try {
                            AgentAvailableDelivery delivery = snapshot.getValue(AgentAvailableDelivery.class);
                            if (delivery != null) {
                                delivery.setDeliveryID(snapshot.getKey());
                                deliveryList.add(delivery);
                                Log.d("AgentAvailable", "Added pending delivery: " + snapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("AgentAvailable", "Error parsing delivery node: " + snapshot.getKey(), e);
                        }
                    }
                }
                
                adapter.updateDeliveries(new ArrayList<>(deliveryList));

                if (deliveryList.isEmpty()) {
                    deliveryRecyclerView.setVisibility(View.GONE);
                    tv_noDeliveries.setVisibility(View.VISIBLE);
                    Log.d("AgentAvailable", "No pending deliveries found.");
                } else {
                    deliveryRecyclerView.setVisibility(View.VISIBLE);
                    tv_noDeliveries.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AgentAvailable", "Database error: " + error.getMessage());
            }
        });
    }

    private void acceptDelivery(AgentAvailableDelivery delivery)
    {
        if (currentAgent != null)
        {
            String agentId = currentAgent.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("Status", "accepted");
            updates.put("AssignedAgent", agentId);
            updates.put("AssignedAgentName", agentName);
            updates.put("AssignedAgentMobile", agentMobile);
            updates.put("AssignedAgentVehicleType", agentVehicleType);
            updates.put("AssignedAgentVehicleRegistration", agentVehicleReg);

            reference.child(delivery.getDeliveryID()).updateChildren(updates).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Delivery accepted!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to accept delivery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkAgentAssigned()
    {
        String agentId = currentAgent.getUid();
        valueEventListener =  reference.orderByChild("AssignedAgent").equalTo(agentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        String status = snapshot.child("Status").getValue(String.class);
                        if (!"Delivered".equalsIgnoreCase(status))
                        {
                            delv_ID = snapshot.getKey();

                            AgentDeliveryNavigationFragment navigationFragment =  new AgentDeliveryNavigationFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("DELIVERY_ID", delv_ID);
                            navigationFragment.setArguments(bundle);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, navigationFragment).addToBackStack(null).commit();
                            return;
                        }
                    }
                }
                fetchAvailableDeliveries();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to check assigned deliveries", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAvailableDeliveries();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reference != null) {
            reference.removeEventListener(valueEventListener);
        }
    }
}
