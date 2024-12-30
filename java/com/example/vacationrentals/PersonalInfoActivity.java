package com.example.vacationrentals;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;

public class PersonalInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button completeButton = findViewById(R.id.completeButton);

        completeButton.setOnClickListener(v -> {

        });
    }
}