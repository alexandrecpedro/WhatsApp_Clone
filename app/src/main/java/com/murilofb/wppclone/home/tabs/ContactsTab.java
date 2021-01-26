package com.murilofb.wppclone.home.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.chat.ChatActivity;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ContactsTab extends Fragment implements Observer {
    private RecyclerView recyclerContacts;
    private static ContactsAdapter adapter;
    private ContactsH contactsH;
    private FloatingActionButton fabAddFriend;

    public ContactsTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        FirebaseH.RealtimeDatabase database = firebaseH.new RealtimeDatabase();
        database.loadFriendsList();
        contactsH = new ContactsH(getActivity());
        fabAddFriend = view.findViewById(R.id.fabAddFriend);
        fabAddFriend.setOnClickListener(v -> contactsH.addFriend());

        adapter = new ContactsAdapter(database.getFriendsList(), getContext(), new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                Intent i = new Intent(getContext(), ChatActivity.class);
                i.putExtra("friend", database.getFriendsList().get(position));
                startActivity(i);
            }
        });
        recyclerContacts = view.findViewById(R.id.recyclerContacts);
        recyclerContacts.setAdapter(adapter);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerContacts.setHasFixedSize(false);
        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_CONTACTS)) {
            adapter.notifyDataSetChanged();
        }
    }


}
