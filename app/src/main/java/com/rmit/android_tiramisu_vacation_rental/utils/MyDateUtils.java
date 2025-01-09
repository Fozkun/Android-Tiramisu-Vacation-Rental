package com.rmit.android_tiramisu_vacation_rental.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MyDateUtils {
    private static  final String TAG = "MyDateUtils";

    private static final String DATE_FORMAT = "HH:mm dd-MM-yyyy";
    public static String formatDate(Date date) {
        if (date == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return formatter.format(date);
    }

    public static Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }
}