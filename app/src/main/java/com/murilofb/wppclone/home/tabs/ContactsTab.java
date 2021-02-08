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
import com.murilofb.wppclone.create_group.CreateGroupAct;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.chat.ChatActivity;
import com.murilofb.wppclone.helpers.FirebaseH;
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
    private List<UserModel> friendList = new ArrayList<>();

    public ContactsTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        FirebaseH.RealtimeDatabase database = firebaseH.new RealtimeDatabase(getActivity());
        database.loadFriendsList(friendList);
        contactsH = new ContactsH(getActivity());
        fabAddFriend = view.findViewById(R.id.fabAddFriend);
        fabAddFriend.setOnClickListener(v -> contactsH.addFriend());
        ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                UserModel friend = friendList.get(position);
                if (friend.getEmail().equals("")) {
                    startActivity(new Intent(getActivity(), CreateGroupAct.class));
                } else {
                    Intent i = new Intent(getContext(), ChatActivity.class);
                    i.putExtra("friend", friend);
                    startActivity(i);
                }

            }
        };
        adapter = new ContactsAdapter(friendList, recyclerClick, false);
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
