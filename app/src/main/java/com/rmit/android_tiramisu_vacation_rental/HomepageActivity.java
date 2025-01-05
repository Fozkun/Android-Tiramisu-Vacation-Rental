package com.rmit.android_tiramisu_vacation_rental;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Calendar;

public class HomepageActivity extends AppCompatActivity implements RecyclerViewHotelCardInterface {
    private static final String TAG = "HomepageActivity";

    private UserSession_Tri userSessionTri;

    private HotelCardAdapter hotelCardAdapter;
    private Button btnCreateHotel;
    private RecyclerView recyclerViewHotelCard;

    private DatabaseReference hotelReference;

    private EditText editTextWhere, editTextStartDate, editTextEndDate;
    private TextView textViewRoomAdults;
    private Button buttonFind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        editTextWhere = findViewById(R.id.editText1); // "Where you want to go?"
        editTextStartDate = findViewById(R.id.editTextStartDate); // "DD-MM-YYYY" (Start Date)
        editTextEndDate = findViewById(R.id.editText); // "DD-MM-YYYY" (End Date)
        textViewRoomAdults = findViewById(R.id.textView6); // "Room, People"
        buttonFind = findViewById(R.id.button); // "Find"


        editTextStartDate.setOnClickListener(v -> showDatePicker(editTextStartDate));


        editTextEndDate.setOnClickListener(v -> showDatePicker(editTextEndDate));


        textViewRoomAdults.setOnClickListener(v -> showRoomPickerDialog(textViewRoomAdults));


        buttonFind.setOnClickListener(v -> handleFindButton());

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

    private void handleFindButton() {
        String destination = editTextWhere.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String roomDetails = textViewRoomAdults.getText().toString().trim();

        if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search for hotel information in Firebase
        searchHotels(destination, startDate, endDate, roomDetails);
    }

    private void showRoomPickerDialog(TextView roomInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_picker, null);


        NumberPicker roomsPicker = dialogView.findViewById(R.id.roomsPicker);
        NumberPicker adultsPicker = dialogView.findViewById(R.id.adultsPicker);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);


        roomsPicker.setMinValue(1);
        roomsPicker.setMaxValue(10);
        adultsPicker.setMinValue(1);
        adultsPicker.setMaxValue(20);


        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        btnConfirm.setOnClickListener(v -> {
            int selectedRooms = roomsPicker.getValue();
            int selectedAdults = adultsPicker.getValue();


            roomInfo.setText(selectedRooms + " Room(s), " + selectedAdults + " People");


            dialog.dismiss();
        });


        dialog.show();
    }


    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month1 + 1, year1);
                    editText.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void searchHotels(String destination, String startDate, String endDate, String roomDetails) {
        // Use Firebase or search logic
        Toast.makeText(this, "Searching for hotels in " + destination, Toast.LENGTH_SHORT).show();
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