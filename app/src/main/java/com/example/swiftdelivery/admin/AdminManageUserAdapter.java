package com.example.swiftdelivery.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftdelivery.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminManageUserAdapter extends RecyclerView.Adapter<AdminManageUserAdapter.UserViewHolder> {

    private Context context;
    private List<AdminManageUser> userList;

    public AdminManageUserAdapter(Context context, List<AdminManageUser> userList)
    {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminManageUser user = userList.get(position);

        holder.txtName.setText("Name: " + user.getName());
        holder.txtEmail.setText("Email: " + user.getEmail());
        holder.txtPhone.setText("Mobile: " + user.getPhone());

        /*
        holder.btnEdit.setOnClickListener(view -> {
            holder.txtName.setEnabled(true);
            holder.txtEmail.setEnabled(true);
            holder.txtPhone.setEnabled(true);
            holder.btnEdit.setText("Save");

            holder.btnEdit.setOnClickListener(saveView -> {
                String updatedName = holder.txtName.getText().toString().trim();
                String updatedEmail = holder.txtEmail.getText().toString().trim();
                String updatedPhone = holder.txtPhone.getText().toString().trim();

                if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                    Toast.makeText(context, "All the fields must be filled .", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUserId());

                userReference.child("name").setValue(updatedName);
                userReference.child("email").setValue(updatedEmail);
                userReference.child("phone").setValue(updatedPhone).addOnSuccessListener(aVoid -> {
                    holder.btnEdit.setText("Edit");
                    holder.txtName.setEnabled(false);
                    holder.txtEmail.setEnabled(false);
                    holder.txtPhone.setEnabled(false);

                    Toast.makeText(context, "User details updated successfully.", Toast.LENGTH_SHORT).show();
                }). addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update user details.", Toast.LENGTH_SHORT).show();
                });
            });
        }); */
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        EditText txtName, txtEmail, txtPhone;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName_User);
            txtEmail = itemView.findViewById(R.id.txtEmail_User);
            txtPhone = itemView.findViewById(R.id.txtPhone_User);
            btnEdit = itemView.findViewById(R.id.btnEdit_User);
            btnDelete = itemView.findViewById(R.id.btnDelete_User);
        }
    }

    public void setUserList(List<AdminManageUser> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
}