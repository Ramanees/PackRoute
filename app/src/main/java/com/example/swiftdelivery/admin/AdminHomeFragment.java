package com.example.swiftdelivery.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.swiftdelivery.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminHomeFragment extends Fragment {

    Button btnManageUsers, btnManageAgents, btnManageDeliveries, btnSupportRequests, btnAppInsights, btnGenReports;
    TextView tvDateTime;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        tvDateTime = view.findViewById(R.id.tv_DateTime);
        btnManageUsers = view.findViewById(R.id.btnManageUsers_admin);
        btnManageAgents = view.findViewById(R.id.btnManageAgents_admin);
        btnManageDeliveries = view.findViewById(R.id.btnManageDelivery_admin);
        btnSupportRequests = view.findViewById(R.id.btnSupport_admin);
        btnAppInsights = view.findViewById(R.id.btnInsights_admin);
        btnGenReports = view.findViewById(R.id.btnReports_admin);

        updateDateTime();

        btnManageUsers.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminManageUserFragment()).addToBackStack(null).commit();
        });

        btnManageAgents.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminManageAgentFragment()).addToBackStack(null).commit();
        });

        btnManageDeliveries.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminManageDeliveryFragment()).addToBackStack(null).commit();
        });

        btnSupportRequests.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminSupportFragment()).addToBackStack(null).commit();
        });

        btnAppInsights.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminAppInsightsFragment()).addToBackStack(null).commit();
        });

        btnGenReports.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_admin, new AdminReportsFragment()).addToBackStack(null).commit();
        });

        return view;
    }
    private void updateDateTime()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE\ndd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                String currentDateTime = dateFormat.format(new Date());
                tvDateTime.setText(currentDateTime);
                handler.postDelayed(this,1000);
            }
        }, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}