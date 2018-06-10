package com.androidacademy.team5.zebratime.domain;

public class Project {

    private String id;
    private String title;

    public Project(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
