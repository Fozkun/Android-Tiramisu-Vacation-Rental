package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class HotelRoomModel_Tri implements Serializable {
    private String id;
    private String name;
    private String hotelId;
    private String description;
    private String imageUrl;
    private HotelRoomStatus status;
    private Date startDate; // Format HH:mm dd-MM-yyyy
    private Date endDate; // Format HH:mm dd-MM-yyyy
    private int people;
    private ArrayList<String> bookedUserIds;
    private Double price;

    public HotelRoomModel_Tri() {}

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

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HotelRoomStatus getStatus() {
        return status;
    }

    public void setStatus(HotelRoomStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public ArrayList<String> getBookedUserIds() {
        return bookedUserIds;
    }

    public void setBookedUserIds(ArrayList<String> bookedUserIds) {
        this.bookedUserIds = bookedUserIds;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "HotelRoomModel_Tri{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", people=" + people +
                ", bookedUserIds=" + bookedUserIds +
                ", price=" + price +
                '}';
    }
}
