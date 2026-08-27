package com.eventmanagement.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public User(int userId, String name, String email, String phone, String password, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "User ID   : " + userId +
                "\nName      : " + name +
                "\nEmail     : " + email +
                "\nPhone     : " + phone +
                "\nRole      : " + role +
                "\nStatus    : " + status +
                "\nCreated   : " + createdAt;
    }
}
