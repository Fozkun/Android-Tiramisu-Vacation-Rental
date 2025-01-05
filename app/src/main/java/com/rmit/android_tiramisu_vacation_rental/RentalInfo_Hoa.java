package com.rmit.android_tiramisu_vacation_rental;

import java.util.ArrayList;
import java.util.List;

public class RentalInfo_Hoa {
    private String hotelName;
    private float hotelRating;
    private String hotelLocation;
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

    public RentalInfo_Hoa(String hotelName, float hotelRating, String hotelLocation, List<Room> rooms, Provider provider) {
        this.hotelName = hotelName;
        this.hotelRating = hotelRating;
        this.hotelLocation = hotelLocation;
        this.rooms = rooms;
        this.provider = provider;
    }

    public RentalInfo_Hoa() {
        hotelName = "";
        hotelRating = 0.0f;
        hotelLocation = "";
        rooms = new ArrayList<>();
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
}
