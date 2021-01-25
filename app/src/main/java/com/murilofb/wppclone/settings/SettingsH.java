package com.murilofb.wppclone.settings;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.SecurityH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.models.UserModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SettingsH {
    public static final int REQUEST_GALLERY_IMG = 1;
    public static final int REQUEST_CAMERA_IMG = 2;
    private AppCompatActivity activity;
    private AlertDialog builder;
    private ToastH toastH;

    public SettingsH(AppCompatActivity activity) {
        this.activity = activity;
        builder = new AlertDialog.Builder(activity).create();
        toastH = new ToastH(activity);
    }

    public void showPhotoOptionsDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_new_picture, null, false);
        ImageButton imgBtnRemovePhoto = view.findViewById(R.id.imgBtnRemovePhoto);
        ImageButton imgBtnTakePhoto = view.findViewById(R.id.imgBtnTakePhoto);
        ImageButton imgBtnOpenGallery = view.findViewById(R.id.imgBtnOpenGallery);
        SecurityH.PermissionsH permissionsH = new SecurityH.PermissionsH(activity);

        View.OnClickListener optionsClick = v -> {
            switch (v.getId()) {
                case R.id.imgBtnRemovePhoto:
                    removeProfilePic();
                    builder.dismiss();
                    break;
                case R.id.imgBtnTakePhoto:
                    if (permissionsH.isCameraPermitted()) {
                        pickImageFromCamera();
                    } else {
                        toastH.showToast(activity.getString(R.string.toast_need_camera_permission));
                    }
                    break;
                case R.id.imgBtnOpenGallery:
                    if (permissionsH.isExternalStoragePermitted()) {
                        pickImageFromGallery();
                    } else {
                        toastH.showToast(activity.getString(R.string.toast_need_storage_permission));
                    }
                    break;
            }
        };
        imgBtnRemovePhoto.setOnClickListener(optionsClick);
        imgBtnTakePhoto.setOnClickListener(optionsClick);
        imgBtnOpenGallery.setOnClickListener(optionsClick);
        builder.setTitle(activity.getString(R.string.dialog_settings_title));
        builder.setView(view);
        builder.show();
    }


    public void showEditUserNameDialog() {
        UserModel currentUser = UserModel.getCurrentUser();
        View layoutView = activity.getLayoutInflater().inflate(R.layout.edit_text_simple, null, false);
        EditText edtNewUserName = layoutView.findViewById(R.id.editText);
        if (currentUser != null) {
            edtNewUserName.setText(currentUser.getUserName());
        }
        AlertDialog.Builder dialogUserName = new AlertDialog.Builder(activity);
        dialogUserName.setView(layoutView);
        dialogUserName.setTitle(activity.getString(R.string.dialog_edt_username_title));
        dialogUserName.setPositiveButton(activity.getString(R.string.dialog_edt_username_positive), (dialog, which) -> {
            new FirebaseH().new RealtimeDatabase().changeUserName(edtNewUserName.getText().toString());
            SettingsActivity.updateUserName(edtNewUserName.getText().toString());
        });
        dialogUserName.setNegativeButton(activity.getString(R.string.dialog_edt_username_negative), null);
        dialogUserName.show();
    }

    public void dismissDialog() {
        builder.dismiss();
    }

    private void removeProfilePic() {
        Glide.with(activity).load(R.drawable.default_user_but_round).into((ImageView) activity.findViewById(R.id.editUserImage));
    }

    private void pickImageFromGallery() {
        Intent galleryImg = new Intent(Intent.ACTION_GET_CONTENT);
        galleryImg.setType("image/*");
        try {
            activity.startActivityForResult(galleryImg, REQUEST_GALLERY_IMG);
        } catch (Exception e) {
            toastH.showToast(activity.getString(R.string.error_pick_from_gallery));
        }

    }

    private void pickImageFromCamera() {
        Intent cameraImg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraImg.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createTempFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", photoFile);
                cameraImg.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


                activity.startActivityForResult(cameraImg, REQUEST_CAMERA_IMG);
            }

        } else {
            toastH.showToast(activity.getString(R.string.error_pick_from_camera));
        }

    }

    private static String imagePath = "";

    private File createTempFile() throws IOException {
        //Create an Image File Name
        String timeStamp = new SimpleDateFormat("ddmmyyyy_hhmmss").format(System.currentTimeMillis());
        String imgFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imgFile = File.createTempFile(imgFileName, ".jpeg", storageDir);
        imagePath = imgFile.getPath();
        return imgFile;
    }

    public static String getCameraPath() {
        return imagePath;
    }

}

