package com.rmit.android_tiramisu_vacation_rental.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HotelModel_Tri implements Serializable {
    private String id;
    private String ownerId;
    private ArrayList<String> roomIds;
    private String name;
    private String address;
    private Location_Tri locationTri;
    private int maxOccupancy;
    private float rating;
    private String imageUrl;

    public HotelModel_Tri() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<String> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(ArrayList<String> roomIds) {
        this.roomIds = roomIds;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location_Tri getLocation() {
        return locationTri;
    }

    public void setLocation(Location_Tri locationTri) {
        this.locationTri = locationTri;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "HotelModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location=" + locationTri +
                ", maxOccupancy=" + maxOccupancy +
                ", rating=" + rating +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
