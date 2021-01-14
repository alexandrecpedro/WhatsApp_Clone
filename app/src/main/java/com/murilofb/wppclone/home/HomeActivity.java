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
    private TransitionsH transactions;
    private FirebaseH.Auth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
        Log.i("firebaseH", "fewfwe");
    }

    private void initComponents() {
        transactions = new TransitionsH(this);
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(this);
        auth = firebaseH.new Auth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("FirebaseH", "ClicouNumItemAi");
        if (item.getItemId() == R.id.menuSignOut) {
            Log.i("FirebaseH", "signoutClicked");
            auth.signOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.i("FirebaseH", "update");
        if (arg.equals(FirebaseH.Auth.ARG_SIGN_OUT)) {
            transactions.openAuthentication();
        }
    }
}
