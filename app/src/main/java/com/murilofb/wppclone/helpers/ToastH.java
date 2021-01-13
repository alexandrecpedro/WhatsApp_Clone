package com.murilofb.wppclone.helpers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ToastH {
    private Toast toast;
    Activity activity;

    public ToastH(Activity activity) {
        toast = new Toast(activity.getApplicationContext());
        this.activity = activity;
    }

    public ToastH(AppCompatActivity activity) {
        toast = new Toast(activity.getApplicationContext());
        this.activity = activity;
    }

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
