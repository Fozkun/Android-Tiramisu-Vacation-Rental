package com.rmit.android_tiramisu_vacation_rental;

public class Provider {
    private String uid; // Assuming you're using Firebase Authentication

    public Provider() {
        // Default constructor (optional)
    }

    public Provider(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}