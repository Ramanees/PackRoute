package com.example.swiftdelivery.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftdelivery.R;
import com.example.swiftdelivery.user.UserLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    EditText et_AdminMail, et_AdminPass;
    Button btnAdminLogin;
    ImageButton btnBack;
    FirebaseAuth mAuth;
    DatabaseReference adminReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        et_AdminMail = findViewById(R.id.etLogEmail_admin);
        et_AdminPass = findViewById(R.id.etLogPassword_admin);
        btnAdminLogin = findViewById(R.id.btnLogin_admin);
        btnBack = findViewById(R.id.imgBtn_BackNavAdmin);



        adminReference = FirebaseDatabase.getInstance().getReference("Admins");

        btnAdminLogin.setOnClickListener(v -> {
            String adminMail = et_AdminMail.getText().toString();
            String adminPassword = et_AdminPass.getText().toString();
            if (TextUtils.isEmpty(adminMail) || TextUtils.isEmpty(adminPassword)) {
                Toast.makeText(AdminLoginActivity.this, "Enter Email and Password.", Toast.LENGTH_SHORT).show();
            } else {
                loginAdmin(adminMail, adminPassword);
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent int_back = new Intent(AdminLoginActivity.this, UserLoginActivity.class);
            startActivity(int_back);
            finish();
        });
    }
    private void loginAdmin(String email, String password)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();
                Log.d("AdminLoginActivity", "Authenticated UID: " + uid);
                adminReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("AdminLogin", "Admin UID found: " + uid);
                            Log.d("AdminLogin", "Admin details: " + snapshot.getValue());
                            Toast.makeText(AdminLoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                            Intent int_AdminLogin = new Intent(AdminLoginActivity.this, AdminNavHomeActivity.class);
                            startActivity(int_AdminLogin);
                        } else {
                            Log.e("AdminLogin", "Admin UID not found: " + uid);
                            mAuth.signOut();
                            Toast.makeText(AdminLoginActivity.this, "Admin privileges required.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AdminLoginActivity","Admin does not exist");
                    }
                });
            } else {
                Toast.makeText(AdminLoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}