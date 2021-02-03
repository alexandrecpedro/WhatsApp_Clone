package com.murilofb.wppclone.home.tabs;

import android.util.Log;

import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class MessagesH {
    private static List<UserModel> friendsChat = new ArrayList<>();
    private Observer observer;
    private static FirebaseH.RealtimeDatabase database;

    public MessagesH(Observer observer) {
        this.observer = observer;
    }

    public void loadLastChats() {
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(observer);
        database = firebaseH.new RealtimeDatabase();
        database.loadFriendsLastMsg(friendsChat);
    }

    public List<UserModel> getFriendsChat() {
        return friendsChat;
    }

    public List<UserModel> queryMessages(String str) {
        List<UserModel> queriedList = new ArrayList<>();
        for (UserModel item : friendsChat) {
            if (item.getName().toLowerCase().contains(str)) {
                queriedList.add(item);
            }
        }
        return queriedList;
    }
}
