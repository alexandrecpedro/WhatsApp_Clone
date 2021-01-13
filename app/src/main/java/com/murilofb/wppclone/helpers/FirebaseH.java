package com.murilofb.wppclone.helpers;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Observable;

public class FirebaseH extends Observable {

    private void updateChanges(String arg) {
        setChanged();
        notifyObservers(arg);
    }

    public class Auth {
        private final FirebaseAuth auth = FirebaseAuth.getInstance();
        public static final String ARG_AUTH = "auth";

        public Auth() {
        }

        public void login(String email, String password) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("Auth", "LoginSuccess");
                    } else {
                        Log.i("Auth", "LoginFailure");
                    }
                }
            });
            updateChanges(ARG_AUTH);
        }

        public void signUp(String email, String password) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("Auth", "SignUpSuccess");
                    } else {
                        Log.i("Auth", "SignUpFailure");
                    }
                }
            });
            updateChanges(ARG_AUTH);
        }
    }
}

