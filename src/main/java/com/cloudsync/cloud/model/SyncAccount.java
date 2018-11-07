package com.cloudsync.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncAccount {
    @JsonProperty
    private String source;
    @JsonProperty
    private String destination;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
