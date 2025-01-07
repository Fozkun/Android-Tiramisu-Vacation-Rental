package com.rmit.android_tiramisu_vacation_rental.helpers;

import android.content.Context;
import android.content.Intent;

public class BottomNavigationHelper {
    public static void navigateTo(Context context, Class activity){
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
