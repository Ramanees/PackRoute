package com.example.swiftdelivery.agent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AgentIncomeFragment extends Fragment {

    private TextView tvTotalEarnings;
    private RecyclerView rvHistory;
    private EarningAdapter adapter;
    private List<Earning> earningList = new ArrayList<>();
    private DatabaseReference agentRef;
    private String agentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_income, container, false);

        tvTotalEarnings = view.findViewById(R.id.tvTotalEarnings);
        rvHistory = view.findViewById(R.id.rvEarningsHistory);
        Button btnWithdraw = view.findViewById(R.id.btnWithdraw);

        agentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        agentRef = FirebaseDatabase.getInstance().getReference("Delivery Agents").child(agentId);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EarningAdapter(earningList);
        rvHistory.setAdapter(adapter);

        btnWithdraw.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Withdrawal request sent to processing.", Toast.LENGTH_SHORT).show();
        });

        loadIncomeData();

        return view;
    }

    private void loadIncomeData() {
        agentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double total = snapshot.child("WalletBalance").getValue(Double.class);
                    if (total == null) total = 0.0;
                    tvTotalEarnings.setText("$" + String.format("%.2f", total));

                    earningList.clear();
                    DataSnapshot earningsSnapshot = snapshot.child("Earnings");
                    for (DataSnapshot data : earningsSnapshot.getChildren()) {
                        Earning e = data.getValue(Earning.class);
                        if (e != null) earningList.add(e);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("IncomeFragment", error.getMessage());
            }
        });
    }
}
