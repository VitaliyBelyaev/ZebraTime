package com.androidacademy.team5.zebratime.domain;

import java.util.List;

public class Task {
    private String id;
    private String title;
    private String comment;
    private String projectId;
    private List<Session> sessions;


    public Task(){}

    public Task(String title, String comment, String projectId) {
        this.title = title;
        this.comment = comment;
        this.projectId = projectId;
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

    public String getProjectId() {
        return projectId;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}

