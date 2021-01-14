package com.murilofb.wppclone.authentication.fragments;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.murilofb.wppclone.authentication.AuthActivity;
import com.murilofb.wppclone.helpers.TransitionsH;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.Base64H;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.ToastH;


public class LoginFragment extends Fragment {
    private Button btnOpenSignUp;
    private Button btnLogin;
    private EditText edtLoginEmail;
    private EditText edtLoginPassword;
    private FirebaseH.Auth auth;
    private TransitionsH transaction;
    private ToastH toast;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initComponents(view);
        setClickListener();
        edtLoginEmail.setText("murilo.kta.leo@gmail.com");
        edtLoginPassword.setText("123456");
        return view;
    }

    private void initComponents(View view) {
        btnOpenSignUp = view.findViewById(R.id.btnLoginSignUp);
        btnLogin = view.findViewById(R.id.btnLogin);
        edtLoginEmail = view.findViewById(R.id.edtLoginEmail);
        edtLoginPassword = view.findViewById(R.id.edtLoginPassword);

        toast = new ToastH(getActivity());
        transaction = new TransitionsH((AppCompatActivity) getActivity());
        AuthActivity authActivity = (AuthActivity) getActivity();
        FirebaseH firebaseH = new FirebaseH();
        firebaseH.addObserver(authActivity.getObserver());
        auth = firebaseH.new Auth();
    }

    private boolean validateEditText() {
        String loginEmail = edtLoginEmail.getText().toString();
        String loginPassword = edtLoginPassword.getText().toString();
        return !loginEmail.isEmpty() && !loginPassword.isEmpty();
    }

    private void setClickListener() {
        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.btnLoginSignUp) {
                transaction.openSignUp();
            } else if (v.getId() == R.id.btnLogin) {
                if (validateEditText()) {
                    String loginEmail = edtLoginEmail.getText().toString();
                    String loginPassword = edtLoginPassword.getText().toString();
                    auth.login(loginEmail, Base64H.encode(loginPassword));
                } else {
                    toast.showToast(getString(R.string.edt_invalid));
                }
            }
        };
        btnOpenSignUp.setOnClickListener(clickListener);
        btnLogin.setOnClickListener(clickListener);
    }

}
