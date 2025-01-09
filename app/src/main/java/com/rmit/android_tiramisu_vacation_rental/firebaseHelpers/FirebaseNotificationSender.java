package com.rmit.android_tiramisu_vacation_rental.firebaseHelpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotificationSender {
    public static final String TAG = "NotificationSender";
    private final String userFcmToken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/tiramisu-vacation-rental/messages:send";

    public FirebaseNotificationSender(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void sendNotification(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mObj = new JSONObject();

        try{
            JSONObject msgObject = new JSONObject();

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);

            msgObject.put("token", userFcmToken);
            msgObject.put("notification", notificationObject);

            mObj.put("message", msgObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mObj, response ->{
                //Code got response
                Log.d(TAG, response.toString());
            }, error -> {
                //Code got error
                VolleyLog.d(error.toString());
            }){
                @NonNull
                @Override
                public Map<String, String> getHeaders(){
                    FirebaseAccessToken firebaseAccessToken = new FirebaseAccessToken();
                    String key = firebaseAccessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "Bearer " + key);
                    return header;
                };
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
