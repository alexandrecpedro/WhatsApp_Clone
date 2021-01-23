package com.murilofb.wppclone.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SecurityH {

    public static class PermissionsH {
        private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
        private static final String EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
        private static final String[] permissionsStrArr = {CAMERA_PERMISSION, EXTERNAL_STORAGE_PERMISSION};
        private static boolean[] permissionsBoolArr = {false, false};
        public static final int REQUEST_ALL_PERMISSIONS = 1;
        private Activity activity;


        public PermissionsH(Activity activity) {
            this.activity = activity;
        }

        private String[] getRemainingPermissions() {
            String[] remainingPermissions = new String[permissionsStrArr.length];
            int boolPos = 0;
            int remainingPos = 0;
            for (String item : permissionsStrArr) {
                if (ContextCompat.checkSelfPermission(activity, item) == PackageManager.PERMISSION_DENIED) {
                    remainingPermissions[remainingPos] = item;
                    remainingPos++;
                } else {
                    permissionsBoolArr[boolPos] = true;
                }
                boolPos++;
            }
            return remainingPermissions;
        }

        public void getUserPermissions() {
            String[] remaingPerm = getRemainingPermissions();
            if (remaingPerm[0] != null) {
                ActivityCompat.requestPermissions(activity, remaingPerm, REQUEST_ALL_PERMISSIONS);
            }
        }

        public boolean isCameraPermitted() {
            return permissionsBoolArr[0];
        }


        public boolean isExternalStoragePermitted() {
            return permissionsBoolArr[1];
        }


        public void attPermissions(String[] permissionsVerified, int[] requestResults) {
            int i = 0;
            for (String totalPermission : permissionsStrArr) {
                for (String verifiedPermission : permissionsVerified) {
                    if (totalPermission.equals(verifiedPermission)) {
                        if (requestResults[i] == PackageManager.PERMISSION_GRANTED) {
                            permissionsBoolArr[i] = true;
                        }
                    }
                }
                i++;
            }
        }
    }
}