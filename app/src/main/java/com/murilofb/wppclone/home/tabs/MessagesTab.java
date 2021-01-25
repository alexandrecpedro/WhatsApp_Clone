package com.murilofb.wppclone.home.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.MessagesAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessagesTab extends Fragment implements Observer {
    private RecyclerView recyclerMessages;
    private static List<UserModel> friendsList = new ArrayList<>();
    private static MessagesAdapter adapter;

    public MessagesTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        FirebaseH.RealtimeDatabase database = firebaseH.new RealtimeDatabase();
        database.loadFriendsList();
        List<UserModel> userModels = new ArrayList<>();
        userModels.add(new UserModel("murilo", "finwe", "isgvbweir"));
        userModels.add(new UserModel("arthur", "finwe", "isgvbweir"));
        userModels.add(new UserModel("joao", "finwe", "isgvbweir"));
        adapter = new MessagesAdapter(UserModel.getFriendsList());
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        recyclerMessages.setAdapter(adapter);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerMessages.setHasFixedSize(false);

        return view;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_CONTACTS)) {
            adapter.notifyDataSetChanged();
            Log.i("Messages", "update");
        }
    }

    public static void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public static void setFriendsList(List<UserModel> list) {
        friendsList = list;
    }

    public static List<UserModel> getFriendsList() {
        return friendsList;
    }
}

