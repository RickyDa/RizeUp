package com.rizeup.models;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RiZeUpQueue {

    private String name;
    private String ownerName;
    private String ownerUid;
    private String key;
    private String imageUrl;
    private double lat;
    private double lng;
    private HashMap<String, QueueParticipant> participants;


    public RiZeUpQueue() {
    }

    public RiZeUpQueue(String name, String ownerName, String ownerUid, String key, String imageUrl, double lat, double lng, HashMap<String, QueueParticipant> participants) {
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof RiZeUpQueue)) {
            return false;
        } else {
            RiZeUpQueue q = (RiZeUpQueue) obj;
            return this.ownerUid.equals(q.ownerUid);
        }
    }

    public void sortParticipants() {
        // Create a list from elements of HashMap
        List<Map.Entry<String, QueueParticipant>> list = new LinkedList<>(this.participants.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, QueueParticipant>>() {
            public int compare(Map.Entry<String, QueueParticipant> o1,
                               Map.Entry<String, QueueParticipant> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<String, QueueParticipant> temp = new HashMap<>();
        for (Map.Entry<String, QueueParticipant> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        this.participants = temp;
    }
}
