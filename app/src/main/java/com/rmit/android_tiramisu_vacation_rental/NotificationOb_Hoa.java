package com.rmit.android_tiramisu_vacation_rental;

public class NotificationOb_Hoa {
    private String productType;
    private String title;
    private String description;
    private String timestamp;

    public NotificationOb_Hoa(String productType, String title, String description, String timestamp) {
        this.productType = productType;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public NotificationOb_Hoa (){
        productType="";
        title="";
        description="";
        timestamp="";
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
