package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.enums.HotelRoomStatus;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.util.ArrayList;

public class HotelRoomAdapter extends RecyclerView.Adapter<HotelRoomAdapter.HotelRoomCardViewHolder> {
    private UserSession_Tri userSession;
    private ArrayList<HotelRoomModel_Tri> rooms;

    public HotelRoomAdapter(ArrayList<HotelRoomModel_Tri> rooms) {
        userSession = UserSession_Tri.getInstance();
        this.rooms = rooms;
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
        holder.textViewHotelRoomDescription.setText(model.getDescription() == null ? "No description": model.getDescription());

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

        } else {

        }
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
        }
    }
}