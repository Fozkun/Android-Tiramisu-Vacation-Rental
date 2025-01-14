package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;
import com.rmit.android_tiramisu_vacation_rental.models.Message;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.adapters.ChatAdapter;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText inputMessage;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private FirebaseDatabase database;
    private DatabaseReference chatReference;

    private String chatId;
    private String userId;
    private String providerId;

    private UserSession_Tri userSessionTri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userSessionTri = UserSession_Tri.getInstance();

        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        database = FirebaseDatabase.getInstance();
        chatReference = database.getReference("chats");

        providerId = getIntent().getStringExtra("providerId");
        userId = userSessionTri.getUserId();
        Log.d("ChatActivity", "UserId: " + userId);

        chatId = providerId + "_" + userId;

        chatAdapter = new ChatAdapter(new ArrayList<>());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        loadChatMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadChatMessages() {
        chatReference.child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    messages.add(message);
                }
                chatAdapter.updateMessages(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to load messages", error.toException());
            }
        });
    }

    private void sendMessage() {
        String content = inputMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            String messageId = chatReference.child(chatId).child("messages").push().getKey();
            Message message = new Message(userId ,content, System.currentTimeMillis());

            assert messageId != null;
            chatReference.child(chatId).child("messages").child(messageId).setValue(message);

            inputMessage.setText(""); // Clear input field
        }
    }
}

