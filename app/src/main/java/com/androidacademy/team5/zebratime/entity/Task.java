package com.androidacademy.team5.zebratime.entity;

import java.util.ArrayList;
import java.util.List;

public class Task {
    String id;
    String title;
    String comment;
    List<Session> sessions;


    public Task(){}

    public Task(String id,String title, String comment) {
        this.id = id;
        this.title = title;
        this.comment = comment;
        this.sessions = new ArrayList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", sessions=" + sessions +
                '}';
    }
}

