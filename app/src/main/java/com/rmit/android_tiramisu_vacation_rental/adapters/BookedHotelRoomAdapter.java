package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewBookedHotelRoomInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelRoomModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.util.ArrayList;

public class BookedHotelRoomAdapter extends RecyclerView.Adapter<BookedHotelRoomAdapter.BookedHotelRoomViewHolder> {
    private RecyclerViewBookedHotelRoomInterface bookedHotelRoomInterface;
    private ArrayList<HotelRoomModel_Tri> rooms;

    public BookedHotelRoomAdapter(ArrayList<HotelRoomModel_Tri> rooms, RecyclerViewBookedHotelRoomInterface bookedHotelRoomInterface) {
        this.bookedHotelRoomInterface = bookedHotelRoomInterface;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public BookedHotelRoomAdapter.BookedHotelRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booked_hotel_room_card, parent, false);

        return new BookedHotelRoomAdapter.BookedHotelRoomViewHolder(view, bookedHotelRoomInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedHotelRoomAdapter.BookedHotelRoomViewHolder holder, int position) {
        HotelRoomModel_Tri model = this.rooms.get(position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.homepage_card_bg)
                .error(R.drawable.homepage_card_bg);

        Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).apply(options).into(holder.imageViewHotelRoom);

        holder.textViewHotelRoomName.setText(model.getName() == null ? "No name" : model.getName());
        holder.textViewHotelRoomDescription.setText(model.getDescription() == null ? "No description" : model.getDescription());

        String priceText = model.getPrice() == null ? "0$" : model.getPrice() + "$";
        holder.textViewHotelRoomPrice.setText("Price: " + priceText);

        String startDateText = model.getStartDate() == null ? "No date" : MyDateUtils.formatDate(model.getStartDate());
        holder.textViewHotelRoomStartDate.setText("Start date: " + startDateText);

        String endDateText = model.getEndDate() == null ? "No date" : MyDateUtils.formatDate(model.getEndDate());
        holder.textViewHotelRoomEndDate.setText("End date: " + endDateText);
    }

    public void setRooms(ArrayList<HotelRoomModel_Tri> newRooms) {
        this.rooms = newRooms;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class BookedHotelRoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewHotelRoom;
        private TextView textViewHotelRoomName, textViewHotelRoomDescription, textViewHotelRoomPrice, textViewHotelRoomStartDate, textViewHotelRoomEndDate;

        public BookedHotelRoomViewHolder(@NonNull View itemView, RecyclerViewBookedHotelRoomInterface bookedHotelRoomInterface) {
            super(itemView);

            imageViewHotelRoom = itemView.findViewById(R.id.imageViewHotelRoom);
            textViewHotelRoomName = itemView.findViewById(R.id.textViewHotelRoomName);
            textViewHotelRoomDescription = itemView.findViewById(R.id.textViewHotelRoomDescription);
            textViewHotelRoomPrice = itemView.findViewById(R.id.textViewHotelRoomPrice);
            textViewHotelRoomStartDate = itemView.findViewById(R.id.textViewHotelRoomStartDate);
            textViewHotelRoomEndDate = itemView.findViewById(R.id.textViewHotelRoomEndDate);

            itemView.setOnClickListener(v -> {
                if (bookedHotelRoomInterface != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        bookedHotelRoomInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}