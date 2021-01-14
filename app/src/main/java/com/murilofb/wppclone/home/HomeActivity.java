package com.murilofb.wppclone.home;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.TransitionsH;
import com.murilofb.wppclone.helpers.FirebaseH;

import java.util.Observable;
import java.util.Observer;

public class HomeActivity extends AppCompatActivity implements Observer {
    private TransitionsH transitions;
    private FirebaseH.Auth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        transitions = new TransitionsH(this, false);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        auth = firebaseH.new Auth(null);
        auth.listenUserAuthStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuSignOut) {
            auth.signOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.Auth.ARG_SIGN_OUT)) {
            transitions.openAuthentication();
        }
    }

}
