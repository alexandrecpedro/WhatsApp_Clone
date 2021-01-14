package com.murilofb.wppclone.models;

public class UserModel {
    private String name;
    private String password;
    private String email;

    public UserModel(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
