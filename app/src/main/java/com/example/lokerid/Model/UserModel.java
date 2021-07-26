package com.example.lokerid.Model;

public class UserModel {
    public String uid;
    public String profile;
    public String email;
    public String username;
    public String saldo;

    public UserModel() {

    }

    public UserModel(String uid, String profile, String email, String username, String saldo) {
        this.uid = uid;
        this.profile = profile;
        this.email = email;
        this.username = username;
        this.saldo = saldo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
}
