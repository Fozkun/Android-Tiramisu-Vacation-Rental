package com.rmit.android_tiramisu_vacation_rental.model_Nghi;

public class NotificationRequest {
    private String userId;
    private String message;

    public NotificationRequest(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
