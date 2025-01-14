package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewCouponInterface;
import com.rmit.android_tiramisu_vacation_rental.models.CouponModel_Tri;

import java.util.ArrayList;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {
    private final RecyclerViewCouponInterface couponInterface;
    public List<CouponModel_Tri> coupons;

    public CouponAdapter(List<CouponModel_Tri> coupons, RecyclerViewCouponInterface recyclerViewCouponInterface) {
        this.coupons = coupons;
        this.couponInterface = recyclerViewCouponInterface;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_claimed_coupon, parent, false);

        return new CouponViewHolder(view, couponInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        CouponModel_Tri model = this.coupons.get(position);
        holder.textViewCouponTitle.setText(model.getTitle());
        holder.textViewCouponDescription.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public void setCoupons(ArrayList<CouponModel_Tri> coupons){
        this.coupons = coupons;
        this.notifyDataSetChanged();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCouponTitle, textViewCouponDescription;
        public CouponViewHolder(@NonNull View itemView, RecyclerViewCouponInterface couponInterface) {
            super(itemView);

            textViewCouponTitle = itemView.findViewById(R.id.textViewCouponTitle);
            textViewCouponDescription = itemView.findViewById(R.id.textViewCouponDescription);

            itemView.setOnClickListener(v -> {
                if (couponInterface != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        couponInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}