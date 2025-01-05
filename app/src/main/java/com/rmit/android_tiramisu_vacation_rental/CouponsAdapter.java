package com.rmit.android_tiramisu_vacation_rental;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {
    private final ArrayList<String> coupons;
    public CouponsAdapter(ArrayList<String> coupons){
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.couponTitle.setText(coupons.get(position));
        holder.claimButton.setOnClickListener(view -> Toast.makeText(view.getContext(), "Coupons Claimed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView couponTitle;
        final TextView claimButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            couponTitle = itemView.findViewById(R.id.couponTitle);
            claimButton = itemView.findViewById(R.id.claimButton);
        }
    }
}