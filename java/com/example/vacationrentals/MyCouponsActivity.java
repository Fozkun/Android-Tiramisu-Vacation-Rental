package com.example.vacationrentals;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class MyCouponsActivity extends AppCompatActivity {
    private EditText promoCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);

       promoCodeInput = findViewById(R.id.promoCodeInput);
        ImageButton submitPromoCodeButton = findViewById(R.id.submitPromoCodeButton);
        RecyclerView couponsRecyclerView = findViewById(R.id.couponsRecycleView);

       ImageButton backButton = findViewById(R.id.backButton);
       backButton.setOnClickListener(view -> finish());

       submitPromoCodeButton.setOnClickListener(view -> {
           String promoCode = promoCodeInput.getText().toString();
           if (!promoCode.isEmpty()) {
               Toast.makeText(this, "Promo Code Applied: " + promoCode, Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(this, "Please enter a promo code" + promoCode, Toast.LENGTH_SHORT).show();
           }
       });

       ArrayList<String> couponList = new ArrayList<>();
       couponList.add("Coupon 1: Save 10%");
       couponList.add("Coupon 1: Last Minute Deals");

       CouponsAdapter couponsAdapter = new CouponsAdapter(couponList);
       couponsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       couponsRecyclerView.setAdapter(couponsAdapter);

       Button filterAll = findViewById(R.id.filterAll);
       filterAll.setOnClickListener(view -> Toast.makeText(this, "All Coupons", Toast.LENGTH_SHORT).show());
    }
}