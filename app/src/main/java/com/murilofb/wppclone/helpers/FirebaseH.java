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
        private final FirebaseAuth.AuthStateListener signOutListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Log.i("FirebaseH", "user null");
                updateChanges(ARG_SIGN_OUT);
                removeListener();
            }
        };

        public static final String ARG_AUTH = "auth";
        public static final String ARG_SIGN_OUT = "authOut";

        public Auth() {
        }

        public void signUp(String email, String password) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("Auth", "SignUpSuccess");
                        updateChanges(ARG_AUTH);
                        auth.addAuthStateListener(signOutListener);
                    } else {
                        Log.i("Auth", "SignUpFailure");
                    }
                }
            });

        }


        public void login(String email, String password) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("Auth", "LoginSuccess");
                        updateChanges(ARG_AUTH);
                        auth.addAuthStateListener(signOutListener);
                    } else {
                        Log.i("Auth", "LoginFailure");
                    }
                }
            });
        }

        private void removeListener() {
            auth.removeAuthStateListener(signOutListener);
        }

        public void signOutUser() {
            auth.signOut();
            Log.i("FirebaseH", "SignOutUser");
        }
    }
}

