package com.example.swiftdelivery.agent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class AgentActiveDeliveryAdapter extends RecyclerView.Adapter<AgentActiveDeliveryAdapter.ViewHolder> {

    private List<AgentAvailableDelivery> deliveries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AgentAvailableDelivery delivery);
    }

    public AgentActiveDeliveryAdapter(List<AgentAvailableDelivery> deliveries, OnItemClickListener listener) {
        this.deliveries = deliveries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_delivery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgentAvailableDelivery delivery = deliveries.get(position);
        holder.deliveryId.setText("Delivery ID: " + delivery.getDeliveryID());
        holder.userName.setText("Sender: " + delivery.getUserName());
        holder.pickupAddr.setText("Pickup: " + delivery.getPickupAddress());
        holder.deliveryAddr.setText("Drop-off: " + delivery.getDeliveryAddress());

        // Hide unused fields to avoid clutter
        holder.userContact.setVisibility(View.GONE);
        holder.pickupContact.setVisibility(View.GONE);
        holder.deliveryContact.setVisibility(View.GONE);
        holder.packageDetails.setVisibility(View.GONE);

        holder.btnAction.setText("Navigate / Chat");
        holder.btnAction.setOnClickListener(v -> listener.onItemClick(delivery));
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryId, userName, pickupAddr, deliveryAddr, userContact, pickupContact, deliveryContact, packageDetails;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryId = itemView.findViewById(R.id.delivery_item_deliveryId);
            userName = itemView.findViewById(R.id.delivery_item_username);
            pickupAddr = itemView.findViewById(R.id.delivery_item_pickupaddress);
            deliveryAddr = itemView.findViewById(R.id.delivery_item_deliveryaddress);
            userContact = itemView.findViewById(R.id.delivery_item_usermobile);
            pickupContact = itemView.findViewById(R.id.delivery_item_pickupcontact);
            deliveryContact = itemView.findViewById(R.id.delivery_item_deliverycontact);
            packageDetails = itemView.findViewById(R.id.delivery_item_packagedetails);
            btnAction = itemView.findViewById(R.id.btnAcceptDelivery);
        }
    }
}
