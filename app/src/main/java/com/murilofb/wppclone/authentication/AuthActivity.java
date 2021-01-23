package com.murilofb.wppclone.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.settings.SettingsActivity;

import java.util.Observable;
import java.util.Observer;

public class AuthActivity extends AppCompatActivity implements Observer {
    private AuthTransitions transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        transaction = new AuthTransitions(this, true);
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
        if (FirebaseH.Auth.ARG_LOGIN.equals(arg)) {
            transaction.openHome();
        } else if (FirebaseH.Auth.ARG_SIGNUP.equals(arg)) {
            transaction.openHome();
            Intent i = new Intent(AuthActivity.this, SettingsActivity.class);
            i.putExtra(SettingsActivity.TAG_SIGNUP, true);
            startActivity(i);
        }


    }


}