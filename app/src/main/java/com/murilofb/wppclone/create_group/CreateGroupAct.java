package com.murilofb.wppclone.create_group;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.murilofb.wppclone.R;

public class CreateGroupAct extends AppCompatActivity {
    private RecyclerView recyclerAdded;
    private RecyclerView recyclerToAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerAdded = findViewById(R.id.recyclerNewGroupAdded);
        recyclerToAdd = findViewById(R.id.recyclerNewGroupToAdd);
        configRecyclers();
    }

    private void configRecyclers() {

    }
}
