package com.rmit.android_tiramisu_vacation_rental.model_Nghi;

public class Coupon {
    private String title;
    private String description;
    private String type;
    private boolean claimed;

    public Coupon(String title, String description, String type, boolean claimed) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.claimed = claimed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
