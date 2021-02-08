package com.murilofb.wppclone.create_group;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CreateGroupAct extends AppCompatActivity implements Observer {
    private RecyclerView recyclerAdded;
    private RecyclerView recyclerToAdd;
    private List<UserModel> addedList = new ArrayList<>();
    private List<UserModel> toAddList = new ArrayList<>();
    private ContactsAdapter addedAdapter;
    private ContactsAdapter toAddAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerAdded = findViewById(R.id.recyclerNewGroupAdded);
        recyclerToAdd = findViewById(R.id.recyclerNewGroupToAdd);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        FirebaseH.RealtimeDatabase database = firebaseH.new RealtimeDatabase(this);
        database.loadFriendsList(toAddList);
        configRecyclers();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.RealtimeDatabase.ARG_ATT_CONTACTS)) {
            if (toAddList.get(0).getEmail().equals("")) {
                toAddList.remove(0);
            }
            toAddAdapter.notifyDataSetChanged();
        }
    }

    private void configRecyclers() {
        //Configuring the List of Contatcts in the To addList
        ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                addedList.add(toAddList.get(position));
                addedAdapter.notifyItemInserted(toAddList.size() - 1);
                toAddList.remove(position);
                toAddAdapter.notifyItemRemoved(position);
            }
        };
        toAddAdapter = new ContactsAdapter(toAddList, recyclerClick, false);
        recyclerToAdd.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerToAdd.setAdapter(toAddAdapter);

        //Configuring the List of Contatcts in the MiniRecycler
        recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {
                toAddList.add(addedList.get(position));
                toAddAdapter.notifyItemInserted(toAddList.size() - 1);
                addedList.remove(position);
                addedAdapter.notifyItemRemoved(position);
            }
        };

        addedAdapter = new ContactsAdapter(addedList, recyclerClick);
        recyclerAdded.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerAdded.setAdapter(addedAdapter);

    }


}
