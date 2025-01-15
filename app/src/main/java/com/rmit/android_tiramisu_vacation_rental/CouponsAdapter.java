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
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.models.CouponModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;


public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {

    private ArrayList<CouponModel_Tri> coupons;
    CouponModel_Tri couponModelTri = new CouponModel_Tri();
    public CouponsAdapter(ArrayList<CouponModel_Tri> couponModelTriArrayList) {
        this.coupons = couponModelTriArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponModel_Tri couponModelTri = coupons.get(position);
        holder.couponTitle.setText(couponModelTri.getTitle());
        holder.couponDescription.setText(couponModelTri.getDescription());

        holder.claimButton.setOnClickListener(view -> {
            if (!couponModelTri.isClaim(UserSession_Tri.getInstance().getUserId())) {
                claimCoupon(couponModelTri, position, holder);
            } else {
                Toast.makeText(view.getContext(), "Coupon already claimed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void claimCoupon(CouponModel_Tri couponModelTri, int position, RecyclerView.ViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(FirebaseConstants.COUPONS);
        couponModelTri.setClaimed(UserSession_Tri.getInstance().getUserId());
        databaseReference.child(couponModelTri.getId()).setValue(couponModelTri).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                couponModelTri.setClaimed(UserSession_Tri.getInstance().getUserId());
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
            claimButton = itemView.findViewById(R.id.btnClaim);
        }
    }
}
