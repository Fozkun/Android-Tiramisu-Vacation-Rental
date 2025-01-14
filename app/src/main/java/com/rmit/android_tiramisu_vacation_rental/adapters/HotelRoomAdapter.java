package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.BookingConfirmationActivity;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseNotificationSender;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HotelRoomAdapter extends RecyclerView.Adapter<HotelRoomAdapter.HotelRoomCardViewHolder> {
    private UserSession_Tri userSession;
    private ArrayList<HotelRoomModel_Tri> rooms;
    private DatabaseReference roomReference, fmTokenReference;

    public HotelRoomAdapter(ArrayList<HotelRoomModel_Tri> rooms) {
        userSession = UserSession_Tri.getInstance();
        this.rooms = rooms;

        roomReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.HOTEL_ROOMS);
        fmTokenReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.FM_TOKENS);
    }

    @NonNull
    @Override
    public HotelRoomAdapter.HotelRoomCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_room_card, parent, false);

        return new HotelRoomAdapter.HotelRoomCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelRoomAdapter.HotelRoomCardViewHolder holder, int position) {
        HotelRoomModel_Tri model = this.rooms.get(position);

        if (model.getImageUrl() == null) {
            holder.imageViewHotelRoom.setImageResource(R.drawable.homepage_card_bg);
        } else {
            Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).into(holder.imageViewHotelRoom);
        }

        holder.textViewHotelRoomName.setText(model.getName() == null ? "No name" : model.getName());
        holder.textViewHotelRoomDescription.setText(model.getDescription() == null ? "No description" : model.getDescription());

        String priceText = model.getPrice() == null ? "0$" : model.getPrice() + "$";
        holder.textViewHotelRoomPrice.setText("Price: " + priceText);

        String startDateText = model.getStartDate() == null ? "No date" : MyDateUtils.formatDate(model.getStartDate());
        holder.textViewHotelRoomStartDate.setText("Start date: " + startDateText);

        String endDateText = model.getEndDate() == null ? "No date" : MyDateUtils.formatDate(model.getEndDate());
        holder.textViewHotelRoomEndDate.setText("End date: " + endDateText);

        String status = model.getStatus() == HotelRoomStatus.AVAILABLE ? "Available" : "Unavailable";
        holder.textViewHotelRoomStatus.setText(status);
        holder.textViewHotelRoomStatus.setTextColor(model.getStatus() == HotelRoomStatus.AVAILABLE ?
                Color.GREEN : Color.RED);

        //Display views based on role
        if (userSession.getUserRole() == UserRole.RENTAL_PROVIDER || userSession.getUserRole() == UserRole.SUPER_USER) {
            holder.btnShowEditHotelRoomForm.setVisibility(View.VISIBLE);
            holder.btnDeleteHotelRoom.setVisibility(View.VISIBLE);
        } else {
            ArrayList<String> bookedIds = model.getBookedUserIds();

            if (bookedIds == null || !bookedIds.contains(userSession.getUserId())) {
                holder.btnBookHotelRoom.setVisibility(View.VISIBLE);
            }
        }

        if (holder.inputLayoutHotelRoomStartDate.getEditText() != null && holder.inputLayoutHotelRoomEndDate.getEditText() != null) {
            holder.inputLayoutHotelRoomStartDate.getEditText().setOnClickListener(v -> showDateTimePicker(holder.itemView.getContext(), holder.inputLayoutHotelRoomStartDate));
            holder.inputLayoutHotelRoomEndDate.getEditText().setOnClickListener(v -> showDateTimePicker(holder.itemView.getContext(), holder.inputLayoutHotelRoomEndDate));
        }

        holder.btnShowEditHotelRoomForm.setOnClickListener(v -> {
            if (holder.layoutEditHotelRoomForm.getVisibility() == View.VISIBLE) {
                holder.layoutEditHotelRoomForm.setVisibility(View.GONE);
            } else {
                holder.layoutEditHotelRoomForm.setVisibility(View.VISIBLE);
            }
        });

        holder.btnChangeHotelRoomStatus.setText(model.getStatus() == HotelRoomStatus.AVAILABLE ? "Available" : "Unavailable");
        holder.btnChangeHotelRoomStatus.setOnClickListener(v -> {
            if (model.getStatus() == HotelRoomStatus.AVAILABLE) {
                model.setStatus(HotelRoomStatus.UNAVAILABLE);
            } else {
                model.setStatus(HotelRoomStatus.AVAILABLE);
            }

            holder.btnChangeHotelRoomStatus.setText(model.getStatus() == HotelRoomStatus.AVAILABLE ? "Available" : "Unavailable");
        });

        holder.btnSaveHotelRoom.setOnClickListener(v -> {
            StringBuilder builder = new StringBuilder();

            TextInputLayout inputLayoutHotelRoomName, inputLayoutHotelRoomDescription, inputLayoutHotelRoomPrice;

            inputLayoutHotelRoomName = holder.itemView.findViewById(R.id.inputLayoutHotelRoomName);
            inputLayoutHotelRoomDescription = holder.itemView.findViewById(R.id.inputLayoutHotelRoomDescription);
            inputLayoutHotelRoomPrice = holder.itemView.findViewById(R.id.inputLayoutHotelRoomPrice);

            if (inputLayoutHotelRoomName.getEditText() != null) {
                String roomName = inputLayoutHotelRoomName.getEditText().getText().toString();

                if (!TextUtils.isEmpty(roomName)) {
                    builder.append("Name: ").append(model.getName()).append("-> ");
                    model.setName(roomName);
                    builder.append(roomName).append("\n");
                }
            }

            if (inputLayoutHotelRoomDescription.getEditText() != null) {
                String roomDescription = inputLayoutHotelRoomDescription.getEditText().getText().toString();

                if (!TextUtils.isEmpty(roomDescription)) {
                    model.setDescription(roomDescription);
                }
            }

            if (inputLayoutHotelRoomPrice.getEditText() != null) {
                String roomPrice = inputLayoutHotelRoomPrice.getEditText().getText().toString();

                if (!TextUtils.isEmpty(roomPrice)) {
                    try {
                        double formatedRoomPrice = Double.parseDouble(roomPrice);

                        builder.append("Price: ").append(model.getPrice()).append("$").append(" -> ");
                        model.setPrice(formatedRoomPrice);
                        builder.append(model.getPrice()).append("$").append("\n");
                    } catch (NumberFormatException e) {
                        inputLayoutHotelRoomPrice.setError("Valid number is required");
                        inputLayoutHotelRoomPrice.requestFocus();
                    }
                }
            }

            if (holder.inputLayoutHotelRoomStartDate.getEditText() != null) {
                String startDate = holder.inputLayoutHotelRoomStartDate.getEditText().getText().toString();

                if (!TextUtils.isEmpty(startDate)) {
                    Date formatedStartDate = MyDateUtils.parseDate(startDate);

                    if (formatedStartDate != null) {
                        builder.append("Start date: ").append(MyDateUtils.formatDate(model.getStartDate())).append(" -> ");
                        model.setStartDate(formatedStartDate);
                        builder.append(MyDateUtils.formatDate(model.getStartDate())).append("\n");
                    }
                }
            }

            if (holder.inputLayoutHotelRoomEndDate.getEditText() != null) {
                String endDate = holder.inputLayoutHotelRoomEndDate.getEditText().getText().toString();

                if (!TextUtils.isEmpty(endDate)) {
                    Date formatedEndDate = MyDateUtils.parseDate(endDate);

                    if (formatedEndDate != null) {
                        builder.append("End date: ").append(MyDateUtils.formatDate(model.getEndDate())).append(" -> ");
                        model.setEndDate(formatedEndDate);
                        builder.append(MyDateUtils.formatDate(model.getEndDate())).append("\n");
                    }
                }
            }

            roomReference.child(model.getId()).setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    holder.layoutEditHotelRoomForm.setVisibility(View.GONE);

                    fmTokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<String> tokens = new ArrayList<>();

                            for (DataSnapshot dataSnapshotToken : snapshot.getChildren()) {
                                String token = dataSnapshotToken.getValue(String.class);

                                if (token != null) {
                                    tokens.add(token);
                                }
                            }

                            for (String token : tokens) {
                                FirebaseNotificationSender sender = new FirebaseNotificationSender(token, "Hotel room information changed", builder.toString(), holder.itemView.getContext());
                                sender.sendNotification();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        });
        holder.btnDeleteHotelRoom.setOnClickListener(v -> {
            AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
            confirmDialogBuilder.setCancelable(false);

            String title = "Delete room confirmation";
            String description = "Are you sure to delete room?";

            confirmDialogBuilder.setTitle(title);
            confirmDialogBuilder.setMessage(description);

            confirmDialogBuilder.setPositiveButton("Yes", ((dialog, which) -> {
                roomReference.child(model.getId()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fmTokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> tokens = new ArrayList<>();

                                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                                    String token = tokenSnapshot.getValue(String.class);

                                    if (token != null) {
                                        tokens.add(token);
                                    }
                                }

                                StringBuilder builder = new StringBuilder();
                                builder.append("Room name: ").append(model.getName()).append("\n");
                                builder.append("Deleted date:").append(MyDateUtils.formatDate(new Date()));

                                new Thread(() -> {
                                    for (String token : tokens) {
                                        FirebaseNotificationSender sender = new FirebaseNotificationSender(token, "Room has deleted", builder.toString(), holder.itemView.getContext());
                                        sender.sendNotification();
                                    }
                                }).start();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                dialog.dismiss();
            }));

            confirmDialogBuilder.setNegativeButton("No", ((dialog, which) -> {
                dialog.dismiss();
            }));

            confirmDialogBuilder.show();
        });
        holder.btnBookHotelRoom.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookingConfirmationActivity.class);
            intent.putExtra("roomId", model.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    public void setRooms(ArrayList<HotelRoomModel_Tri> newRooms) {
        this.rooms = newRooms;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class HotelRoomCardViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout inputLayoutHotelRoomStartDate, inputLayoutHotelRoomEndDate;
        private LinearLayout layoutEditHotelRoomForm;
        private Button btnBookHotelRoom, btnShowEditHotelRoomForm, btnDeleteHotelRoom, btnSaveHotelRoom, btnChangeHotelRoomStatus;
        private ImageView imageViewHotelRoom;
        private TextView textViewHotelRoomName, textViewHotelRoomDescription, textViewHotelRoomPrice, textViewHotelRoomStartDate, textViewHotelRoomEndDate, textViewHotelRoomStatus;

        public HotelRoomCardViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewHotelRoom = itemView.findViewById(R.id.imageViewHotelRoom);
            textViewHotelRoomName = itemView.findViewById(R.id.textViewHotelRoomName);
            textViewHotelRoomDescription = itemView.findViewById(R.id.textViewHotelRoomDescription);
            textViewHotelRoomPrice = itemView.findViewById(R.id.textViewHotelRoomPrice);
            textViewHotelRoomStartDate = itemView.findViewById(R.id.textViewHotelRoomStartDate);
            textViewHotelRoomEndDate = itemView.findViewById(R.id.textViewHotelRoomEndDate);
            textViewHotelRoomStatus = itemView.findViewById(R.id.textViewHotelRoomStatus);

            layoutEditHotelRoomForm = itemView.findViewById(R.id.layoutEditHotelRoomForm);
            inputLayoutHotelRoomStartDate = itemView.findViewById(R.id.inputLayoutHotelRoomStartDate);
            inputLayoutHotelRoomEndDate = itemView.findViewById(R.id.inputLayoutHotelRoomEndDate);
            btnBookHotelRoom = itemView.findViewById(R.id.btnBookHotelRoom);
            btnShowEditHotelRoomForm = itemView.findViewById(R.id.btnShowEditHotelRoomForm);
            btnDeleteHotelRoom = itemView.findViewById(R.id.btnDeleteHotelRoom);
            btnChangeHotelRoomStatus = itemView.findViewById(R.id.btnChangeHotelRoomStatus);
            btnSaveHotelRoom = itemView.findViewById(R.id.btnSaveHotelRoom);
        }
    }

    private void showDateTimePicker(Context context, TextInputLayout inputLayout) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year1, month1, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            context,
                            (timeView, hourOfDay, minuteOfHour) -> {
                                String selectedDateTime = String.format(Locale.getDefault(), "%02d:%02d %02d-%02d-%04d", hourOfDay, minuteOfHour, dayOfMonth, month1 + 1, year1);

                                if (inputLayout.getEditText() != null) {
                                    inputLayout.getEditText().setText(selectedDateTime);
                                }
                            },
                            hour, minute, true // Use true for 24-hour format
                    );
                    timePickerDialog.show();
                },
                year, month, day);

        datePickerDialog.show();
    }
}