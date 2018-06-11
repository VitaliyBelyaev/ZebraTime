package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;

import com.androidacademy.team5.zebratime.entity.Project;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ProjectsAdapter.ProjectOnClickHandler,
                   TasksAdapter.TaskOnClickHandler {

    private DrawerLayout drawerLayout;
    public static final String PROJECT_ID = "projectId";
    public static final String TASK_ID = "taskId";


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference projectRefJ = database.getReference("Projects");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);


        if (getApp().timer.getTask() != null) {
            Task task = getApp().timer.getTask();
            onTaskClick(task.getId(), task.getIdProject());
            finish();
        }

        AllProjectsFragment allProjectsFragment = AllProjectsFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_container, allProjectsFragment)
                .commit();

        ValueEventListener projectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Project> projects = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for(DataSnapshot project:snapshots){
                    projects.add(project.getValue(Project.class));
                }
                ProjectFragment projectFragment = ProjectFragment.newInstance(projects.get(0).getId());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, projectFragment)
                        .commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        projectRefJ.addValueEventListener(projectsListener);

    }

    @Override
    public void onProjectClick(String projectId) {
        drawerLayout.closeDrawer(Gravity.START);
        ProjectFragment projectFragment = ProjectFragment.newInstance(projectId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, projectFragment)
                .commit();

    }

    private App getApp(){
        return (App) getApplication();
    }

    @Override
    public void onTaskClick(String taskId, String projectId) {
        Intent intent = new Intent(this,TimerActivity.class);
        intent.putExtra(PROJECT_ID,projectId);
        intent.putExtra(TASK_ID,taskId);
        startActivity(intent);
    }
}
