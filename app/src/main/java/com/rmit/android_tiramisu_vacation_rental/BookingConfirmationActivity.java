package com.rmit.android_tiramisu_vacation_rental;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;



public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView hotelNameTextView;
    private TextView hotelAddressTextView;
    private Button continueButton;
    private String userId, bookingId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        hotelNameTextView = findViewById(R.id.hotelNameTextView);
        hotelNameTextView = findViewById(R.id.hotelAddressTextView);
        continueButton = findViewById(R.id.continueButton);

        loadHotelData();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingConfirmationActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadHotelData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bookings").child(userId).child(bookingId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String hotelName = dataSnapshot.child("hotelName").getValue(String.class);
                    String hotelAddress = dataSnapshot.child("address").getValue(String.class);
                    hotelNameTextView.setText(hotelName);
                    hotelAddressTextView.setText(hotelAddress);
                } else {
                    showError("Booking not found. Please check your booking ID.");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Failed to load hotel data: " + databaseError.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}