package com.example.swiftdelivery.agent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgentAccountFragment extends Fragment {

    EditText etAgentName, etAgentMobile, etAgentRegistration;
    Spinner spinVehicleType;
    Button btnSave;
    private DatabaseReference reference;
    private FirebaseUser currentAgent;
    private ArrayAdapter<String> vehicle_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agent_account, container, false);

        etAgentName = view.findViewById(R.id.etAgentName);
        etAgentMobile = view.findViewById(R.id.etAgentPhone);
        etAgentRegistration = view.findViewById(R.id.etAgentVehicleReg);
        spinVehicleType = view.findViewById(R.id.spinVehicleType);
        btnSave = view.findViewById(R.id.btnSaveChanges);

        String[] updateVehicleTypes = getResources().getStringArray(R.array.UpdateVehicleType);
        vehicle_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, updateVehicleTypes);
        vehicle_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinVehicleType.setAdapter(vehicle_adapter);

        currentAgent = FirebaseAuth.getInstance().getCurrentUser();
        if (currentAgent != null)
        {
            String userId = currentAgent.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(userId);
            loadAgentData();
        }

        btnSave.setOnClickListener(v -> updateAgentData());
        return view;
    }
    private void setSpinnerItem(String vType)
    {
        int pos = vehicle_adapter.getPosition(vType);
        if (pos != -1)
        {
            spinVehicleType.setSelection(pos);
        }
    }

    private void loadAgentData()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String name = snapshot.child("name").getValue(String.class);
                    String mobile = snapshot.child("phone").getValue(String.class);
                    String vType = snapshot.child("vehicle").getValue(String.class);
                    String vReg = snapshot.child("registration").getValue(String.class);

                    etAgentName.setText(name);
                    etAgentMobile.setText(mobile);
                    etAgentRegistration.setText(vReg);
                    setSpinnerItem(vType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load agent data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAgentData()
    {
        String name = etAgentName.getText().toString().trim();
        String phone = etAgentMobile.getText().toString().trim();
        String registration = etAgentRegistration.getText().toString().trim();
        String vehicle = spinVehicleType.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(registration))
        {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        reference.child("name").setValue(name);
        reference.child("phone").setValue(phone);
        reference.child("registration").setValue(registration);
        reference.child("vehicle").setValue(vehicle).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}