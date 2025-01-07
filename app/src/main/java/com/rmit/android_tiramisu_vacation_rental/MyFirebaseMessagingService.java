package com.rmit.android_tiramisu_vacation_rental;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    public NotificationManager notificationManager;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        UserSession_Tri sessionTri = UserSession_Tri.getInstance();
        if(sessionTri != null){
            sendRegistrationToServer(token, sessionTri.getUserId());
        }
    }

    private void sendRegistrationToServer(String token, String userId) {
        DatabaseReference fcmTokensReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FCM_TOKENS);
        fcmTokensReference.child(userId).setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);

        builder.setContentTitle(message.getNotification().getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(100, builder.build());
    }
}
