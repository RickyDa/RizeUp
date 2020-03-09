package com.rizeup.CreateQueue;

import com.rizeup.ManageQueue.QueueParticipant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RizeUpQueue {

    private String name;
    private String ownerName;
    private String ownerUid;
    private String key;
    private String imageUrl;
    private double lat;
    private double lng;
    private HashMap<String, QueueParticipant> participants;


    public RizeUpQueue() {
    }

    public RizeUpQueue(String name, String ownerName, String ownerUid, String key, String imageUrl, double lat, double lng, HashMap<String, QueueParticipant> participants) {
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

    public HashMap<String, QueueParticipant> getParticipants() {
        return participants;
    }
}
