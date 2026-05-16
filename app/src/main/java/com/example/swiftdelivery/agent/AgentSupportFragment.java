package com.example.swiftdelivery.agent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AgentSupportFragment extends Fragment {

    Button btnSubmit;
    EditText et_subject, et_question;
    private String agentId, agentName, agentEmail;
    private DatabaseReference agentReference, supportReference;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_support, container, false);

        et_subject = view.findViewById(R.id.etAgentSupportSubject);
        et_question = view.findViewById(R.id.etAgentSupportQuestion);
        btnSubmit = view.findViewById(R.id.btnAgentSupportSubmit);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentAgent = auth.getCurrentUser();
        if (currentAgent != null) {
            agentId = currentAgent.getUid();
        } else {
            Toast.makeText(getActivity(), "Delivery Agent not authorized.", Toast.LENGTH_SHORT).show();
        }

        agentReference = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(agentId);
        supportReference = FirebaseDatabase.getInstance().getReference("Agent Support");

        btnSubmit.setOnClickListener(v -> submitAgentSupportRequest());

        return view;
    }
    private void submitAgentSupportRequest()
    {
        String agentSubject = et_subject.getText().toString().trim();
        String agentQuestion = et_question.getText().toString().trim();

        if (TextUtils.isEmpty(agentSubject) || TextUtils.isEmpty(agentQuestion)) {
            Toast.makeText(getActivity(), "Please fill the Subject/Question.", Toast.LENGTH_SHORT).show();
            return;
        }

        agentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    agentName = snapshot.child("name").getValue(String.class);
                    agentEmail = snapshot.child("email").getValue(String.class);

                    if (agentName != null && agentEmail != null)
                    {
                        Map<String, String> agentSupportData = new HashMap<>();
                        agentSupportData.put("ID", agentId);
                        agentSupportData.put("name", agentName);
                        agentSupportData.put("email", agentEmail);
                        agentSupportData.put("subject", agentSubject);
                        agentSupportData.put("question", agentQuestion);

                        supportReference.push().setValue(agentSupportData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Thank You.\nWe will get back to you Shortly.", Toast.LENGTH_SHORT).show();
                                et_subject.setText("");
                                et_question.setText("");
                            } else {
                                Toast.makeText(getActivity(), "Failed to submit support request.\nPlease try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Log.e("AgentSupportFragment", "agentName and agentEmail found to be empty");
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Delivery agent details not found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving delivery agent data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}