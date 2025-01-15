package com.rmit.android_tiramisu_vacation_rental.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rmit.android_tiramisu_vacation_rental.R;
import com.rmit.android_tiramisu_vacation_rental.helpers.firebase.FirebaseConstants;
import com.rmit.android_tiramisu_vacation_rental.interfaces.RecyclerViewChatBoxInterface;
import com.rmit.android_tiramisu_vacation_rental.models.ChatBoxModel;
import com.rmit.android_tiramisu_vacation_rental.models.Message;
import com.rmit.android_tiramisu_vacation_rental.models.UserModel_Tri;
import com.rmit.android_tiramisu_vacation_rental.models.UserSession_Tri;

import java.util.ArrayList;

public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.ChatBoxViewHolder> {
    private ChatAdapter chatAdapter;
    private RecyclerViewChatBoxInterface recyclerViewChatBoxInterface;
    public ArrayList<ChatBoxModel> chatBoxModels;
    private DatabaseReference userReference, chatReference;

    public ChatBoxAdapter(ArrayList<ChatBoxModel> chatBoxModels, RecyclerViewChatBoxInterface recyclerViewChatBoxInterface) {
        this.recyclerViewChatBoxInterface = recyclerViewChatBoxInterface;

        userReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.REGISTERED_USERS);
        chatReference = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.CHATS);

        this.chatBoxModels = chatBoxModels;
    }

    @NonNull
    @Override
    public ChatBoxAdapter.ChatBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_box, parent, false);
        return new ChatBoxViewHolder(view, recyclerViewChatBoxInterface);
    }

    public void updateData(ArrayList<ChatBoxModel> newChatBoxModels){
        this.chatBoxModels.clear();
        this.chatBoxModels.addAll(newChatBoxModels);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxViewHolder holder, int position) {
        ChatBoxModel model = chatBoxModels.get(position);

        String userId = model.getUserId();
        if (TextUtils.isEmpty(userId)) {
            holder.textViewUsername.setText("Unknown User");
        } else {
            userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel_Tri userModel = snapshot.getValue(UserModel_Tri.class);
                    if (userModel != null) {
                        holder.textViewUsername.setText(userModel.username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        chatAdapter = new ChatAdapter(model.getMessages());
        holder.recyclerViewMessage.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
        holder.recyclerViewMessage.setAdapter(chatAdapter);

        holder.textViewUsername.setOnClickListener(v -> {
            if(holder.layoutChatContainer.getVisibility() == View.GONE){
                holder.layoutChatContainer.setVisibility(View.VISIBLE);
            }else{
                holder.layoutChatContainer.setVisibility(View.GONE);
            }
        });

        holder.btnSendMessage.setOnClickListener(v -> {
            String senderId = UserSession_Tri.getInstance().getUserId();

            String textInput = holder.editTextInputMessage.getText().toString();

            ArrayList<Message> messages = new ArrayList<>();
            if (model.getMessages() != null) {
                messages = model.getMessages();
            }

            messages.add(new Message(senderId, textInput, System.currentTimeMillis()));
            model.setMessages(messages);
            chatReference.child(model.getId()).setValue(model).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    holder.editTextInputMessage.setText("");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return chatBoxModels.size();
    }

    public static class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutChatContainer;
        TextView textViewUsername;
        RecyclerView recyclerViewMessage;
        EditText editTextInputMessage;
        Button btnSendMessage;

        public ChatBoxViewHolder(@NonNull View itemView, RecyclerViewChatBoxInterface chatBoxInterface) {
            super(itemView);

            layoutChatContainer = itemView.findViewById(R.id.layoutChatContainer);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            recyclerViewMessage = itemView.findViewById(R.id.recyclerViewMessage);
            editTextInputMessage = itemView.findViewById(R.id.editTextInputMessage);
            btnSendMessage = itemView.findViewById(R.id.btnSendMessage);

            itemView.setOnClickListener(v -> {
                if (chatBoxInterface != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        chatBoxInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}
