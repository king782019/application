package com.cloudsync.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

    private String id;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
