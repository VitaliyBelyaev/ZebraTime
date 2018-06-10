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

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public String getId() {
        return id;
    }

    private void addSession(Session session){
        sessions.add(session);
    }
}

