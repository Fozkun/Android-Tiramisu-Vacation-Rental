package com.rmit.android_tiramisu_vacation_rental;

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
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.Location_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

public class HomepageActivity extends AppCompatActivity implements RecyclerViewHotelCardInterface {
    private static final String TAG = "HomepageActivity";

    private UserSession_Tri userSessionTri;

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

        userSessionTri = UserSession_Tri.getInstance();
        if(userSessionTri.hasSession()){
            UserRole userRole = userSessionTri.getUserRole();

            if(userRole != UserRole.RENTAL_PROVIDER){
                btnCreateHotel.setVisibility(View.VISIBLE);
            }

            recyclerViewHotelCard.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewHotelCard.setItemAnimator(null);

            FirebaseRecyclerOptions<HotelModel_Tri> options
                    = new FirebaseRecyclerOptions.Builder<HotelModel_Tri>()
                    .setQuery(hotelReference, HotelModel_Tri.class)
                    .build();

            hotelCardAdapter = new HotelCardAdapter(options, this);
            recyclerViewHotelCard.setAdapter(hotelCardAdapter);

            hotelCardAdapter.startListening();

            btnCreateHotel.setOnClickListener(v -> {
                HotelModel_Tri model = new HotelModel_Tri();

                String modelId = hotelReference.push().getKey();
                model.setId(modelId);
                model.setName("A hotel");
                model.setAddress("Address");
                model.setLocation(new Location_Tri());
                model.setRating(0);

                hotelReference.child(modelId).setValue(model);
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        HotelModel_Tri model = hotelCardAdapter.getItem(position);
        Log.d(TAG, model.toString());
        //Intent intent = new Intent(this, Hotel.class);
        //intent.putExtra("siteId", siteModel.getSiteId());
        //startActivity(intent);
    }
}