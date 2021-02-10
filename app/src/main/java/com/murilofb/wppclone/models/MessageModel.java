package com.murilofb.wppclone.models;

import java.sql.Timestamp;

public class MessageModel {
    private String message;
    private String photoUrl;
    private boolean sent;
    private long messageTime;
    private String sentBy;
    private GroupModel groupModel;
    private boolean group = false;


    //SimpleMessage
    public MessageModel(String message) {
        this.message = message;
        sent = true;
        messageTime = new Timestamp(System.currentTimeMillis()).getTime();
    }

    //GroupMessage
    public MessageModel(String message, String sentId, GroupModel group) {
        this.message = message;
        this.group = true;
        this.sentBy = sentId;
        this.groupModel = group;
        messageTime = new Timestamp(System.currentTimeMillis()).getTime();
    }

    //Photomessage
    public MessageModel() {
        sent = true;
        messageTime = new Timestamp(System.currentTimeMillis()).getTime();
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public GroupModel getGroupModel() {
        return groupModel;
    }

    public void setGroupModel(GroupModel group) {
        this.groupModel = group;
    }

    public boolean isGroup() {
        return group;
    }

    public void setIsGroup(boolean group) {
        this.group = group;
    }
}

