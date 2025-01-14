package com.rmit.android_tiramisu_vacation_rental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.UserListAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView userListRecyclerView;
    private UserListAdapter userListAdapter;
    private DatabaseReference chatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListRecyclerView = findViewById(R.id.userListRecyclerView);
        userListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatReference = FirebaseDatabase.getInstance().getReference("chats");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> userList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String userId = data.child("userId").getValue(String.class);
                    if (userId != null && !userList.contains(userId)) {
                        userList.add(userId);
                    }
                }
                userListAdapter = new UserListAdapter(userList, userId -> {
                    // Handle the click event here, e.g., open ChatActivity
                    Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
                    intent.putExtra("userId", userId); // Pass the selected userId to the ChatActivity
                    startActivity(intent);
                });

                userListRecyclerView.setAdapter(userListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserListActivity", "Failed to load users", error.toException());
            }
        });
    }
}
