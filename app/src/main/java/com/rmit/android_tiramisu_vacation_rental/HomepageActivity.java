package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel;
import com.rmit.android_tiramisu_vacation_rental.models.Location;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession;

public class HomepageActivity extends AppCompatActivity implements RecyclerViewHotelCardInterface {
    private static final String TAG = "HomepageActivity";

    private UserSession userSession;

    private HotelCardAdapter hotelCardAdapter;
    private Button btnCreateHotel;
    private RecyclerView recyclerViewHotelCard;

    private DatabaseReference hotelReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCreateHotel = findViewById(R.id.btnCreateHotel);
        recyclerViewHotelCard = findViewById(R.id.recyclerViewHotelCard);

        hotelReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.HOTELS);

        userSession = UserSession.getInstance();
        if(userSession.hasSession()){
            UserRole userRole = userSession.getUserRole();

            if(userRole != UserRole.RENTAL_PROVIDER){
                btnCreateHotel.setVisibility(View.VISIBLE);
            }

            recyclerViewHotelCard.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewHotelCard.setItemAnimator(null);

            FirebaseRecyclerOptions<HotelModel> options
                    = new FirebaseRecyclerOptions.Builder<HotelModel>()
                    .setQuery(hotelReference, HotelModel.class)
                    .build();

            hotelCardAdapter = new HotelCardAdapter(options, this);
            recyclerViewHotelCard.setAdapter(hotelCardAdapter);

            hotelCardAdapter.startListening();

            btnCreateHotel.setOnClickListener(v -> {
                HotelModel model = new HotelModel();

                String modelId = hotelReference.push().getKey();
                model.setId(modelId);
                model.setName("A hotel");
                model.setAddress("Address");
                model.setLocation(new Location());
                model.setRating(0);

                hotelReference.child(modelId).setValue(model);
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        HotelModel model = hotelCardAdapter.getItem(position);
        Log.d(TAG, model.toString());
        //Intent intent = new Intent(this, Hotel.class);
        //intent.putExtra("siteId", siteModel.getSiteId());
        //startActivity(intent);
    }
}