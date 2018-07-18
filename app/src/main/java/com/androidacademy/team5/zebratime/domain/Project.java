package com.androidacademy.team5.zebratime.domain;

import java.util.ArrayList;

public class Project {
    String title;
    String id;
    ArrayList<Task> tasks;


    public Project(){}

    public Project(String title) {
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
