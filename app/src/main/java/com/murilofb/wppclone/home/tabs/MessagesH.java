package com.murilofb.wppclone.home.tabs;

import android.app.Activity;
import android.util.Log;

import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class MessagesH {
    List<UserModel> friendsChat = new ArrayList<>();
    Observer observer;

    public MessagesH(Observer observer) {
        this.observer = observer;
    }

    public void loadLastChats() {
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver( observer);
        FirebaseH.RealtimeDatabase database = firebaseH.new RealtimeDatabase();
        database.loadFriendsLastMsg(friendsChat);
    }

    public List<UserModel> getFriendsChat() {
        return friendsChat;
    }
}
