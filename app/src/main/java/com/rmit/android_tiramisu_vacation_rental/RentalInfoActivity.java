package com.rmit.android_tiramisu_vacation_rental;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView roomTypesRecyclerView;
    private RoomTypesAdapter roomTypesAdapter;
    private List<String> roomTypesList = new ArrayList<>();
    private String selectedRoomType;
    private TextView hotelNameTextView;
    private RatingBar hotelRatingRatingBar;
    private TextView hotelLocationTextView;
    private TextView maxOccupancyTextView;
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
        Button btnCreateRoom = findViewById(R.id.btnCreateRoom);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        checkOwnership();
        checkBookingStatus();
        rentalInfo = new RentalInfo_Hoa();
        populateRentalInfo();
        writeToDatabase();
        // Set up RecyclerView
        roomTypesRecyclerView = findViewById(R.id.roomTypesRecyclerView);
        roomTypesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomTypesAdapter = new RoomTypesAdapter(new ArrayList<>()); // Initialize with an empty list
        roomTypesRecyclerView.setAdapter(roomTypesAdapter);

        // Get rental ID from intent
        String rentalId = getIntent().getStringExtra("rentalId");

        // Fetch rental information from Firebase
        fetchRentalInfo(rentalId);

        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoom();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editHotelInfo();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHotel();
            }
        });
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
        rentalInfo.setMaxOccupancy(40);

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

    private void createNewRoom() {
        // Get references to EditText fields
        EditText roomNameEditText = findViewById(R.id.InputRoomName);
        EditText roomDetailsEditText = findViewById(R.id.InputRoomDetails);
        EditText roomPriceEditText = findViewById(R.id.InputRoomPrice);
        ImageView roomImage = findViewById(R.id.roomImage);

        // Get room data from EditText fields
        String roomName = roomNameEditText.getText().toString().trim();
        String roomDetails = roomDetailsEditText.getText().toString().trim();
        String roomPriceStr = roomPriceEditText.getText().toString().trim();
        String roomImageStr = roomImage.getTag().toString();

        if (roomName.isEmpty() || roomDetails.isEmpty() || roomPriceStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double roomPrice = Double.parseDouble(roomPriceStr);

            // Create a new Room object
            RentalInfo_Hoa.Room newRoom = new RentalInfo_Hoa.Room(roomName, roomDetails, roomPrice, roomImageStr);

            // Add the new room to the existing list of rooms
            rentalInfo.getRooms().add(newRoom);

            // Update the hotel information in the database
            updateHotelInfoInDatabase();

            // Clear input fields
            roomNameEditText.setText("");
            roomDetailsEditText.setText("");
            roomPriceEditText.setText("");


        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHotelInfoInDatabase() {
        String rentalId = getIntent().getStringExtra("rentalId"); // Get the rental ID

        DatabaseReference rentalRef = mDatabase.child("rentals").child(rentalId);
        rentalRef.setValue(rentalInfo)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Toast.makeText(this, "Room added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Error adding room.", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Error adding room to database.", e);
                });
    }

    private void editHotelInfo() {
        // Get references to TextViews where hotel info is displayed
        TextView hotelNameTextView = findViewById(R.id.hotelName);
        RatingBar hotelRatingTextView = findViewById(R.id.hotelRating);
        TextView hotelLocationTextView = findViewById(R.id.hotelLocation);
        TextView maxOccupancyTextView = findViewById(R.id.textViewOccupancy);

        // Get updated values from TextViews
        String updatedHotelName = hotelNameTextView.getText().toString();
        float updatedHotelRating = Float.parseFloat(hotelRatingTextView.toString());
        String updatedHotelLocation = hotelLocationTextView.getText().toString();
        int updatedMaxOccupancy = Integer.parseInt(maxOccupancyTextView.getText().toString());

        // Update the rentalInfo object with the edited values
        rentalInfo.setHotelName(updatedHotelName);
        rentalInfo.setHotelRating(updatedHotelRating);
        rentalInfo.setHotelLocation(updatedHotelLocation);
        rentalInfo.setMaxOccupancy(updatedMaxOccupancy);

        // Update the hotel information in the database
        updateHotelInfoInDatabase();
    }

    private void deleteHotel() {
        String rentalId = getIntent().getStringExtra("rentalId"); // Get the rental ID

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this hotel?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the hotel from the database
                    mDatabase.child("rentals").child(rentalId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Deletion successful
                                Toast.makeText(this, "Hotel deleted successfully.", Toast.LENGTH_SHORT).show();
                                finish(); // Finish the activity after deletion
                            })
                            .addOnFailureListener(e -> {
                                // Handle deletion errors
                                Toast.makeText(this, "Error deleting hotel.", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Error deleting hotel from database.", e);
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkOwnership() {

        String rentalId = getIntent().getStringExtra("rentalId"); // Get rentalId from intent

        DatabaseReference rentalRef = mDatabase.child("rentals").child(rentalId);
        rentalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ownerId = dataSnapshot.child("ownerId").getValue(String.class);
                    if (ownerId.equals(userId)) {
                        // Current user is the owner
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    } else {
                        // Current user is not the owner
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    }
                } else {
                    // Rental not found
                    Toast.makeText(RentalInfoActivity.this, "Rental not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(RentalInfoActivity.this, "Error checking ownership.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RoomTypesAdapter extends RecyclerView.Adapter<RoomTypesAdapter.ViewHolder> {

        private List<String> roomTypes;

        public RoomTypesAdapter(List<String> roomTypes) {
            this.roomTypes = roomTypes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_type_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String roomType = roomTypes.get(position);
            holder.roomTypeNameTextView.setText(roomType);

            holder.itemView.setOnClickListener(v -> {
                selectedRoomType = roomType;

                // Update UI to display rooms of the selected type
                updateRoomList();
            });
        }

        @Override
        public int getItemCount() {
            return roomTypes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView roomTypeNameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                roomTypeNameTextView = itemView.findViewById(R.id.roomName);
            }
        }
    }

    private void updateRoomList() {
        // Filter rooms based on the selected room type
        List<RentalInfo_Hoa.Room> selectedRoomList = new ArrayList<>();
        for (RentalInfo_Hoa.Room room : rentalInfo.getRooms()) {
            if (room.getRoomName().equals(selectedRoomType)) {
                selectedRoomList.add(room);
            }
        }

        // Update the UI to display the selected rooms
        // (e.g., populate a new RecyclerView with the selectedRoomList)

        // Example using a simple TextView (replace with your desired UI)
        TextView roomDetailsTextView = findViewById(R.id.roomDetails); // Assuming you have this TextView in your layout
        StringBuilder roomDetailsStringBuilder = new StringBuilder();
        for (RentalInfo_Hoa.Room room : selectedRoomList) {
            roomDetailsStringBuilder.append("Room Name: ").append(room.getRoomName()).append("\n");
            roomDetailsStringBuilder.append("Details: ").append(room.getRoomDetails()).append("\n");
            roomDetailsStringBuilder.append("Price: ").append(room.getRoomPrice()).append("\n\n");
        }
        roomDetailsTextView.setText(roomDetailsStringBuilder.toString());
    }

    private void getRoomTypesFromRentalInfo() {
        roomTypesList.clear(); // Clear any existing data
        for (RentalInfo_Hoa.Room room : rentalInfo.getRooms()) {
            if (!roomTypesList.contains(room.getRoomName())) {
                roomTypesList.add(room.getRoomName());
            }
        }
    }

    private void fetchRentalInfo(String rentalId) {
        DatabaseReference rentalRef = mDatabase.child("rentals").child(rentalId);
        rentalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rentalInfo = dataSnapshot.getValue(RentalInfo_Hoa.class);
                    if (rentalInfo != null) {
                        // Update UI with rental information
                        hotelNameTextView.setText(rentalInfo.getHotelName());
                        hotelRatingRatingBar.setRating(rentalInfo.getHotelRating());
                        hotelLocationTextView.setText(rentalInfo.getHotelLocation());
                        maxOccupancyTextView.setText("Max Occupancy: " + rentalInfo.getMaxOccupancy());

                        // Populate room types list
                        getRoomTypesFromRentalInfo();
                        // Pass roomTypesList to the adapter
                        roomTypesAdapter = new RoomTypesAdapter(roomTypesList);
                        roomTypesRecyclerView.setAdapter(roomTypesAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}