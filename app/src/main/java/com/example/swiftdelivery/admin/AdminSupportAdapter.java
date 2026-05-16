package com.example.swiftdelivery.admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class AdminSupportAdapter extends RecyclerView.Adapter<AdminSupportAdapter.ViewHolder> {

    private Context context;
    private List<AdminSupport> questionList;

    public AdminSupportAdapter(Context context, List<AdminSupport> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_support_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminSupport question = questionList.get(position);

        holder.txtUID.setText(question.getUID());
        holder.txtName.setText(question.getName());
        holder.txtEmail.setText(question.getEmail());
        holder.txtSubject.setText(question.getSubject());
        holder.txtQuestion.setText(question.getQuestion());

        holder.btnReply.setOnClickListener(v -> {
            // Open email client to reply
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{question.getEmail()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "REPLY: " + question.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\n");

            try {
                context.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show();
                Log.e("AdminSupportAdapter", "No email client found");
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUID, txtName, txtEmail, txtSubject, txtQuestion;
        Button btnReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUID = itemView.findViewById(R.id.txtUID_support);
            txtName = itemView.findViewById(R.id.txtName_support);
            txtEmail = itemView.findViewById(R.id.txtEmail_support);
            txtSubject = itemView.findViewById(R.id.txtSubject_support);
            txtQuestion = itemView.findViewById(R.id.txtQuestion_support);
            btnReply = itemView.findViewById(R.id.btnReply_support);
        }
    }
}