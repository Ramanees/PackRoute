package com.example.swiftdelivery.agent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftdelivery.R;
import com.example.swiftdelivery.user.UserLoginActivity;
import com.example.swiftdelivery.user.UserNavHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgentLoginActivity extends AppCompatActivity {

    Button btnNewAgent, btnLogin, btnUser;
    EditText etAgentEmail, etAgentPassword;
    TextView tvForgotPass;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agent_login);

        // Initializing UI elements
        btnNewAgent = findViewById(R.id.btnNewAgent);
        etAgentEmail = findViewById((R.id.etAgentLogEmail));
        etAgentPassword = findViewById(R.id.etAgentLogPassword);
        btnLogin = findViewById(R.id.btnAgentLogin);
        btnUser = findViewById(R.id.btnUser);
        tvForgotPass = findViewById(R.id.tvAgentForgotPassword);

        mAuth = FirebaseAuth.getInstance(); // Firebase Authentication
        reference = FirebaseDatabase.getInstance().getReference("Delivery Agents");

        // Navigate to AgentRegisterActivity
        btnNewAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register = new Intent(AgentLoginActivity.this, AgentRegisterActivity.class);
                startActivity(intent_register);
                finish();
            }
        });

        tvForgotPass.setOnClickListener(v -> forgotPass());

        // Navigate to UserLoginActivity
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_userLogin = new Intent(AgentLoginActivity.this, UserLoginActivity.class);
                startActivity(intent_userLogin);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEmail() || !checkPassword()) {}
                else
                {
                    loginAgent();
                }
            }
        });

    }

    // Validate email input
    public boolean checkEmail(){
        String val = etAgentEmail.getText().toString();
        if (val.isEmpty()){
            etAgentEmail.setError("Enter Email !");
            return false;
        }
        else {
            etAgentEmail.setError(null);
            return true;
        }
    }

    // Validate password input
    public boolean checkPassword(){
        String val = etAgentPassword.getText().toString();
        if (val.isEmpty()){
            etAgentPassword.setError("Enter Password !");
            return false;
        }
        else {
            etAgentPassword.setError(null);
            return true;
        }
    }

    // Handling agent login using Firebase Authentication
    public void loginAgent()
    {
        String email = etAgentEmail.getText().toString().trim();
        String password = etAgentPassword.getText().toString().trim();

        // Authenticate agent via email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(AgentLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AgentLoginActivity.this, AgentNavHomeActivity.class);
                startActivity(intent);
                finish();
            } else
            {
                Toast.makeText(AgentLoginActivity.this, "Login Unsuccessful.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void forgotPass()
    {
        String email = etAgentEmail.getText().toString().trim();

        if (email.isEmpty())
        {
            etAgentEmail.setError("Enter Email Address !");
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