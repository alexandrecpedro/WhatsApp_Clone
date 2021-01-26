package com.murilofb.wppclone.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.MessagesAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.MessageModel;
import com.murilofb.wppclone.models.UserModel;


import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements Observer {
    private Toolbar toolbarChat;
    private ImageButton imgBtnSendMessage;
    private boolean isFriendAlready;
    private boolean messageSent;
    private UserModel friend;
    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private FirebaseH.RealtimeDatabase database;
    private EditText edtNewMessage;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        imgBtnSendMessage = findViewById(R.id.imgBtnSendMessage);
        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtNewMessage = findViewById(R.id.edtNewMessage);
        recyclerMessages = findViewById(R.id.recyclerMessages);


        friend = (UserModel) getIntent().getSerializableExtra("friend");
        isFriendAlready = getIntent().getBooleanExtra("isFriendAlready", true);

        setOnCLick();
        configureRecycler();
        edtNewMessage.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                Log.i("Messages", String.valueOf(event.getAction()));
                return false;
            }
        });

        if (friend != null) {
            getSupportActionBar().setTitle("");
            final CircleImageView friendPhoto = findViewById(R.id.imgChatFriendPhoto);
            Glide.with(this)
                    .load(friend.getProfileImgLink())
                    .placeholder(R.drawable.default_user_but_round)
                    .into(friendPhoto);
            final TextView txtChatFriendName = findViewById(R.id.txtChatFriendName);
            txtChatFriendName.setText(friend.getName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFriendAlready) {
            if (messageSent) {
                if (friend != null) {
                    String friendKey = getIntent().getStringExtra("friendKey");
                    new FirebaseH().new RealtimeDatabase().addFriend(friendKey, friend.getEmail());
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.i("Messages", "OnUpdate");
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_MESSAGES)) {
            Log.i("Messages", "OnUpdateIf");
            adapter.notifyDataSetChanged();
            recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void configureRecycler() {
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        database = firebaseH.new RealtimeDatabase();
        database.loadMessages(friend.getUserId());
        adapter = new MessagesAdapter(database.getMessagesList());
        recyclerMessages.setAdapter(adapter);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerMessages.setHasFixedSize(true);

    }

    private void setOnCLick() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                messageSent = true;
            }
        };
        imgBtnSendMessage.setOnClickListener(clickListener);
    }

    private void sendMessage() {
        MessageModel message = new MessageModel(edtNewMessage.getText().toString());
        database.sendMessage(message, friend.getUserId());
    }
}
