package com.cloudsync.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

    private String id;
    private String account;

    @JsonProperty("account")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
