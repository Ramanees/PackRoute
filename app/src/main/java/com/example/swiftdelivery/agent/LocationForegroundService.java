package com.example.swiftdelivery.agent;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.swiftdelivery.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LocationForegroundService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference agentLocationReference;
    private LocationCallback locationCallback;
    private static final String CHANNEL_ID = "LocationUpdatesChannel";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Swift")
                .setContentText("Updating your location...")
                .setSmallIcon(R.drawable.swift_icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        startForeground(1, notification);

        startLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null ;
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Updates",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationChannel.setDescription("Channel for location update notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
            {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void startLocationUpdates()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null)
                {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    updateLocationInFirebase(latitude, longitude);
                }
            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else
        {
            Log.e("LocationUpdateService", "Location Permission not granted: ");
        }
    }

    private void updateLocationInFirebase(double latitude, double longitude)
    {
        String agentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        agentLocationReference = FirebaseDatabase.getInstance().getReference("Agent Locations").child(agentId);
        agentLocationReference.setValue(new HashMap<String, Object>() {
            {
                put("latitude", latitude);
                put("longitude", longitude);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null && locationCallback != null)
        {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}