package com.murilofb.wppclone.models;

import java.sql.Timestamp;

public class MessageModel {
    String message;
    String photoUrl;
    boolean sent;
    long messageTime;

    public MessageModel(String message) {
        this.message = message;
        sent = true;
        messageTime = new Timestamp(System.currentTimeMillis()).getTime();
    }

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
}

