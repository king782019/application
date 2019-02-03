package com.cloudsync.cloud.model;

import com.cloudsync.cloud.constraint.ValidPassword;

import javax.validation.constraints.NotEmpty;

public class UserPasswordToken {

    @ValidPassword
    @NotEmpty
    private String password;

    @NotEmpty
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
