package com.cloudsync.cloud.model;

import java.util.Objects;

public class WorkerAccount {
    private String token;
    private String account;

    public WorkerAccount(String account, String token) {
        this.token = token;
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerAccount account1 = (WorkerAccount) o;
        return Objects.equals(token, account1.token) &&
                Objects.equals(account, account1.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, account);
    }
}
