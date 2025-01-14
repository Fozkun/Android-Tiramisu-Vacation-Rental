package com.rmit.android_tiramisu_vacation_rental;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.HotelRoomAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.CouponType;
import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseNotificationSender;
import com.rmit.android_tiramisu_vacation_rental.models.CouponModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.Location_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RentalInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "RentalInfoActivity"; //Tag use for Logcat
    private UserSession_Tri userSession; //User session to access user role and id
    private DatabaseReference userReference, hotelReference, roomReference, couponReference, fmTokenReference, mDatabase;
    private HotelModel_Tri hotelModel;
    private ArrayList<HotelRoomModel_Tri> rooms = new ArrayList<>();

    // All views
    private TextView textViewHotelName, textViewHotelLocation, latLngFinderHyperLink;
    ;
    private RatingBar ratingBarHotel;
    private LinearLayout layoutActionButtons, layoutEditHotelForm, layoutCreateHotelRoomForm, layoutCreateHotelCouponForm;
    private Button btnCreateRoom, btnDeleteHotel, btnSaveHotel, btnCreateHotelCoupon, btnShowEditHotelForm, btnShowCreateHotelRoom, btnShowCreateHotelCouponForm;

    private TextInputLayout inputLayoutRoomStartDate, inputLayoutRoomEndDate;
    private HotelRoomAdapter hotelRoomAdapter;
    private RecyclerView recyclerViewHotelRoomCard;
    // All bottom navigation buttons
    private LinearLayout navHome, navMyTrips, navCoupons, navNotification, navProfile;

    private GoogleMap mMap;

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
        couponReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.Coupons);
        fmTokenReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.FM_TOKENS);

        //Find view by id
        latLngFinderHyperLink = findViewById(R.id.latLngFinderHyperLink);
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
        btnCreateRoom = findViewById(R.id.btnCreateRoom);
        inputLayoutRoomStartDate = findViewById(R.id.inputCreateRoomStartDate);
        inputLayoutRoomEndDate = findViewById(R.id.inputCreateRoomEndDate);

        //Find all bottom navigation ids
        navHome = findViewById(R.id.nav_home);
        navCoupons = findViewById(R.id.nav_coupons);
        navMyTrips = findViewById(R.id.nav_myTrips);
        navNotification = findViewById(R.id.nav_notification);
        navProfile = findViewById(R.id.nav_profile);

        // Setup click event listener for all buttons
        btnSaveHotel.setOnClickListener(v -> {
            EditText editTextHotelName = findViewById(R.id.editText_hotel_name);
            EditText editTextHotelAddress = findViewById(R.id.editText_hotel_address);
            EditText editTextLatitude = findViewById(R.id.editText_latitude);
            EditText editTextLongitude = findViewById(R.id.editText_longitude);
            EditText editTextMaxOccupancy = findViewById(R.id.editText_max_occupancy);

            if (!editTextHotelName.getText().toString().equals("")) {
                this.hotelModel.setName(editTextHotelName.getText().toString());
            }


            if (!editTextHotelAddress.getText().toString().equals("")) {
                this.hotelModel.setAddress(editTextHotelAddress.getText().toString());
            }

            if (!editTextLatitude.getText().toString().equals("") && !editTextLongitude.getText().toString().equals("")) {
                double latitude = Double.parseDouble(editTextLatitude.getText().toString());
                double longtitude = Double.parseDouble(editTextLongitude.getText().toString());
                Location_Tri locationTri = new Location_Tri(latitude, longtitude);
                this.hotelModel.setLocation(locationTri);
            }

            if (!editTextMaxOccupancy.getText().toString().equals("")) {
                int maxOccupancy = Integer.parseInt(editTextMaxOccupancy.getText().toString());
                this.hotelModel.setMaxOccupancy(maxOccupancy);
            }

            hotelReference.child(this.hotelModel.getId()).setValue(this.hotelModel);
        });

        inputLayoutRoomStartDate.getEditText().setOnClickListener(view -> showDateTimePicker(this, inputLayoutRoomStartDate));
        inputLayoutRoomEndDate.getEditText().setOnClickListener(view -> showDateTimePicker(this, inputLayoutRoomEndDate));
        btnCreateRoom.setOnClickListener(v -> {
            TextInputLayout inputLayoutRoomName, inputLayoutRoomDescription, inputLayoutRoomPrice, inputLayoutRoomStartDate, inputLayoutRoomEndDate, inputLayoutRoomStatus, inputLayoutPeople;
            inputLayoutRoomName = findViewById(R.id.inputCreateRoonName);
            inputLayoutRoomDescription = findViewById(R.id.inputCreateRoomDescription);
            inputLayoutRoomPrice = findViewById(R.id.inputCreateRoomPrice);
            inputLayoutRoomStartDate = findViewById(R.id.inputCreateRoomStartDate);
            inputLayoutRoomEndDate = findViewById(R.id.inputCreateRoomEndDate);
            inputLayoutRoomStatus = findViewById(R.id.inputCreateRoomStatus);
            inputLayoutPeople = findViewById(R.id.inputCreateRoomPeople);

            String roomName = "";
            String roomDescription = "";
            Double roomPrice = 0.0;
            Date roomStartDate = null;
            Date roomEndDate = null;
            HotelRoomStatus roomStatus = null;
            int roomPeople = 0;
            String roomImage = null;
            ArrayList<String> bookUserId = new ArrayList<>();

            if (inputLayoutRoomName.getEditText() != null) {
                roomName = inputLayoutRoomName.getEditText().getText().toString();
            }

            if (inputLayoutRoomDescription.getEditText() != null) {
                roomDescription = inputLayoutRoomDescription.getEditText().getText().toString();
            }

            if (inputLayoutRoomPrice.getEditText() != null) {
                roomPrice = Double.parseDouble(inputLayoutRoomPrice.getEditText().getText().toString());
            }

            if (inputLayoutRoomStatus.getEditText() != null) {
                String status = inputLayoutRoomStatus.getEditText().getText().toString();
                if (status.equals("Available")) {
                    roomStatus = HotelRoomStatus.AVAILABLE;
                } else {
                    roomStatus = HotelRoomStatus.UNAVAILABLE;
                }
            }

            if (inputLayoutPeople.getEditText() != null) {
                roomPeople = Integer.parseInt(inputLayoutPeople.getEditText().getText().toString());
            }

            if (inputLayoutRoomStartDate.getEditText() != null) {
                inputLayoutRoomStartDate.getEditText().setOnClickListener(view -> showDateTimePicker(this, inputLayoutRoomStartDate));
                roomStartDate = MyDateUtils.parseDate(inputLayoutRoomStartDate.getEditText().getText().toString());
            }

            if (inputLayoutRoomEndDate.getEditText() != null) {
                roomEndDate = MyDateUtils.parseDate(inputLayoutRoomEndDate.getEditText().getText().toString());
            }

            if (TextUtils.isEmpty(roomName)) {
                inputLayoutRoomName.setError("Room name is required");
                inputLayoutRoomName.requestFocus();
            } else if (TextUtils.isEmpty(roomDescription)) {
                inputLayoutRoomDescription.setError("Room description is required");
                inputLayoutRoomDescription.requestFocus();
            } else if (TextUtils.isEmpty(inputLayoutRoomPrice.getEditText().getText().toString())) {
                inputLayoutRoomPrice.setError("Room price is required");
                inputLayoutRoomPrice.requestFocus();
            } else if (TextUtils.isEmpty(inputLayoutRoomStatus.getEditText().getText().toString())) {
                inputLayoutRoomStatus.setError("Room status is required");
                inputLayoutRoomStatus.requestFocus();
            } else if (TextUtils.isEmpty(inputLayoutPeople.getEditText().getText().toString())) {
                inputLayoutPeople.setError("Room people is required");
                inputLayoutPeople.requestFocus();
            } else if (TextUtils.isEmpty(inputLayoutRoomStartDate.getEditText().getText().toString())) {
                inputLayoutRoomStartDate.setError("Room start date is required");
                inputLayoutRoomStartDate.requestFocus();
            } else if (TextUtils.isEmpty(inputLayoutRoomEndDate.getEditText().getText().toString())) {
                inputLayoutRoomEndDate.setError("Room end date is required");
                inputLayoutRoomEndDate.requestFocus();
            } else {
                HotelRoomModel_Tri hotelRoomModelTri = new HotelRoomModel_Tri();
                hotelRoomModelTri.setName(roomName);
                hotelRoomModelTri.setDescription(roomDescription);
                hotelRoomModelTri.setPrice(roomPrice);
                hotelRoomModelTri.setStatus(roomStatus);
                hotelRoomModelTri.setStartDate(roomStartDate);
                hotelRoomModelTri.setEndDate(roomEndDate);
                hotelRoomModelTri.setPeople(roomPeople);
                hotelRoomModelTri.setImageUrl(roomImage);
                hotelRoomModelTri.setHotelId(hotelModel.getId());
                hotelRoomModelTri.setBookedUserIds(bookUserId);

                String key = roomReference.push().getKey();
                if (key != null) {
                    hotelRoomModelTri.setId(key);
                    roomReference.child(key).setValue(hotelRoomModelTri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, hotelRoomModelTri.toString());
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            }
                        }

                        layoutCreateHotelRoomForm.setVisibility(View.GONE);
                    });
                }
            }

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
            if (layoutCreateHotelCouponForm.getVisibility() == View.GONE) {
                layoutCreateHotelCouponForm.setVisibility(View.VISIBLE);
            } else {
                layoutCreateHotelCouponForm.setVisibility(View.GONE);
            }
        });

        btnCreateHotelCoupon.setOnClickListener(v -> {
            TextInputLayout inputLayoutCouponTitle, inputLayoutCouponDescription, inputLayoutCouponValue;

            inputLayoutCouponTitle = findViewById(R.id.inputLayoutCouponTitle);
            inputLayoutCouponDescription = findViewById(R.id.inputLayoutCouponDescription);
            inputLayoutCouponValue = findViewById(R.id.inputLayoutCouponValue);

            String titleInput = "";
            String description = "";
            String value = "";
            if (inputLayoutCouponTitle.getEditText() != null) {
                titleInput = inputLayoutCouponTitle.getEditText().getText().toString();
            }

            if (inputLayoutCouponDescription.getEditText() != null) {
                description = inputLayoutCouponDescription.getEditText().getText().toString();
            }

            if (inputLayoutCouponValue.getEditText() != null) {
                value = inputLayoutCouponValue.getEditText().getText().toString();
            }

            if (TextUtils.isEmpty(titleInput)) {
                inputLayoutCouponTitle.setError("Title is required");
                inputLayoutCouponTitle.requestFocus();
            } else if (TextUtils.isEmpty(description)) {
                inputLayoutCouponDescription.setError("Description is required");
                inputLayoutCouponDescription.requestFocus();
            } else if (TextUtils.isEmpty(value)) {
                inputLayoutCouponValue.setError("Coupon value is required");
                inputLayoutCouponValue.requestFocus();
            } else {
                boolean isValid = true;
                int percentageValue = 0;

                try {
                    percentageValue = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    inputLayoutCouponValue.setError("Valid value is required");
                    inputLayoutCouponValue.requestFocus();
                    isValid = false;
                }

                if (isValid) {
                    CouponModel_Tri model = new CouponModel_Tri();
                    model.setTitle(titleInput);
                    model.setDescription(description);
                    model.setValue(String.valueOf(percentageValue));
                    model.setType(CouponType.PERCENTAGE_DISCOUNT);
                    model.setCreatedDate(new Date());

                    String key = couponReference.push().getKey();
                    if (key != null) {
                        model.setId(key);
                        couponReference.child(key).setValue(model).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, model.toString());
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                }
                            }

                            layoutCreateHotelCouponForm.setVisibility(View.GONE);
                            sendNotificationToUsers("New coupon dropped", model.getDescription());
                        });
                    }
                }
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

        latLngFinderHyperLink.setMovementMethod(LinkMovementMethod.getInstance());

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

                roomReference.orderByChild("hotelId").equalTo(hotelModel.getId()).addValueEventListener(new ValueEventListener() {
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

    public void sendNotificationToUsers(String title, String description) {
        new Thread(() -> {
            fmTokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> allKeys = new ArrayList<>();

                    for (DataSnapshot snapshotFcKey : snapshot.getChildren()) {
                        String key = snapshotFcKey.getValue(String.class);

                        if (key != null) {
                            allKeys.add(key);
                        }
                    }

                    new Thread(() -> {
                        for (String key : allKeys) {
                            FirebaseNotificationSender firebaseNotificationSender = new FirebaseNotificationSender(key, title, description, RentalInfoActivity.this);
                            firebaseNotificationSender.sendNotification();
                        }
                    }).start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }).start();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateHotelMapView();
    }

    private void showDateTimePicker(Context context, TextInputLayout inputLayout) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year1, month1, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            context,
                            (timeView, hourOfDay, minuteOfHour) -> {
                                String selectedDateTime = String.format(Locale.getDefault(), "%02d:%02d %02d-%02d-%04d", hourOfDay, minuteOfHour, dayOfMonth, month1 + 1, year1);

                                if (inputLayout.getEditText() != null) {
                                    inputLayout.getEditText().setText(selectedDateTime);
                                }
                            },
                            hour, minute, true // Use true for 24-hour format
                    );
                    timePickerDialog.show();
                },
                year, month, day);

        datePickerDialog.show();
    }
}