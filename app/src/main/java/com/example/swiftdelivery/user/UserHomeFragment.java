package com.example.swiftdelivery.user;

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


public class UserHomeFragment extends Fragment {

    TextView tvName;
    Button btnLogout;
    ImageButton ibAccount, ibNewDelv, ibHistory, ibSupport;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_user_home, container, false);

        btnLogout = root.findViewById(R.id.btnLogout);
        tvName = root.findViewById(R.id.tvName);
        ibAccount = root.findViewById(R.id.ib_myacnt);
        ibNewDelv = root.findViewById(R.id.ib_newdelv);
        ibHistory = root.findViewById(R.id.ib_delvhist);
        ibSupport = root.findViewById(R.id.ib_support);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            String uid = currentUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null)
                        {
                            tvName.setText("Welcome, " +name+"!");
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
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, new UserAccountFragment()).addToBackStack(null).commit();
            }
        });

        ibNewDelv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDeliveryFragment deliveryFragment = new UserDeliveryFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, deliveryFragment).addToBackStack(null).commit();
            }
        });

        ibHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDeliveryHistoryFragment deliveryHistoryFragment = new UserDeliveryHistoryFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, deliveryHistoryFragment).addToBackStack(null).commit();
            }
        });

        ibSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSupportFragment supportFragment = new UserSupportFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, supportFragment).addToBackStack(null).commit();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent int_logout = new Intent(getActivity(), UserLoginActivity.class);
                startActivity(int_logout);
            }
        });

        return root;
    }
}