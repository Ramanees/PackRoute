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

public class AdminManageDeliveryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_delivery, container, false);

        RecyclerView deliveryRecyclerView = view.findViewById(R.id.adminManageDeliveryRecyclerView);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AdminManageDeliveryAdapter adapter = new AdminManageDeliveryAdapter(getContext(), new ArrayList<>());
        deliveryRecyclerView.setAdapter(adapter);

        DatabaseReference deliveriesReference = FirebaseDatabase.getInstance().getReference("Deliveries");
        deliveriesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminManageDelivery> deliveryList = new ArrayList<>();
                for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                    AdminManageDelivery delivery = deliverySnapshot.getValue(AdminManageDelivery.class);
                    deliveryList.add(delivery);
                }
                adapter.setDeliveryList(deliveryList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}