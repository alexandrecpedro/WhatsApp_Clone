package com.murilofb.wppclone.home.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.adapters.ContactsAdapter;

import java.util.Observable;
import java.util.Observer;

public class MessagesTab extends Fragment implements Observer {
    private RecyclerView recyclerMessages;
    private ContactsAdapter adapter;

    public MessagesTab() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        configRecycler();
        return view;
    }


    @Override
    public void update(Observable o, Object arg) {

    }

    private void configRecycler() {
        //adapter = new ContactsAdapter()
        //recyclerMessages.setAdapter(adapter);
    }
}

