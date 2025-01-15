package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.CouponAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseNotificationSender;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewCouponInterface;
import com.rmit.android_tiramisu_vacation_rental.models.CouponModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSettings_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;


public class BookingConfirmationActivity extends AppCompatActivity implements RecyclerViewCouponInterface {
    private static final String TAG = "BookingConfirmationActivity"; //Tag use for Logcat
    private UserSession_Tri userSession;
    private DatabaseReference hotelReference, roomReference, couponReference, fmTokenReference, userSettingsReference;
    private HotelRoomModel_Tri hotelRoomModel;

    //All views
    private TextView textViewHotelRoomName, textViewHotelRoomDescription, textViewHotelRoomPrice, textViewSelectCoupon, textViewHotelRoomFinalPrice;
    private CouponAdapter couponAdapter;
    private CouponModel_Tri selectedCoupon;
    private RecyclerView recyclerViewCoupon;
    private LinearLayout layoutPaymentInfo;
    private Button btnConfirmBooking, btnConfirmPurchasedHotelRoom;

    // All bottom navigation buttons
    private LinearLayout navHome, navMyTrips, navCoupons, navNotification, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);
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
        userSettingsReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_SETTINGS);
        hotelReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.HOTELS);
        roomReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.HOTEL_ROOMS);
        couponReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.COUPONS);
        fmTokenReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.FM_TOKENS);

        //Find view by id
        textViewHotelRoomName = findViewById(R.id.textViewHotelRoomName);
        textViewHotelRoomDescription = findViewById(R.id.textViewHotelRoomDescription);
        textViewHotelRoomPrice = findViewById(R.id.textViewHotelRoomPrice);
        textViewSelectCoupon = findViewById(R.id.textViewSelectCoupon);
        textViewHotelRoomFinalPrice = findViewById(R.id.textViewHotelRoomFinalPrice);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnConfirmPurchasedHotelRoom = findViewById(R.id.btnConfirmPurchasedHotelRoom);
        layoutPaymentInfo = findViewById(R.id.layoutPaymentInfo);
        recyclerViewCoupon = findViewById(R.id.recyclerViewCoupon);

        //Find all bottom navigation ids
        navHome = findViewById(R.id.nav_home);
        navCoupons = findViewById(R.id.nav_coupons);
        navMyTrips = findViewById(R.id.nav_myTrips);
        navNotification = findViewById(R.id.nav_notification);
        navProfile = findViewById(R.id.nav_profile);

        // Setup click event listener for all buttons
        btnConfirmBooking.setOnClickListener(v -> {
            btnConfirmBooking.setEnabled(false);
            layoutPaymentInfo.setVisibility(View.VISIBLE);
            recyclerViewCoupon.setAdapter(null);
        });
        btnConfirmPurchasedHotelRoom.setOnClickListener(v -> {
            ArrayList<String> bookedUserIds = new ArrayList();
            if (hotelRoomModel.getBookedUserIds() != null) {
                bookedUserIds = hotelRoomModel.getBookedUserIds();
            }

            bookedUserIds.add(userSession.getUserId());
            hotelRoomModel.setBookedUserIds(bookedUserIds);
            hotelRoomModel.setStatus(HotelRoomStatus.UNAVAILABLE);

            roomReference.child(hotelRoomModel.getId()).setValue(hotelRoomModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    hotelReference.child(hotelRoomModel.getHotelId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HotelModel_Tri hotelModel = snapshot.getValue(HotelModel_Tri.class);
                            if (hotelModel != null) {
                                String hotelOwnerId = hotelModel.getOwnerId();

                                ArrayList<String> usersToNotify = new ArrayList<>();
                                usersToNotify.add(hotelOwnerId);

                                for (String userId : usersToNotify) {
                                    userSettingsReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserSettings_Tri settings = snapshot.getValue(UserSettings_Tri.class);

                                            if (settings != null) {
                                                if (settings.pushNotificationEnabled) {
                                                    fmTokenReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String userFmToken = snapshot.getValue(String.class);
                                                            Log.d(TAG, userFmToken == null ? "" : userFmToken);

                                                            if (userFmToken != null) {
                                                                new Thread(() -> {
                                                                    StringBuilder builder = new StringBuilder();
                                                                    builder.append("Room name: ").append(hotelRoomModel.getName()).append("\n");
                                                                    builder.append("Book date: ").append(MyDateUtils.formatDate(new Date())).append("\n");
                                                                    builder.append("Booked user id: ").append(userSession.getUserId());

                                                                    FirebaseNotificationSender sender = new FirebaseNotificationSender(userFmToken, "New room has been booked", builder.toString(), btnConfirmPurchasedHotelRoom.getContext());
                                                                    sender.sendNotification();
                                                                }).start();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    BottomNavigationHelper.navigateTo(btnConfirmPurchasedHotelRoom.getContext(), HomepageActivity.class);
                }
            });
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
        couponAdapter = new CouponAdapter(new ArrayList<>(), this);
        recyclerViewCoupon.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCoupon.setAdapter(couponAdapter);

        // Get hotel id from intent
        Intent intent = getIntent();
        String roomId = intent.getStringExtra("roomId");

        if (roomId == null) {
            Log.d(TAG, "Missing room id?");
            finish();
            return;
        }

        Log.d(TAG, roomId);

        roomReference.child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HotelRoomModel_Tri currentModel = snapshot.getValue(HotelRoomModel_Tri.class);

                if (currentModel != null) {
                    hotelRoomModel = currentModel;
                    updateView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateView() {
        if (hotelRoomModel == null) {
            return;
        }

        textViewHotelRoomName.setText("Name: " + hotelRoomModel.getName());
        textViewHotelRoomDescription.setText("Description: " + hotelRoomModel.getDescription());
        textViewHotelRoomPrice.setText("Price: " + hotelRoomModel.getPrice() + "$");
        textViewHotelRoomFinalPrice.setText("Final Price: " + hotelRoomModel.getPrice() + "$");

        couponReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<CouponModel_Tri> couponModels = new ArrayList<>();

                for (DataSnapshot snapshotRoom : snapshot.getChildren()) {
                    CouponModel_Tri currentCoupon = snapshotRoom.getValue(CouponModel_Tri.class);

                    if (currentCoupon != null) {
                        if (currentCoupon.getClaimedUserIds() == null || currentCoupon.getClaimedUserIds().contains(userSession.getUserId())) {
                            couponModels.add(currentCoupon);
                        }
                    }

                    couponAdapter.setCoupons(couponModels);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        CouponModel_Tri currentModel = couponAdapter.coupons.get(position);
        if (currentModel != null) {
            if (selectedCoupon == null) {
                selectedCoupon = currentModel;
                textViewSelectCoupon.setText("Selected coupon: " + selectedCoupon.getTitle());
            } else if (currentModel == selectedCoupon) {
                selectedCoupon = null;
                textViewSelectCoupon.setText("Select coupon");
            } else {
                selectedCoupon = currentModel;
                textViewSelectCoupon.setText("Selected coupon: " + selectedCoupon.getTitle());
            }

            Double finalPrice = hotelRoomModel.getPrice();
            if (selectedCoupon != null) {
                finalPrice = finalPrice - (finalPrice * Integer.parseInt(selectedCoupon.getValue()) / 100);
            }

            textViewHotelRoomFinalPrice.setText("Final Price: " + finalPrice + "$");
        }
    }
}