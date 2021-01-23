package com.murilofb.wppclone.authentication;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.murilofb.wppclone.authentication.AuthActivity;
import com.murilofb.wppclone.authentication.fragments.LoginFragment;
import com.murilofb.wppclone.authentication.fragments.SignUpFragment;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.home.HomeActivity;

public class AuthTransitions {
    public static final String TAG_AUTH = "auth";
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private AppCompatActivity activity;


    public AuthTransitions(AppCompatActivity activity, boolean manipulatesFragments) {
        this.activity = activity;
        if (manipulatesFragments) {
            this.manager = activity.getSupportFragmentManager();
            this.transaction = manager.beginTransaction();
            this.transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
        }
    }

    public void openLogin() {
        transaction.replace(R.id.frame_authentication, new LoginFragment(), TAG_AUTH);
        transaction.commit();
    }

    public void openSignUp() {
        transaction.replace(R.id.frame_authentication, new SignUpFragment(), TAG_AUTH);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openHome() {
        activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
        activity.finish();
    }

    public boolean canGoBack() {
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
            return true;
        } else {
            return false;
        }
    }

    public void openAuthentication() {
        Log.i("FirebaseH", "OpenAuth");
            activity.startActivity(new Intent(activity.getApplicationContext(), AuthActivity.class));
            activity.finish();
    }

}
