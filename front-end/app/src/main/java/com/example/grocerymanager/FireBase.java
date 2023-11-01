package com.example.grocerymanager;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FireBase extends FirebaseMessagingService {
    @SuppressLint("MissingPermission")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Display chat implementation


        // Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.google.android.gms.auth.api.R.drawable.notification_oversize_large_icon_bg)
                .setContentTitle("New Chat Message")
                .setContentText("You have a new chat message!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }


}
