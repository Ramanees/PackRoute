package com.example.swiftdelivery.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class AdminManageDeliveryAdapter extends RecyclerView.Adapter<AdminManageDeliveryAdapter.AdminDeliveryViewHolder> {

    private Context context;
    private List<AdminManageDelivery> deliveryList;

    public AdminManageDeliveryAdapter(Context context, List<AdminManageDelivery> deliveryList)
    {
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public AdminDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_delivery, parent, false);
        return new AdminDeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDeliveryViewHolder holder, int position) {
        AdminManageDelivery delivery = deliveryList.get(position);

        holder.txtUserName.setText("User Name: " + delivery.getUserName());
        holder.txtUserID.setText("User ID: " + delivery.getUserID());
        holder.txtUserPhone.setText("User Mobile: " + delivery.getUserMobile());
        holder.txtAgentName.setText("Agent Name: " + delivery.getAssignedAgentName());
        holder.txtAgentID.setText("Agent ID: " + delivery.getAssignedAgent());
        holder.txtAgentPhone.setText("Agent Mobile: " + delivery.getAssignedAgentMobile());
        holder.txtPickupLocation.setText("Pickup Location: " + delivery.getPickupAddress());
        holder.txtDeliveryLocation.setText("Delivery Location: " + delivery.getDeliveryAddress());
        holder.txtPackageDetails.setText("Package Details: " + delivery.getPackageDetails());
        holder.txtDeliveryStatus.setText("Delivery Status: " + delivery.getStatus());
        holder.txtDeliveryDate.setText("Delivery Date: " + delivery.getDeliveryRequestDate());

    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public static class AdminDeliveryViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName, txtUserID, txtUserPhone, txtAgentName, txtAgentID, txtAgentPhone, txtPickupLocation, txtDeliveryLocation, txtPackageDetails, txtDeliveryDate;
        EditText txtDeliveryStatus;
        public AdminDeliveryViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txtUserName_Admin);
            txtUserID = itemView.findViewById(R.id.txtUserID_Admin);
            txtUserPhone = itemView.findViewById(R.id.txtUserPhone_Admin);
            txtAgentName = itemView.findViewById(R.id.txtAgentName_Admin);
            txtAgentID = itemView.findViewById(R.id.txtAgentID_Admin);
            txtAgentPhone = itemView.findViewById(R.id.txtAgentPhone_Admin);
            txtPickupLocation = itemView.findViewById(R.id.txtPickupLocation_Admin);
            txtDeliveryLocation = itemView.findViewById(R.id.txtDeliveryLocation_Admin);
            txtPackageDetails = itemView.findViewById(R.id.txtPackageDetails_Admin);
            txtDeliveryDate = itemView.findViewById(R.id.txtDeliveryDate_Admin);
            txtDeliveryStatus = itemView.findViewById(R.id.txtDeliveryStatus_Admin);
        }
    }

    public void setDeliveryList(List<AdminManageDelivery> deliveryList) {
        this.deliveryList = deliveryList;
        notifyDataSetChanged();
    }
}