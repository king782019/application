package com.cloudsync.cloud.model;

import com.cloudsync.cloud.constraint.ValidPassword;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user", schema = "public")
@Scope("prototype")
public class User {
    private Long id;

    @Email
    @NotEmpty
    private String username;

    @ValidPassword
    @NotEmpty
    private String password;

    private Boolean enabled = false;



    private String googleAccount;
    private String googleToken;
    private String dropboxAccount;
    private String dropboxToken;
    private String boxAccount;
    private String boxToken;
    private String onedriveAccount;
    private String onedriveToken;
    private String yandexAccount;
    private String yandexToken;
    private String hidriveAccount;
    private String hidriveToken;
    private String pcloudAccount;
    private String pcloudToken;


    public String getPcloudAccount() {
        return pcloudAccount;
    }

    public void setPcloudAccount(String pcloudAccount) {
        this.pcloudAccount = pcloudAccount;
    }

    public String getPcloudToken() {
        return pcloudToken;
    }

    public void setPcloudToken(String pcloudToken) {
        this.pcloudToken = pcloudToken;
    }

    public String getHidriveAccount() {
        return hidriveAccount;
    }

    public void setHidriveAccount(String hidriveAccount) {
        this.hidriveAccount = hidriveAccount;
    }

    public String getHidriveToken() {
        return hidriveToken;
    }

    public void setHidriveToken(String hidriveToken) {
        this.hidriveToken = hidriveToken;
    }

    public String getYandexAccount() {
        return yandexAccount;
    }

    public void setYandexAccount(String yandexAccount) {
        this.yandexAccount = yandexAccount;
    }

    public String getYandexToken() {
        return yandexToken;
    }

    public void setYandexToken(String yandexToken) {
        this.yandexToken = yandexToken;
    }

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
