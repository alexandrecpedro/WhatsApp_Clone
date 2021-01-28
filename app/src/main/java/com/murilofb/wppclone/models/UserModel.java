package com.murilofb.wppclone.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.murilofb.wppclone.helpers.FirebaseH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UserModel extends Observable implements Serializable {
    private String name;
    private String password;
    private String email;
    private String profileImgLink;
    private String userName;
    private String userId;
    @Exclude
    private static UserModel currentUser;


    public UserModel(String name, String email, String password) {
        this.name = name;
        this.userName = name;
        this.password = password;
        this.email = email;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImgLink() {
        return profileImgLink;
    }

    public static void loadCurrentUser() {
        FirebaseH firebaseH = new FirebaseH();
        FirebaseH.RealtimeDatabase realtimeDatabase = firebaseH.new RealtimeDatabase();
        realtimeDatabase.loadUserInfo();
    }

    @Exclude
    public static UserModel getCurrentUser() {
        return currentUser;
    }

    @Exclude
    public static void setCurrentUser(UserModel currentUser) {
        UserModel.currentUser = currentUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
