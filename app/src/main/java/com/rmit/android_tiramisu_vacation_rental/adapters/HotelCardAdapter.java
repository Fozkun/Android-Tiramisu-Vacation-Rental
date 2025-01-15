package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewHotelCardInterface;
import com.rmit.android_tiramisu_vacation_rental.models.HotelModel_Tri;

import java.util.ArrayList;
import java.util.List;

public class HotelCardAdapter extends FirebaseRecyclerAdapter<HotelModel_Tri, HotelCardAdapter.HotelCardViewHolder> {
    private final RecyclerViewHotelCardInterface hotelCardInterface;
    private List<HotelModel_Tri> filteredHotels = new ArrayList<>();

    public HotelCardAdapter(
            @NonNull FirebaseRecyclerOptions<HotelModel_Tri> options, RecyclerViewHotelCardInterface hotelCardInterface) {
        super(options);
        this.hotelCardInterface = hotelCardInterface;
    }

    @Override
    protected void
    onBindViewHolder(@NonNull HotelCardViewHolder holder,
                     int position, @NonNull HotelModel_Tri model) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.homepage_card_bg)
                .error(R.drawable.homepage_card_bg);

        Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).apply(options).into(holder.imageViewHotel);

        holder.textViewHotelName.setText(model.getName());
        holder.textViewHotelLocation.setText(model.getAddress());

        String maxOccupancyText = "Max occupancy: " + model.getMaxOccupancy();
        holder.textViewHotelMaxOccupancy.setText(maxOccupancyText);
        holder.ratingBarHotelRating.setRating(model.getRating());
    }


    @NonNull
    @Override
    public HotelCardViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homepage_hotel_card, parent, false);

        return new HotelCardViewHolder(view, hotelCardInterface);
    }

    public void updateFilteredHotels(HotelModel_Tri hotel) {
        this.filteredHotels.add(hotel);
        notifyDataSetChanged();
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