package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.models.UserModel_Tri;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private TextView textViewSignin;

    private EditText editTextEmail, editTextUsername, editTextNickname, editTextPassword, editTextConfirmPassword;

    private RadioButton radioButtonUser, radioButtonRentalProvider;

    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        setContentView(R.layout.activity_signup);

        textViewSignin = findViewById(R.id.textViewSignin);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        radioButtonUser = findViewById(R.id.radioButtonUser);
        radioButtonRentalProvider = findViewById(R.id.radioButtonRentalProvider);

        btnSignUp = findViewById(R.id.btnSignUp);

        textViewSignin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String username = editTextUsername.getText().toString().trim();
            String nickname = editTextNickname.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                editTextEmail.setError("Valid email is required");
                editTextEmail.requestFocus();
            } else if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
                editTextUsername.setError("Username is required");
                editTextUsername.requestFocus();
            } else if (TextUtils.isEmpty(nickname)) {
                Toast.makeText(this, "Please enter your nickname", Toast.LENGTH_LONG).show();
                editTextNickname.setError("Nickname is required");
                editTextNickname.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Valid password is required");
                editTextPassword.requestFocus();
            }else if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter your confirm password", Toast.LENGTH_LONG).show();
                editTextConfirmPassword.setError("Confirm password is required");
                editTextConfirmPassword.requestFocus();
            } else if(!password.equals(confirmPassword)){
                Toast.makeText(this, "Your confirm password is not match with the password", Toast.LENGTH_LONG).show();
                editTextConfirmPassword.setError("Valid confirm password is required");
                editTextConfirmPassword.requestFocus();
            } else {
                UserRole userRole;

                if (radioButtonUser.isChecked()) {
                    userRole = UserRole.USER;
                } else {
                    userRole = UserRole.RENTAL_PROVIDER;
                }

                UserModel_Tri userModelTri = new UserModel_Tri();

                userModelTri.username = username;
                userModelTri.userRole = userRole;
                userModelTri.nickname = nickname;

                registerUser(email, password, userModelTri);
            }
        });
    }

    private void registerUser(String email, String password, UserModel_Tri userModelTri) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show();

                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser == null) {
                    Log.d(TAG, "Firebase user is null!");
                }

                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference(FirebaseConstants.REGISTERED_USERS);

                referenceUser.child(firebaseUser.getUid()).setValue(userModelTri).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        //Send verification email
                        firebaseUser.sendEmailVerification();

                        Toast.makeText(this, "Registered successfully. Please verify your email", Toast.LENGTH_LONG).show();

                        //Go to sign-in page when done
                        Intent intent = new Intent(this, SigninActivity.class);

                        //Remove register activity from stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Registered failed. Please try again", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Failed to register user");
                    }
                });
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e) {
                    editTextPassword.setError("Your password is too weak. Please include alphabet, number and even better special characters");
                    editTextPassword.requestFocus();

                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    editTextPassword.setError("Your email is invalid or already in use. Please enter new one");
                    editTextPassword.requestFocus();
                } catch (FirebaseAuthUserCollisionException e) {
                    editTextPassword.setError("User is already registered with this email, please use another email");
                    editTextPassword.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}