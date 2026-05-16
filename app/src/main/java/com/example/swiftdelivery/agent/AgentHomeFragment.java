package com.example.swiftdelivery.agent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

public class AgentHomeFragment extends Fragment {

    TextView tvName;
    Button btnLogout;
    ImageButton ibAccount, ibAvailDelv, ibHistory, ibSupport;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_agent_home, container, false);

        btnLogout = root.findViewById(R.id.btnAgentLogout);
        tvName = root.findViewById(R.id.tvAgentName);
        ibAccount = root.findViewById(R.id.ib_agent_myacnt);
        ibAvailDelv = root.findViewById(R.id.ib_agent_availdelv);
        ibHistory = root.findViewById(R.id.ib_agent_delvhist);
        ibSupport = root.findViewById(R.id.ib_agent_support);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentAgent = mAuth.getCurrentUser();

        if (currentAgent != null)
        {
            String uid = currentAgent.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(uid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null)
                        {
                            tvName.setText("Welcome, Agent "+name+"!");
                        }
                    } else
                    {
                        Toast.makeText(getActivity(), "User details not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to retrieve user details.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        ibAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentAccountFragment()).addToBackStack(null).commit();
            }
        });

        ibAvailDelv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentAvailableDeliveryFragment()).addToBackStack(null).commit();
            }
        });

        ibHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentDeliveryHistoryFragment()).addToBackStack(null).commit();
            }
        });

        ibSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentSupportFragment()).addToBackStack(null).commit();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent int_agent_logout = new Intent(getActivity(), AgentLoginActivity.class);
                startActivity(int_agent_logout);
            }
        });

        return root;
    }
}