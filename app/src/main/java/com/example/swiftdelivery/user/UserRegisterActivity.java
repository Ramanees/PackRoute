package com.example.swiftdelivery.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftdelivery.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    EditText etName, etEmail, etPassword, etMobile;
    Button btnRegister;
    CheckBox checkboxUserConsent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_register);

        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPass);
        etMobile = findViewById(R.id.etRegMobile);
        btnRegister = findViewById(R.id.btnRegister);
        checkboxUserConsent = findViewById(R.id.chkbxUserConsent);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        checkboxUserConsent.setOnClickListener(v -> {
            if (v.getId() == R.id.chkbxUserConsent) {
                showUserTermsAndConditions();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void registerUser()
    {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = "+968" + etMobile.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Enter mobile", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkboxUserConsent.isChecked()) {
            Toast.makeText(this, "You must accept the Terms and Conditions to register", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful())
            {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null)
                {
                    saveUserDetails(user, name);
                    etName.setText("");
                    etEmail.setText("");
                    etPassword.setText("");
                    etMobile.setText("");
                    checkboxUserConsent.setChecked(false);
                    Intent intent_login = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
                    startActivity(intent_login);
                    finish();
                }
            } else
            {
                Toast.makeText(UserRegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails(FirebaseUser user, String name)
    {
        String uid = user.getUid();
        String email = user.getEmail();
        String phone = "+968" + etMobile.getText().toString();
        String password = etPassword.getText().toString();
        String role = "user";
        User userDetails = new User(name, email, phone, password, role);

        reference.child(uid).setValue(userDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserRegisterActivity.this, "User details saved.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserRegisterActivity.this, "Failed to save user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserTermsAndConditions() {
        new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage("1. Acceptance of Terms\n" +
                        "By signing up and using the Swift App, you agree to comply with and be bound by these Terms and Conditions. If you do not agree to these terms, you must not use the app.\n\n" +
                        "2. User Responsibilities\n" +
                        "•\tYou are solely responsible for the accuracy of all delivery-related details, including pickup and delivery addresses, contact information, and package descriptions.\n" +
                        "•\tYou must ensure that the packages you send comply with applicable laws and regulations.\n" +
                        "•\tYou must not send any prohibited, illegal, or hazardous items using the Swift App.\n\n" +
                        "3. Prohibited Items\n" +
                        "The following items are strictly prohibited for delivery:\n" +
                        "•\tIllegal drugs, firearms, ammunition, explosives, and other contraband.\n" +
                        "•\tHazardous materials such as chemicals, flammable substances, or radioactive items.\n" +
                        "•\tPerishable goods that require specific storage conditions unless the app explicitly supports it.\n" +
                        "•\tCounterfeit goods, stolen property, or any items in violation of intellectual property rights.\n\n" +
                        "4. Delivery Policy\n" +
                        "•\tSwift aims to provide timely delivery services; however, delays may occur due to unforeseen circumstances, such as weather, traffic, or other external factors.\n" +
                        "•\tSwift is not liable for delays or delivery failures caused by factors beyond its control.\n\n" +
                        "5. Liability\n" +
                        "•\tSwift is not responsible for:\n" +
                        "o\tLoss or damage to packages caused by third-party delivery agents or events beyond its control.\n" +
                        "o\tDelays or issues resulting from incorrect information provided by the user.\n" +
                        "•\tIn case of disputes, Swift will assist in mediating between users and delivery agents but cannot guarantee resolutions.\n\n" +
                        "6. Privacy\n" +
                        "•\tSwift collects and processes your personal data in compliance with its Privacy Policy.\n" +
                        "•\tYour information and location details will only be shared with authorized delivery agents to complete your delivery request.\n\n" +
                        "7. Account Security\n" +
                        "•\tYou are responsible for maintaining the confidentiality of your account credentials.\n" +
                        "•\tSwift will not be liable for unauthorized access to your account caused by negligence or failure to secure your account information.\n\n" +
                        "8. Termination\n" +
                        "Swift reserves the right to terminate or suspend your account without prior notice if:\n" +
                        "•\tYou violate the mentioned Terms and Conditions.\n" +
                        "•\tYour actions compromise the safety or integrity of the app, other users, or delivery agents.\n\n" +
                        "9. Modifications to Terms\n" +
                        "Swift reserves the right to modify these Terms and Conditions at any time. Continued use of the app constitutes acceptance of any updated terms.\n\n" +
                        "10. Contact Us\n" +
                        "If you have any questions or concerns regarding these Terms and Conditions, please contact us at support_swiftapp@gmail.com.\n\n" +
                        "Please read carefully before proceeding.\n")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static class User
    {
        public String name;
        public String email;
        public String phone;
        public String password;
        public String role;

        public User()
        {
        }
        public User(String name, String email, String phone, String password, String role) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.role = role;
        }
    }
}