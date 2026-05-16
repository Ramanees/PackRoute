package com.example.swiftdelivery.user;

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

public class UserDeliveryHistoryFragment extends Fragment {

    private RecyclerView deliveryHistoryRecyclerView;
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private UserDeliveryHistoryAdapter adapter;
    private List<UserDeliveryHistory> deliveryHistoryList = new ArrayList<>();
    private String userId, deliveryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_delivery_history, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
        {
            userId = currentUser.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference("Deliveries");

        deliveryHistoryRecyclerView = view.findViewById(R.id.userDeliveryHistoryRecyclerView);
        deliveryHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserDeliveryHistoryAdapter(deliveryHistoryList, new UserDeliveryHistoryAdapter.OnDeliveryClickListener() {
            @Override
            public void onDeliveryClick(UserDeliveryHistory delivery) {
                trackDelivery(delivery);
            }
        });

        deliveryHistoryRecyclerView.setAdapter(adapter);

        fetchAvailableDeliveries();

        return view;
    }
    private void fetchAvailableDeliveries()
    {
        if (userId == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) userId = user.getUid();
        }

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        reference.orderByChild("UserID").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryHistoryList.clear();
                Log.d("UserHistory", "Data changed. Exists: " + dataSnapshot.exists() + ", Count: " + dataSnapshot.getChildrenCount());
                
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("UserHistory", "Snapshot Key: " + snapshot.getKey() + " | Raw Data: " + snapshot.getValue());
                        try {
                            UserDeliveryHistory delivery = snapshot.getValue(UserDeliveryHistory.class);
                            if (delivery != null) {
                                delivery.setDeliveryID(snapshot.getKey());
                                deliveryHistoryList.add(delivery);
                                Log.d("UserHistory", "Successfully added delivery: " + delivery.getDeliveryID());
                            }
                        } catch (Exception e) {
                            Log.e("UserHistory", "Error parsing snapshot: " + snapshot.getKey(), e);
                        }
                    }
                } else {
                    Log.w("UserHistory", "No data found for UserID: " + userId);
                }
                
                adapter.updateDeliveries(new ArrayList<>(deliveryHistoryList));
                
                if (deliveryHistoryList.isEmpty() && isAdded()) {
                    Toast.makeText(getContext(), "No deliveries found in your history.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserHistory", "Database Error: " + error.getMessage());
                if (isAdded()) {
                    Toast.makeText(getContext(), "Sync Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void trackDelivery(UserDeliveryHistory delivery)
    {
        Intent int_track = new Intent(getActivity(), UserTrackingActivity.class);
        int_track.putExtra("DeliveryID", delivery.getDeliveryID());
        startActivity(int_track);
    }
}