package com.murilofb.wppclone.home.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.chat.ChatActivity;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.util.Observable;
import java.util.Observer;

public class MessagesTab extends Fragment implements Observer {
    private RecyclerView recyclerMessages;
    private ContactsAdapter adapter;
    private MessagesH messagesH;

    public MessagesTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        messagesH = new MessagesH(this);
        messagesH.loadLastChats();
        configRecycler();
        return view;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_LAST_MESSAGES)) {
            adapter.notifyDataSetChanged();
        }
    }

    private void configRecycler() {
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ContactsAdapter(messagesH.getFriendsChat(), new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                UserModel friend = messagesH.getFriendsChat().get(position);
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra("friend", friend);
                startActivity(i);
            }
        });
        recyclerMessages.setAdapter(adapter);
    }
}

