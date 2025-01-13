package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;

import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.android_tiramisu_vacation_rental.model_Nghi.Coupon;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;


public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView tvHotelName, tvRoomDetails, tvRoomPrice, tvTotalPrice, tvFinalPrice;
    private RecyclerView rvCoupons;
    private int totalPrice;
    private double finalPrice = totalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);

        // Find views
        tvHotelName = findViewById(R.id.tvHotelName);
        tvRoomDetails = findViewById(R.id.tvRoomDetails);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        rvCoupons = findViewById(R.id.rvCoupons);

        tvHotelName.setText("Hotel Name: ");
        tvRoomDetails.setText("Room Details: ");
        tvTotalPrice.setText("Total Price: " + totalPrice + "đ");
        tvFinalPrice.setText("Final Price: " + finalPrice + "đ");


        List<Coupon> coupons = loadCoupons();

        CouponsAdapter adapter = new CouponsAdapter(coupons, coupon -> {
            finalPrice = totalPrice - coupon.getDiscountAmount();
            tvFinalPrice.setText("Final Price: " + finalPrice + "đ");
        });

        rvCoupons.setLayoutManager(new LinearLayoutManager(this));
        rvCoupons.setAdapter(adapter);
    }

    private List<Coupon> loadCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        return coupons;
    }
}