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

public class AdminManageUserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_user, container, false);

        RecyclerView userRecyclerView = view.findViewById(R.id.adminManageUserRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AdminManageUserAdapter adapter = new AdminManageUserAdapter(getContext(), new ArrayList<>());
        userRecyclerView.setAdapter(adapter);

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AdminManageUser> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    AdminManageUser user = userSnapshot.getValue(AdminManageUser.class);
                    userList.add(user);
                }
                adapter.setUserList(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}