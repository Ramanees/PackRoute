package com.example.swiftdelivery.admin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminAppInsightsFragment extends Fragment {

    private PieChart pieChartUsersAgents;
    private BarChart barChartGrowth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_app_insights, container, false);

        pieChartUsersAgents = view.findViewById(R.id.pieChartUsersAgents);
        barChartGrowth = view.findViewById(R.id.barChartDeliveriesGrowth);

        loadUserAgentData();
        loadMonthlyDeliveryGrowth();

        return view;
    }
    private void loadUserAgentData()
    {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference agentsReference = FirebaseDatabase.getInstance().getReference("Delivery Agents");

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCnt = snapshot.getChildrenCount();

                agentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long agentCnt = snapshot.getChildrenCount();


                        ArrayList<PieEntry> entries_pie = new ArrayList<>();
                        entries_pie.add(new PieEntry(userCnt, "Users"));
                        entries_pie.add(new PieEntry(agentCnt, "Delivery Agents"));

                        int userColor = ContextCompat.getColor(getContext(), R.color.bg);
                        int agentColor = ContextCompat.getColor(getContext(), R.color.bg2);

                        List<Integer> colors = new ArrayList<>();
                        colors.add(userColor);
                        colors.add(agentColor);

                        PieDataSet dataSet_pie = new PieDataSet(entries_pie, "Users and Delivery Agents");
                        dataSet_pie.setColors(colors);
                        dataSet_pie.setValueTextSize(14f);
                        dataSet_pie.setValueTextColor(Color.WHITE);

                        PieData data_pie = new PieData(dataSet_pie);

                        pieChartUsersAgents.setData(data_pie);
                        pieChartUsersAgents.setCenterText("Users and Delivery Agents");
                        pieChartUsersAgents.setCenterTextSize(14f);
                        pieChartUsersAgents.setHoleRadius(40f);
                        pieChartUsersAgents.setTransparentCircleRadius(45f);
                        pieChartUsersAgents.getDescription().setEnabled(false);
                        pieChartUsersAgents.setEntryLabelTextSize(10f);
                        pieChartUsersAgents.setEntryLabelColor(Color.BLACK);
                        pieChartUsersAgents.invalidate();

                        Legend legend = pieChartUsersAgents.getLegend();
                        legend.setEnabled(true);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        legend.setDrawInside(false);
                        legend.setTextSize(8f);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error retrieving Delivery Agents data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error retrieving Users data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMonthlyDeliveryGrowth()
    {
        DatabaseReference deliveriesReference = FirebaseDatabase.getInstance().getReference("Deliveries");
        deliveriesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<Integer, Integer> monthlyDeliveries = new HashMap<>();
                for (int i = 1; i<=12; i++) {
                    monthlyDeliveries.put(i, 0);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                    String deliveryDate = deliverySnapshot.child("DeliveryRequestDate").getValue(String.class);
                    if (deliveryDate != null) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dateFormat.parse(deliveryDate));
                            int month = calendar.get(Calendar.MONTH) + 1;
                            monthlyDeliveries.put(month, monthlyDeliveries.get(month) + 1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ArrayList<BarEntry> entries_bar = new ArrayList<>();
                for (int month = 1; month <=12; month++) {
                    entries_bar.add(new BarEntry(month, monthlyDeliveries.get(month)));
                }

                BarDataSet dataSet_bar = new BarDataSet(entries_bar, "Monthly Deliveries");
                BarData data_bar = new BarData(dataSet_bar);
                barChartGrowth.setData(data_bar);
                barChartGrowth.getDescription().setText("Deliveries per Month");
                barChartGrowth.getXAxis().setLabelCount(12);
                barChartGrowth.invalidate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error retrieving Monthly Deliveries data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}