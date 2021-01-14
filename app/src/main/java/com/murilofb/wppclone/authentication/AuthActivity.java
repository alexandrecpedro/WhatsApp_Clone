package com.murilofb.wppclone.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.TransitionsH;

import java.util.Observable;
import java.util.Observer;

public class AuthActivity extends AppCompatActivity implements Observer {
    private TransitionsH transaction;
    private Observer observer = this;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        transaction = new TransitionsH(this);
        transaction.openLogin();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed() {
        if (!transaction.canGoBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals(FirebaseH.Auth.ARG_AUTH)) {
            transaction.openHome();
        }

    }

    public Observer getObserver() {
        return observer;
    }

}