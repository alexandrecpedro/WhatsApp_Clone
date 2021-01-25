package com.murilofb.wppclone.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.murilofb.wppclone.helpers.FirebaseH;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UserModel extends Observable {
    private String name;
    private String password;
    private String email;
    private String profileImgLink;
    private String userName;
    @Exclude
    private static UserModel currentUser;
    @Exclude
    private static List<UserModel> friendsList = new ArrayList<>();

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

    @Exclude
    public static List<UserModel> getFriendsList() {
        return friendsList;
    }

    @Exclude
    public static void setFriendsList(List<UserModel> friendsList) {
        UserModel.friendsList = friendsList;
    }
}
