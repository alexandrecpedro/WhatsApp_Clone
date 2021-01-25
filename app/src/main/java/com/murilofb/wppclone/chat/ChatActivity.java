package com.murilofb.wppclone.chat;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.models.UserModel;


import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbarChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UserModel friend = (UserModel) getIntent().getSerializableExtra("friend");
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
}
