package com.example.mingle.roleplapp;

public class User {
    private int use_id;
    private String use_name;
    private String use_email;
    private String use_password;

    public User(int use_id, String use_name, String use_email, String use_password) {
        this.use_id = use_id;
        this.use_name = use_name;
        this.use_email = use_email;
        this.use_password = use_password;
    }

    public int getUse_id() {
        return use_id;
    }

    public void setUse_id(int use_id) {
        this.use_id = use_id;
    }

    public String getUse_name() {
        return use_name;
    }

    public void setUse_name(String use_name) {
        this.use_name = use_name;
    }

    public String getUse_email() {
        return use_email;
    }

    public void setUse_email(String use_email) {
        this.use_email = use_email;
    }

    public String getUse_password() {
        return use_password;
    }

    public void setUse_password(String use_password) {
        this.use_password = use_password;
    }

    @Override
    public String toString() {
        return "User{" +
                "use_id=" + use_id +
                ", use_name='" + use_name + '\'' +
                ", use_email='" + use_email + '\'' +
                ", use_password='" + use_password + '\'' +
                '}';
    }
}
