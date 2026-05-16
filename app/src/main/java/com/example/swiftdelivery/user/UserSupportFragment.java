package com.example.swiftdelivery.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserSupportFragment extends Fragment {
    Button btnSubmit;
    EditText et_subject, et_question;
    private String userId, userName, userEmail;
    private DatabaseReference userReference, supportReference;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_support, container, false);

        et_subject = view.findViewById(R.id.etUserSupportSubject);
        et_question = view.findViewById(R.id.etUserSupportQuestion);
        btnSubmit = view.findViewById(R.id.btnUserSupportSubmit);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getActivity(), "User not authorized.", Toast.LENGTH_SHORT).show();
        }

        userReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        supportReference = FirebaseDatabase.getInstance().getReference("User Support");

        btnSubmit.setOnClickListener(v -> submitUserSupportRequest());

        return view;
    }
    private void submitUserSupportRequest()
    {
        String userSubject = et_subject.getText().toString().trim();
        String userQuestion = et_question.getText().toString().trim();

        if (TextUtils.isEmpty(userSubject) || TextUtils.isEmpty(userQuestion)) {
            Toast.makeText(getActivity(), "Please fill the Subject/Question.", Toast.LENGTH_SHORT).show();
            return;
        }

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    userName = snapshot.child("name").getValue(String.class);
                    userEmail = snapshot.child("email").getValue(String.class);

                    if (userName != null && userEmail != null)
                    {
                        Map<String, String> userSupportData = new HashMap<>();
                        userSupportData.put("ID", userId);
                        userSupportData.put("name", userName);
                        userSupportData.put("email", userEmail);
                        userSupportData.put("subject", userSubject);
                        userSupportData.put("question", userQuestion);

                        supportReference.push().setValue(userSupportData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Thank You.\nWe will get back to you Shortly.", Toast.LENGTH_SHORT).show();
                                et_subject.setText("");
                                et_question.setText("");
                            } else {
                                Toast.makeText(getActivity(), "Failed to submit support request. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Log.e("UserSupportFragment", "userName and userEmail found to be empty");
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "User details not found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error retrieving user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}