package com.rmit.android_tiramisu_vacation_rental.models;

import android.util.Log;

public class Message {
    private String userId;
    private String content;
    private long timestamp;


    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
        Log.d("Message", "Default constructor called by Firebase");
    }
    public Message(String userId, String content, long timestamp) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
