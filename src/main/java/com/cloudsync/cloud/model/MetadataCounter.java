package com.cloudsync.cloud.model;

import com.kloudless.model.Metadata;

import java.util.List;

public class MetadataCounter {
    private int counter;
    private List<Metadata> metadataList;
    private boolean isHidrive = false;

    public MetadataCounter(int counter, List<Metadata> metadataList) {
        this.counter = counter;
        this.metadataList = metadataList;
    }

    public boolean getHidrive() {
        return isHidrive;
    }

    public void setHidrive(boolean hidrive) {
        isHidrive = hidrive;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

}
