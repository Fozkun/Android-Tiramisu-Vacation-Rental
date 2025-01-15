package com.rmit.android_tiramisu_vacation_rental;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.model_Nghi.Coupon;
import com.rmit.android_tiramisu_vacation_rental.models.CouponModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;

public class MyCouponsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<CouponModel_Tri> couponList;
    private CouponsAdapter couponsAdapter;
    private RecyclerView couponsRecyclerView;
    private LinearLayout navHomepage, navMyTrips, navCoupons, navNotification, navProfile;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);

        navHomepage = findViewById(R.id.homeButton);
        navCoupons = findViewById(R.id.couponsButton);
        navMyTrips = findViewById(R.id.tripsButton);
        navNotification = findViewById(R.id.notificationsButton);
        navProfile = findViewById(R.id.profileButton);


        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.Coupons);
        couponsRecyclerView = findViewById(R.id.couponsRecyclerView);
        couponList = new ArrayList<>();
        couponsAdapter = new CouponsAdapter(couponList);
        couponsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        couponsRecyclerView.setAdapter(couponsAdapter);

        loadCoupons();
        // Bottom Navigation Bar
        navHomepage.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, HomepageActivity.class);
        });
        navMyTrips.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyTripsActivity.class);
        });
        navCoupons.setOnClickListener(v -> {
        });
        navNotification.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, NotificationActivity.class);
        });
        navProfile.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, Profile.class);
        });
    }

    private void loadCoupons() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                couponList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CouponModel_Tri couponModelTri = snapshot.getValue(CouponModel_Tri.class);
                    if (!couponModelTri.isClaim(UserSession_Tri.getInstance().getUserId())){
                        couponList.add(couponModelTri);
                    }
                }
                couponsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyCouponsActivity.this, "Failed to load coupons.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}