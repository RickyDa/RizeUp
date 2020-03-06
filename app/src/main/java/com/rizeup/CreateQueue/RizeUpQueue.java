package com.rizeup.CreateQueue;

import com.rizeup.SignUp.RiZeUpUser;
import com.rizeup.utils.User;

import java.util.ArrayList;

public class RizeUpQueue {

    private String name;
    private String ownerName;
    private String ownerUid;
    private String key;
    private String imageUrl;
    private double lat;
    private double lng;
    private ArrayList<String> participants;


    public RizeUpQueue() {
    }

    public RizeUpQueue(String name, String ownerName, String ownerUid, String key, String imageUrl, double lat, double lng, ArrayList<String> participants) {
        this.name = name;
        this.ownerName = ownerName;
        this.ownerUid = ownerUid;
        this.key = key;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.participants = participants;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
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

    public ArrayList<String> getParticipants() {
        return participants;
    }
}
