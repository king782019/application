package com.cloudsync.cloud.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Provider {
    private String accessToken;

    private Account account;

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    @JsonProperty("account")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
