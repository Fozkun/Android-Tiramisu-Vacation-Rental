package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.CouponType;

import java.util.ArrayList;
import java.util.Date;

public class CouponModel_Tri {
    private String id;
    private String title;
    private String description;
    private CouponType type;
    private String value;
    private ArrayList<String> claimedUserIds;
    private Date createdDate;

    public CouponModel_Tri() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getClaimedUserIds() {
        return claimedUserIds;
    }

    public void setClaimedUserIds(ArrayList<String> claimedUserIds) {
        this.claimedUserIds = claimedUserIds;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public  boolean isClaim(String userId) {
        return this.claimedUserIds != null && this.claimedUserIds.contains(userId);
    }

    public void setClaimed(String userId) {
        ArrayList <String> clamId = new ArrayList<>();
        if(this.claimedUserIds != null) {
            clamId = this. claimedUserIds;
        }
        if (clamId.contains(userId)) {
            return;
        }
        clamId.add(userId);
        this.claimedUserIds = clamId;
    }
}
