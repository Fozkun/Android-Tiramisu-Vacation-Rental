package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.HotelRoomAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.Location_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;
import java.util.List;

public class RentalInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "RentalInfoActivity"; //Tag use for Logcat
    private UserSession_Tri userSession; //User session to access user role and id
    private DatabaseReference userReference, hotelReference, roomReference, fmTokenReference, mDatabase;
    private HotelModel_Tri hotelModel;
    private ArrayList<HotelRoomModel_Tri> rooms = new ArrayList<>();

    // All views
    private TextView textViewHotelName, textViewHotelLocation;
    private RatingBar ratingBarHotel;
    private LinearLayout layoutActionButtons, layoutEditHotelForm, layoutCreateHotelRoomForm, layoutCreateHotelCouponForm;
    private Button btnDeleteHotel, btnSaveHotel, btnCreateHotelCoupon, btnShowEditHotelForm, btnShowCreateHotelRoom, btnShowCreateHotelCouponForm;
    private HotelRoomAdapter hotelRoomAdapter;
    private RecyclerView recyclerViewHotelRoomCard;
    // All bottom navigation buttons
    private LinearLayout navHome, navMyTrips, navCoupons, navNotification, navProfile;

    private GoogleMap mMap;
    private MapView mapView;
    private RentalInfo_Hoa rentalInfo;
    private FirebaseAuth mAuth;
    private String userId;
    private Button btnCreateRoom, btnEditRoom, btnDeleteRoom;
    private RecyclerView roomTypesRecyclerView;
    //private RoomTypesAdapter roomTypesAdapter;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Check user session
        userSession = UserSession_Tri.getInstance();
        if (!userSession.hasSession()) {
            Log.d(TAG, "No user session");
            finish();
            return;
        }

        //Define firebase references
        userReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.REGISTERED_USERS);
        hotelReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.HOTELS);
        roomReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.HOTEL_ROOMS);
        fmTokenReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.FM_TOKENS);

        //Find view by id
        textViewHotelName = findViewById(R.id.textViewHotelName);
        textViewHotelLocation = findViewById(R.id.textViewHotelLocation);
        ratingBarHotel = findViewById(R.id.ratingBarHotel);
        layoutActionButtons = findViewById(R.id.layoutActionButtons);
        layoutCreateHotelRoomForm = findViewById(R.id.layoutCreateHotelRoomForm);
        layoutCreateHotelCouponForm = findViewById(R.id.layoutCreateHotelCouponForm);
        layoutEditHotelForm = findViewById(R.id.layoutEditHotelForm);
        btnDeleteHotel = findViewById(R.id.btnDeleteHotel);
        btnShowEditHotelForm = findViewById(R.id.btnShowEditHotelForm);
        btnShowCreateHotelRoom = findViewById(R.id.btnShowCreateHotelRoomForm);
        btnShowCreateHotelCouponForm = findViewById(R.id.btnShowCreateHotelCouponForm);
        btnCreateHotelCoupon = findViewById(R.id.btnCreateHotelCoupon);
        btnSaveHotel = findViewById(R.id.btnSaveHotel);
        recyclerViewHotelRoomCard = findViewById(R.id.recyclerViewHotelRoomCard);

        //Find all bottom navigation ids
        navHome = findViewById(R.id.nav_home);
        navCoupons = findViewById(R.id.nav_coupons);
        navMyTrips = findViewById(R.id.nav_myTrips);
        navNotification = findViewById(R.id.nav_notification);
        navProfile = findViewById(R.id.nav_profile);

        // Setup click event listener for all buttons
        btnSaveHotel.setOnClickListener(v -> {
            EditText editTextHotelName  = findViewById(R.id.editText_hotel_name);

            if(!editTextHotelName.getText().toString().equals("")){
                this.hotelModel.setName(editTextHotelName.getText().toString());
            }

            /*
            if(){}

            if(){

            }
             */


            hotelReference.child(this.hotelModel.getId()).setValue(this.hotelModel);
        });

        btnDeleteHotel.setOnClickListener(v -> {

        });

        btnShowEditHotelForm.setOnClickListener(v -> {
            if (layoutEditHotelForm.getVisibility() == View.GONE) {
                layoutEditHotelForm.setVisibility(View.VISIBLE);
            } else {
                layoutEditHotelForm.setVisibility(View.GONE);
            }
        });

        btnShowCreateHotelRoom.setOnClickListener(v -> {
            if (layoutCreateHotelRoomForm.getVisibility() == View.GONE) {
                layoutCreateHotelRoomForm.setVisibility(View.VISIBLE);
            } else {
                layoutCreateHotelRoomForm.setVisibility(View.GONE);
            }
        });

        btnShowCreateHotelCouponForm.setOnClickListener(v -> {
            if(layoutCreateHotelCouponForm.getVisibility() == View.GONE){
                layoutCreateHotelCouponForm.setVisibility(View.VISIBLE);
            }else{
                layoutCreateHotelCouponForm.setVisibility(View.GONE);
            }
        });

        // Setup click event listener for all bottom buttons
        navHome.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, HomepageActivity.class);
        });
        navCoupons.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyCouponsActivity.class);
        });
        navMyTrips.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyTripsActivity.class);
        });
        navNotification.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, NotificationActivity.class);
        });
        navProfile.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, Profile.class);
        });

        //Setup recycler view and adapter;
        hotelRoomAdapter = new HotelRoomAdapter(this.rooms);
        recyclerViewHotelRoomCard.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewHotelRoomCard.setAdapter(hotelRoomAdapter);

        // Get hotel id from intent
        Intent intent = getIntent();
        String hotelId = intent.getStringExtra("hotelId");

        if (hotelId == null) {
            Log.d(TAG, "Hotel id is missing?");
            finish();
            return;
        }

        hotelReference.child(hotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HotelModel_Tri foundModel = snapshot.getValue(HotelModel_Tri.class);
                if (foundModel == null) {
                    Log.d(TAG, "Hotel is not found with id: " + hotelId);
                    hotelReference.removeEventListener(this);
                    finish();
                }

                hotelModel = foundModel;
                updateView();

                roomReference.orderByChild("hotelId").equalTo(hotelModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<HotelRoomModel_Tri> foundRooms = new ArrayList<>();

                        for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                            HotelRoomModel_Tri foundRoom = roomSnapshot.getValue(HotelRoomModel_Tri.class);

                            if (foundRoom != null) {
                                foundRooms.add(foundRoom);
                            }
                        }

                        rooms = foundRooms;
                        hotelRoomAdapter.setRooms(rooms);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        //Find view by id
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        btnCreateRoom = findViewById(R.id.btnCreateRoom);
        btnEditRoom = findViewById(R.id.btnEdit);
        btnDeleteRoom = findViewById(R.id.btnDelete);
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

        btnCreateRoom.setOnClickListener(view -> createNewRoom());
        btnEditRoom.setOnClickListener(view -> editHotelInfo());
        btnDeleteRoom.setOnClickListener(view -> deleteHotel());
         */
    }

    private void updateView() {
        if (hotelModel == null) {
            return;
        }

        this.textViewHotelName.setText(hotelModel.getName());
        this.textViewHotelLocation.setText(hotelModel.getAddress());
        this.ratingBarHotel.setRating(hotelModel.getRating());

        //Display views based on role
        String userId = userSession.getUserId();
        UserRole userRole = userSession.getUserRole();

        if ((userRole == UserRole.RENTAL_PROVIDER && userId.equals(hotelModel.getOwnerId())) || userRole == UserRole.SUPER_USER) {
            layoutActionButtons.setVisibility(View.VISIBLE);
        }
        // Setup click event listener;
    }

    private void updateHotelMapView() {
        if (hotelModel == null) {
            return;
        }

        Location_Tri hotelLocation = hotelModel.getLocation();
        LatLng latLng = new LatLng(0, 0);
        if (hotelLocation != null) {
            latLng = new LatLng(hotelLocation.latitude, hotelLocation.longitude);
        }

        if (mMap != null) {
            mMap.clear(); // Clear existing markers if any

            // Add a marker at the hotel location
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(hotelModel.getName())
                    .snippet("Address: " + hotelModel.getAddress()));

            // Move and zoom the camera to the hotel location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            Log.e(TAG, "GoogleMap is not initialized.");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateHotelMapView();
    }

    /*
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
        rentalInfo.setOwnerID(userId);

        List<RentalInfo_Hoa.Room> rooms = new ArrayList<>();

        RentalInfo_Hoa.Room room1 = new RentalInfo_Hoa.Room();
        room1.setRoomName("Deluxe Room");
        room1.setRoomDetails("King-size bed, balcony, city view");
        room1.setRoomPrice(150.00);
        room1.setStatus(HotelRoomStatus.AVAILABLE);
        room1.setImageUrl("https://example.com/room1.jpg");
        // Set default start and end dates (you might need to adjust this)
        room1.setStartDate(new Date());
        room1.setEndDate(new Date());
        rooms.add(room1);

        rentalInfo.setRooms(rooms);
    }


    private void writeToDatabase() {
        String rentalId = mDatabase.child("rentals").push().getKey();

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

 */

    /*
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
        EditText startDateEditText = findViewById(R.id.InputStartDate);
        EditText endDateEditText = findViewById(R.id.InputEndDate);

        // Get room data from EditText fields
        String roomName = roomNameEditText.getText().toString().trim();
        String roomDetails = roomDetailsEditText.getText().toString().trim();
        String roomPriceStr = roomPriceEditText.getText().toString().trim();
        String roomImageStr = roomImage.getTag().toString();
        String startDateStr = startDateEditText.getText().toString().trim();
        String endDateStr = endDateEditText.getText().toString().trim();

        Date startDate = MyDateUtils.parseDate(startDateStr);
        Date endDate = MyDateUtils.parseDate(endDateStr);
        if (roomName.isEmpty() || roomDetails.isEmpty() || roomPriceStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double roomPrice = Double.parseDouble(roomPriceStr);

            // Create a new Room object
            RentalInfo_Hoa.Room newRoom = new RentalInfo_Hoa.Room();
            newRoom.setRoomName(roomName);
            newRoom.setRoomDetails(roomDetails);
            newRoom.setRoomPrice(roomPrice);
            newRoom.setStatus(HotelRoomStatus.AVAILABLE); // Set initial status
            newRoom.setImageUrl(roomImageStr);
            newRoom.setStartDate(startDate);
            newRoom.setEndDate(endDate);

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

    private void checkOwnership(String ownerId) {
        if (ownerId.equals(userId)) {
            // Current user is the owner
            btnEditRoom.setVisibility(View.VISIBLE);
            btnDeleteRoom.setVisibility(View.VISIBLE);
        } else {
            // Current user is not the owner
            btnEditRoom.setVisibility(View.GONE);
            btnDeleteRoom.setVisibility(View.GONE);
        }
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


        TextView roomDetailsTextView = findViewById(R.id.roomDetails);
        StringBuilder roomDetailsStringBuilder = new StringBuilder();
        for (RentalInfo_Hoa.Room room : selectedRoomList) {
            roomDetailsStringBuilder.append("Room Name: ").append(room.getRoomName()).append("\n");
            roomDetailsStringBuilder.append("Details: ").append(room.getRoomDetails()).append("\n");
            roomDetailsStringBuilder.append("Price: ").append(room.getRoomPrice()).append("\n\n");
            roomDetailsStringBuilder.append("Start Date: ").append(room.getStartDate()).append("\n\n");
            roomDetailsStringBuilder.append("End Date: ").append(room.getEndDate()).append("\n\n");
            roomDetailsStringBuilder.append("Statis: ").append(room.getStatus()).append("\n\n");
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

                        // Check ownership
                        checkOwnership(rentalInfo.getOwnerID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
     */
}