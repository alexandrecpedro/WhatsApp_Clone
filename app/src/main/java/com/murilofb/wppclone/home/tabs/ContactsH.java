package com.murilofb.wppclone.home.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.murilofb.wppclone.R;
import com.murilofb.wppclone.helpers.FirebaseH;
import com.murilofb.wppclone.helpers.ToastH;

public class ContactsH {
    private Activity activity;
    private ToastH toastH;

    public ContactsH(Activity activity) {
        this.activity = activity;
        toastH = new ToastH(activity);
    }

    public void addFriend() {

        showDialog();
    }

    private void showDialog() {
        View newFriendView = activity.getLayoutInflater().inflate(R.layout.edit_text_simple, null, false);
        EditText edtNewFriend = newFriendView.findViewById(R.id.editText);
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(newFriendView)
                .setTitle(activity.getString(R.string.dialog_title_new_contact))
                .setMessage(activity.getString(R.string.dialog_message_new_contact))
                .setPositiveButton(activity.getString(R.string.dialog_new_contact_positive), null)
                .setNegativeButton(activity.getString(R.string.dialog_new_contact_negative), null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newFriendEmail = edtNewFriend.getText().toString();
                        if (!newFriendEmail.equals("")) {
                            new FirebaseH().new RealtimeDatabase(activity).findUserByEmail(newFriendEmail, alertDialog);

                        } else {
                            toastH.showToast(activity.getString(R.string.toast_new_contact_empty));
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }
}
