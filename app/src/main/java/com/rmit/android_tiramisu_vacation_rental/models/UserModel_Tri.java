package com.rmit.android_tiramisu_vacation_rental.models;

import com.rmit.android_tiramisu_vacation_rental.enums.UserRole;

import java.io.Serializable;

public class UserModel_Tri implements Serializable {
    public String  username, nickname;
    public UserRole userRole;

    public UserModel_Tri(){}

    public UserModel_Tri(String username, String nickname, UserRole userRole) {
        this.username = username;
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
