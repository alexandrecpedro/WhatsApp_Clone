package com.murilofb.wppclone.models;

import java.sql.Timestamp;

public class MessageModel {
    String message;
    boolean sent;
    long messageTime;

    public MessageModel(String message) {
        this.message = message;
        sent = true;
        messageTime = new Timestamp(System.currentTimeMillis()).getTime();
    }

    public MessageModel() {
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

    public long getMessageTime() {
        return messageTime;
    }
}

