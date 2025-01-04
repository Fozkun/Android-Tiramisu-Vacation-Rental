package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentalInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    private DatabaseReference mDatabase;
    private RentalInfo_Hoa rentalInfo;
    private FirebaseAuth mAuth;
    private String userId;
    private Button editButton, deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rental_info);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        editButton = findViewById(R.id.btnEdit);
        deleteButton = findViewById(R.id.btnDelete);

        checkBookingStatus();
        rentalInfo = new RentalInfo_Hoa();
        populateRentalInfo();

        // Write rental info to the database
        checkOwnership();
        writeToDatabase();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Example: Hotel coordinates (replace with actual values)
        double latitude = 37.7749;
        double longitude = -122.4194;
        LatLng hotelLocation = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(hotelLocation).title("Hotel Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 15f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private void populateRentalInfo() {
        rentalInfo.setHotelName("Example Hotel");
        rentalInfo.setHotelRating(4.5f);
        rentalInfo.setHotelLocation("123 Main St, City, Country");

        List<RentalInfo_Hoa.Room> rooms = new ArrayList<>();

        RentalInfo_Hoa.Room room1 = new RentalInfo_Hoa.Room();
        room1.setRoomName("Deluxe Room");
        room1.setRoomDetails("King-size bed, balcony, city view");
        room1.setRoomPrice(150.00);
        // room1.setImageUrl("https://example.com/room1.jpg");


        rooms.add(room1);

        rentalInfo.setRooms(rooms);
    }

    private void writeToDatabase() {
        String rentalId = mDatabase.child("rentals").push().getKey();

        // Store ownerId (assuming you have the current user's ID)
        Provider currentProvider = new Provider(userId);

        rentalInfo.setProvider(currentProvider);

        // Write the entire RentalInfo object to the database
        mDatabase.child("rentals").child(rentalId).setValue(rentalInfo)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully
                    Log.d("TAG", "RentalInfo saved to database successfully.");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("TAG", "Error saving RentalInfo to database.", e);
                });

    }
        private void checkBookingStatus() {
        // where rentalId is the unique identifier of the rental

        DatabaseReference bookingRef = mDatabase.child("users").child(userId).child("bookings");
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(rentalInfo.getHotelName())) {
                    // User has already booked this rental
                    Button bookNowButton = findViewById(R.id.bookNowButton);
                    bookNowButton.setEnabled(false);
                    bookNowButton.setAlpha(0.5f); // Fade the button
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(RentalInfoActivity.this, "Error checking booking status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOwnership() {

        String rentalId = getIntent().getStringExtra("rentalId");

        DatabaseReference rentalRef = mDatabase.child("rentals").child(rentalId);
        rentalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Provider provider = dataSnapshot.child("provider").getValue(Provider.class);
                    if (provider != null && provider.getUid().equals(userId)) {
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    } else {
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(RentalInfoActivity.this, "Rental not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RentalInfoActivity.this, "Error checking ownership.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}