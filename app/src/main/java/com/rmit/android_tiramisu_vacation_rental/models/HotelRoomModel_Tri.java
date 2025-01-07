package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;

import java.io.Serializable;
import java.util.Date;

public class HotelRoomModel_Tri implements Serializable {
    private String id;
    private String name;
    private Date startDate;
    private Date endDate;
    private HotelRoomStatus status;

    private int people;
    private String description;

    public HotelRoomModel_Tri() {
    }
}
