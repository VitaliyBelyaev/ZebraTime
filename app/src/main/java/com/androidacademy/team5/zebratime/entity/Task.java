package com.androidacademy.team5.zebratime.entity;
import com.androidacademy.team5.zebratime.entity.Session;

import java.util.ArrayList;
import java.util.List;

public class Task {
    String id;
    String title;
    String comment;
    String idProject;
    List<Session> sessions;

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
    }

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
}

