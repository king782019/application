package com.cloudsync.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncAccount {
    @JsonProperty
    private Integer source;
    @JsonProperty
    private Integer destination;

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getDestination() {
        return destination;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }
}
