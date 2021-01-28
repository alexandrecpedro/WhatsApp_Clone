package com.murilofb.wppclone.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.FileProvider;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.SecurityH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.settings.SettingsH;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ChatH {
    private Activity activity;
    private ToastH toastH;
    private AlertDialog builder;

    public ChatH(Activity activity) {
        this.activity = activity;
        toastH = new ToastH(activity);
        builder = new AlertDialog.Builder(activity).create();
    }

    public void showPhotoOptionsDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_send_photo, null, false);

        ImageButton imgBtnTakePhoto = view.findViewById(R.id.imgBtnTakePhoto);
        ImageButton imgBtnOpenGallery = view.findViewById(R.id.imgBtnOpenGallery);
        SecurityH.PermissionsH permissionsH = new SecurityH.PermissionsH(activity);


        View.OnClickListener optionsClick = v -> {
            switch (v.getId()) {
                case R.id.imgBtnTakePhoto:
                    if (permissionsH.checkCameraPermission()) {
                        pickImageFromCamera();
                    }
                    break;
                case R.id.imgBtnOpenGallery:
                    if (permissionsH.checkCameraPermission()) {
                        pickImageFromGallery();
                    }
                    break;
            }
        };
        imgBtnTakePhoto.setOnClickListener(optionsClick);
        imgBtnOpenGallery.setOnClickListener(optionsClick);
        builder.setTitle(activity.getString(R.string.dialog_settings_title));
        builder.setView(view);
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent galleryImg = new Intent(Intent.ACTION_GET_CONTENT);
        galleryImg.setType("image/*");
        try {
            activity.startActivityForResult(galleryImg, SettingsH.REQUEST_GALLERY_IMG);
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
                activity.startActivityForResult(cameraImg, SettingsH.REQUEST_CAMERA_IMG);
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

    public void dismissDialog() {
        builder.dismiss();
    }
}
