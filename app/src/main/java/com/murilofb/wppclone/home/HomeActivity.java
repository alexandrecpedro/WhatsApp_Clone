package com.murilofb.wppclone.home;


import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.FirebaseDatabase;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.authentication.AuthTransitions;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.home.tabs.ContactsTab;
import com.murilofb.wppclone.home.tabs.MessagesTab;
import com.murilofb.wppclone.models.UserModel;
import com.murilofb.wppclone.settings.SettingsActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Observable;
import java.util.Observer;


public class HomeActivity extends AppCompatActivity implements Observer {
    private AuthTransitions transitions;
    private FirebaseH.Auth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        transitions = new AuthTransitions(this, false);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        auth = firebaseH.new Auth(null);
        auth.listenUserAuthStatus();
        UserModel.loadCurrentUser();
        configureSmartTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSearch:
                break;
            case R.id.menuSettings:
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
             //   i.putExtra("signup", true);
                startActivity(i);
                break;
            case R.id.menuSignOut:
                auth.signOutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.Auth.ARG_SIGN_OUT)) {
            transitions.openAuthentication();
        }
    }

    private void configureSmartTab() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(getString(R.string.title_messages_frag), MessagesTab.class)
                .add(getString(R.string.title_contacts_frag), ContactsTab.class)
                .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPagerHome);
        viewPager.setAdapter(adapter);


        SmartTabLayout tabLayout = findViewById(R.id.tabLayoutHome);
        tabLayout.setViewPager(viewPager);
    }

}
