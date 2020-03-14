package com.rizeup.models;

import androidx.annotation.Nullable;

public class QueueParticipant implements Comparable<QueueParticipant> {

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

    @Override
    public int compareTo(QueueParticipant qp) {
        if (this.timeStamp > qp.timeStamp)
            return 1;
        else if (this.timeStamp < qp.timeStamp)
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof QueueParticipant)){
            return false;
        }else {
            QueueParticipant qp = (QueueParticipant) obj;
            return this.uid.equals(qp.uid);
        }
    }
}
