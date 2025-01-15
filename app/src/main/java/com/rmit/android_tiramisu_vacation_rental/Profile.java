package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.helpers.BottomNavigationHelper;
import com.rmit.android_tiramisu_vacation_rental.models.UserModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSettings_Tri;

public class Profile extends AppCompatActivity {
    private UserSession_Tri userSession;
    private UserSettings_Tri userSettings;
    private DatabaseReference userReference;
    private DatabaseReference userSettingReference;

    private SwitchCompat switchNotification; // Switch for Push Notification
    private FrameLayout detailContainer; // Overlay container for details
    private TextView detailTextView, textViewUsername, textViewNickname; // TextView to display details
    private LinearLayout mainContent;

    private LinearLayout navHomepage, navCoupons, navNotification, navProfile, navMyTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile); // Ensure the XML file is linked correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        userSettingReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS_SETTINGS);
        userSettingReference.child(userSession.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserSettings_Tri settings = snapshot.getValue(UserSettings_Tri.class);

                if (settings != null) {
                    boolean pushNotificationEnabled = settings.pushNotificationEnabled;
                    switchNotification.setChecked(pushNotificationEnabled);

                    userSettings = settings;
                }else{
                    userSettingReference.child(userSession.getUserId()).setValue(new UserSettings_Tri(userSession.getUserId()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Initialize bottom navigation buttons
        navHomepage = findViewById(R.id.homeButton);
        navCoupons = findViewById(R.id.couponsButton);
        navMyTrips = findViewById(R.id.tripsButton);
        navNotification = findViewById(R.id.notificationsButton);
        navProfile = findViewById(R.id.profileButton);

        // Set click listeners for bottom navigation buttons
        navHomepage.setOnClickListener(v -> BottomNavigationHelper.navigateTo(this, HomepageActivity.class));
        navCoupons.setOnClickListener(v -> BottomNavigationHelper.navigateTo(this, MyCouponsActivity.class));
        navMyTrips.setOnClickListener(v -> BottomNavigationHelper.navigateTo(this, MyTripsActivity.class));
        navNotification.setOnClickListener(v -> BottomNavigationHelper.navigateTo(this, NotificationActivity.class));
        navProfile.setOnClickListener(v -> {
        });

        // Handle Help & Support click
        LinearLayout helpSupportSection = findViewById(R.id.helpSupportSection);
        helpSupportSection.setOnClickListener(v -> showDetails("",
                "Help & Support\n" +
                        "\n" +
                        "Welcome to the Help & Support section! Below are common issues and solutions to ensure a seamless experience with the app.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Login Issues  \n" +
                        "- Ensure your device is connected to the internet.  \n" +
                        "- Double-check that your Gmail credentials are entered correctly.  \n" +
                        "- If the issue persists, update the app or clear the app cache.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Image Upload Fails  \n" +
                        "- Use image formats such as JPEG or PNG.  \n" +
                        "- Verify your internet connection is stable.  \n" +
                        "- Ensure Firebase Storage is configured correctly.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Payment Issues  \n" +
                        "- Make sure the QR code is scanned properly.  \n" +
                        "- Double-check payment details and sufficient balance.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Chat Not Working  \n" +
                        "- Confirm that you are logged in and connected to the internet.  \n" +
                        "- Update the app to ensure the chat feature is functioning properly.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "App Performance \n" +
                        "- Restart your device to refresh system performance.  \n" +
                        "- Clear the app cache or reinstall the app to resolve persistent issues.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Contact Support  \n" +
                        "- Email: fozkun230403@gmail.com  \n" +
                        "- Phone: 0901360668  \n" +
                        "- Live Chat: Use the in-app chat system for immediate assistance.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "Feedback and Suggestions  \n" +
                        "We value your feedback! If you have suggestions to improve the app, please email us at feedback@vacationrentalapp.com.  \n" +
                        "\n" +
                        "---\n" +
                        "\n" +
                        "This version is now clean, spaced evenly, and visually structured for easy reading and integration into your project. Let me know if further refinements are needed!"));

        // Handle About App click
        LinearLayout aboutAppSection = findViewById(R.id.aboutAppSection);
        aboutAppSection.setOnClickListener(v -> showDetails("",

                "About App\n" + "\n" + "\n" +
                        "The Tiramisu Vacation Rental is an Android-base platform designed to Users, Rental Providers and Super Users for a seamless vacation rental experience. Let's book a hotel for travelling "
        ));
        // Handle Close Details
        detailContainer.setOnClickListener(v -> closeDetails());

        // Set an OnCheckedChangeListener for the switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
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

            if(userSettings != null){
                userSettings.pushNotificationEnabled = !userSettings.pushNotificationEnabled;
                userSettingReference.child(userSession.getUserId()).setValue(userSettings);
            }
        });

        // Handle Log Out Button click
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
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
}
