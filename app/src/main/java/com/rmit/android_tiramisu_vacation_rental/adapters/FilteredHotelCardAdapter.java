package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewHotelCardInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;

import java.util.List;

public class FilteredHotelCardAdapter extends RecyclerView.Adapter<FilteredHotelCardAdapter.HotelCardViewHolder> {
    private final RecyclerViewHotelCardInterface hotelCardInterface;
    private final List<HotelModel_Tri> hotels;

    public FilteredHotelCardAdapter(List<HotelModel_Tri> hotels, RecyclerViewHotelCardInterface hotelCardInterface) {
        this.hotels = hotels;
        this.hotelCardInterface = hotelCardInterface;
    }

    @NonNull
    @Override
    public FilteredHotelCardAdapter.HotelCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homepage_hotel_card, parent, false);

        return new HotelCardViewHolder(view, hotelCardInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredHotelCardAdapter.HotelCardViewHolder holder, int position) {
        HotelModel_Tri model = this.hotels.get(position);

        if (model.getImageUrl() == null) {
            holder.imageViewHotel.setImageResource(R.drawable.homepage_card_bg);
        } else {
            Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).into(holder.imageViewHotel);
        }

        holder.textViewHotelName.setText(model.getName());
        holder.textViewHotelLocation.setText(model.getAddress());

        String maxOccupancyText = "Max occupancy: " + model.getMaxOccupancy();
        holder.textViewHotelMaxOccupancy.setText(maxOccupancyText);
        holder.ratingBarHotelRating.setRating(model.getRating());
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public static class HotelCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewHotel;
        public TextView textViewHotelName, textViewHotelLocation, textViewHotelMaxOccupancy;
        public RatingBar ratingBarHotelRating;

        public HotelCardViewHolder(@NonNull View itemView, RecyclerViewHotelCardInterface hotelCardInterface) {
            super(itemView);

            imageViewHotel = itemView.findViewById(R.id.imageViewHotel);
            textViewHotelName = itemView.findViewById(R.id.textViewHotelName);
            textViewHotelLocation = itemView.findViewById(R.id.textViewHotelLocation);
            textViewHotelMaxOccupancy = itemView.findViewById(R.id.textViewMaxOccupancy);
            ratingBarHotelRating = itemView.findViewById(R.id.ratingBarHotelRating);

            itemView.setOnClickListener(v -> {
                if (hotelCardInterface != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        hotelCardInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}