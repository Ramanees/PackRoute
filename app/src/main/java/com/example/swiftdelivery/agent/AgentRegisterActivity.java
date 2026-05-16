package com.example.swiftdelivery.agent;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class AgentRegisterActivity extends AppCompatActivity {

    EditText etAgentName, etAgentEmail, etAgentPassword, etAgentMobile, etResidentID, etVehicleRegistration;
    Spinner spinVehicleType;
    Button btnAgentRegister;
    CheckBox checkboxAgentConsent;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String vehicleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agent_register);

        etAgentName = findViewById(R.id.etRegName);
        etAgentEmail = findViewById(R.id.etRegEmail);
        etAgentPassword = findViewById(R.id.etRegPass);
        etAgentMobile = findViewById(R.id.etRegMobile);
        etResidentID = findViewById(R.id.etRegResidentID);
        etVehicleRegistration = findViewById(R.id.etRegVehicleRegistration);
        spinVehicleType = findViewById(R.id.spinVehicleType);
        btnAgentRegister = findViewById(R.id.btnRegister);
        checkboxAgentConsent = findViewById(R.id.chkbxAgentConsent);

        int customColor = getResources().getColor(R.color.bg2);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Delivery Agents");

        btnAgentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {registerAgent();}
        });

        checkboxAgentConsent.setOnClickListener(v -> {
            if (v.getId() == R.id.chkbxAgentConsent) {
                showAgentTermsAndConditions();
            }
        });

        String[] vehicleTypes = getResources().getStringArray(R.array.vehicleType);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleTypes) {

            @Override
            public boolean isEnabled(int position)
            {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (position == 0)
                {
                    view.setTextColor(Color.GRAY);
                }
                else
                {
                    view.setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinVehicleType.setAdapter(spinnerAdapter);

        spinVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleType = parent.getItemAtPosition(position).toString();
                if (vehicleType.equals(vehicleTypes[0]))
                {
                    ((TextView) view).setTextColor(Color.GRAY);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void registerAgent()
    {
        String name = etAgentName.getText().toString().trim();
        String email = etAgentEmail.getText().toString().trim();
        String mobile = etAgentMobile.getText().toString().trim();
        String password = etAgentPassword.getText().toString().trim();
        String resident = etResidentID.getText().toString().trim();
        String registration = etVehicleRegistration.getText().toString().trim();

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
        if (TextUtils.isEmpty(resident)) {
            Toast.makeText(this, "Enter resident ID number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(registration)) {
            Toast.makeText(this, "Enter vehicle registration number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkboxAgentConsent.isChecked()) {
            Toast.makeText(this, "You must accept the Terms and Conditions to register", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful())
            {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null)
                {
                    saveAgentDetails(user, name);
                    etAgentName.setText("");
                    etAgentEmail.setText("");
                    etAgentPassword.setText("");
                    etAgentMobile.setText("");
                    etResidentID.setText("");
                    etVehicleRegistration.setText("");
                    checkboxAgentConsent.setChecked(false);
                    Intent intent_agentLogin = new Intent(AgentRegisterActivity.this, AgentLoginActivity.class);
                    startActivity(intent_agentLogin);
                    finish();
                }
            }
        });
    }

    private void saveAgentDetails(FirebaseUser user, String name)
    {
        String uid = user.getUid();
        String email = user.getEmail();
        String phone = etAgentMobile.getText().toString();
        String password = etAgentPassword.getText().toString();
        String resident = etResidentID.getText().toString();
        String registration = etVehicleRegistration.getText().toString();
        String role = "agent";
        Agent agentDetails = new Agent(name, email, phone, password, resident, vehicleType, registration, role);

        reference.child(uid).setValue(agentDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(AgentRegisterActivity.this, "Agent details saved.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(AgentRegisterActivity.this, "Failed to save agent details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAgentTermsAndConditions()
    {
        new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage("1. Acceptance of Terms\n" +
                        "By signing up as a delivery agent on the Swift Delivery Application, you agree to comply with these Terms and Conditions. If you do not agree to these terms, you must not use the app or accept delivery assignments.\n\n" +
                        "2. Delivery Agent Responsibilities\n" +
                                "•\tYou must ensure that you have valid identification, vehicle registration, and any licenses required to perform delivery services.\n" +
                                "•\tYou must verify the package contents at the time of pickup to ensure compliance with Swift’s prohibited items policy.\n" +
                                "•\tYou are responsible for ensuring that the package is delivered to the correct recipient and address.\n" +
                                "•\tYou must maintain professionalism, integrity, and courteous behavior during all interactions with users.\n\n" +
                        "3. Prohibited Items\n" +
                                "You must not accept or transport any of the following items:\n" +
                                "•\tIllegal drugs, firearms, ammunition, explosives, or other contraband.\n" +
                                "•\tHazardous materials such as chemicals, flammable substances, or radioactive items.\n" +
                                "•\tPerishable goods requiring special storage conditions, unless explicitly approved by Swift.\n" +
                                "•\tStolen goods, counterfeit items, or packages in violation of intellectual property rights.\n\n" +
                        "4. Vehicle and Equipment\n" +
                                "•\tYou must ensure that your vehicle is in safe and operational condition for carrying out deliveries.\n" +
                                "•\tAny equipment or tools needed to safely transport packages (e.g., straps, storage boxes) must be provided by yourself.\n\n" +
                        "6. Insurance and Liability\n" +
                                "•\tSwift does not provide insurance coverage for packages, your vehicle, or yourself. You are solely responsible for ensuring adequate insurance is in place.\n" +
                                "•\tYou are liable for any loss, damage, or theft of packages while in your possession.\n" +
                                "•\tIn case of disputes or delivery issues, Swift will assist in mediation but is not liable for resolution.\n\n" +
                        "7. Conduct and Professionalism\n" +
                                "•\tDelivery agents must behave professionally and respectfully toward all users and recipients.\n" +
                                "•\tAny complaints of misconduct, unprofessionalism, or illegal activities will lead to immediate suspension or termination of your account.\n\n" +
                        "8. Privacy\n" +
                                "•\tYou must not use or disclose any personal information of users or recipients obtained through the Swift App for purposes other than completing the delivery.\n" +
                                "•\tMisuse of user data will result in legal consequences and account termination.\n\n" +
                        "9. Compliance with Laws\n" +
                                "•\tYou must comply with all applicable laws and regulations regarding delivery services, transportation, and road safety.\n" +
                                "•\tAny violation of local, state, or national laws will result in the suspension or termination of your account.\n\n" +
                        "10. Delivery Timeliness\n" +
                                "•\tYou must make every effort to deliver packages within the specified time.\n" +
                                "•\tIn case of delays due to unforeseen circumstances (e.g., weather, traffic), you must inform the recipient about the estimated time of delivery.\n\n" +
                        "11. Prohibited Activities\n" +
                                "Delivery agents must not:\n" +
                                "•\tOpen or tamper with any packages in transit.\n" +
                                "•\tUse the app to engage in fraudulent activities or scams.\n" +
                                "•\tSubcontract deliveries to unauthorized individuals.\n\n" +
                        "12. Account Suspension or Termination\n" +
                                "Swift reserves the right to suspend or terminate your account without prior notice if:\n" +
                                "•\tYou violate these Terms and Conditions.\n" +
                                "•\tYou fail to maintain professionalism or adhere to Swift’s policies.\n" +
                                "•\tComplaints are received regarding package theft, damage, or unprofessional behavior.\n\n" +
                        "14. Modifications to Terms\n" +
                                "Swift reserves the right to update these Terms and Conditions at any time. Continued use of the app constitutes acceptance of any updated terms.\n\n" +
                        "15. Contact Us\n" +
                                "If you have any questions or concerns about these Terms and Conditions, please contact us at support_swiftapp@gmail.com.\n\n" +
                        "Please read carefully before proceeding.\n")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static class Agent
    {
        public String name;
        public String email;
        public String phone;
        public String password;
        public String resident;
        public String vehicle;
        public String registration;
        public String role;

        public Agent() {}

        public Agent(String name, String email, String phone, String password, String resident, String vehicle, String registration, String role) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.resident = resident;
            this.vehicle = vehicle;
            this.registration = registration;
            this.role = role;
        }
    }
}