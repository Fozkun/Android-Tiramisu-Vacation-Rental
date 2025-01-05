package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.model_Nghi.NotificationRequest;
import com.rmit.android_tiramisu_vacation_rental.model_Nghi.NotificationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {
    private TextView hotelNameTextView;
    private EditText cardNumberEditText, expiryDateEditText, cvvEditText;
    private Button purchaseButton;
    private String userId, bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        hotelNameTextView = findViewById(R.id.hotelNameTextView);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        purchaseButton = findViewById(R.id.purchaseButton);

        loadHotelData();

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCard()) {
                    sendPushNotification(userId, "Payment successful!");
                }
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
                    hotelNameTextView.setText(hotelName);
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

    private boolean validateCard() {
        String cardNumber= cardNumberEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();
        if (cardNumber.isEmpty() || !isValidCardNumber(cardNumber)) {
            showError("Invalid card number.");
            return false;
        }
        if (expiryDate.isEmpty() || !isValidExpiryDate(expiryDate)) {
            showError("Invalid expiry date.");
            return false;
        }
        if (cvv.isEmpty() || !isValidCVV(cvv)) {
            showError("Invalid CVV.");
            return false;
        }
        return true;
    }
    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}");
    }

    private boolean isValidExpiryDate(String expiryDate) {
        return expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    private boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3}");
    }

    private void sendPushNotification(String userId, String message) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-server-url/") // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NotificationService notificationService = retrofit.create(NotificationService.class);
        NotificationRequest notificationRequest = new NotificationRequest(userId, message);

        Call<Void> call = notificationService.sendNotification(notificationRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to send notification: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}