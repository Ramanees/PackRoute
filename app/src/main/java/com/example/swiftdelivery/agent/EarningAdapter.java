package com.example.swiftdelivery.agent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class EarningAdapter extends RecyclerView.Adapter<EarningAdapter.ViewHolder> {

    private List<Earning> earningList;

    public EarningAdapter(List<Earning> earningList) {
        this.earningList = earningList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Earning earning = earningList.get(position);
        holder.tvId.setText("Order #" + earning.getDeliveryId().substring(Math.max(0, earning.getDeliveryId().length() - 6)));
        holder.tvDate.setText(earning.getDate());
        holder.tvAmount.setText("+$" + String.format("%.2f", earning.getAmount()));
    }

    @Override
    public int getItemCount() {
        return earningList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvEarningDeliveryId);
            tvDate = itemView.findViewById(R.id.tvEarningDate);
            tvAmount = itemView.findViewById(R.id.tvEarningAmount);
        }
    }
}
