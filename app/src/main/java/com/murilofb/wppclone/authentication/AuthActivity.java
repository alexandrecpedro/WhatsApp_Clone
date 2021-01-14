package com.murilofb.wppclone.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.TransitionsH;

import java.util.Observable;
import java.util.Observer;

public class AuthActivity extends AppCompatActivity implements Observer {
    private TransitionsH transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        transaction = new TransitionsH(this, true);
        FirebaseH firebaseH = new FirebaseH();
        boolean someoneLogged = firebaseH.new Auth(null).isSomeoneLogged();
        if (someoneLogged) {
            transaction.openHome();
        }
        transaction.openLogin();
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


}