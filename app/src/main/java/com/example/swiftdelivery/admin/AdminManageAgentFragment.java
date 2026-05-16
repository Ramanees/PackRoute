package com.example.swiftdelivery.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminManageAgentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_agent, container, false);

        RecyclerView agentRecyclerView = view.findViewById(R.id.adminManageAgentRecyclerView);
        agentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AdminManageAgentAdapter adapter = new AdminManageAgentAdapter(getContext(), new ArrayList<>());
        agentRecyclerView.setAdapter(adapter);

        DatabaseReference agentsReference = FirebaseDatabase.getInstance().getReference("Delivery Agents");
        agentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminManageAgent> agentList = new ArrayList<>();
                for (DataSnapshot agentSnapshot : snapshot.getChildren()) {
                    AdminManageAgent agent = agentSnapshot.getValue(AdminManageAgent.class);
                    agentList.add(agent);
                }
                adapter.setAgentList(agentList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load delivery agents.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}