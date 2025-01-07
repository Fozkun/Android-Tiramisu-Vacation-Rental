package com.rmit.android_tiramisu_vacation_rental.helpers;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class NotificationOptions {
    public String title;
    public String content;
    public int iconId;
    public int priorityId;
}

public class NotificationHelper {
    public static void sendPushNotification(Context context, NotificationOptions notificationOptions) {
    }
}
