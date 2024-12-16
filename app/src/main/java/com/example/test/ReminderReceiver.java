package com.example.test;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the medicine name from the intent
        String medicineName = intent.getStringExtra("medicineName");

        // Check if the medicineName is null or empty
        if (medicineName == null || medicineName.isEmpty()) {
            // If the name is not valid, don't show a notification
            return;
        }

        // Create the notification if medicineName is valid
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "medication_channel";

        // Create a notification channel for devices running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medication Reminder";
            String description = "Channel for medication reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.instagram_icon) // Replace with your icon
                .setContentTitle("Medication Reminder")
                .setContentText("Take " + medicineName + " now")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}


