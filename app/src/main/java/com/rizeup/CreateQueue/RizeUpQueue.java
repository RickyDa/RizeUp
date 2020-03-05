package com.rizeup.CreateQueue;

import com.rizeup.utils.User;

public class RizeUpQueue {

    private String name;
    private User owner;
    private String key;

    public RizeUpQueue() {
    }

    public RizeUpQueue(String name, User owner, String key) {

        if (name.trim().equals(""))
            name = "No Name";
        this.name = name;
        this.owner = owner;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }
}
