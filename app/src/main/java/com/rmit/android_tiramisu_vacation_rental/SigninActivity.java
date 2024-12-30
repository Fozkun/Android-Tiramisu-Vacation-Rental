package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SigninActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false; // Track the visibility state of the password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // Link the XML layout

        // Find views by ID
        EditText usernameField = findViewById(R.id.username);
        EditText passwordField = findViewById(R.id.password);
        ImageButton togglePasswordButton = findViewById(R.id.togglePassword);
        Button signInButton = findViewById(R.id.btnSignIn);

        // Toggle Password Visibility
        togglePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    togglePasswordButton.setImageResource(R.drawable.ic_eye); // Change to eye icon
                } else {
                    // Show password
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //togglePasswordButton.setImageResource(R.drawable.ic_eye_off); // Change to eye-off icon
                }
                isPasswordVisible = !isPasswordVisible; // Toggle state
                passwordField.setSelection(passwordField.getText().length()); // Keep cursor at end
            }
        });

        // Handle Sign-In Button Click
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                // Check if fields are empty
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SigninActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform sign-in logic here (e.g., API call)
                    Toast.makeText(SigninActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
