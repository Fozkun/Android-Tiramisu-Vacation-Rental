package com.rmit.android_tiramisu_vacation_rental.firebaseHelpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.SigninActivity;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        UserSession_Tri sessionTri = UserSession_Tri.getInstance();
        if (sessionTri != null) {
            sendRegistrationToServer(token, sessionTri.getUserId());
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, Objects.requireNonNull(message.getFrom()));

        Log.d(TAG, Objects.requireNonNull(message.getNotification()).toString());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Channel_Id");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);

        Intent resultIntent = new Intent(this, SigninActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(message.getNotification().getTitle());
        builder.setContentText(message.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "Channel_Id";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel name", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        Log.d(TAG,"Trying to notify");

        mNotificationManager.notify(100,builder.build());
    }

    private void sendRegistrationToServer(String token, String userId) {
        DatabaseReference fcmTokensReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FM_TOKENS);
        fcmTokensReference.child(userId).setValue(token);
    }
}
