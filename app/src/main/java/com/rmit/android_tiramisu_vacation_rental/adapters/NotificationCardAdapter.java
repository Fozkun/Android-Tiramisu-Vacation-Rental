package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.models.NotificationModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.utils.MyDateUtils;

import java.util.Date;
import java.util.List;

public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.NotificationCardViewHolder> {
    private final List<NotificationModel_Tri> notifications;

    public NotificationCardAdapter(List<NotificationModel_Tri> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationCardAdapter.NotificationCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);

        return new NotificationCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationCardAdapter.NotificationCardViewHolder holder, int position) {
        NotificationModel_Tri model = this.notifications.get(position);

        String tagText = "";
        switch(model.getTag()){
            case WELCOME_OFFER:{
                tagText = "👋 Welcome Offer";
                holder.textViewNotificationTag.setTextColor(Color.GREEN);
                break;
            }
            case SPECIAL_EVENTS: {
                tagText = "🎆 Special Events";
                holder.textViewNotificationTag.setTextColor(Color.BLUE);
                break;
            }
            case EXCLUSIVE_DEALS:{
                tagText = "⏰ Exclusive Deals";
                holder.textViewNotificationTag.setTextColor(Color.RED);
            }
        }
        holder.textViewNotificationTag.setText(tagText);

        holder.textViewNotificationTitle.setText(model.getTitle());
        holder.textViewNotificationDescription.setText(model.getDescription());

        Date creationDate = model.getCreationDate();
        String timestamp;

        if(creationDate != null){
            timestamp = MyDateUtils.formatDate(creationDate);
        }else{
            timestamp = "";
        }

        holder.textViewNotificationTimestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationCardViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNotificationTag, textViewNotificationTitle, textViewNotificationDescription, textViewNotificationTimestamp;

        public NotificationCardViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textViewNotificationTag = itemView.findViewById(R.id.textViewNotificationTag);
            this.textViewNotificationTitle = itemView.findViewById(R.id.textViewNotificationTitle);
            this.textViewNotificationDescription = itemView.findViewById(R.id.textViewNotificationDescription);
            this.textViewNotificationTimestamp = itemView.findViewById(R.id.textViewNotificationTimestamp);
        }
    }
}