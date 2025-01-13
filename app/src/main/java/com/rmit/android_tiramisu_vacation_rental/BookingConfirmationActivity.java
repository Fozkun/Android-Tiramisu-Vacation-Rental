package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.model_Nghi.Coupon;

import java.util.ArrayList;
import java.util.List;


public class BookingConfirmationActivity extends AppCompatActivity {

        private TextView tvHotelName, tvRoomDetails, tvRoomPrice, tvFinalPrice;
        private RecyclerView rvCoupons;
        private Button btnConfirmBooking;
        private final double roomPrice = 5550000; // Example price
        private double finalPrice = roomPrice;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_booking_confirm);

            tvHotelName = findViewById(R.id.tvHotelName);
            tvRoomDetails = findViewById(R.id.tvRoomDetails);
            tvRoomPrice = findViewById(R.id.tvRoomPrice);
            tvFinalPrice = findViewById(R.id.tvFinalPrice);
            rvCoupons = findViewById(R.id.rvCoupons);
            btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

            loadCoupons();

            btnConfirmBooking.setOnClickListener(v -> {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("finalPrice", finalPrice);
                startActivity(intent);
            });
        }

        private void loadCoupons() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("coupons");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Coupon> coupons = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Coupon coupon = child.getValue(Coupon.class);
                        coupons.add(coupon);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookingConfirmationActivity.this, "Failed to load coupons", Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void applyCoupon(Coupon coupon) {
        finalPrice = roomPrice - (roomPrice * coupon.getDiscount() / 100);
        tvFinalPrice.setText(String.format("Final Price: %.2f VND", finalPrice));
    }
}