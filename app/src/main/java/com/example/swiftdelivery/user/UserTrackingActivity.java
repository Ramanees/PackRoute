package com.example.swiftdelivery.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.swiftdelivery.LiveChatActivity;
import com.example.swiftdelivery.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "UserTrackingActivity";
    private GoogleMap mMap;
    private Marker agentMarker, pickupMarker, deliveryMarker;
    private String deliveryId, assignedAgentId;
    private DatabaseReference deliveriesReference, agentLocationReference;
    private ValueEventListener deliveriesValueEventListener, agentLocationValueEventListener;
    TextView tv_deliveryId, tv_agentName, tv_agentMobile, tv_agentVehicleTwo, tv_agentVehicleFour, tv_deliveryStatus, tv_userChat;
    ImageButton btn_chat;
    Fragment mapFragment;
    ScrollView sv_userTracking;
    LinearLayout deliveredLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_tracking);

        tv_deliveryId = findViewById(R.id.tv_userDeliveryId);
        tv_agentName = findViewById(R.id.tv_agentName);
        tv_agentMobile = findViewById(R.id.tv_agentMobile);
        tv_agentVehicleTwo = findViewById(R.id.tv_agentVehicleNumberBike);
        tv_agentVehicleFour = findViewById(R.id.tv_agentVehicleNumberCar);
        tv_deliveryStatus = findViewById(R.id.tv_deliveryStatus);
        btn_chat = findViewById(R.id.imgBtn_UserChat);
        tv_userChat = findViewById(R.id.tv_ChatWithAgent);

        mapFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_map_tracking);
        sv_userTracking = findViewById(R.id.scrollView_userTracking);
        deliveredLayout = findViewById(R.id.deliveredLayout);

        deliveryId = getIntent().getStringExtra("DeliveryID");
        tv_deliveryId.setText(deliveryId);

        if (deliveryId == null || deliveryId.isEmpty())
        {
            Toast.makeText(this, "Delivery ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        deliveriesReference = FirebaseDatabase.getInstance().getReference("Deliveries").child(deliveryId);

        if (deliveryId != null)
        {
            deliveriesValueEventListener =  deliveriesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String agentName = snapshot.child("AssignedAgentName").getValue(String.class);
                        String agentMobile = snapshot.child("AssignedAgentMobile").getValue(String.class);
                        String agentVehicleType = snapshot.child("AssignedAgentVehicleType").getValue(String.class);
                        String agentVehicleReg = snapshot.child("AssignedAgentVehicleRegistration").getValue(String.class);
                        String deliveryStatus = snapshot.child("Status").getValue(String.class);
                        tv_agentName.setText("Name: "+agentName);
                        tv_agentMobile.setText("Mobile Number: "+agentMobile);
                        tv_deliveryStatus.setText(deliveryStatus);
                        if ("2-wheeler".equalsIgnoreCase(agentVehicleType))
                        {
                            tv_agentVehicleFour.setVisibility(View.GONE);
                            tv_agentVehicleTwo.setVisibility(View.VISIBLE);
                            tv_agentVehicleTwo.setText("Vehicle Number: "+agentVehicleReg);
                        }
                        else if ("4-wheeler".equalsIgnoreCase(agentVehicleType))
                        {
                            tv_agentVehicleTwo.setVisibility(View.GONE);
                            tv_agentVehicleFour.setVisibility(View.VISIBLE);
                            tv_agentVehicleFour.setText("Vehicle Number: "+agentVehicleReg);
                        }
                        else
                        {
                            Toast.makeText(UserTrackingActivity.this, "Unknown vehicle type.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(UserTrackingActivity.this, "Delivery agent details not found.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserTrackingActivity.this, "Delivery does not exist.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_tracking);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }
        else
        {
            Toast.makeText(this, "Failed to initialize map.", Toast.LENGTH_SHORT).show();
        }

        monitorDeliveryStatus(deliveryId);

        btn_chat.setOnClickListener(v -> {
            Intent int_chat = new Intent(UserTrackingActivity.this, LiveChatActivity.class);
            int_chat.putExtra("DELIVERY_ID", deliveryId);
            int_chat.putExtra("SENDER_TYPE", "user");
            startActivity(int_chat);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        fetchDeliveryDetails();
    }

    private void fetchDeliveryDetails()
    {
        deliveriesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    double pickupLat = snapshot.child("PickupLatitude").getValue(Double.class);
                    double pickupLong = snapshot.child("PickupLongitude").getValue(Double.class);
                    double deliveryLat = snapshot.child("DeliveryLatitude").getValue(Double.class);
                    double deliveryLong = snapshot.child("DeliveryLongitude").getValue(Double.class);

                    assignedAgentId = snapshot.child("AssignedAgent").getValue(String.class);

                    addPickupAndDeliveryMarkers(pickupLat, pickupLong, deliveryLat, deliveryLong);

                    if (assignedAgentId != null && !assignedAgentId.isEmpty())
                    {
                        trackAgentLocation(assignedAgentId);
                    }
                    else
                    {
                        Toast.makeText(UserTrackingActivity.this, "No agent assigned to this delivery.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserTrackingActivity.this, "Delivery details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch delivery details: " + error.getMessage());
                Toast.makeText(UserTrackingActivity.this, "Failed to fetch delivery details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPickupAndDeliveryMarkers(double pickupLat, double pickupLong, double deliveryLat, double deliveryLong)
    {
        LatLng pickupLatLng = new LatLng(pickupLat, pickupLong);
        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location"));

        LatLng deliveryLatLng = new LatLng(deliveryLat, deliveryLong);
        deliveryMarker = mMap.addMarker(new MarkerOptions().position(deliveryLatLng).title("Delivery Location"));

        LatLng midpoint = new LatLng((pickupLat+deliveryLat)/2, (pickupLong+deliveryLong)/2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(midpoint,12));
    }

    private void trackAgentLocation(String agentId)
    {
        agentLocationReference = FirebaseDatabase.getInstance().getReference("Agent Locations").child(agentId);
        agentLocationValueEventListener = agentLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Double agentLat = snapshot.child("latitude").getValue(Double.class);
                    Double agentLong = snapshot.child("longitude").getValue(Double.class);

                    if (agentLat != null && agentLong != null)
                    {
                        LatLng agentLatLng = new LatLng(agentLat, agentLong);

                        if (agentMarker == null)
                        {
                            agentMarker = mMap.addMarker(new MarkerOptions()
                                    .position(agentLatLng)
                                    .title("Delivery Agent")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.swift_icon)));
                        }
                        else
                        {
                            agentMarkerMovement(agentMarker, agentLatLng);
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(agentLatLng, 15));
                    }
                    else
                    {
                        Log.e(TAG, "Agent latitude or longitude is null.");
                    }
                }
                else
                {
                    Log.e(TAG, "Agent location snapshot does not exist.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to track agent location: " + error.getMessage());
                Toast.makeText(UserTrackingActivity.this, "Failed to track agent location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void monitorDeliveryStatus(String deliveryId)
    {
        DatabaseReference deliveryStatusReference = FirebaseDatabase.getInstance().getReference("Deliveries").child(deliveryId).child("Status");
        deliveryStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String status = snapshot.getValue(String.class);
                    if ("delivered".equalsIgnoreCase(status))
                    {
                        mapFragment.getView().setVisibility(View.GONE);
                        sv_userTracking.setVisibility(View.GONE);;
                        tv_deliveryId.setVisibility(View.GONE);
                        tv_deliveryStatus.setVisibility(View.GONE);
                        btn_chat.setVisibility(View.GONE);
                        tv_userChat.setVisibility(View.GONE);
                        deliveredLayout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Log.e("DeliveryStatus", "delivery status field not found.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DeliveryStatus", "Failed to monitor delivery status");
            }
        });
    }

    private void agentMarkerMovement(Marker marker, LatLng finalPosition)
    {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final long duration = 1000;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lat = t * finalPosition.latitude + (1 - t) * startLatLng.latitude;
                double lng = t * finalPosition.longitude + (1 - t) * startLatLng.longitude;
                marker.setPosition(new LatLng(lat, lng));


                if (t < 1.0)
                {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        monitorDeliveryStatus(deliveryId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deliveriesReference != null) {
            deliveriesReference.removeEventListener(deliveriesValueEventListener);
        }
    }
}