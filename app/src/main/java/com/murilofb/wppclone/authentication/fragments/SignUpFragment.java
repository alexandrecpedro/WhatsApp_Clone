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
import androidx.fragment.app.Fragment;


import com.murilofb.wppclone.R;
import com.murilofb.wppclone.authentication.AuthActivity;
import com.murilofb.wppclone.helpers.Base64H;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.ToastH;
import com.murilofb.wppclone.models.UserModel;

public class SignUpFragment extends Fragment {
    private Button btnSignup;
    private EditText edtSignUpPassword;
    private EditText edtSignUpEmail;
    private EditText edtSignUpName;
    private FirebaseH.Auth auth;
    private ToastH toast;

    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        initComponents(view);
        setClickListener();
        return view;
    }

    private void initComponents(View view) {
        btnSignup = view.findViewById(R.id.btnSignup);
        edtSignUpEmail = view.findViewById(R.id.edtSignUpEmail);
        edtSignUpName = view.findViewById(R.id.edtSignUpName);
        edtSignUpPassword = view.findViewById(R.id.edtSignUpPassword);

        toast = new ToastH(getActivity());
        FirebaseH firebaseH = new FirebaseH();
        AuthActivity activity = (AuthActivity) getActivity();
        firebaseH.addObserver(activity);
        auth = firebaseH.new Auth(getActivity());
    }

    private void setClickListener() {
        View.OnClickListener clickListener = v -> {
            if (v.getId() == R.id.btnSignup) {
                if (validateEditText()) {
                    Log.i("SignUp", "notEmpty");
                    String signUpName = edtSignUpName.getText().toString();
                    String signUpEmail = edtSignUpEmail.getText().toString();
                    String signUpPassword = edtSignUpPassword.getText().toString();

                    UserModel model = new UserModel(signUpName, signUpEmail, Base64H.encode(signUpPassword));
                    auth.signUp(model);
                } else {
                    toast.showToast(getString(R.string.edt_invalid));
                }
            }
        };
        btnSignup.setOnClickListener(clickListener);
    }

    private boolean validateEditText() {
        String signUpPassword = edtSignUpPassword.getText().toString();
        String signUpName = edtSignUpName.getText().toString();
        String signUpEmail = edtSignUpEmail.getText().toString();

        return !signUpPassword.isEmpty() && !signUpName.isEmpty() && !signUpEmail.isEmpty();
    }

}
