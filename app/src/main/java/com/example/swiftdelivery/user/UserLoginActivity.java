package com.example.swiftdelivery.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftdelivery.R;
import com.example.swiftdelivery.admin.AdminLoginActivity;
import com.example.swiftdelivery.agent.AgentLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity extends AppCompatActivity {

    EditText etMail, etPass;
    Button btnLogin, btnNewUser, btnAgentLogin;
    ImageView logo;
    TextView tvForgotPass;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private int logoClickCount = 0;
    private Handler handlerLogo = new Handler();
    private static final int CLICK_RESET_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        etMail = findViewById(R.id.etLogEmail);
        etPass = findViewById(R.id.etLogPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnNewUser = findViewById(R.id.btnNewUser);
        btnAgentLogin = findViewById(R.id.btnAgent);
        logo = findViewById(R.id.logo_user);
        tvForgotPass = findViewById(R.id.tvUserForgotPassword);

        logo.setOnClickListener(v -> {
            logoClickCount++;
            if (logoClickCount == 3) {
                logoClickCount = 0; // Click count is reset
                Toast.makeText(this, "Navigating to Admin Login Interface", Toast.LENGTH_SHORT).show();
                Intent int_admin = new Intent(UserLoginActivity.this, AdminLoginActivity.class);
                startActivity(int_admin);
                finish();
            }
            handlerLogo.postDelayed(() -> logoClickCount = 0, CLICK_RESET_TIME);
        });

        tvForgotPass.setOnClickListener(v -> forgotPass());

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEmail() | !checkPassword()){}
                else {
                    loginUser();
                }
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signup = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                startActivity(intent_signup);
                finish();
            }
        });

        btnAgentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_agent = new Intent(UserLoginActivity.this, AgentLoginActivity.class);
                startActivity(intent_agent);
                finish();
            }
        });
    }

    public boolean checkEmail(){
        String val = etMail.getText().toString();
        if (val.isEmpty()){
            etMail.setError("Invalid Email !");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            etMail.setError("Enter a valid email");
            etMail.requestFocus();
            return false;
        } else {
            etMail.setError(null);
            return true;
        }
    }

    public boolean checkPassword(){
        String val = etPass.getText().toString();
        if (val.isEmpty()){
            etPass.setError("Enter Password !");
            return false;
        } else {
            etPass.setError(null);
            return true;
        }
    }

    public void loginUser()
    {
        String email = etMail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(UserLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserLoginActivity.this, UserNavHomeActivity.class);
                startActivity(intent);
                finish();
            } else
            {
                Toast.makeText(UserLoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void forgotPass()
    {
        String email = etMail.getText().toString().trim();

        if (email.isEmpty())
        {
            etMail.setError("Enter Email Address !");
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "The Password reset email has been sent to\n" + email, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}