package com.androidacademy.team5.zebratime.domain;

public class Session {
    long startDate;
    long duration;
    String id;
    String idTask;


    public Session(){}

    public Session(long startDate, long duration) {
        this.startDate = startDate;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartDate() {
        return startDate;

    }

    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
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
