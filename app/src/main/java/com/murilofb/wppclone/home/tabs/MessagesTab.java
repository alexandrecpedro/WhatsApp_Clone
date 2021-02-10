package com.murilofb.wppclone.home.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessagesTab extends Fragment implements Observer {
    private static RecyclerView recyclerMessages;
    private static ContactsAdapter adapter;
    private static MessagesH messagesH;
    private static Activity activity;
    int changedPosition;

    public MessagesTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        messagesH = new MessagesH(this);
        messagesH.loadLastChats();
        activity = getActivity();
        configRecycler();
        return view;
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_LAST_MESSAGES)) {
            if (adapter.getItemCount() == 0){
                adapter.notifyDataSetChanged();
            }else {
                adapter.notifyItemRangeChanged(0, adapter.getItemCount() );
            }

        }
    }


    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(manager);

        final ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                if (messagesH.getFriendsChat() != null || messagesH.getFriendsChat().size() > 0) {
                    UserModel friend = messagesH.getFriendsChat().get(position);
                    Intent i = new Intent(activity, ChatActivity.class);
                    i.putExtra("friend", friend);
                    startActivity(i);
                }
            }
        };

        adapter = new ContactsAdapter(messagesH.getFriendsChat(), recyclerClick, true);
        recyclerMessages.setAdapter(adapter);
    }


    public void queryMessages(String string) {
        List<UserModel> queriedMessages = messagesH.queryMessages(string);

        final ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                changedPosition = position;
                UserModel friend = queriedMessages.get(position);
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("friend", friend);
                startActivity(i);
            }
        };
        adapter = new ContactsAdapter(queriedMessages, recyclerClick, true);
        adapter.notifyDataSetChanged();
        recyclerMessages.setAdapter(adapter);
    }

    public void showDefaultMessages() {
        final ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                UserModel friend = messagesH.getFriendsChat().get(position);
                Intent i = new Intent(activity, ChatActivity.class);
                i.putExtra("friend", friend);
                startActivity(i);
            }
        };
        adapter = new ContactsAdapter(messagesH.getFriendsChat(), recyclerClick, true);
        recyclerMessages.setAdapter(adapter);
    }

    ;
}

