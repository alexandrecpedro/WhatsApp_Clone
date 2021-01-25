package com.murilofb.wppclone.settings;


import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.SecurityH;
import com.murilofb.wppclone.models.UserModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {
    public static final String TAG_SIGNUP = "signupTag";

    private FloatingActionButton fabEditPic;
    private static CircleImageView editUserImage;
    private ImageButton imgBtnEditUserName;
    private static TextView txtUserName;

    private SettingsH settingsH;
    private SecurityH.PermissionsH permissionsH;
    private static boolean isShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        fabEditPic = findViewById(R.id.fabEditPic);
        editUserImage = findViewById(R.id.editUserImage);
        //edtChangeUserNick = findViewById(R.id.edtChangeUserNick);
        imgBtnEditUserName = findViewById(R.id.edtUserNameButton);
        txtUserName = findViewById(R.id.txtSettingsUserName);

        settingsH = new SettingsH(this);
        permissionsH = new SecurityH.PermissionsH(this);

        boolean cameFromSignUp = getIntent().getBooleanExtra(TAG_SIGNUP, false);
        setClickListener();
        if (cameFromSignUp) {
            TextView txtWelcomeTitle = findViewById(R.id.txtWelcomeTitle);
            TextView txtWelcomeMessage = findViewById(R.id.txtWelcomeMessage);
            txtWelcomeTitle.setText(R.string.welcome_title);
            txtWelcomeMessage.setText(R.string.welcome_message);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            UserModel currentUser = UserModel.getCurrentUser();

            if (currentUser != null) {
                txtUserName.setText(currentUser.getUserName());
                Glide.with(SettingsActivity.this)
                        .load(currentUser.getProfileImgLink())
                        .placeholder(R.drawable.default_user_but_round)
                        .into(editUserImage);
            }
        }
        permissionsH.getUserPermissions();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isShow = false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String from = "";

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
                            .setAspectRatio(1, 1)
                            .start(this);
                    break;
                case SettingsH.REQUEST_CAMERA_IMG:
                    imgUri = Uri.fromFile(new File(SettingsH.getCameraPath()));
                    from = "camera";
                    CropImage.activity(imgUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
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
                    settingsH.dismissDialog();
                    break;
            }

            if (imageBitmap != null) {
                Log.i("SettingsH", "nonNUll Bitmap");
                editUserImage.setImageBitmap(imageBitmap);
                FirebaseH firebaseH = new FirebaseH();
                FirebaseH.StorageH storageH = firebaseH.new StorageH();
                storageH.uploadProfileImage(imageBitmap, from);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionsH.REQUEST_ALL_PERMISSIONS) {
            permissionsH.attPermissions(permissions, grantResults);
        }

    }

    private void setClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.edtUserNameButton:
                        settingsH.showEditUserNameDialog();
                        break;
                    case R.id.fabEditPic:
                        settingsH.showPhotoOptionsDialog();
                        break;
                }

            }
        };
        fabEditPic.setOnClickListener(clickListener);
        imgBtnEditUserName.setOnClickListener(clickListener);
    }

    public static void updateUserName(String newUserName) {
        txtUserName.setText(newUserName);
    }

}
