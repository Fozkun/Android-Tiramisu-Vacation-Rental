package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notification extends AppCompatActivity {
    private LinearLayout notificationList;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        notificationList = findViewById(R.id.notificationList);
        mDatabase = FirebaseDatabase.getInstance();

        // Get notifications from Firebase Realtime Database (replace with your logic)
        fetchNotifications();
    }

    private void fetchNotifications() {
        // Assuming you have a "notifications" node in your database
        DatabaseReference notificationRef = mDatabase.getReference("notifications");
        NotificationOb_Hoa notificationOb_hoa = new NotificationOb_Hoa();
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear existing notification cards
                    notificationList.removeAllViews();

                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        // Get notification data
                        Notification notification = notificationSnapshot.getValue(Notification.class);

                        // Inflate notification card layout
                        LayoutInflater inflater = LayoutInflater.from(Notification.this);
                        View notificationCard = inflater.inflate(R.layout.activity_notification, notificationList, false);

                        // Populate notification card views with data
                        TextView productLabel = notificationCard.findViewById(R.id.productLabel);
                        TextView notificationTitle = notificationCard.findViewById(R.id.notificationTitle);
                        TextView notificationDescription = notificationCard.findViewById(R.id.notificationDescription);
                        TextView notificationTimestamp = notificationCard.findViewById(R.id.notificationTimestamp);

                        productLabel.setText(notificationOb_hoa.getProductType()); // Assuming a "productType" field
                        notificationTitle.setText(notificationOb_hoa.getTitle());
                        notificationDescription.setText(notificationOb_hoa.getDescription());
                        notificationTimestamp.setText(notificationOb_hoa.getTimestamp()); // Assuming a "timestamp" field

                        // Add notification card to the list
                        notificationList.addView(notificationCard);
                    }
                } else {
                    // Handle no notifications found
                    Toast.makeText(Notification.this, "No notifications found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.w("NotificationActivity", "Error fetching notifications:", databaseError.toException());
            }
        });
    }
}