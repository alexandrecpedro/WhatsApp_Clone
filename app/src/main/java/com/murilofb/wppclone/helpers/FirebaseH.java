package com.murilofb.wppclone.helpers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.murilofb.wppclone.R;
import com.murilofb.wppclone.chat.ChatActivity;
import com.murilofb.wppclone.models.MessageModel;
import com.murilofb.wppclone.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class FirebaseH extends Observable {


    private void updateChanges(String arg) {
        setChanged();
        notifyObservers(arg);
    }

    public class Auth {
        private FirebaseAuth auth = FirebaseAuth.getInstance();
        private final FirebaseAuth.AuthStateListener signOutListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                updateChanges(ARG_SIGN_OUT);
                removeListener();
            }
        };
        public static final String ARG_LOGIN = "login";
        public static final String ARG_SIGNUP = "signup";
        public static final String ARG_SIGN_OUT = "authOut";
        private ToastH toast;
        private Activity activity;

        public Auth(@Nullable Activity activity) {
            this.activity = activity;
            if (activity != null) {
                toast = new ToastH(activity);
            }
        }

        public String getUserUid() {
            return auth.getCurrentUser().getUid();
        }

        protected String getUserEmail() {
            return auth.getCurrentUser().getEmail();
        }

        public void signUp(UserModel model) {
            auth.createUserWithEmailAndPassword(model.getEmail(), model.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        model.setUserId(auth.getCurrentUser().getUid());
                        new RealtimeDatabase().putUserData(model);
                        updateChanges(ARG_SIGNUP);
                    } else {
                        Log.i("Auth", "SignUpFailure");
                        String message = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            message = activity.getString(R.string.toast_email_collision);
                        } catch (FirebaseAuthWeakPasswordException e) {
                            message = activity.getString(R.string.toast_weak_password);
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            message = activity.getString(R.string.toast_invalid_credentials);
                        } catch (Exception e) {
                            message = activity.getString(R.string.toast_default_signin_error);
                            e.printStackTrace();
                        }
                        toast.showToast(message);
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
                        updateChanges(ARG_LOGIN);
                    } else {
                        String message = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            message = activity.getString(R.string.toast_invalid_credentials);
                        } catch (Exception e) {
                            message = activity.getString(R.string.toast_default_login_error);
                            e.printStackTrace();
                        }
                        toast.showToast(message);
                    }
                }
            });
        }

        public boolean isSomeoneLogged() {
            return auth.getCurrentUser() != null;
        }

        public void listenUserAuthStatus() {
            auth.addAuthStateListener(signOutListener);
        }

        private void removeListener() {
            auth.removeAuthStateListener(signOutListener);
        }

        public void signOutUser() {
            auth.signOut();

        }
    }

    public class RealtimeDatabase {
        public static final String ARG_ATT_CONTACTS = "attCont";
        public static final String ARG_ATT_MESSAGES = "attMsg";
        private List<UserModel> friendsList = new ArrayList<>();
        private List<MessageModel> messagesList = new ArrayList<>();
        private final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        private final DatabaseReference usersReference = rootReference.child("users");
        private final DatabaseReference currentUserReference = usersReference
                .child(new Auth(null).getUserUid());
        private final DatabaseReference userDataReference = currentUserReference.child("userData");
        private final DatabaseReference userMessagesReference = currentUserReference.child("messages");
        private final DatabaseReference uploadedImages = currentUserReference.child("uploadedImages");
        private final DatabaseReference userFriendsReference = currentUserReference.child("friends");
        private Activity activity;
        private ToastH toastH;

        public RealtimeDatabase(Activity activity) {
            if (activity != null) {
                toastH = new ToastH(activity);
            }
            this.activity = activity;
        }

        public RealtimeDatabase() {
        }

        public void loadUserInfo() {
            userDataReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel.setCurrentUser(snapshot.getValue(UserModel.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        public void loadFriendsList() {
            userFriendsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    friendsList.clear();
                    Log.i("friendSearch", snapshot.getKey());
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Log.i("friendSearch", item.getKey());
                        usersReference.child(item.getKey()).child("userData").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.i("friendSearch", snapshot.getKey());
                                friendsList.add(snapshot.getValue(UserModel.class));
                                updateChanges(ARG_ATT_CONTACTS);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void findUserByEmail(String email, AlertDialog alertDialog) {
            Query emailQuery = usersReference.orderByChild("userData/email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        toastH.showToast("null");
                    } else {

                        for (DataSnapshot item : snapshot.getChildren()) {
                            Log.i("friendSearch", item.getKey());
                            String foundEmail = item.child("userData").child("email").getValue().toString();
                            if (foundEmail.equals(new Auth(null).getUserEmail())) {
                                toastH.showToast(activity.getString(R.string.toast_new_contact_same_email));
                            } else {
                                UserModel foundFriend = item.child("userData").getValue(UserModel.class);
                                Intent i = new Intent(activity.getApplicationContext(), ChatActivity.class);
                                i.putExtra("friend", foundFriend);
                                i.putExtra("friendKey", item.getKey());
                                i.putExtra("isFriendAlready", false);
                                activity.startActivity(i);
                                alertDialog.dismiss();
                            }
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public List<UserModel> getFriendsList() {
            return friendsList;
        }

        public void addFriend(String key, String email) {
            currentUserReference.child("friends").child(key).setValue(email);

        }

        public void sendMessage(MessageModel message, String to) {
            //Adicionando a minha Db como mensagem mandada
            userMessagesReference.child(to).push().setValue(message);
            message.setSent(false);
            //Adicionando a db do outro usuário como mensagem recebida
            usersReference.child(to)
                    .child("messages")
                    .child(new Auth(null).getUserUid())
                    .push()
                    .setValue(message);
        }

        public void loadMessages(String from) {
            Query messagesQuery = userMessagesReference.child(from).orderByChild("messageTime");
            messagesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messagesList.clear();
                    Log.i("Messages", snapshot.getKey());
                    for (DataSnapshot item : snapshot.getChildren()) {

                        MessageModel message = item.getValue(MessageModel.class);

                        messagesList.add(message);
                    }
                    updateChanges(ARG_ATT_MESSAGES);
                    Log.i("Messages", "UpdateChanges");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public List<MessageModel> getMessagesList() {
            return messagesList;
        }

        public void changeUserName(String newUserName) {
            userDataReference.child("userName").setValue(newUserName);
        }

        protected void putUserData(UserModel model) {
            userDataReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                    }
                }
            });
        }

        protected void putProfileImage(String path, String downloadLink) {
            userDataReference.child("profileImgLink").setValue(downloadLink);
            uploadedImages.child("profileImg").setValue(path);
        }
    }

    public class StorageH {
        private final StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        private final StorageReference userProfileRef = rootRef.child("images")
                .child("profilePic")
                .child(new Auth(null).getUserUid() + ".jpeg");

        public void uploadProfileImage(Bitmap bitmap, String from) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (from.equals("camera")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            } else if (from.equals("gallery")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
            }

            userProfileRef.putBytes(baos.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        userProfileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                new RealtimeDatabase(null).putProfileImage(userProfileRef.getPath(), task.getResult().toString());
                                bitmap.recycle();
                            }
                        });

                    }
                }
            });


        }

    }
}

