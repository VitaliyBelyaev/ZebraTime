package com.androidacademy.team5.zebratime.entity;

import java.util.ArrayList;
import java.util.List;

public class Task {
    String id;
    String title;
    String comment;
    List<Session>sessions;


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

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    private void addSession(Session session){
        sessions.add(session);
    }
}

