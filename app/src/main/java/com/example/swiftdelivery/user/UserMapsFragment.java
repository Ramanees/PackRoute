package com.example.swiftdelivery.user;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserMapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng pickupLocation;
    private LatLng dropoffLocation;
    TextView tvPick,tvDrop;
    Button btnAddr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPick = view.findViewById(R.id.tv_PickAddr);
        tvDrop = view.findViewById(R.id.tv_DropAddr);
        btnAddr = view.findViewById(R.id.btnSubmitAddr);

        btnAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDeliveryFragment deliveryFragment = new UserDeliveryFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user, deliveryFragment).addToBackStack(null).commit();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.5797013, 58.3930057), 10));

        mMap.setOnMapClickListener(latLng -> {
            if (pickupLocation == null) {
                pickupLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Pickup Location"));
                String pickupAddress = getAddressFromLocation(latLng);
                SharedPreferences pickupPref = requireContext().getSharedPreferences("PICKUP_PREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor pickup_editor = pickupPref.edit();
                pickup_editor.putString("PICKUP",pickupAddress);
                pickup_editor.putLong("PICK_LAT", Double.doubleToLongBits(latLng.latitude));
                pickup_editor.putString("PICKUP_LAT",String.valueOf(latLng.latitude));
                pickup_editor.putLong("PICK_LNG", Double.doubleToLongBits(latLng.longitude));
                pickup_editor.putString("PICKUP_LONG",String.valueOf(latLng.longitude));
                pickup_editor.apply();
                tvPick.setText("Pickup Address: "+pickupAddress);
                Toast.makeText(getContext(), "Pickup location set!", Toast.LENGTH_SHORT).show();

            } else {
                dropoffLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Delivery Location"));
                String dropoffAddress = getAddressFromLocation(latLng);
                SharedPreferences dropoffPref = requireContext().getSharedPreferences("DROPOFF_PREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor dropoff_editor = dropoffPref.edit();
                dropoff_editor.putString("DROPOFF",dropoffAddress);
                dropoff_editor.putLong("DELV_LAT", Double.doubleToLongBits(latLng.latitude));
                dropoff_editor.putString("DROPOFF_LAT",String.valueOf(latLng.latitude));
                dropoff_editor.putLong("DELV_LNG", Double.doubleToLongBits(latLng.longitude));
                dropoff_editor.putString("DROPOFF_LONG",String.valueOf(latLng.longitude));
                dropoff_editor.apply();
                tvDrop.setText("Delivery Address: "+dropoffAddress);
                Toast.makeText(getContext(), "Delivery location set!", Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
    }

    private String getAddressFromLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        String address = "Address not found";

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                // Get the address string
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error getting address", Toast.LENGTH_SHORT).show();
        }

        return address;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}