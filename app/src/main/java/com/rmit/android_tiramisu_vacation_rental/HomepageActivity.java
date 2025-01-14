package com.rmit.android_tiramisu_vacation_rental;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rmit.android_tiramisu_vacation_rental.adapters.FilteredHotelCardAdapter;
import com.rmit.android_tiramisu_vacation_rental.adapters.HotelCardAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewHotelCardInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.Location_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomepageActivity extends AppCompatActivity implements RecyclerViewHotelCardInterface {
    private static final String TAG = "HomepageActivity"; //Tag use for Logcat

    private UserSession_Tri userSessionTri; //User session to access user role and id
    private String userFirebaseMsgToken; //User token for notification
    private DatabaseReference roomReference, hotelReference, fmTokenReference;
    private EditText editTextFindHotelWhere, editTextFindHotelStartDate, editTextFindHotelEndDate;
    private TextView textViewFindHotelRoomAdults, latLngFinderHyperLink;
    private Button btnFindHotel, btnShowCreateHotelForm, btnCreateHotel;

    private ArrayList<HotelModel_Tri> filteredHotels = new ArrayList<>();
    private HotelCardAdapter hotelCardAdapter;
    private FilteredHotelCardAdapter filteredHotelCardAdapter;
    private RecyclerView recyclerViewHotelCard;
    private LinearLayout layoutCreateHotelForm;

    // All bottom navigation buttons
    private LinearLayout navMyTrips, navCoupons, navNotification, navProfile;

    private ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getUserFirebaseMsgToken();
            Log.d(TAG, "Notification permission granted");
        } else {
            Log.d(TAG, "Notification permission not granted");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Check user session
        userSessionTri = UserSession_Tri.getInstance();
        if (!userSessionTri.hasSession()) {
            Log.d(TAG, "No user session");
            finish();
            return;
        }

        requestPermission(); // Request permission needed

        //Define firebase references
        roomReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTEL_ROOMS);
        hotelReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTELS);
        fmTokenReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FM_TOKENS);

        //Find view by id
        editTextFindHotelWhere = findViewById(R.id.editTextFindHotelWhere);
        editTextFindHotelStartDate = findViewById(R.id.editTextFindHotelStartDate);
        editTextFindHotelEndDate = findViewById(R.id.editTextFindHotelEndDate);
        textViewFindHotelRoomAdults = findViewById(R.id.textViewFindHotelRoomAdults);
        latLngFinderHyperLink = findViewById(R.id.latLngFinderHyperLink);
        btnFindHotel = findViewById(R.id.button);
        btnShowCreateHotelForm = findViewById(R.id.btnShowCreateHotelForm);
        layoutCreateHotelForm = findViewById(R.id.layoutCreateHotelForm);
        btnCreateHotel = findViewById(R.id.btnCreateHotel);
        recyclerViewHotelCard = findViewById(R.id.recyclerViewHotelCard);

        //Find all bottom navigation ids
        navCoupons = findViewById(R.id.nav_coupons);
        navMyTrips = findViewById(R.id.nav_myTrips);
        navNotification = findViewById(R.id.nav_notification);
        navProfile = findViewById(R.id.nav_profile);

        //Display views based on role
        if (userSessionTri.getUserRole() == UserRole.RENTAL_PROVIDER || userSessionTri.getUserRole() == UserRole.SUPER_USER) {
            btnShowCreateHotelForm.setVisibility(View.VISIBLE);
        } else {
            btnShowCreateHotelForm.setVisibility(View.GONE);
        }

        // Setup click event listener;
        editTextFindHotelStartDate.setOnClickListener(v -> showDateTimePicker(editTextFindHotelStartDate));
        editTextFindHotelEndDate.setOnClickListener(v -> showDateTimePicker(editTextFindHotelEndDate));
        textViewFindHotelRoomAdults.setOnClickListener(v -> showRoomPickerDialog(textViewFindHotelRoomAdults));
        btnFindHotel.setOnClickListener(v -> handleBtnFindHotel());
        btnShowCreateHotelForm.setOnClickListener(v -> {
            if (layoutCreateHotelForm.getVisibility() == View.GONE) {
                layoutCreateHotelForm.setVisibility(View.VISIBLE);
            } else {
                layoutCreateHotelForm.setVisibility(View.GONE);
            }
        });
        btnCreateHotel.setOnClickListener(v -> {
            EditText editTextHotelName = findViewById(R.id.editText_hotel_name);
            EditText editTextHotelAddress = findViewById(R.id.editText_hotel_address);
            EditText editTextLatitude = findViewById(R.id.editText_latitude);
            EditText editTextLongitude = findViewById(R.id.editText_longitude);
            EditText editTextMaxOccupancy = findViewById(R.id.editText_max_occupancy);

            String hotelName = editTextHotelName.getText().toString();
            String hotelAddress = editTextHotelAddress.getText().toString();
            String hotelLatitude = editTextLatitude.getText().toString();
            String hotelLongitude = editTextLongitude.getText().toString();
            String maxOccupancy = editTextMaxOccupancy.getText().toString();

            if (TextUtils.isEmpty(hotelName)) {
                editTextHotelName.setError("Hotel name is required");
                editTextHotelName.requestFocus();
            } else if (TextUtils.isEmpty(hotelAddress)) {
                editTextHotelAddress.setError("Hotel address is required");
                editTextHotelAddress.requestFocus();
            } else if (TextUtils.isEmpty(hotelLatitude)) {
                editTextLatitude.setError("Latitude is required");
                editTextLatitude.requestFocus();
            } else if (TextUtils.isEmpty(hotelLongitude)) {
                editTextLongitude.setError("Longitude is required");
                editTextLongitude.requestFocus();
            } else {
                Location_Tri location = new Location_Tri(Double.parseDouble(hotelLatitude), Double.parseDouble(hotelLongitude));

                HotelModel_Tri hotel = new HotelModel_Tri();
                hotel.setName(hotelName);
                hotel.setAddress(hotelAddress);
                hotel.setLocation(location);
                hotel.setMaxOccupancy(Integer.parseInt(maxOccupancy));
                hotel.setOwnerId(this.userSessionTri.getUserId());

                String key = hotelReference.push().getKey();
                if (key != null) {
                    hotel.setId(key);
                    hotelReference.child(key).setValue(hotel).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, key);
                            layoutCreateHotelForm.setVisibility(View.GONE);
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                            }
                        }
                    });
                }
            }
        });

        // Setup chat icon;
        ImageButton chatButton = findViewById(R.id.imageButton);
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomepageActivity.this, ChatActivity.class);
            intent.putExtra("providerId", userSessionTri.getUserId()); //
            startActivity(intent);
        });

        // Setup click event listener for all bottom buttons
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
        FirebaseRecyclerOptions<HotelModel_Tri> options
                = new FirebaseRecyclerOptions.Builder<HotelModel_Tri>()
                .setQuery(hotelReference, HotelModel_Tri.class)
                .build();
        hotelCardAdapter = new HotelCardAdapter(options, this);
        recyclerViewHotelCard.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHotelCard.setItemAnimator(null);

        recyclerViewHotelCard.setAdapter(hotelCardAdapter);
        hotelCardAdapter.startListening();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                getUserFirebaseMsgToken();
            } else {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            //Get user token from firebase
            getUserFirebaseMsgToken();
        }
    }

    private void getUserFirebaseMsgToken() {
        if (userFirebaseMsgToken != null) {
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d(TAG, token);

                userFirebaseMsgToken = token;
                fmTokenReference.child(userSessionTri.getUserId()).setValue(token).addOnCompleteListener(dbTask -> {
                    if (!dbTask.isSuccessful()) {
                        try {
                            throw Objects.requireNonNull(dbTask.getException());
                        } catch (Exception e) {
                            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }

    private void showDateTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minuteOfHour) -> {
                                String selectedDateTime = String.format(Locale.getDefault(), "%02d:%02d %02d-%02d-%04d", hourOfDay, minuteOfHour, dayOfMonth, month1 + 1, year1);
                                editText.setText(selectedDateTime);
                            },
                            hour, minute, true // Use true for 24-hour format
                    );
                    timePickerDialog.show();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showRoomPickerDialog(TextView roomInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_picker, null);

        NumberPicker roomsPicker = dialogView.findViewById(R.id.roomsPicker);
        NumberPicker adultsPicker = dialogView.findViewById(R.id.adultsPicker);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        roomsPicker.setMinValue(1);
        roomsPicker.setMaxValue(10);
        adultsPicker.setMinValue(1);
        adultsPicker.setMaxValue(20);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            int selectedRooms = roomsPicker.getValue();
            int selectedAdults = adultsPicker.getValue();

            roomInfo.setText(MessageFormat.format("{0} Room(s), {1} People", selectedRooms, selectedAdults));

            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleBtnFindHotel() {
        String destination = editTextFindHotelWhere.getText().toString().trim();
        String startDate = editTextFindHotelStartDate.getText().toString().trim();
        String endDate = editTextFindHotelEndDate.getText().toString().trim();
        int roomCount = 0;
        int adultCount = 0;

        if (TextUtils.isEmpty(destination) && TextUtils.isEmpty(startDate) && TextUtils.isEmpty(endDate)) {
            recyclerViewHotelCard.setAdapter(hotelCardAdapter);
            return;
        }

        // Get room and adult count
        String[] roomAdultsDetails = textViewFindHotelRoomAdults.getText().toString().split(",");
        String roomsText = roomAdultsDetails[0].trim();
        String adultsText = roomAdultsDetails[1].trim();
        Log.d(TAG, "Room text: " + roomsText + "\nAdult text:" + adultsText);

        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(roomsText);
        if (matcher.find()) {
            roomCount = Integer.parseInt(matcher.group());
        }

        matcher = pattern.matcher(adultsText);
        if (matcher.find()) {
            adultCount = Integer.parseInt(matcher.group());
        }

        findHotels(destination, MyDateUtils.parseDate(startDate), MyDateUtils.parseDate(endDate), roomCount, adultCount);
    }

    private void updateRecyclerViewContainingFilteredHotels() {
        filteredHotelCardAdapter = new FilteredHotelCardAdapter(filteredHotels, HomepageActivity.this);
        recyclerViewHotelCard.setAdapter(filteredHotelCardAdapter);
    }

    private void findHotels(String destination, Date startDate, Date endDate, int roomCount, int adultCount) {
        Log.d(TAG, "Destination: " + destination + "\nStart date: " + (startDate == null ? null : startDate.toString()) + "\nEnd Date: " + (endDate == null ? null : endDate.toString()) + "\nAdultCount: " + adultCount + "\nRoomCount: " + roomCount);

        filteredHotels = new ArrayList<>();
        updateRecyclerViewContainingFilteredHotels();

        hotelReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                        HotelModel_Tri hotel = hotelSnapshot.getValue(HotelModel_Tri.class);
                        if (hotel != null) {
                            if (TextUtils.isEmpty(destination) || hotel.getAddress().toLowerCase().contains(destination.toLowerCase())) {
                                roomReference.orderByChild("hotelId").equalTo(hotel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            ArrayList<HotelRoomModel_Tri> foundRooms = new ArrayList<>();

                                            for (DataSnapshot hotelRoomSnapshot : snapshot.getChildren()) {
                                                HotelRoomModel_Tri room = hotelRoomSnapshot.getValue(HotelRoomModel_Tri.class);

                                                if (room != null) {
                                                    boolean isRoomFound = room.getStatus() == HotelRoomStatus.AVAILABLE &&
                                                            (startDate == null || endDate == null || (room.getStartDate().equals(startDate) && room.getEndDate().equals(endDate))) &&
                                                            room.getPeople() >= adultCount;

                                                    if (isRoomFound) {
                                                        foundRooms.add(room);
                                                        Log.d(TAG, "Room found: " + room);
                                                    }
                                                }
                                            }

                                            if (foundRooms.size() >= roomCount) {
                                                Log.d(TAG, "Total room found: " + foundRooms.size());

                                                if (!filteredHotels.contains(hotel)) {
                                                    filteredHotels.add(hotel);
                                                    Log.d(TAG, "Total hotel found: " + filteredHotels.size());

                                                    // Update adapter outside of the loop for better performance
                                                    updateRecyclerViewContainingFilteredHotels();
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "Something is wrong in hotel rooms database");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Database error: " + error.getMessage());
                                    }
                                });
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Something is wrong in hotel database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        HotelModel_Tri model = hotelCardAdapter.getItem(position);
        Log.d(TAG, model.toString());

        Intent intent = new Intent(this, RentalInfoActivity.class);
        intent.putExtra("hotelId", model.getId());
        startActivity(intent);
    }
}