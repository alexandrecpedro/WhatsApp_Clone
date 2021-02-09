package com.murilofb.wppclone.create_group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.models.UserModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AddContactsAct extends AppCompatActivity implements Observer {
    private RecyclerView recyclerAdded;
    private RecyclerView recyclerToAdd;
    private FloatingActionButton fabContinue;
    private List<UserModel> addedList = new ArrayList<>();
    private List<UserModel> toAddList = new ArrayList<>();
    private ContactsAdapter addedAdapter;
    private ContactsAdapter toAddAdapter;
    int totalCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.toolbar_create_group_title));
        recyclerAdded = findViewById(R.id.recyclerNewGroupAdded);
        recyclerToAdd = findViewById(R.id.recyclerNewGroupToAdd);
        fabContinue = findViewById(R.id.fabContinue);
        fabContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ConfigGroupAct.class);
                i.putExtra("groupList", (Serializable) addedList);
                startActivity(i);
            }
        });
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
            toAddAdapter.notifyItemRemoved(0);
        }
        totalCount = toAddList.size();
        attSubtitle();
    }

    private void configRecyclers() {
        //Configuring the List of Contatcts in the To addList
        ContactsAdapter.onRecyclerClick recyclerClick = new ContactsAdapter.onRecyclerClick() {
            @Override
            public void onClick(int position) {

                addedList.add(toAddList.get(position));
                addedAdapter.notifyItemInserted(addedList.size() - 1);
                toAddList.remove(position);
                toAddAdapter.notifyItemRemoved(position);
                attSubtitle();
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
                attSubtitle();
            }
        };

        addedAdapter = new ContactsAdapter(addedList, recyclerClick);
        recyclerAdded.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerAdded.setAdapter(addedAdapter);

    }

    private void attSubtitle() {

        int selectedCount = addedList.size();
        String sub1 = getString(R.string.toolbar_create_group_subtitle1);
        String sub2 = getString(R.string.toolbar_create_group_subtitle2);
        String subtitle = selectedCount + " " + sub1 + " " + totalCount + " " + sub2;
        getSupportActionBar().setSubtitle(subtitle);
    }

}
