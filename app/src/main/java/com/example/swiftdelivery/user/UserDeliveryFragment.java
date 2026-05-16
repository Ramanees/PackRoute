package com.example.swiftdelivery.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserDeliveryFragment extends Fragment {

    EditText etPick, etDelv, etPickPhone, etDelvPhone, etPickNote, etDelvNote, etPackDeets;
    Button btnDelivery, btnAiSize;
    TextView tvSizeResult;
    private FirebaseAuth mAuth;
    private String estimatedSize = "Standard";
    private FirebaseUser user;
    private DatabaseReference reference, userNameReference;
    private String userName, userMobile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_user_delivery, container, false);

        etPick = root.findViewById(R.id.etPickAdd);
        etDelv = root.findViewById(R.id.etDelvAdd);
        etPick.setText("");
        etDelv.setText("");
        etPickPhone = root.findViewById(R.id.etPickPhone);
        etDelvPhone = root.findViewById(R.id.etDelvPhone);
        etPickNote = root.findViewById(R.id.etPickNote);
        etDelvNote = root.findViewById(R.id.etDelvNote);
        etPackDeets = root.findViewById(R.id.etPackDetail);
        btnDelivery = root.findViewById(R.id.btnCreateDelivery);
        btnAiSize = root.findViewById(R.id.btnAiSizeEstimator);
        tvSizeResult = root.findViewById(R.id.tvEstimatedSize);

        btnAiSize.setOnClickListener(v -> {
            // Simulated AI Size Estimation Logic
            String[] sizes = {"Small (Envelope)", "Medium (Shoebox)", "Large (Backpack)"};
            estimatedSize = sizes[(int) (Math.random() * sizes.length)];
            tvSizeResult.setText("AI Estimated Size: " + estimatedSize);
            Toast.makeText(getContext(), "AI Scanning Package...", Toast.LENGTH_SHORT).show();
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Deliveries");

        if (user != null)
        {
            String userId = user.getUid();
            userNameReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        userName = snapshot.child("name").getValue(String.class);
                        userMobile = snapshot.child("phone").getValue(String.class);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "User details not found.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "User does not exist.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        SharedPreferences pickupPref = this.getActivity().getSharedPreferences("PICKUP_PREF", Context.MODE_PRIVATE);
        SharedPreferences dropoffPref = this.getActivity().getSharedPreferences("DROPOFF_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor pickup_editor = pickupPref.edit();
        SharedPreferences.Editor dropff_editor = dropoffPref.edit();
        String pickAddr = pickupPref.getString("PICKUP","");
        String delvAddr = dropoffPref.getString("DROPOFF","");


        etPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, new UserMapsFragment()).addToBackStack(null).commit();
            }
        });

        if (pickAddr != "")
        {
            etPick.setText(pickAddr);
        }
        if (delvAddr != "")
        {
            etDelv.setText(delvAddr);
        }

        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPick.getText().toString().isEmpty() || etDelv.getText().toString().isEmpty() || etPickPhone.getText().toString().isEmpty() ||
                        etDelvPhone.getText().toString().isEmpty() || etPickNote.getText().toString().isEmpty() || etDelvNote.getText().toString().isEmpty() ||
                        etPackDeets.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity(),"Please Enter the details !",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String pickupAddress = etPick.getText().toString();
                    String deliveryAddress = etDelv.getText().toString();
                    double pickupLat = Double.longBitsToDouble(pickupPref.getLong("PICK_LAT", Double.doubleToRawLongBits(0.0)));
                    double pickupLng = Double.longBitsToDouble(pickupPref.getLong("PICK_LNG", Double.doubleToRawLongBits(0.0)));
                    double delivLat = Double.longBitsToDouble(dropoffPref.getLong("DELV_LAT", Double.doubleToRawLongBits(0.0)));
                    double delivLng = Double.longBitsToDouble(dropoffPref.getLong("DELV_LNG", Double.doubleToRawLongBits(0.0)));
                    String pickupPhone = etPickPhone.getText().toString();
                    String deliveryPhone = etDelvPhone.getText().toString();
                    String pickupNote = etPickNote.getText().toString();
                    String deliveryNote = etDelvNote.getText().toString();
                    String packageDetails = etPackDeets.getText().toString();
                    String otp = String.valueOf((int) (Math.random() * 9000) + 1000); // 4-digit OTP
                    storeDeliveryDetails(pickupAddress, pickupLat, pickupLng, pickupPhone, pickupNote, deliveryAddress, delivLat, delivLng, deliveryPhone, deliveryNote, packageDetails, userName, userMobile, otp, estimatedSize);
                }
            }
        });

        return root;
    }
    private void storeDeliveryDetails(String pickupAddress, double pickupLat, double pickupLong, String pickupPhone, String pickupNote, String deliveryAddress, double deliveryLat, double deliveryLong, String deliveryPhone, String deliveryNote, String packageDetails, String uName, String uMobile, String otp, String pkgSize)
    {
        if (user != null)
        {
            String userId = user.getUid();
            String deliveryId = reference.push().getKey();

            if(!TextUtils.isEmpty(deliveryId))
            {
                String status = "pending";
                String agentId = "";
                String agentName = "";
                String agentMobile = "";
                String agentVehicleType = "";
                String agentVehicleRegistration = "";
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                Map<String, Object> deliveryDetails = new HashMap<>();
                deliveryDetails.put("DeliveryID", deliveryId);
                deliveryDetails.put("UserID", userId);
                deliveryDetails.put("UserName", uName);
                deliveryDetails.put("UserMobile", uMobile);
                deliveryDetails.put("PickupAddress", pickupAddress);
                deliveryDetails.put("PickupLatitude", pickupLat);
                deliveryDetails.put("PickupLongitude", pickupLong);
                deliveryDetails.put("PickupPhone", pickupPhone);
                deliveryDetails.put("PickupNote", pickupNote);
                deliveryDetails.put("DeliveryAddress", deliveryAddress);
                deliveryDetails.put("DeliveryLatitude", deliveryLat);
                deliveryDetails.put("DeliveryLongitude", deliveryLong);
                deliveryDetails.put("DeliveryPhone", deliveryPhone);
                deliveryDetails.put("DeliveryNote", deliveryNote);
                deliveryDetails.put("PackageDetails", packageDetails);
                deliveryDetails.put("Status", status);
                deliveryDetails.put("AssignedAgent", agentId);
                deliveryDetails.put("AssignedAgentName", agentName);
                deliveryDetails.put("AssignedAgentMobile", agentMobile);
                deliveryDetails.put("AssignedAgentVehicleType", agentVehicleType);
                deliveryDetails.put("AssignedAgentVehicleRegistration", agentVehicleRegistration);
                deliveryDetails.put("DeliveryRequestDate", currentDate);
                deliveryDetails.put("Otp", otp);
                deliveryDetails.put("PackageSize", pkgSize);

                reference.child(deliveryId).setValue(deliveryDetails).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), "Delivery Request Created !", Toast.LENGTH_SHORT).show();
                        if (isAdded()) {
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container_user, new UserHomeFragment())
                                    .commit();
                        }
                    } else
                    {
                        Toast.makeText(getActivity(), "Failed to store delivery details.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else
            {
                Toast.makeText(getActivity(), "User is not logged in!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}