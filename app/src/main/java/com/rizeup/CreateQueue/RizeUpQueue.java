package com.rizeup.CreateQueue;

import com.rizeup.utils.User;

public class RizeUpQueue {

    private String name;
    private String ownerName;
    private String ownerUid;
    private String key;
    private String imageUrl;

    public RizeUpQueue() {
    }

    public RizeUpQueue(String name, String ownerName, String ownerUid, String key, String imageUrl) {
        this.name = name;
        this.ownerName = ownerName;
        this.ownerUid = ownerUid;
        this.key = key;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUid() {
        return ownerUid;
    }
}
