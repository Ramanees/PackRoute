package com.example.swiftdelivery.agent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftdelivery.LiveChatActivity;
import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgentDeliveryNavigationFragment extends Fragment{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    TextView tv_deliveryId, tv_userName, tv_userMobile, tv_status;
    Button btn_startNav, btnBackHome;
    ImageButton btn_Chat;
    EditText etVerifyOtp;
    View layoutHandoff, layoutFinished;
    SlideToActView sliderPickedUp, sliderDelivered;
    private String agentId, deliveryId, currentDeliveryState, correctOtp;
    private double pickupLat, pickupLong, deliveryLat, deliveryLong;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agent_delivery_navigation, container, false);

        tv_deliveryId = view.findViewById(R.id.tv_deliveryId);
        tv_userName = view.findViewById(R.id.tv_username);
        tv_userMobile = view.findViewById(R.id.tv_usermobile);
        tv_status = view.findViewById(R.id.tv_status_nav);
        
        sliderPickedUp = view.findViewById(R.id.slideToAct_PickedUp);
        sliderDelivered = view.findViewById(R.id.slideToAct_Delivered);
        btn_startNav = view.findViewById(R.id.btn_startNavigation);
        btn_Chat = view.findViewById(R.id.imgBtn_AgentChat);
        etVerifyOtp = view.findViewById(R.id.etVerifyOtp);
        layoutHandoff = view.findViewById(R.id.layoutHandoff);
        layoutFinished = view.findViewById(R.id.layoutFinished);
        btnBackHome = view.findViewById(R.id.btnBackHome);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) agentId = user.getUid();

        Bundle bundle = getArguments();
        if (bundle != null) deliveryId = bundle.getString("DELIVERY_ID");

        if (deliveryId == null) {
            Toast.makeText(getContext(), "Error: Delivery ID not found.", Toast.LENGTH_SHORT).show();
            return view;
        }

        reference = FirebaseDatabase.getInstance().getReference("Deliveries").child(deliveryId);

        currentDeliveryState = getStateFromPreferences();
        
        fetchDeliveryDetails();

        btn_startNav.setOnClickListener( v -> {
            if ("Initial".equals(currentDeliveryState)) {
                navigateToLocation(pickupLat, pickupLong);
            } else {
                navigateToLocation(deliveryLat, deliveryLong);
            }
            checkLocationPermission();
        });

        sliderPickedUp.setOnSlideCompleteListener(v -> {
            currentDeliveryState = "GoingToDeliver";
            saveStateToPreferences(currentDeliveryState);
            updateDeliveryStatus("Going to Deliver");
            updateUIBasedOnState();
        });

        sliderDelivered.setOnSlideCompleteListener(v -> {
            String enteredOtp = etVerifyOtp.getText().toString().trim();
            if (enteredOtp.length() < 4) {
                sliderDelivered.resetSlider();
                Toast.makeText(getContext(), "Please enter the 4-digit OTP first.", Toast.LENGTH_SHORT).show();
            } else if (enteredOtp.equals(correctOtp)) {
                currentDeliveryState = "Delivered";
                saveStateToPreferences(currentDeliveryState);
                updateDeliveryStatus("Delivered");
                addEarningToWallet(5.00); 
                updateUIBasedOnState();
            } else {
                sliderDelivered.resetSlider();
                Toast.makeText(getContext(), "Incorrect OTP code!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackHome.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_agent, new AgentActiveDeliveryFragment()).commit();
            }
        });

        btn_Chat.setOnClickListener(v -> {
            Intent int_chat = new Intent(getActivity(), LiveChatActivity.class);
            int_chat.putExtra("DELIVERY_ID", deliveryId);
            int_chat.putExtra("SENDER_TYPE", "agent");
            startActivity(int_chat);
        });

        updateUIBasedOnState();
        return view;
    }

    private void fetchDeliveryDetails() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isAdded()) {
                    String dbStatus = snapshot.child("Status").getValue(String.class);
                    correctOtp = snapshot.child("Otp").getValue(String.class);
                    
                    tv_userName.setText("Customer: " + snapshot.child("UserName").getValue(String.class));
                    tv_userMobile.setText("Mobile: " + snapshot.child("UserMobile").getValue(String.class));
                    tv_deliveryId.setText("Order: " + deliveryId.substring(Math.max(0, deliveryId.length() - 8)));
                    tv_status.setText("Current Phase: " + dbStatus);

                    Double pLat = snapshot.child("PickupLatitude").getValue(Double.class);
                    Double pLng = snapshot.child("PickupLongitude").getValue(Double.class);
                    Double dLat = snapshot.child("DeliveryLatitude").getValue(Double.class);
                    Double dLng = snapshot.child("DeliveryLongitude").getValue(Double.class);
                    pickupLat = pLat != null ? pLat : 0.0;
                    pickupLong = pLng != null ? pLng : 0.0;
                    deliveryLat = dLat != null ? dLat : 0.0;
                    deliveryLong = dLng != null ? dLng : 0.0;

                    if ("Delivered".equalsIgnoreCase(dbStatus)) {
                        currentDeliveryState = "Delivered";
                    } else if ("Going to Deliver".equalsIgnoreCase(dbStatus)) {
                        currentDeliveryState = "GoingToDeliver";
                    } else {
                        currentDeliveryState = "Initial";
                    }
                    saveStateToPreferences(currentDeliveryState);
                    updateUIBasedOnState();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateUIBasedOnState()
    {
        if (getView() == null || btn_startNav == null) return;

        if ("Delivered".equals(currentDeliveryState)) {
            btn_startNav.setVisibility(View.GONE);
            sliderPickedUp.setVisibility(View.GONE);
            sliderDelivered.setVisibility(View.GONE);
            layoutHandoff.setVisibility(View.GONE);
            layoutFinished.setVisibility(View.VISIBLE);
        } else if ("GoingToDeliver".equals(currentDeliveryState)) {
            btn_startNav.setVisibility(View.VISIBLE);
            btn_startNav.setText("Navigate to Receiver");
            sliderPickedUp.setVisibility(View.GONE);
            sliderDelivered.setVisibility(View.VISIBLE);
            layoutHandoff.setVisibility(View.VISIBLE);
            layoutFinished.setVisibility(View.GONE);
        } else {
            btn_startNav.setVisibility(View.VISIBLE);
            btn_startNav.setText("Navigate to Pickup");
            sliderPickedUp.setVisibility(View.VISIBLE);
            sliderDelivered.setVisibility(View.GONE);
            layoutHandoff.setVisibility(View.GONE);
            layoutFinished.setVisibility(View.GONE);
        }
    }

    private void navigateToLocation(double latitude, double longitude)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "Google Maps app required.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDeliveryStatus(String status)
    {
        reference.child("Status").setValue(status);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdateService();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdateService() {
        if (getContext() == null) return;
        ContextCompat.startForegroundService(requireContext(), new Intent(requireContext(), LocationForegroundService.class));
    }

    private void addEarningToWallet(double amount) {
        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(agentId);
        agentRef.child("WalletBalance").runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @NonNull
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                Double balance = currentData.getValue(Double.class);
                if (balance == null) balance = 0.0;
                currentData.setValue(balance + amount);
                return com.google.firebase.database.Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable com.google.firebase.database.DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                    agentRef.child("Earnings").push().setValue(new Earning(deliveryId, date, amount));
                }
            }
        });
    }

    private void saveStateToPreferences(String state) {
        if (getActivity() == null) return;
        getActivity().getSharedPreferences("AgentPrefs", Context.MODE_PRIVATE)
                .edit().putString("state_" + deliveryId, state).apply();
    }

    private String getStateFromPreferences() {
        if (getActivity() == null) return "Initial";
        return getActivity().getSharedPreferences("AgentPrefs", Context.MODE_PRIVATE)
                .getString("state_" + deliveryId, "Initial");
    }
}
