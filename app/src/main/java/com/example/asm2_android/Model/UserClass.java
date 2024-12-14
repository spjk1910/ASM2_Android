package com.example.asm2_android.Model;

import java.util.Date;

public class UserClass {
    private String name;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private BloodEnum bloodType;
    private UserRoleEnum role;
    private String profileImage;
    private Date birthday;

    public UserClass(String name, String email, String username, String password, String phoneNumber,
                     String address, BloodEnum bloodType, UserRoleEnum role,String profileImage,Date birthday) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bloodType = bloodType;
        this.role = role;
        this.profileImage = profileImage;
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BloodEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodEnum bloodType) {
        this.bloodType = bloodType;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
