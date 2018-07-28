package com.cloudsync.cloud.model;

import com.cloudsync.cloud.constraint.ValidPassword;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user", schema = "public")
public class User {
    private Long id;

    @Size(min = 5, max = 30)
    @NotEmpty
    private String username;

    @ValidPassword
    @NotEmpty
    private String password;

    private String googleAccount;
    private String googleToken;
    private String dropboxAccount;
    private String dropboxToken;
    private String boxAccount;
    private String boxToken;
    private String onedriveAccount;
    private String onedriveToken;

    public String getGoogleAccount() {
        return googleAccount;
    }

    public void setGoogleAccount(String googleAccount) {
        this.googleAccount = googleAccount;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getDropboxAccount() {
        return dropboxAccount;
    }

    public void setDropboxAccount(String dropboxAccount) {
        this.dropboxAccount = dropboxAccount;
    }

    public String getDropboxToken() {
        return dropboxToken;
    }

    public void setDropboxToken(String dropboxToken) {
        this.dropboxToken = dropboxToken;
    }

    public String getBoxAccount() {
        return boxAccount;
    }

    public void setBoxAccount(String boxAccount) {
        this.boxAccount = boxAccount;
    }

    public String getBoxToken() {
        return boxToken;
    }

    public void setBoxToken(String boxToken) {
        this.boxToken = boxToken;
    }

    public String getOnedriveAccount() {
        return onedriveAccount;
    }

    public void setOnedriveAccount(String onedriveAccount) {
        this.onedriveAccount = onedriveAccount;
    }

    public String getOnedriveToken() {
        return onedriveToken;
    }

    public void setOnedriveToken(String onedriveToken) {
        this.onedriveToken = onedriveToken;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
