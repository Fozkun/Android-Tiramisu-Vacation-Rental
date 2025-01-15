package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.util.Log;
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
import com.rmit.android_tiramisu_vacation_rental.adapters.NotificationCardAdapter;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.models.NotificationModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity"; //Tag use for Logcat

    private UserSession_Tri userSessionTri;

    private ArrayList<NotificationModel_Tri> notifications = new ArrayList<>();

    private DatabaseReference notificationReference;

    private NotificationCardAdapter notificationCardAdapter;
    private RecyclerView recyclerViewNotificationCard;

    // All bottom navigation buttons
    private LinearLayout navHomepage, navMyTrips, navCoupons, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
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
        notificationReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.NOTIFICATIONS);

        //Find view by id
        recyclerViewNotificationCard = findViewById(R.id.recyclerViewNotificationCard);

        //Find all bottom navigation ids
        navHomepage = findViewById(R.id.nav_homepage);
        navCoupons = findViewById(R.id.nav_coupons);
        navMyTrips = findViewById(R.id.nav_myTrips);
        navProfile = findViewById(R.id.nav_profile);

        // Setup click event listener for all bottom buttons
        navHomepage.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, HomepageActivity.class);
        });
        navCoupons.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyCouponsActivity.class);
        });
        navMyTrips.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, MyTripsActivity.class);
        });
        navProfile.setOnClickListener(v -> {
            BottomNavigationHelper.navigateTo(this, Profile.class);
        });

        //Setup recycler view and adapter
        notificationCardAdapter = new NotificationCardAdapter(this.notifications);
        recyclerViewNotificationCard.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewNotificationCard.setAdapter(notificationCardAdapter);

        displayNotifications();
    }

    private void displayNotifications() {
        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<NotificationModel_Tri> sortedModels = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                        NotificationModel_Tri model = notificationSnapshot.getValue(NotificationModel_Tri.class);

                        if (model != null) {
                            sortedModels.add(model);
                        }
                    }
                }

                sortedModels.sort((o1, o2) -> {
                    Date o1Date = o1.getCreationDate();
                    Date o2Date = o2.getCreationDate();
                    if (o1Date == null || o2Date == null) {
                        return 0;
                    } else {
                        return o2Date.compareTo(o1Date);
                    }
                });

                notifications = sortedModels;
                notificationCardAdapter = new NotificationCardAdapter(notifications);
                recyclerViewNotificationCard.setAdapter(notificationCardAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    private void testCreateNotifications(){
        ArrayList<NotificationModel_Tri> notificationsTest = new ArrayList<>();

        notificationsTest.add(new NotificationModel_Tri("1", NotificationTag.WELCOME_OFFER, "Welcome Offer", "Get 20% off your first purchase!", MyDateUtils.parseDate("10:00 19-06-2024")));
        notificationsTest.add(new NotificationModel_Tri("2", NotificationTag.SPECIAL_EVENTS, "New Year's Eve Sale", "Celebrate with up to 50% off on select items!", MyDateUtils.parseDate("09:00 18-07-2024")));
        notificationsTest.add(new NotificationModel_Tri("3", NotificationTag.EXCLUSIVE_DEALS, "Exclusive Deal: Free Shipping", "Get free shipping on orders over $50 today!", MyDateUtils.parseDate("07:20 22-10-2024")));
        notificationsTest.add(new NotificationModel_Tri("4", NotificationTag.WELCOME_OFFER, "Welcome to Our Store!", "Enjoy a special gift with your first order.", MyDateUtils.parseDate("08:15 23-08-2024")));
        notificationsTest.add(new NotificationModel_Tri("5", NotificationTag.SPECIAL_EVENTS, "Black Friday Flash Sale", "Flash sale for 24 hours only, up to 70% off!", MyDateUtils.parseDate("06:30 20-04-2024")));
        notificationsTest.add(new NotificationModel_Tri("6", NotificationTag.EXCLUSIVE_DEALS, "VIP Exclusive Discount", "VIP members enjoy an extra 15% off on all purchases!", MyDateUtils.parseDate("09:15 16-01-2024")));

        for(NotificationModel_Tri model: notificationsTest){
            String key = notificationReference.push().getKey();

            if(key != null){
                notificationReference.child(key).setValue(model);
            }
        }
    }*/
}