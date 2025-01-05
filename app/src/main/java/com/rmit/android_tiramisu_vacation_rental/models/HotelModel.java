package com.rmit.android_tiramisu_vacation_rental.models;

import java.io.Serializable;

public class HotelModel implements Serializable {
    private String id;
    private String name;
    private String address;
    private Location location;
    private int maxOccupancy;
    private float rating;
    private String imageUrl;

    public HotelModel() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
                ", location=" + location +
                ", maxOccupancy=" + maxOccupancy +
                ", rating=" + rating +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
