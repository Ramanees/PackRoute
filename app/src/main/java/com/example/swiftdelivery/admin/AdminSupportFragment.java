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

public class AdminSupportFragment extends Fragment {

    private RecyclerView recyclerViewUserSupport, recyclerViewAgentSupport;
    private AdminSupportAdapter userAdapter, agentAdapter;
    private List<AdminSupport> userSupportList, agentSupportList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_support, container, false);

        recyclerViewUserSupport = view.findViewById(R.id.recyclerViewUserSupport);
        recyclerViewAgentSupport = view.findViewById(R.id.recyclerViewAgentSupport);
        recyclerViewUserSupport.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAgentSupport.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        userSupportList = new ArrayList<>();
        agentSupportList = new ArrayList<>();
        userAdapter = new AdminSupportAdapter(getContext(), userSupportList);
        agentAdapter = new AdminSupportAdapter(getContext(), agentSupportList);

        recyclerViewUserSupport.setAdapter(userAdapter);
        recyclerViewAgentSupport.setAdapter(agentAdapter);

        fetchUserSupportQuestions();
        fetchAgentSupportQuestions();

        return view;
    }
    private void fetchUserSupportQuestions()
    {
        DatabaseReference userSupportReference = FirebaseDatabase.getInstance().getReference("User Support");
        userSupportReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSupportList.clear();
                for (DataSnapshot userQuestionSnapshot : snapshot.getChildren()) {
                    String id = userQuestionSnapshot.getKey();
                    String uid = userQuestionSnapshot.child("ID").getValue(String.class);
                    String uname = userQuestionSnapshot.child("name").getValue(String.class);
                    String uemail = userQuestionSnapshot.child("email").getValue(String.class);
                    String usubject = userQuestionSnapshot.child("subject").getValue(String.class);
                    String uquestion = userQuestionSnapshot.child("question").getValue(String.class);
                    userSupportList.add(new AdminSupport(id, uid, uname, uemail, usubject, uquestion));
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Retrieving User Support Requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAgentSupportQuestions()
    {
        DatabaseReference agentSupportReference = FirebaseDatabase.getInstance().getReference("Agent Support");
        agentSupportReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                agentSupportList.clear();
                for (DataSnapshot agentQuestionSnapshot : snapshot.getChildren()) {
                    String id = agentQuestionSnapshot.getKey();
                    String aid = agentQuestionSnapshot.child("ID").getValue(String.class);
                    String aname = agentQuestionSnapshot.child("name").getValue(String.class);
                    String aemail = agentQuestionSnapshot.child("email").getValue(String.class);
                    String asubject = agentQuestionSnapshot.child("subject").getValue(String.class);
                    String aquestion = agentQuestionSnapshot.child("question").getValue(String.class);
                    agentSupportList.add(new AdminSupport(id, aid, aname, aemail, asubject, aquestion));
                }
                agentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Retrieving Agent Support Requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}