package com.example.swiftdelivery.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;

import java.util.List;

public class AdminManageAgentAdapter extends RecyclerView.Adapter<AdminManageAgentAdapter.AgentViewHolder> {

    private Context context;
    private List<AdminManageAgent> agentList;

    public AdminManageAgentAdapter(Context context, List<AdminManageAgent> agentList)
    {
        this.context = context;
        this.agentList = agentList;
    }

    @NonNull
    @Override
    public AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_agent, parent, false);
        return new AgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentViewHolder holder, int position) {
        AdminManageAgent agent = agentList.get(position);

        holder.txtName.setText("Name: " + agent.getName());
        holder.txtEmail.setText("Email: " + agent.getEmail());
        holder.txtPhone.setText("Mobile: " + agent.getPhone());
        holder.txtResident.setText("Resident ID: " + agent.getResident());
        holder.txtVehicleType.setText("Vehicle Type: " + agent.getVehicle());
        holder.txtVehicleRegistration.setText("Vehicle Registration: " + agent.getRegistration());

        holder.btnEdit.setOnClickListener(view -> {
            // Show edit dialog or navigate to an edit screen
            Toast.makeText(context, "Edit " + agent.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return agentList.size();
    }

    public static class AgentViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtEmail, txtPhone, txtResident, txtVehicleType, txtVehicleRegistration;
        Button btnEdit, btnDelete;

        public AgentViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName_Agent);
            txtEmail = itemView.findViewById(R.id.txtEmail_Agent);
            txtPhone = itemView.findViewById(R.id.txtPhone_Agent);
            txtResident = itemView.findViewById(R.id.txtResident_Agent);
            txtVehicleType = itemView.findViewById(R.id.txtVehicleType_Agent);
            txtVehicleRegistration = itemView.findViewById(R.id.txtVehicleRegistration_Agent);
            btnEdit = itemView.findViewById(R.id.btnEdit_Agent);
            btnDelete = itemView.findViewById(R.id.btnDelete_Agent);
        }
    }

    public void setAgentList(List<AdminManageAgent> agentList) {
        this.agentList = agentList;
        notifyDataSetChanged();
    }
}