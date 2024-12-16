package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends ArrayAdapter<Medication> {
    private Context context;
    private List<Medication> medications;

    public MedicationAdapter(Context context, List<Medication> medications) {
        super(context, 0, medications);
        this.context = context;
        this.medications = medications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use a ViewHolder for performance optimization
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate the layout for each list item
            convertView = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);

            // Initialize the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.medicationName = convertView.findViewById(R.id.medication_name);
            viewHolder.medicationQuantity = convertView.findViewById(R.id.medication_quantity);
            viewHolder.medicationTime = convertView.findViewById(R.id.medication_time);
            viewHolder.btnEdit = convertView.findViewById(R.id.btn_edit);
            viewHolder.btnDelete = convertView.findViewById(R.id.btn_delete);

            // Tag the ViewHolder for later reuse
            convertView.setTag(viewHolder);
        } else {
            // Reuse the ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current medication
        Medication medication = medications.get(position);

        // Set the data in the views
        viewHolder.medicationName.setText(medication.getName());
        viewHolder.medicationQuantity.setText(String.valueOf(medication.getQuantity()));
        viewHolder.medicationTime.setText(medication.getTime());

        // Set up the Edit icon click listener
        viewHolder.btnEdit.setOnClickListener(v -> {
            // Handle Edit action
            showEditDialog(medication);
        });

        // Set up the Delete icon click listener
        viewHolder.btnDelete.setOnClickListener(v -> {
            // Handle Delete action
            deleteMedication(position, medication);
        });

        return convertView;
    }

    // Method to show an Edit dialog
    private void showEditDialog(Medication medication) {
        // Create a dialog to edit medication details
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_medicine, null);

        EditText medicationNameInput = dialogView.findViewById(R.id.et_medicine_name);
        EditText medicationQuantityInput = dialogView.findViewById(R.id.et_medicine_quantity);
        EditText medicationTimeInput = dialogView.findViewById(R.id.et_medicine_time);

        // Set existing values in the input fields
        medicationNameInput.setText(medication.getName());
        medicationQuantityInput.setText(String.valueOf(medication.getQuantity()));
        medicationTimeInput.setText(medication.getTime());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog1, which) -> {
                    // Get updated data and update the medication
                    String updatedName = medicationNameInput.getText().toString();
                    int updatedQuantity = Integer.parseInt(medicationQuantityInput.getText().toString());
                    String updatedTime = medicationTimeInput.getText().toString();

                    medication.setName(updatedName);
                    medication.setQuantity(updatedQuantity);
                    medication.setTime(updatedTime);

                    // Update the list and Firebase
                    updateMedicationInDatabase(medication);
                    notifyDataSetChanged(); // Notify the adapter that data has changed
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    // Method to update medication in Firebase
    private void updateMedicationInDatabase(Medication medication) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("medications");
        dbRef.child(medication.getName()).setValue(medication) // Assuming the medication has an "id" property
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated
                    Toast.makeText(context, "Medication updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to update medication", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to delete medication from the list and Firebase
    private void deleteMedication(int position, Medication medication) {
        // Remove from the list
        medications.remove(position);
        notifyDataSetChanged();

        // Also remove from Firebase
        deleteMedicationFromDatabase(medication);
    }

    // Method to remove medication from Firebase
    private void deleteMedicationFromDatabase(Medication medication) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("medications");
        dbRef.child(medication.getName()).removeValue() // Assuming the medication has an "id" property
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted
                    Toast.makeText(context, "Medication deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to delete medication", Toast.LENGTH_SHORT).show();
                });
    }

    // ViewHolder pattern for better performance
    static class ViewHolder {
        TextView medicationQuantity;
        TextView medicationName;
        TextView medicationTime;
        ImageView btnEdit;
        ImageView btnDelete;
    }
}




