package com.cloudsync.cloud.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserEmail {
    @Email
    @NotEmpty
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
