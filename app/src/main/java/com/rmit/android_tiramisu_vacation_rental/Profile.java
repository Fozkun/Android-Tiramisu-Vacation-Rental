package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.models.UserModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

public class Profile extends AppCompatActivity {
    private UserSession_Tri userSession;
    private DatabaseReference userReference;

    private SwitchCompat switchNotification; // Switch for Push Notification
    private FrameLayout detailContainer; // Overlay container for details
    private TextView detailTextView, textViewUsername, textViewNickname; // TextView to display details
    private LinearLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure the XML file is linked correctly

        // Find views
        textViewNickname = findViewById(R.id.textViewNickname);
        textViewUsername = findViewById(R.id.textViewUsername);
        mainContent = findViewById(R.id.mainContent);
        detailContainer = findViewById(R.id.detailContainer);
        detailTextView = findViewById(R.id.detailTextView);

        switchNotification = findViewById(R.id.switchNotification);

        userSession = UserSession_Tri.getInstance();
        if(!userSession.hasSession()){
            return;
        }

        userReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.REGISTERED_USERS);
        userReference.child(userSession.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel_Tri model = snapshot.getValue(UserModel_Tri.class);

                if(model != null){
                    textViewNickname.setText(model.nickname);
                    textViewUsername.setText(String.format("@%s", model.username));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Handle Home button click (assuming homeButton is an ImageView in XML)
        ImageView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> navigateToHome());

        // Handle Coupons button click (assuming couponsButton is an ImageView in XML)
        ImageView couponsButton = findViewById(R.id.couponsButton);
        couponsButton.setOnClickListener(v -> navigateToCoupons());

        // Handle Trips button click (assuming tripsButton is an ImageView in XML)
        ImageView tripsButton = findViewById(R.id.tripsButton);
        tripsButton.setOnClickListener(v -> navigateToTrips());

        // Handle Notifications button click (assuming notificationsButton is an ImageView in XML)
        ImageView notificationsButton = findViewById(R.id.notificationsButton);
        notificationsButton.setOnClickListener(v -> navigateToNotifications());

        // Handle Profile button click (assuming profileButton is an ImageView in XML)
        ImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            // Already on the profile page, do nothing
        });

        // Handle Help & Support click
        LinearLayout helpSupportSection = findViewById(R.id.helpSupportSection);
        helpSupportSection.setOnClickListener(v -> showDetails("Help & Support",
                "If you need assistance, contact us at support@example.com."));

        // Handle About App click
        LinearLayout aboutAppSection = findViewById(R.id.aboutAppSection);
        aboutAppSection.setOnClickListener(v -> showDetails("About App",
                "This app is designed for Tiramisu vacation rentals.\nVersion: 1.0.0"));

        // Handle Close Details
        detailContainer.setOnClickListener(v -> closeDetails());


        // Initialize Push Notification Switch
        switchNotification = findViewById(R.id.switchNotification);

        // Load saved state for notification switch
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPref", MODE_PRIVATE);
        boolean isNotificationEnabled = sharedPreferences.getBoolean("isNotificationEnabled", false);
        switchNotification.setChecked(isNotificationEnabled);

        // Set an OnCheckedChangeListener for the switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save state in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isNotificationEnabled", isChecked);
            editor.apply();

            // Update UI based on switch state
            if (isChecked) {
                // User enabled notifications
                switchNotification.setThumbTintList(getResources().getColorStateList(android.R.color.holo_green_light));
                switchNotification.setTrackTintList(getResources().getColorStateList(android.R.color.holo_green_dark));
                Toast.makeText(Profile.this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                // User disabled notifications
                switchNotification.setThumbTintList(getResources().getColorStateList(android.R.color.darker_gray));
                switchNotification.setTrackTintList(getResources().getColorStateList(R.color.light_gray));
                Toast.makeText(Profile.this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Log Out Button click
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Clear user session or preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            userSession.clearSession();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            auth.addAuthStateListener(firebaseAuth -> {
                if(auth.getCurrentUser() == null){
                    // Navigate back to Sign In
                    Intent intent = new Intent(Profile.this, SigninActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                    startActivity(intent);
                    Toast.makeText(Profile.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }



    /**
     * Show details in the overlay container
     *
     * @param title Title to display
     * @param content Content to display
     */
    private void showDetails(String title, String content) {
        mainContent.setAlpha(0.3f); // Dim main content
        detailTextView.setVisibility(View.VISIBLE);
        detailTextView.setText(title + "\n\n" + content);
        detailContainer.setVisibility(View.VISIBLE); // Show overlay
    }

    private void closeDetails() {
        mainContent.setAlpha(1.0f); // Restore main content
        detailContainer.setVisibility(View.GONE); // Hide overlay
    }

    /**
     * Navigate to Home screen
     */
    private void navigateToHome() {
        // Implement navigation logic to the Home screen
    }

    /**
     * Navigate to Coupons screen
     */
    private void navigateToCoupons() {
        // Implement navigation logic to the Coupons screen
    }

    /**
     * Navigate to Trips screen
     */
    private void navigateToTrips() {
        // Implement navigation logic to the Trips screen
    }

    /**
     * Navigate to Notifications screen
     */
    private void navigateToNotifications() {
        // Implement navigation logic to the Notifications screen
    }
}
