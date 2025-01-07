package com.rmit.android_tiramisu_vacation_rental.model_Nghi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationService {
    @POST("sendNotification") // Replace with your actual endpoint
    Call<Void> sendNotification(@Body NotificationRequest notificationRequest);
}
