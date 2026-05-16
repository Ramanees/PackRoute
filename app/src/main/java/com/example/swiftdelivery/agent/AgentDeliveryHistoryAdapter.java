package com.example.swiftdelivery.agent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class AgentDeliveryHistoryAdapter extends RecyclerView.Adapter<AgentDeliveryHistoryAdapter.AgentDeliveryViewHolder> {

    private List<AgentDeliveryHistory> deliveries;

    public AgentDeliveryHistoryAdapter(List<AgentDeliveryHistory> deliveries)
    {
        this.deliveries = deliveries;
    }

    @NonNull
    @Override
    public AgentDeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_history_agent, parent, false);
        return new AgentDeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentDeliveryViewHolder holder, int position) {
        AgentDeliveryHistory delivery = deliveries.get(position);
        holder.deliveryId.setText("Delivery ID: " + delivery.getDeliveryID());
        holder.userName.setText("Name: " + delivery.getUserName());
        holder.userContact.setText("Mobile Number: " + delivery.getUserMobile());
        holder.pickupAddress.setText("Pickup Address: " + delivery.getPickupAddress());
        holder.packageDetails.setText("Package Summary: " + delivery.getPackageDetails());
        holder.deliveryAddress.setText("Delivery Address: " + delivery.getDeliveryAddress());
        holder.status.setText("Status: " + delivery.getStatus());
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public void updateDeliveries(List<AgentDeliveryHistory> deliveries)
    {
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }

    public static class AgentDeliveryViewHolder extends RecyclerView.ViewHolder
    {
        TextView deliveryId, userName, userContact, pickupAddress, packageDetails, deliveryAddress, status;

        public AgentDeliveryViewHolder(View itemView)
        {
            super(itemView);

            deliveryId = itemView.findViewById(R.id.item_history_agent_deliveryId);
            userName = itemView.findViewById(R.id.item_history_agent_userName);
            userContact = itemView.findViewById(R.id.item_history_agent_userMobile);
            pickupAddress = itemView.findViewById(R.id.item_history_agent_pickupAddress);
            deliveryAddress = itemView.findViewById(R.id.item_history_agent_deliveryAddress);
            packageDetails = itemView.findViewById(R.id.item_history_agent_packageDetails);
            status = itemView.findViewById(R.id.item_history_agent_status);
        }
    }
}