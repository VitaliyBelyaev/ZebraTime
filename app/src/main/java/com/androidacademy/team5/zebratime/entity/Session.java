package com.androidacademy.team5.zebratime.entity;

public class Session {
    long startDate;
    long duration;

    public Session(long startDate, long duration) {
        this.startDate = startDate;
        this.duration = duration;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
