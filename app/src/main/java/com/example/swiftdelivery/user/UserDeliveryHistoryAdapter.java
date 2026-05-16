package com.example.swiftdelivery.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class UserDeliveryHistoryAdapter extends RecyclerView.Adapter<UserDeliveryHistoryAdapter.DeliveryViewHolder> {

    private List<UserDeliveryHistory> deliveries;
    private OnDeliveryClickListener listener;

    public UserDeliveryHistoryAdapter(List<UserDeliveryHistory> deliveries, OnDeliveryClickListener listener)
    {
        this.deliveries = deliveries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_history_user, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        UserDeliveryHistory delivery = deliveries.get(position);
        holder.deliveryId.setText("Delivery ID: " + delivery.getDeliveryID());
        
        String agentName = delivery.getAssignedAgentName();
        String agentMobile = delivery.getAssignedAgentMobile();
        
        if (agentName == null || agentName.isEmpty()) {
            holder.agentName.setText("Traveler: Searching for matches...");
            holder.agentContact.setText("Mobile: N/A");
            holder.btnTrackDelivery.setEnabled(false);
            holder.btnTrackDelivery.setAlpha(0.5f);
        } else {
            holder.agentName.setText("Traveler: " + agentName);
            holder.agentContact.setText("Mobile: " + agentMobile);
            holder.btnTrackDelivery.setEnabled(true);
            holder.btnTrackDelivery.setAlpha(1.0f);
        }

        holder.pickupAddress.setText("Pickup: " + delivery.getPickupAddress());
        holder.deliveryAddress.setText("Drop-off: " + delivery.getDeliveryAddress());
        holder.packageDetails.setText("Details: " + delivery.getPackageDetails());
        holder.status.setText("Status: " + delivery.getStatus());

        holder.btnTrackDelivery.setOnClickListener(v -> listener.onDeliveryClick(delivery));
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public void updateDeliveries(List<UserDeliveryHistory> deliveries)
    {
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }

    public static class DeliveryViewHolder extends RecyclerView.ViewHolder
    {
        TextView deliveryId, agentName, agentContact, pickupAddress, packageDetails, deliveryAddress, status;
        Button btnTrackDelivery;

        public DeliveryViewHolder(View itemView)
        {
            super(itemView);
            deliveryId = itemView.findViewById(R.id.history_item_deliveryId);
            agentName = itemView.findViewById(R.id.history_item_agentName);
            agentContact = itemView.findViewById(R.id.history_item_agentMobile);
            pickupAddress = itemView.findViewById(R.id.history_item_pickupaddress);
            deliveryAddress = itemView.findViewById(R.id.history_item_deliveryaddress);
            packageDetails = itemView.findViewById(R.id.history_item_packageDetails);
            status = itemView.findViewById(R.id.history_item_status);
            btnTrackDelivery = itemView.findViewById(R.id.btnTrackDelivery);
        }
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(UserDeliveryHistory delivery);
    }
}