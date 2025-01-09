package com.rmit.android_tiramisu_vacation_rental;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rmit.android_tiramisu_vacation_rental.adapters.HotelCardAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.firebaseHelpers.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.firebaseHelpers.FirebaseNotificationSender;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewHotelCardInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.Location_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class HomepageActivity extends AppCompatActivity implements RecyclerViewHotelCardInterface {
    private static final String TAG = "HomepageActivity";

    // ------------------------------------------
    private UserSession_Tri userSessionTri;
    private String firebaseMessagingToken;

    private HotelCardAdapter hotelCardAdapter;
    private Button btnCreateHotel;
    private RecyclerView recyclerViewHotelCard;

    private DatabaseReference roomReference;
    private DatabaseReference hotelReference;
    private DatabaseReference fcmTokenReference;

    private EditText editTextWhere, editTextStartDate, editTextEndDate;
    private TextView textViewRoomAdults;
    private Button buttonFind;

    // ------------------------------------------
    // Define all bottom buttons
    private LinearLayout navMyTrips, navCoupons, navNotification, navProfile;

    private ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            //Get device token from firebase
            getDeviceToken();
            Log.d(TAG, "Permission granted");
        } else {
            Log.d(TAG, "Permission not granted");
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

        editTextWhere = findViewById(R.id.editText1); // "Where you want to go?"
        editTextStartDate = findViewById(R.id.editTextStartDate); // "DD-MM-YYYY" (Start Date)
        editTextEndDate = findViewById(R.id.editText); // "DD-MM-YYYY" (End Date)
        textViewRoomAdults = findViewById(R.id.textView6); // "Room, People"
        buttonFind = findViewById(R.id.button); // "Find"

        //Find and set with id
        navMyTrips = findViewById(R.id.nav_myTrips);
        navProfile = findViewById(R.id.nav_profile);


        editTextStartDate.setOnClickListener(v -> showDatePicker(editTextStartDate));
        editTextEndDate.setOnClickListener(v -> showDatePicker(editTextEndDate));

        textViewRoomAdults.setOnClickListener(v -> showRoomPickerDialog(textViewRoomAdults));

        buttonFind.setOnClickListener(v -> handleFindButton());

        btnCreateHotel = findViewById(R.id.btnCreateHotel);
        recyclerViewHotelCard = findViewById(R.id.recyclerViewHotelCard);

        roomReference = FirebaseDatabase.getInstance().getReference("HotelRooms");
        hotelReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTELS);
        fcmTokenReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FCM_TOKENS);

        // ------------------------------------------
        userSessionTri = UserSession_Tri.getInstance();
        if (userSessionTri.hasSession()) {
            UserRole userRole = userSessionTri.getUserRole();

            if (userRole == UserRole.RENTAL_PROVIDER) {
                btnCreateHotel.setVisibility(View.VISIBLE);
            }

            recyclerViewHotelCard.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewHotelCard.setItemAnimator(null);

            FirebaseRecyclerOptions<HotelModel_Tri> options
                    = new FirebaseRecyclerOptions.Builder<HotelModel_Tri>()
                    .setQuery(hotelReference, HotelModel_Tri.class)
                    .build();

            hotelCardAdapter = new HotelCardAdapter(options, this);
            recyclerViewHotelCard.setAdapter(hotelCardAdapter);

            hotelCardAdapter.startListening();

            btnCreateHotel.setOnClickListener(v -> {
                HotelModel_Tri model = new HotelModel_Tri();

                String modelId = hotelReference.push().getKey();
                model.setId(modelId);
                model.setName("A hotel");
                model.setAddress("Address");
                model.setLocation(new Location_Tri());
                model.setRating(0);

                hotelReference.child(modelId).setValue(model);
            });

            // ------------------------------------------
            // Set on click event for all bottom buttons
            navMyTrips.setOnClickListener(v -> {
                BottomNavigationHelper.navigateTo(this, MyTripsActivity.class);
            });
            navProfile.setOnClickListener(v -> {
                BottomNavigationHelper.navigateTo(this, Profile.class);
            });

            requestPermission();
        }
    }

    private void handleFindButton() {
        String destination = editTextWhere.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String roomDetails = textViewRoomAdults.getText().toString().trim();

        if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search for hotel information in Firebase
        searchHotels(destination, startDate, endDate, roomDetails);
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


            roomInfo.setText(selectedRooms + " Room(s), " + selectedAdults + " People");


            dialog.dismiss();
        });


        dialog.show();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month1 + 1, year1);
                    editText.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void searchHotels(String destination, String startDate, String endDate, String roomDetails) {
        hotelReference.orderByChild("address").equalTo(destination)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                                HotelModel_Tri hotel = hotelSnapshot.getValue(HotelModel_Tri.class);
                                if (hotel != null) {
                                    filterRooms(hotel, startDate, endDate, roomDetails);
                                }
                            }
                        } else {
                            Toast.makeText(HomepageActivity.this, "No hotels found at this location", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomepageActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterRooms(@NonNull HotelModel_Tri hotel, String startDate, String endDate, String roomDetails) {

        roomReference.orderByChild("hotelId").equalTo(hotel.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean foundRoom = false;
                        for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                            HotelRoomModel_Tri room = roomSnapshot.getValue(HotelRoomModel_Tri.class);

                            // Áp dụng bộ lọc cho loại phòng và ngày
                            if (room != null &&
                                    (room.getDescription().equalsIgnoreCase("Deluxe Room") || room.getDescription().equalsIgnoreCase("Standard Room")) &&
                                    isRoomAvailable(room, startDate, endDate, roomDetails)) {
                                foundRoom = true;
                                displayHotel(hotel);
                                break;
                            }
                        }

                        if (!foundRoom) {
                            Toast.makeText(HomepageActivity.this, "No available rooms found for this hotel.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomepageActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isRoomAvailable(HotelRoomModel_Tri room, String startDate, String endDate, String roomDetails) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


            Date inputStartDate = dateFormat.parse(startDate);
            Date inputEndDate = dateFormat.parse(endDate);


            String roomStartDateString = dateFormat.format(room.getStartDate());
            String roomEndDateString = dateFormat.format(room.getEndDate());

            Date roomStartDate = dateFormat.parse(roomStartDateString);
            Date roomEndDate = dateFormat.parse(roomEndDateString);


            String[] roomInfo = roomDetails.split(", ");
            int requestedRooms = Integer.parseInt(roomInfo[0].split(" ")[0]);
            int requestedPeople = Integer.parseInt(roomInfo[1].split(" ")[0]);

            return !(inputStartDate.after(roomEndDate) || inputEndDate.before(roomStartDate)) &&
                    requestedPeople <= room.getPeople();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void displayHotel(HotelModel_Tri hotel) {
        hotelCardAdapter.updateFilteredHotels(hotel); ///////////////
    }
    @Override
    public void onItemClick(int position) {
        HotelModel_Tri model = hotelCardAdapter.getItem(position);
        Log.d(TAG, model.toString());
        //Intent intent = new Intent(this, Hotel.class);
        //intent.putExtra("siteId", siteModel.getSiteId());
        //startActivity(intent);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                getDeviceToken();
            } else {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            //Get device token from firebase
            getDeviceToken();
        }
    }

    public void getDeviceToken() {
        if (firebaseMessagingToken != null) {
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d(TAG, token);

                firebaseMessagingToken = token;
                fcmTokenReference.child(userSessionTri.getUserId()).setValue(token);

            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }

    public void sendNotificationToUsers() {
        new Thread(() -> {
            FirebaseNotificationSender firebaseNotificationSender = new FirebaseNotificationSender("dlJ0orWGSDGDdirmVFAhs4:APA91bGBCBkVGPCJalkBTJvw7Vz8eVp51s8YAR1oL7R_BfZizscSXi6tszidePSQDgN1Rr5TiQLgU5sbqiBOhnHQorHj17imfed1CBVhLy4hMOkLyq1NwUc", "Test noti send", "Test if this work", HomepageActivity.this);
            firebaseNotificationSender.sendNotification();
        }).start();
    }
}