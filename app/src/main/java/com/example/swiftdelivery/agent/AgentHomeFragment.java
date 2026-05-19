package com.example.swiftdelivery.agent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgentHomeFragment extends Fragment {

    TextView tvName, tvViewEarnings;
    Button btnLogout;
    ImageButton ibAccount, ibAvailDelv, ibHistory, ibSupport;
    CardView cardOnMyWay;
    SwitchMaterial switchOnMyWay;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_agent_home, container, false);

        btnLogout = root.findViewById(R.id.btnAgentLogout);
        tvName = root.findViewById(R.id.tvAgentName);
        tvViewEarnings = root.findViewById(R.id.tvViewEarnings);
        ibAccount = root.findViewById(R.id.ib_agent_myacnt);
        ibAvailDelv = root.findViewById(R.id.ib_agent_availdelv);
        ibHistory = root.findViewById(R.id.ib_agent_delvhist);
        ibSupport = root.findViewById(R.id.ib_agent_support);
        cardOnMyWay = root.findViewById(R.id.cardOnMyWay);
        switchOnMyWay = root.findViewById(R.id.switchOnMyWay);

        View.OnClickListener openDashboard = v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_agent, new AgentIncomeFragment())
                    .addToBackStack(null).commit();
        };

        cardOnMyWay.setOnClickListener(openDashboard);
        tvViewEarnings.setOnClickListener(openDashboard);

        switchOnMyWay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Passive matching enabled along your route.", Toast.LENGTH_SHORT).show();
            }
        });

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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        ibAccount.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentAccountFragment()).addToBackStack(null).commit());
        ibAvailDelv.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentAvailableDeliveryFragment()).addToBackStack(null).commit());
        ibHistory.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentDeliveryHistoryFragment()).addToBackStack(null).commit());
        ibSupport.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentSupportFragment()).addToBackStack(null).commit());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit().remove("UserRole").apply();
            startActivity(new Intent(getActivity(), AgentLoginActivity.class));
            getActivity().finish();
        });

        return root;
    }
}
