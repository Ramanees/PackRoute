package com.example.swiftdelivery.agent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class AgentAvailableDeliveryAdapter extends RecyclerView.Adapter<AgentAvailableDeliveryAdapter.DeliveryViewHolder> {

    private List<AgentAvailableDelivery> deliveries;
    private OnDeliveryClickListener listener;

    public AgentAvailableDeliveryAdapter(List<AgentAvailableDelivery> deliveries, OnDeliveryClickListener listener)
    {
        this.deliveries = deliveries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeliveryViewHolder holder, int position) {
        AgentAvailableDelivery delivery = deliveries.get(position);
        Log.d("Adapter", "Binding data: " + delivery.getDeliveryAddress());
        holder.deliveryId.setText("Delivery ID: " + delivery.getDeliveryID());
        holder.userName.setText("Name: " + delivery.getUserName());
        holder.userContact.setText("Mobile Number: " + delivery.getUserMobile());
        holder.pickupAddress.setText("Pickup Address: " + delivery.getPickupAddress());
        holder.pickupContact.setText("Pickup Contact: " + delivery.getPickupPhone());
        holder.packageDetails.setText("Package Summary: " + delivery.getPackageDetails());
        holder.deliveryAddress.setText("Delivery Address: " + delivery.getDeliveryAddress());
        holder.deliveryContact.setText("Delivery Contact: " + delivery.getDeliveryPhone());
        holder.btnAcceptDelivery.setOnClickListener(v -> listener.onDeliveryClick(delivery));
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public void updateDeliveries(List<AgentAvailableDelivery> deliveries)
    {
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }

    public static class DeliveryViewHolder extends RecyclerView.ViewHolder
    {
        TextView deliveryId, userName, userContact, pickupAddress, pickupContact, packageDetails, deliveryAddress, deliveryContact;
        Button btnAcceptDelivery;

        public DeliveryViewHolder(View itemView)
        {
            super(itemView);
            deliveryId = itemView.findViewById(R.id.delivery_item_deliveryId);
            btnAcceptDelivery = itemView.findViewById(R.id.btnAcceptDelivery);
            userName = itemView.findViewById(R.id.delivery_item_username);
            userContact = itemView.findViewById(R.id.delivery_item_usermobile);
            pickupAddress = itemView.findViewById(R.id.delivery_item_pickupaddress);
            pickupContact = itemView.findViewById(R.id.delivery_item_pickupcontact);
            packageDetails = itemView.findViewById(R.id.delivery_item_packagedetails);
            deliveryAddress = itemView.findViewById(R.id.delivery_item_deliveryaddress);
            deliveryContact = itemView.findViewById(R.id.delivery_item_deliverycontact);
        }
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(AgentAvailableDelivery delivery);
    }
}