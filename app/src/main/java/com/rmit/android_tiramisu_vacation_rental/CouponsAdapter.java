package com.rmit.android_tiramisu_vacation_rental;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rmit.android_tiramisu_vacation_rental.model_Nghi.Coupon;

import java.util.ArrayList;


public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {

    private ArrayList<Coupon> coupons;

    public CouponsAdapter(ArrayList<Coupon> coupons) {
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
        Coupon coupon = coupons.get(position);
        holder.couponTitle.setText(coupon.getTitle());
        holder.couponDescription.setText(coupon.getDescription());

        holder.claimButton.setOnClickListener(view -> {
            if (!coupon.isClaimed()) {
                claimCoupon(coupon, position);
            } else {
                Toast.makeText(view.getContext(), "Coupon already claimed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void claimCoupon(Coupon coupon, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("coupons")
                .child("coupon" + (position + 1));

        databaseReference.child("claimed").setValue(true).addOnCompleteListener(task -> {
            RecyclerView.ViewHolder holder = null;
            if (task.isSuccessful()) {
                coupon.setClaimed(true);
                notifyItemChanged(position);
                Toast.makeText(holder.itemView.getContext(), "Coupon claimed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Failed to claim coupon.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView couponTitle, couponDescription;
        Button claimButton;

        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            couponTitle = itemView.findViewById(R.id.couponTitle);
            couponDescription = itemView.findViewById(R.id.couponDescription);
            claimButton = itemView.findViewById(R.id.claimButton);
        }
    }
}
