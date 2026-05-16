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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftdelivery.LiveChatActivity;
import com.example.swiftdelivery.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

public class AgentDeliveryNavigationFragment extends Fragment{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    TextView tv_deliveryId, tv_userName, tv_userMobile;
    Button btn_startNav;
    ImageButton btn_Chat;
    SlideToActView sliderPickedUp, sliderDelivered;
    private String agentId, deliveryId, currentDeliveryState;;
    private double pickupLat, pickupLong, deliveryLat, deliveryLong;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private static final String API_KEY = "AIzaSyDw_briairLHtt8eaTFmSmMfCr2XldEtEw";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agent_delivery_navigation, container, false);

        tv_deliveryId = view.findViewById(R.id.tv_deliveryId);
        tv_userName = view.findViewById(R.id.tv_username);
        tv_userMobile = view.findViewById(R.id.tv_usermobile);
        sliderPickedUp = view.findViewById(R.id.slideToAct_PickedUp);
        sliderDelivered = view.findViewById(R.id.slideToAct_Delivered);
        btn_startNav = view.findViewById(R.id.btn_startNavigation);
        btn_Chat = view.findViewById(R.id.imgBtn_AgentChat);

        mAuth = FirebaseAuth.getInstance();
        agentId = mAuth.getCurrentUser().getUid();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("DELIVERY_ID")) {
            deliveryId = bundle.getString("DELIVERY_ID");
            if (deliveryId != null) {
                tv_deliveryId.setText("Delivery ID: " + deliveryId);
            } else {
                Toast.makeText(getContext(), "Invalid delivery ID.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Delivery ID is missing.", Toast.LENGTH_SHORT).show();
        }

        reference = FirebaseDatabase.getInstance().getReference("Deliveries").child(deliveryId);

        currentDeliveryState = getStateFromPreferences();
        updateUIBasedOnState();

        fetchDeliveryDetails();

        btn_startNav.setOnClickListener( v -> {
            currentDeliveryState = "Initial";
            saveStateToPreferences(currentDeliveryState);
            navigateToLocation(pickupLat, pickupLong);
            checkLocationPermission();
            btn_startNav.setVisibility(View.GONE);
            sliderPickedUp.setVisibility(View.VISIBLE);
        });

        sliderPickedUp.setOnSlideCompleteListener(v -> {
            currentDeliveryState = "GoingToDeliver";
            saveStateToPreferences(currentDeliveryState);
            updateDeliveryStatus("Going to Deliver");
            navigateToLocation(deliveryLat, deliveryLong);
            sliderPickedUp.setVisibility(View.GONE);
            sliderDelivered.setVisibility(View.VISIBLE);
        });

        sliderDelivered.setOnSlideCompleteListener(v -> {
            currentDeliveryState = "Delivered";
            saveStateToPreferences(currentDeliveryState);
            updateDeliveryStatus("Delivered");
            if (isAdded() && getContext() != null) {
                stopLocationUpdateService();
                requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_agent, new AgentAvailableDeliveryFragment()).addToBackStack(null).commit();
            } else {
                Log.e("AgentDeliveryFragment", "Fragment is not attached to context.");
            }
        });

        btn_Chat.setOnClickListener(v -> {
            Intent int_chat = new Intent(getActivity(), LiveChatActivity.class);
            int_chat.putExtra("DELIVERY_ID", deliveryId);
            int_chat.putExtra("SENDER_TYPE", "agent");
            startActivity(int_chat);
        });

        return view;
    }

    private void fetchDeliveryDetails() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("UserName").getValue(String.class);
                    String userMobile = snapshot.child("UserMobile").getValue(String.class);
                    pickupLat = snapshot.child("PickupLatitude").getValue(Double.class);
                    pickupLong = snapshot.child("PickupLongitude").getValue(Double.class);
                    deliveryLat = snapshot.child("DeliveryLatitude").getValue(Double.class);
                    deliveryLong = snapshot.child("DeliveryLongitude").getValue(Double.class);

                    tv_userName.setText("Name: " + userName);
                    tv_userMobile.setText("Mobile Number: " + userMobile);
                } else {
                    Toast.makeText(getContext(), "Delivery details not found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AgentDeliveryFragment", "Failed to fetch delivery details: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load delivery details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLocation(double latitude, double longitude)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
            Uri playStoreUri = Uri.parse("market://details?id=com.google.android.apps.maps");
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
            startActivity(playStoreIntent);
        }
    }

    private void updateDeliveryStatus(String status)
    {
        reference.child("Status").setValue(status).addOnSuccessListener(aVoid -> {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(getContext(), "Delivery status updated to: " + status, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener( e -> {
            Context context = getContext();
            if (context != null) {
                Toast.makeText(getContext(), "Failed to update status.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            startLocationUpdateService();
            updateDeliveryStatus("Going to Pickup");
        }
        else
        {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    updateDeliveryStatus("Going to Pickup");
                    break;
                }
            }
            if (allGranted) {
                startLocationUpdateService();
            } else {
                Toast.makeText(getContext(), "Location permission is required to update your location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdateService() {
        Intent int_locationService = new Intent(requireContext(), LocationForegroundService.class);
        ContextCompat.startForegroundService(requireContext(), int_locationService);
        Toast.makeText(requireContext(), "Starting location updates...", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdateService() {
        Context context = getContext();
        if (context != null) {
            Intent int_stopLocationService = new Intent(requireContext(), LocationForegroundService.class);
            context.stopService(int_stopLocationService);
            Toast.makeText(requireContext(), "Location updates stopped.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStateToPreferences(String state) {
        requireActivity().getSharedPreferences("AgentDeliveryPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("deliveryState_" + deliveryId, state)
                .apply();
    }

    private String getStateFromPreferences() {
        return requireActivity().getSharedPreferences("AgentDeliveryPrefs", Context.MODE_PRIVATE)
                .getString("deliveryState_" + deliveryId, "Initial");
    }

    private void updateUIBasedOnState()
    {
        switch (currentDeliveryState) {
            case "Initial":
                btn_startNav.setVisibility(View.VISIBLE);
                sliderPickedUp.setVisibility(View.GONE);
                sliderDelivered.setVisibility(View.GONE);
                break;
            case "GoingToDeliver":
                btn_startNav.setVisibility(View.GONE);
                sliderPickedUp.setVisibility(View.GONE);
                sliderDelivered.setVisibility(View.VISIBLE);
                break;
            case "Delivered":
                btn_startNav.setVisibility(View.GONE);
                sliderPickedUp.setVisibility(View.GONE);
                sliderDelivered.setVisibility(View.GONE);
                break;
        }
    }
}