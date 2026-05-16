package com.example.swiftdelivery.admin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swiftdelivery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminReportsFragment extends Fragment {

    EditText etMonthYear;
    Button btnGenerateReport;
    private DatabaseReference usersReference, agentsReference, deliveriesReference;
    private int userCount, agentCount, deliveriesForMonth;
    private String monthYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_reports, container, false);

        etMonthYear = view.findViewById(R.id.etMonthYear);
        btnGenerateReport = view.findViewById(R.id.btnGenerateReport);

        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        agentsReference = FirebaseDatabase.getInstance().getReference("Delivery Agents");
        deliveriesReference = FirebaseDatabase.getInstance().getReference("Deliveries");

        btnGenerateReport.setOnClickListener(v -> {
            monthYear = etMonthYear.getText().toString().trim();
            if (monthYear.isEmpty()) {
                Toast.makeText(getContext(), "Enter Month and Year", Toast.LENGTH_SHORT).show();
            } else {
                generateReport(monthYear);
            }
        });

        return view;
    }
    private void generateReport(String monthYear)
    {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userCount = (int) snapshot.getChildrenCount();

                agentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        agentCount = (int) snapshot.getChildrenCount();

                        deliveriesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                deliveriesForMonth = 0;
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                for (DataSnapshot deliverySnapshot : snapshot.getChildren()) {
                                    String dateTime = deliverySnapshot.child("DeliveryRequestDate").getValue(String.class);
                                    Log.d("DeliveryDate", "Retrieved DeliveryRequestDate: " + dateTime);
                                    if (dateTime != null) {
                                        try {
                                            Date deliveryDate = dateFormat.parse(dateTime);
                                            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                                            String formattedMonthYear = monthYearFormat.format(deliveryDate);
                                            if (formattedMonthYear.equals(monthYear)) {
                                                deliveriesForMonth++;
                                            }
                                        } catch (Exception e) {
                                            Log.e("DateParsingError", "Error parsing date: " + dateTime, e);
                                        }
                                    }
                                }
                                launchStoragePicker();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error fetching deliveries.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching delivery agents.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchStoragePicker()
    {
        Intent int_storage = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        int_storage.setType("application/pdf");
        int_storage.putExtra(Intent.EXTRA_TITLE, "SwiftReport_" + monthYear + ".pdf");
        int_storage.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(int_storage, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                generatePdfReport(fileUri);
            }
        }
    }

    private void generatePdfReport(Uri fileUri)
    {
        try {
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(fileUri);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            document.add(new Paragraph("Swift Delivery - Monthly Report"));
            document.add(new Paragraph("Month: " + monthYear));
            document.add(new Paragraph("Number of Current Users: " + userCount));
            document.add(new Paragraph("Number of Registered Agents: " + agentCount));
            document.add(new Paragraph("Number of Deliveries: " + deliveriesForMonth));
            document.close();
            Toast.makeText(getContext(), "Report saved Successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating report.", Toast.LENGTH_SHORT).show();
        }
    }
}