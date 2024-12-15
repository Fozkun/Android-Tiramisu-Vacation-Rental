package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RentalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rental_info);
        TextView roomNameTextView = findViewById(R.id.roomName);
        TextView roomDetailsTextView = findViewById(R.id.roomDetails);
        TextView roomPriceTextView = findViewById(R.id.roomPrice);
        Button bookNowButton = findViewById(R.id.bookNowButton);

// Set the text for the TextViews and Button
        roomNameTextView.setText("Room Name");
        roomDetailsTextView.setText("Room Details (e.g., bed type, occupancy)");
        roomPriceTextView.setText("Price per night");
        bookNowButton.setText("Book Now");
    }
}