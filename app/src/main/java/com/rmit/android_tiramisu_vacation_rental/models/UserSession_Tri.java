package com.rmit.android_tiramisu_vacation_rental.models;

import android.util.Log;

import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;

public class UserSession_Tri {
    private static UserSession_Tri instance;

    private String userId;
    private UserRole userRole;

    private UserSession_Tri() {
        userId = null;
        userRole = null;
    }

    public static synchronized UserSession_Tri getInstance() {
        if (instance == null) {
            instance = new UserSession_Tri();
        }

        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public UserRole getUserRole() {
        if (userRole == null) {
            Log.d("UserSession", "User role is null");
        }

        return userRole;
    }

    public void setSession(String userId, UserRole userRole){
        this.userId = userId;
        this.userRole = userRole;
    }
    public boolean hasSession() {
        return this.userRole != null && this.userId != null;
    }

    public void clearSession() {
        this.userRole = null;
        this.userId = null;
    }
}