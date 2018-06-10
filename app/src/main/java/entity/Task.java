package entity;

import java.util.ArrayList;
import java.util.List;

public class Task {
    String id;
    String title;
    String comment;
    List<Session>sessions;


    public Task(String id,String title, String comment) {
        this.id = id;
        this.title = title;
        this.comment = comment;
        this.sessions = new ArrayList();
    }

    public String getId() {
        return id;
    }
/*
    private void addSessions(){

    }*/
}

