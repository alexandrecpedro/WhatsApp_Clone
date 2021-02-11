package com.murilofb.wppclone.home.tabs;

import android.content.Intent;
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

import com.github.clans.fab.FloatingActionButton;
import com.murilofb.wppclone.create_group.AddContactsAct;
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
                    startActivity(new Intent(getActivity(), AddContactsAct.class));
                } else {
                    Intent i = new Intent(getContext(), ChatActivity.class);
                    i.putExtra("friend", friend);
                    startActivity(i);
                }

            }
        };
        adapter = new ContactsAdapter(friendList, recyclerClick, false);
        //adapter.setHasStableIds(true);
        recyclerContacts = view.findViewById(R.id.recyclerContacts);
        recyclerContacts.setAdapter(adapter);
        recyclerContacts.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));


        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_CONTACTS)) {
            /*
            Log.i("Contacts", "FriendCount: " + friendList.size());
            if (friendList.size() < 3) {
                Log.i("Contacts", "MENOR QUE 3");
                if (friendList.get(0).getEmail().equals("")) {
                    Log.i("Contacts", "Removeu " + friendList.get(0).getName());
                    friendList.remove(0);
                    adapter.notifyItemRemoved(0);
                }
            } else {
                if (!friendList.get(0).getEmail().equals("")){
                    UserModel group = new UserModel();
                    group.setName(getString(R.string.item_new_group));
                    group.setEmail("");
                    friendList.add(0,group);
                }*/
            adapter.notifyDataSetChanged();


        }
    }

    public void queryMessages(String string) {
        List<UserModel> queriedMessages = getQueriedList(string);

        final ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                UserModel friend = queriedMessages.get(position);
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("friend", friend);
                startActivity(i);
            }
        };
        adapter = new ContactsAdapter(queriedMessages, recyclerClick, false);
        adapter.notifyDataSetChanged();
        recyclerContacts.setAdapter(adapter);
    }

    public List<UserModel> getQueriedList(String str) {
        List<UserModel> queriedList = new ArrayList<>();
        for (UserModel item : friendList) {
            String lcName = "";
            String lcUserName = "";
            if (item.getName() != null) {
                lcName = item.getName().toLowerCase();
            }
            if (item.getUserName() != null) {
                lcUserName = item.getUserName().toLowerCase();
            }

            if (lcName.contains(str) || lcUserName.contains(str)) {
                queriedList.add(item);
            }
        }
        return queriedList;
    }

    public void showDefaultMessages() {
        final ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                UserModel friend = friendList.get(position);
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("friend", friend);
                startActivity(i);
            }
        };
        adapter = new ContactsAdapter(friendList, recyclerClick, false);
        recyclerContacts.setAdapter(adapter);
    }

}
