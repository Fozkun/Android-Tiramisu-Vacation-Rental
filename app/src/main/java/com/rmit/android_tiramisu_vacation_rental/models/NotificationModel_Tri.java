package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.NotificationTag;

import java.util.Date;

public class NotificationModel_Tri {
    private String id;
    private NotificationTag tag;
    private String title;
    private String description;
    private Date creationDate;

    public NotificationModel_Tri() {
    }

    public NotificationModel_Tri(String id, NotificationTag tag, String title, String description, Date creationDate) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotificationTag getTag() {
        return tag;
    }

    public void setTag(NotificationTag tag) {
        this.tag = tag;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
