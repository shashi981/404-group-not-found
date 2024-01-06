package com.example.grocerymanager.firebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.grocerymanager.R;
import com.example.grocerymanager.TokenManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    final static String TAG = "FirebaseMessagingService"; //identify where log is coming from


    //    ChatGPT Usage: No. Help from https://www.youtube.com/watch?v=aQ-v4gw9AnY&list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&index=2
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Token: " + token);
        TokenManager tokenManager = new TokenManager(this);
        tokenManager.saveToken(token);
    }

    //    ChatGPT Usage: No. Help from https://www.youtube.com/watch?v=aQ-v4gw9AnY&list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&index=2
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "Message: " + message.getNotification().getBody());
        super.onMessageReceived(message);

        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(0, builder.build());
        }
    }
}
