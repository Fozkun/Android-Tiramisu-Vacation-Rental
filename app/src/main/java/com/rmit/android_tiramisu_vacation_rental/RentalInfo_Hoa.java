package com.rmit.android_tiramisu_vacation_rental;

import java.util.ArrayList;
import java.util.List;

public class RentalInfo_Hoa {
    private String id;
    private String hotelName;
    private float hotelRating;
    private String hotelLocation;
    private int maxOccupancy;
    private List<Room> rooms;
    private Provider provider;


    public static class Room {
        private String roomName;
        private String roomDetails;
        private double roomPrice;
        private String imageUrl;

        public Room() {
            roomName = "";
            roomDetails = "";
            roomPrice = 0.0;
            imageUrl = "";
        }

        public Room(String roomName, String roomDetails, double roomPrice, String imageUrl) {
            this.roomName = roomName;
            this.roomDetails = roomDetails;
            this.roomPrice = roomPrice;
            this.imageUrl = imageUrl;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomDetails() {
            return roomDetails;
        }

        public void setRoomDetails(String roomDetails) {
            this.roomDetails = roomDetails;
        }

        public double getRoomPrice() {
            return roomPrice;
        }

        public void setRoomPrice(double roomPrice) {
            this.roomPrice = roomPrice;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public RentalInfo_Hoa() {
    }

    public RentalInfo_Hoa(String id, String hotelName, float hotelRating, String hotelLocation, int maxOccupancy, List<Room> rooms, Provider provider) {
        this.id = id;
        this.hotelName = hotelName;
        this.hotelRating = hotelRating;
        this.hotelLocation = hotelLocation;
        this.maxOccupancy = maxOccupancy;
        this.rooms = rooms;
        this.provider = provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public float getHotelRating() {
        return hotelRating;
    }

    public void setHotelRating(float hotelRating) {
        this.hotelRating = hotelRating;
    }

    public String getHotelLocation() {
        return hotelLocation;
    }

    public void setHotelLocation(String hotelLocation) {
        this.hotelLocation = hotelLocation;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "RentalInfo_Hoa{" +
                "id='" + id + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", hotelRating=" + hotelRating +
                ", hotelLocation='" + hotelLocation + '\'' +
                ", maxOccupancy=" + maxOccupancy +
                ", rooms=" + rooms +
                ", provider=" + provider +
                '}';
    }
}
