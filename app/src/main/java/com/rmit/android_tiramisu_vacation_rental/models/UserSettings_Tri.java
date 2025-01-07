package com.rmit.android_tiramisu_vacation_rental.models;

import java.io.Serializable;

public class UserSettings_Tri implements Serializable {
    public String userId;
    public boolean pushNotificationEnabled = false;

    public UserSettings_Tri() {
    }

    public UserSettings_Tri(String userId) {
        this.userId = userId;
    }
}
