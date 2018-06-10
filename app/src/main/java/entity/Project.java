package entity;

import java.util.ArrayList;
import java.util.List;

public class Project {
    String title;
    String id;
    ArrayList<Task> tasks;


    public Project(String title, String id, ArrayList<Task> tasks) {
        this.title = title;
        this.id = id;
        this.tasks = tasks;
    }

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
