package com.rmit.android_tiramisu_vacation_rental;

import android.os.Bundle;

import com.rmit.android_tiramisu_vacation_rental.adapters.ChatBoxAdapter;
import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewChatBoxInterface;
import com.rmit.android_tiramisu_vacation_rental.models.ChatBoxModel;
import com.rmit.android_tiramisu_vacation_rental.models.Message;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class ChatActivity extends AppCompatActivity implements RecyclerViewChatBoxInterface {
    private final String TAG = "ChatActivity";
    private UserSession_Tri userSessionTri;

    private LinearLayout layoutMessageInput;
    private EditText inputMessage;
    private Button sendButton;
    private RecyclerView recyclerViewChatSuperUser, recyclerViewChatUser;

    private ChatAdapter chatAdapter;
    private ChatBoxAdapter chatBoxAdapter;

    private DatabaseReference chatReference;

    private ChatBoxModel chatBoxModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userSessionTri = UserSession_Tri.getInstance();
        if (!userSessionTri.hasSession()) {
            Log.d(TAG, "No user session");
            finish();
            return;
        }

        // Find view by id
        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        layoutMessageInput = findViewById(R.id.messageInputLayout);
        recyclerViewChatUser = findViewById(R.id.recyclerViewChatUser);
        recyclerViewChatSuperUser = findViewById(R.id.recyclerViewChatSuperUser);

        // Define database
        chatReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.CHATS);

        if (userSessionTri.getUserRole() == UserRole.SUPER_USER) {
            recyclerViewChatSuperUser.setVisibility(View.VISIBLE);
            chatBoxAdapter = new ChatBoxAdapter(new ArrayList<>(), ChatActivity.this);
            recyclerViewChatSuperUser.setLayoutManager(new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, false));
            recyclerViewChatSuperUser.setAdapter(chatBoxAdapter);

            chatReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<ChatBoxModel> chatBoxModels = new ArrayList<>();

                    for (DataSnapshot chatBoxSnapshot : snapshot.getChildren()) {
                        ChatBoxModel currentModel = chatBoxSnapshot.getValue(ChatBoxModel.class);

                        if (currentModel != null) {
                            chatBoxModels.add(currentModel);
                        }
                    }

                    chatBoxAdapter.updateData(chatBoxModels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            recyclerViewChatUser.setVisibility(View.VISIBLE);
            layoutMessageInput.setVisibility(View.VISIBLE);
            chatAdapter = new ChatAdapter(new ArrayList<>());
            recyclerViewChatUser.setLayoutManager(new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, false));
            recyclerViewChatUser.setAdapter(chatAdapter);

            chatReference.orderByChild("userId").equalTo(userSessionTri.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ChatBoxModel currentModel = null;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        currentModel = dataSnapshot.getValue(ChatBoxModel.class);
                        break;
                    }

                    if (currentModel != null) {
                        chatBoxModel = currentModel;
                        chatAdapter.updateMessages(chatBoxModel.getMessages());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sendButton.setOnClickListener(v -> {
                ArrayList<Message> messages = new ArrayList<>();

                ChatBoxModel modelToSave;
                if (chatBoxModel == null) {
                    modelToSave = new ChatBoxModel();
                    String key = chatReference.push().getKey();
                    if (key != null) {
                        modelToSave.setId(key);
                        modelToSave.setUserId(userSessionTri.getUserId());
                        modelToSave.setMessages(new ArrayList<>());
                    }
                } else {
                    modelToSave = chatBoxModel;
                }

                if (modelToSave.getId() == null) {
                    return;
                }

                if (modelToSave.getMessages() != null) {
                    messages = modelToSave.getMessages();
                }

                messages.add(new Message(userSessionTri.getUserId(), inputMessage.getText().toString(), System.currentTimeMillis()));
                modelToSave.setMessages(messages);

                chatReference.child(modelToSave.getId()).setValue(modelToSave).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatAdapter.updateMessages(modelToSave.getMessages());
                        chatBoxModel = modelToSave;
                    }
                });
            });
        }
    }

    @Override
    public void onItemClick(int position) {
    }
}

