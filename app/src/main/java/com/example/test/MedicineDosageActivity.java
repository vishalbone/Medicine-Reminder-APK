package com.example.test;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.test.Medication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MedicineDosageActivity extends AppCompatActivity {
    private ListView medicationListView;
    private FloatingActionButton addMedicationButton;
    private ArrayList<Medication> medicationList;
    private MedicationAdapter medicationAdapter;
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dosage);

        // Initialize the list and adapter
        medicationList = new ArrayList<>();
        setupMedicationList();  // Set up the ListView with the adapter

        // Fetch the medication data from Firebase
        fetchMedications();
        
        addButton = findViewById(R.id.add_medication_button);
        addButton.setOnClickListener(View -> showAddMedicineDialog());
    }

    private void setupMedicationList() {
        medicationListView = findViewById(R.id.medication_list_view);

        // Create an adapter for your medication list
        medicationAdapter = new MedicationAdapter(this, medicationList);
        medicationListView.setAdapter(medicationAdapter);
    }


    private void showAddMedicineDialog() {
        // Inflate custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medicine, null);

        EditText medicineNameInput = dialogView.findViewById(R.id.et_medicine_name);
        Spinner medicineTypeSpinner = dialogView.findViewById(R.id.spinner_medicine_type);
        EditText medicineTimeInput = dialogView.findViewById(R.id.et_medicine_time);
        EditText medicineQuantityInput = dialogView.findViewById(R.id.et_medicine_quantity);  // Quantity input
        Button saveButton = dialogView.findViewById(R.id.btn_save_medicine);
        Spinner frequencySpinner = dialogView.findViewById(R.id.spinner_frequency); // Frequency spinner

        // Set up Spinner for medicine types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.medicine_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineTypeSpinner.setAdapter(adapter);

        // Set up Spinner for frequency (How many times per week)
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);

        // Show dialog
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        dialog.show();

        // Set time picker for medicine time
        medicineTimeInput.setFocusable(false); // Disable typing to show TimePicker
        medicineTimeInput.setOnClickListener(v -> showTimePicker(medicineTimeInput));

        saveButton.setOnClickListener(v -> {
            String medicineName = medicineNameInput.getText().toString();
            String medicineType = medicineTypeSpinner.getSelectedItem().toString();
            String medicineTime = medicineTimeInput.getText().toString();
            String frequency = frequencySpinner.getSelectedItem().toString();
            String quantityString = medicineQuantityInput.getText().toString();  // Get quantity input

            if (medicineName.isEmpty() || medicineTime.isEmpty() || quantityString.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extract hour and minute
            String[] timeParts = medicineTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Convert quantity to integer
            int quantity = Integer.parseInt(quantityString);

            // Add medicine to database
            addMedicineToDatabase(medicineName, medicineType, medicineTime, frequencySpinner.getSelectedItem().toString(), quantity);
            setReminder(medicineName, hour, minute);
            dialog.dismiss();
        });
    }


    private void showTimePicker(final EditText medicineTimeInput) {
        // Use a TimePickerDialog to choose time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            // Format the time and display it in the EditText
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            medicineTimeInput.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }


    private void addMedicineToDatabase(String name, String type, String time, String frequency, int quantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Create a new Medication object with all the parameters
        Medication medication = new Medication(frequency,name, quantity,time,type);

        // Save to Firebase Realtime Database under "users/userId/medications"
        database.child("users").child(userId).child("medications").push().setValue(medication)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicine added successfully", Toast.LENGTH_SHORT).show();
                    fetchMedications();  // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add medicine", Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchMedications() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Fetch medications for the current user
        database.child("users").child(userId).child("medications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicationList.clear(); // Clear existing list before adding new data

                // Iterate through the medications retrieved
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);
                    if (medication != null) {
                        medicationList.add(medication);  // Add medication to the list
                    }
                }
                medicationAdapter.notifyDataSetChanged();  // Notify adapter that the data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch medications: " + databaseError.getMessage());
                Toast.makeText(MedicineDosageActivity.this, "Failed to fetch medications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder(String medicineName, int hour, int minute) {
        // Check if the medicineName is valid before proceeding
        if (medicineName == null || medicineName.isEmpty()) {
            Toast.makeText(this, "Please enter a valid medicine name", Toast.LENGTH_SHORT).show();
            return; // Prevent setting an alarm if the name is invalid
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicineName", medicineName);  // Only pass valid data

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Add FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(this, "Reminder set for " + medicineName, Toast.LENGTH_SHORT).show();
    }

}
