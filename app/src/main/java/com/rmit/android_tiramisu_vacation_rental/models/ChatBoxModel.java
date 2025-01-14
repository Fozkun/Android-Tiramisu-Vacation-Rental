package com.rmit.android_tiramisu_vacation_rental.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ChatBoxModel {
    private String id;
    private String userId;
    private ArrayList<Message> messages;

    public ChatBoxModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: " + this.id + " UserID: " + this.userId;
    }
}
