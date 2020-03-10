package com.rizeup.ManageQueue;

public class QueueParticipant {

    private String uid;
    private long timeStamp;

    public QueueParticipant() {
    }

    public QueueParticipant(String uid, long timestamp) {
        this.uid = uid;
        this.timeStamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
