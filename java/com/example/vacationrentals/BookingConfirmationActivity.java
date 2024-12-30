package com.example.vacationrentals;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;



import androidx.appcompat.app.AppCompatActivity;


public class BookingConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        Button confirmButton = findViewById(R.id.confirm_booking_button);

        confirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        });
    }
}