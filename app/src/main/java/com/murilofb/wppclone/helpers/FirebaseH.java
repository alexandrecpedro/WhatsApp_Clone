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
import com.murilofb.wppclone.models.GroupModel;
import com.murilofb.wppclone.models.MessageModel;
import com.murilofb.wppclone.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

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
        public static final String ARG_ATT_LAST_MESSAGES = "attLast";

        private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        private final DatabaseReference usersRef = rootRef.child("users");
        private final DatabaseReference currentUserRef = usersRef.child(new Auth(null).getUserUid());
        private final DatabaseReference userDataRef = currentUserRef.child("userData");
        private final DatabaseReference userMessagesRef = currentUserRef.child("messages");
        private final DatabaseReference userLastMessagesRef = currentUserRef.child("lastMessages");
        private final DatabaseReference uploadedImages = currentUserRef.child("uploadedImages");
        private final DatabaseReference userFriendsReference = currentUserRef.child("friends");
        private final DatabaseReference groupsReference = rootRef.child("groups");
        private Activity activity;
        private ToastH toastH;

        public RealtimeDatabase(Activity activity) {
            currentUserRef.keepSynced(true);
            if (activity != null) {
                toastH = new ToastH(activity);
            }
            this.activity = activity;
        }

        public RealtimeDatabase() {
            currentUserRef.keepSynced(true);
        }

        public void loadUserInfo() {
            userDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel.setCurrentUser(snapshot.getValue(UserModel.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        public void loadFriendsList(List<UserModel> friendsList) {
            userFriendsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    friendsList.clear();
                    UserModel group = new UserModel();
                    group.setUserName(activity.getString(R.string.item_new_group));
                    group.setEmail("");
                    friendsList.add(group);
                    for (DataSnapshot item : snapshot.getChildren()) {
                        usersRef.child(item.getKey()).child("userData").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

        public void loadFriendsLastMsg(final List<UserModel> listFriends) {
            Query messagesInOrder = userLastMessagesRef.orderByChild("messageTime");
            messagesInOrder.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listFriends.clear();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String friendID = item.getKey();
                        String lastMsg = "msg";
                        MessageModel messageModel = item.getValue(MessageModel.class);
                        if (messageModel.getMessage() != null) {
                            lastMsg = messageModel.getMessage();
                        }
                        final String finalLastMsg = lastMsg;
                        if (messageModel.isGroup()) {
                            String groupId = messageModel.getGroupModel().getGroupId();
                            groupsReference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    GroupModel groupModel = snapshot.getValue(GroupModel.class);
                                    UserModel userModel = new UserModel();

                                    userModel.setUserName(groupModel.getGroupName());
                                    userModel.setProfileImgLink(groupModel.getIconUrl());
                                    userModel.setName(finalLastMsg);
                                    userModel.setUserId(groupModel.getGroupId());
                                    userModel.setGroupModel(groupModel);

                                    listFriends.add(userModel);
                                    updateChanges(ARG_ATT_LAST_MESSAGES);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            usersRef.child(friendID).child("userData").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    if (userModel != null) {
                                        userModel.setName(finalLastMsg);
                                        listFriends.add(userModel);
                                    }
                                    updateChanges(ARG_ATT_LAST_MESSAGES);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void findUserByEmail(String email, AlertDialog alertDialog) {
            Query emailQuery = usersRef.orderByChild("userData/email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        toastH.showToast("null");
                    } else {

                        for (DataSnapshot item : snapshot.getChildren()) {
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

        public void addFriend(String key, String email) {
            currentUserRef.child("friends").child(key).setValue(email);
        }

        public void sendMessage(MessageModel message, String to) {
            //Adicionando a minha Db como mensagem mandada
            userMessagesRef.child(to).push().setValue(message);
            userLastMessagesRef.child(to).setValue(message);
            message.setSent(false);
            //Adicionando a db do outro usuário como mensagem recebida
            usersRef.child(to)
                    .child("messages")
                    .child(new Auth(null).getUserUid())
                    .push()
                    .setValue(message);
            usersRef.child(to)
                    .child("lastMessages")
                    .child(new Auth(null).getUserUid())
                    .setValue(message);
        }

        public void createGroup(GroupModel group, Bitmap groupIcon) {
            groupsReference.child(group.getGroupId()).setValue(group);
            if (groupIcon != null) {
                new StorageH().uploadGroupImage(groupIcon, group.getGroupId());
            }
            UserModel currentUser = UserModel.getCurrentUser();
            String currentUserName = "";
            if (currentUser != null) {
                currentUserName = currentUser.getUserName();
            }
            String newGroupMsgSample = activity.getString(R.string.new_group_message);

            MessageModel newGroupMessage = new MessageModel(newGroupMsgSample + " " + currentUserName, currentUser.getUserId(), group);

            sendGroupMessage(group.getParticipants(), newGroupMessage, group.getGroupId());

        }

        public void sendGroupMessage(List<UserModel> participants, MessageModel message, String groupKey) {
            for (UserModel user : participants) {
                String userId = user.getUserId();
                usersRef.child(userId).child("messages").child(groupKey).push().setValue(message);
                usersRef.child(userId).child("lastMessages").child(groupKey).setValue(message);
            }
        }

        public void loadMessages(String from, List<MessageModel> messagesList) {
            Query messagesQuery;
            messagesQuery = userMessagesRef.child(from).orderByChild("messageTime");
            messagesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messagesList.clear();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        MessageModel message = item.getValue(MessageModel.class);
                        messagesList.add(message);
                    }
                    updateChanges(ARG_ATT_MESSAGES);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void changeUserName(String newUserName) {
            userDataRef.child("userName").setValue(newUserName);
        }

        protected void putUserData(UserModel model) {
            userDataRef.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                    }
                }
            });
        }

        protected void putProfileImage(String path, String downloadLink) {
            userDataRef.child("profileImgLink").setValue(downloadLink);
            uploadedImages.child("profileImg").setValue(path);
        }
    }

    public class StorageH {
        private final StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        private final StorageReference userProfileRef = rootRef.child("images")
                .child("profilePic")
                .child(new Auth(null).getUserUid() + ".jpeg");

        private final StorageReference groupsRef = rootRef.child("images")
                .child("groups");


        private final StorageReference sentImageRef = rootRef.child("images")
                .child("sent")
                .child(new Auth(null).getUserUid())
                .child(UUID.randomUUID() + ".jpeg");

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
                                new RealtimeDatabase().putProfileImage(userProfileRef.getPath(), task.getResult().toString());
                                bitmap.recycle();
                            }
                        });

                    }
                }
            });


        }

        public void uploadGroupImage(Bitmap bitmap, String groupKey) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
            StorageReference groupIconReference = groupsRef.child(groupKey + ".png");

            groupIconReference.putBytes(baos.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        groupIconReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                new RealtimeDatabase().groupsReference
                                        .child(groupKey)
                                        .child("iconUrl")
                                        .setValue(task.getResult().toString());
                                bitmap.recycle();
                            }
                        });

                    }
                }
            });


        }

        public void sendImage(Bitmap bitmap, String from, String to) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (from.equals("camera")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            } else if (from.equals("gallery")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
            }

            sentImageRef.putBytes(baos.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        sentImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                MessageModel message = new MessageModel();
                                message.setPhotoUrl(task.getResult().toString());
                                message.setMessage("");
                                RealtimeDatabase database = new FirebaseH().new RealtimeDatabase();
                                database.uploadedImages.push().setValue(sentImageRef.getPath());
                                database.sendMessage(message, to);
                                bitmap.recycle();

                            }
                        });

                    }
                }
            });

        }

        public void sendGroupImage(Bitmap bitmap, String from, GroupModel groupModel) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (from.equals("camera")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            } else if (from.equals("gallery")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
            }

            sentImageRef.putBytes(baos.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        sentImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                MessageModel message = new MessageModel();
                                message.setPhotoUrl(task.getResult().toString());
                                message.setIsGroup(true);
                                message.setGroupModel(groupModel);
                                message.setMessage("");
                                message.setSentBy(UserModel.getCurrentUser().getUserId());

                                RealtimeDatabase database = new FirebaseH().new RealtimeDatabase();
                                database.uploadedImages.push().setValue(sentImageRef.getPath());
                                database.sendGroupMessage(groupModel.getParticipants(), message, groupModel.getGroupId());
                                bitmap.recycle();

                            }
                        });

                    }
                }
            });

        }
    }
}

