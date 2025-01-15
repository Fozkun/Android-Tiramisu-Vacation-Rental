package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.BookedHotelRoomAdapter;
import com.rmit.android_tiramisu_vacation_rental.adapters.HotelRoomAdapter;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewBookedHotelRoomInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;

public class MyTripsActivity extends AppCompatActivity implements RecyclerViewBookedHotelRoomInterface {
    private static final String TAG = "MyTripsActivity"; //Tag use for Logcat
    private UserSession_Tri userSessionTri; //User session to access user role and id
    private DatabaseReference roomReference, hotelReference, fmTokenReference;
    // All views
    private EditText editTextSearch;
    private BookedHotelRoomAdapter bookedHotelRoomAdapter;
    private RecyclerView recyclerViewBookedHotelRoom;
    private ArrayList<HotelRoomModel_Tri> bookedRooms = new ArrayList<>();

    // All bottom navigation buttons
    private LinearLayout navHome, navCoupons, navNotification, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_trips);
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

        //Define firebase references
        roomReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTEL_ROOMS);
        hotelReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTELS);
        fmTokenReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.FM_TOKENS);

        //Find view by id
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerViewBookedHotelRoom = findViewById(R.id.recyclerViewBookedHotelRoom);

        // Setup click event listener
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = editTextSearch.getText().toString();

                ArrayList<HotelRoomModel_Tri> filteredRooms = new ArrayList<>();

                for (HotelRoomModel_Tri model: bookedRooms){
                    if(TextUtils.isEmpty(text) || model.getName().toLowerCase().contains(text.toLowerCase())){
                        filteredRooms.add(model);
                    }
                }

                bookedHotelRoomAdapter.setRooms(filteredRooms);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Find all bottom navigation ids
        navHome = findViewById(R.id.nav_home);
        navCoupons = findViewById(R.id.nav_coupons);
        navNotification = findViewById(R.id.nav_notification);
        navProfile = findViewById(R.id.nav_profile);

        // Setup click event listener for all bottom buttons
        navHome.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, HomepageActivity.class);
        });
        navCoupons.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyCouponsActivity.class);
        });
        navNotification.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, NotificationActivity.class);
        });
        navProfile.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, Profile.class);
        });

        //Setup recycler view and adapter;
        bookedHotelRoomAdapter = new BookedHotelRoomAdapter(bookedRooms, this);
        recyclerViewBookedHotelRoom.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewBookedHotelRoom.setAdapter(bookedHotelRoomAdapter);

        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HotelRoomModel_Tri> foundRooms = new ArrayList<>();

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    HotelRoomModel_Tri roomModel = roomSnapshot.getValue(HotelRoomModel_Tri.class);
                    if (roomModel != null) {
                        if (roomModel.getBookedUserIds() != null && roomModel.getBookedUserIds().contains(userSessionTri.getUserId())) {
                            foundRooms.add(roomModel);
                        }
                    }
                }

                bookedRooms = foundRooms;
                bookedHotelRoomAdapter.setRooms(bookedRooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}