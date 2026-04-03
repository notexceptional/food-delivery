package com.fooddelivery.models;

public abstract class User {

    private String userId;
    private String userName;
    private String password;
    private String email;
    private String phone;

    public User(String userId, String userName, String password, String email, String phone) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
