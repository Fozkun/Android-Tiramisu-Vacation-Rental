package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.models.UserModel;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {
    private static final String TAG = "SigninActivity";

    private TextView textViewSignup;
    private EditText editTextEmail, editTextPassword;
    private Button btnSignIn;
    private ImageButton btnTogglePassword;

    private FirebaseAuth authProfile;
    private DatabaseReference usersReference;

    private UserSession userSession;

    private boolean isPasswordVisible = false; // Track the visibility state of the password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin); // Link the XML layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        textViewSignup = findViewById(R.id.textViewSignup);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        usersReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.REGISTERED_USERS);

        userSession = UserSession.getInstance();

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            firebaseUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Successfully reload user");

                    usersReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model != null) {
                                UserRole userRole = model.userRole;

                                if (userSession.hasSession()) {
                                    userSession.clearSession();
                                }

                                userSession.setSession(firebaseUser.getUid(), userRole);
                                redirectToHomepage();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(this, "There was an error, please sign in again", Toast.LENGTH_LONG).show();
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                }
            });


        }

        // Toggle Password Visibility
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye); // Change to eye icon
            } else {
                // Show password
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                //togglePasswordButton.setImageResource(R.drawable.ic_eye_off); // Change to eye-off icon
            }
            isPasswordVisible = !isPasswordVisible; // Toggle state
            editTextPassword.setSelection(editTextPassword.getText().length()); // Keep cursor at end
        });

        // Handle Sign-In Button Click
        btnSignIn.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please re-enter email", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Valid email is required");
                editTextEmail.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
            } else {
                loginUser(email, password);
            }
        });

        textViewSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        });
    }

    private void redirectToHomepage() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String email, String password) {
        this.authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Get user instance
                FirebaseUser firebaseUser = authProfile.getCurrentUser();

                if (firebaseUser == null) {
                    Log.d(TAG, "User is null");
                    return;
                }

                //Check if user verified email
                if (firebaseUser.isEmailVerified()) {
                    Toast.makeText(this, "Successfully logged in", Toast.LENGTH_LONG).show();

                    DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("Registered Users");
                    mBase.child(firebaseUser.getUid()).get().addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            DataSnapshot snapshot = task1.getResult();
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Log.d(TAG, snapshot.toString());

                            if (userModel != null) {
                                userSession.setSession(firebaseUser.getUid(), userModel.userRole);
                                redirectToHomepage();
                            }
                        }
                    });
                } else {
                    firebaseUser.sendEmailVerification();
                    showAlertDialogVerifyEmail();
                }
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    editTextEmail.setError("User does not exist. Please register again");
                    editTextEmail.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    editTextEmail.setError("Invalid credentials");
                    editTextEmail.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialogVerifyEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email is not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Open email app in new window
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
