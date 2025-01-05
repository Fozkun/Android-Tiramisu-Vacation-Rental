package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;

import java.io.Serializable;

public class UserModel implements Serializable {
    public String  username, nickname;
    public UserRole userRole;

    public UserModel(){}

    public UserModel(String username, String nickname, UserRole userRole) {
        this.username = username;
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
