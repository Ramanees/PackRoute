package com.example.swiftdelivery;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LiveChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    EditText chatInput;
    TextView tv_LiveChat;
    Button btnSend;
    private LiveChatAdapter chatAdapter;
    private List<LiveChat> chatList = new ArrayList<>();
    private DatabaseReference chatReference;
    private String deliveryId, senderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_live_chat);

        chatRecyclerView = findViewById(R.id.RecyclerView_Chat);
        chatInput = findViewById(R.id.chatInput);
        btnSend = findViewById(R.id.btnSendMsg);
        tv_LiveChat = findViewById(R.id.tv_LiveChat);

        deliveryId = getIntent().getStringExtra("DELIVERY_ID");
        chatReference = FirebaseDatabase.getInstance().getReference("Chats").child(deliveryId);
        senderType = getIntent().getStringExtra("SENDER_TYPE");

        int bgColor, txtColor;
        if ("user".equals(senderType)) {
            bgColor = getColor(R.color.bg);
            txtColor = getColor(R.color.white);
        } else if ("agent".equals(senderType)) {
            bgColor = getColor(R.color.bg2);
            txtColor = getColor(R.color.white);
        } else {
            bgColor = getColor(android.R.color.transparent);
            txtColor = getColor(R.color.black);
        }

        tv_LiveChat.setTextColor(bgColor);
        btnSend.setBackgroundColor(bgColor);
        btnSend.setTextColor(txtColor);

        chatAdapter = new LiveChatAdapter(chatList, senderType);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.addItemDecoration(new ChatItemDecoration());

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages()
    {
        chatReference.child("Messages").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Log.d("FirebaseData", "Snapshot: " + dataSnapshot.toString());
                    LiveChat chat = dataSnapshot.getValue(LiveChat.class);
                    if (chat != null) {
                        chatList.add(chat);
                    } else {
                        Log.e("LiveChatError", "Chat message found to be Null");
                    }
                }
                Log.d("ChatList", "Loaded " + chatList.size() + " messages.");
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatList.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error loading chats: " + error.getMessage());
                Toast.makeText(LiveChatActivity.this, "Failed to load chats.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage()
    {
        String message = chatInput.getText().toString();
        if (!message.isEmpty())
        {
            String messageId = chatReference.child("Messages").push().getKey();
            LiveChat chat = new LiveChat(senderType, message, System.currentTimeMillis());

            chatReference.child("Messages").child(messageId).setValue(chat).addOnSuccessListener(aVoid -> chatInput.setText("")).addOnFailureListener(e -> Toast.makeText(LiveChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show());
        }
        else
        {
            Toast.makeText(this, "Please type your message", Toast.LENGTH_SHORT).show();
        }
    }
}