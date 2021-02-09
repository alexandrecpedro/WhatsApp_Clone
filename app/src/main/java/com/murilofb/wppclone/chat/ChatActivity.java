package com.murilofb.wppclone.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.DialogTitle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ChatAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.SecurityH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.models.MessageModel;
import com.murilofb.wppclone.models.UserModel;
import com.murilofb.wppclone.settings.SettingsH;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements Observer {
    private Toolbar toolbarChat;
    private ImageButton imgBtnSendMessage;
    private RecyclerView recyclerMessages;
    private EditText edtNewMessage;
    private ImageButton imgBtnSendPhoto;
    private UserModel friend;
    private ChatAdapter adapter;
    private FirebaseH.RealtimeDatabase database;
    private ToastH toastH;
    //    private boolean isFriendAlready;
    private boolean messageSent;
    private ChatH chatH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        imgBtnSendMessage = findViewById(R.id.imgBtnSendMessage);
        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtNewMessage = findViewById(R.id.edtNewMessage);
        recyclerMessages = findViewById(R.id.recyclerChat);
        imgBtnSendPhoto = findViewById(R.id.imgBtnSendPhoto);
        toastH = new ToastH(this);
        chatH = new ChatH(this);

        friend = (UserModel) getIntent().getSerializableExtra("friend");
        // isFriendAlready = getIntent().getBooleanExtra("isFriendAlready", true);

        setOnCLick();
        configureRecycler();

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
        //if (!isFriendAlready) {
        if (messageSent) {
            if (friend != null) {

                new FirebaseH().new RealtimeDatabase().addFriend(friend.getUserId(), friend.getEmail());
            }
        }
        //}
    }

    boolean loaded = false;

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_MESSAGES)) {
            Log.i("ChatActivity", "update");
            if (!loaded) {
                adapter.notifyItemRangeChanged(0, database.getMessagesList().size() - 1);
                loaded = true;
            } else {
                adapter.notifyItemInserted(database.getMessagesList().size() - 1);
            }
            if (adapter.getItemCount() > 0) {
                recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
            }

        }
    }

    String from = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imageBitmap = null;
            Uri imgUri;
            switch (requestCode) {
                case SettingsH.REQUEST_GALLERY_IMG:
                    imgUri = data.getData();
                    from = "gallery";
                    CropImage.activity(imgUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    break;
                case SettingsH.REQUEST_CAMERA_IMG:
                    imgUri = Uri.fromFile(new File(ChatH.getCameraPath()));
                    from = "camera";
                    CropImage.activity(imgUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    imgUri = result.getUri();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    chatH.dismissDialog();
                    break;
            }

            if (imageBitmap != null) {

                FirebaseH firebaseH = new FirebaseH();
                FirebaseH.StorageH storageH = firebaseH.new StorageH();
                storageH.sendImage(imageBitmap, from, friend.getUserId());

            }

        }
    }

    private void configureRecycler() {
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        database = firebaseH.new RealtimeDatabase();
        database.loadMessages(friend.getUserId());
        adapter = new ChatAdapter(database.getMessagesList());
        recyclerMessages.setAdapter(adapter);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void setOnCLick() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.imgBtnSendPhoto) {
                    new ChatH(ChatActivity.this).showPhotoOptionsDialog();
                } else if (v.getId() == R.id.imgBtnSendMessage) {
                    sendMessage("txt");
                    messageSent = true;
                }

            }
        };
        imgBtnSendMessage.setOnClickListener(clickListener);
        imgBtnSendPhoto.setOnClickListener(clickListener);
    }

    private void sendMessage(String msgType) {
        if (msgType.equals("txt")) {
            String messageStr = edtNewMessage.getText().toString();
            if (messageStr.equals("")) {
                toastH.showToast(getString(R.string.toast_empty_message));
            } else {
                MessageModel message = new MessageModel(messageStr);
                database.sendMessage(message, friend.getUserId());
                edtNewMessage.setText("");
            }
        }
    }


}
