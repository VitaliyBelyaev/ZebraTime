package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;

import com.androidacademy.team5.zebratime.domain.Project;
import com.androidacademy.team5.zebratime.domain.Session;
import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ProjectsAdapter.ProjectOnClickHandler,
        TasksAdapter.TaskOnClickHandler,
        AllProjectsFragment.NewProjectHandler,
        NewProjectFragment.CreatedProjectHandler,
        ProjectFragment.NewTaskHandler {

    private ActionBarDrawerToggle toggle;
    private static Toolbar toolbar;

    private DrawerLayout drawerLayout;
    public static final String PROJECT_ID = "projectId";
    public static final String TASK_ID = "taskId";


    private FirebaseDatabase database;
    private DatabaseReference tasksRef;
    private DatabaseReference sessionsRef;
    private static DatabaseReference projectsRef;

    private String projectId;
    private Fragment selectedFragment;
    private ProjectFragment projectFragment;
    private NewProjectFragment newProjectFragment;
    private NewTaskFragment newTaskFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);

        database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("Tasks");
        sessionsRef = database.getReference("Sessions");
        projectsRef = database.getReference("Projects");

        AllProjectsFragment allProjectsFragment = AllProjectsFragment.newInstance(this);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_container, allProjectsFragment)
                .commit();

        setLastProject();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void setLastProject() {
        sessionsRef.orderByChild("startDate")
                .addListenerForSingleValueEvent(createSessionsListener());
    }

    private ValueEventListener createSessionsListener() {

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Session> sessions = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                Log.i("FB", "In first dataChange");
                for (DataSnapshot snapshot : snapshots) {
                    sessions.add(snapshot.getValue(Session.class));
                }


                if (sessions.size() > 0) {
                    String taskId = sessions.get(sessions.size() - 1).getIdTask();

                    tasksRef.child(taskId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Task task = dataSnapshot.getValue(Task.class);
                                    projectId = task.getProjectId();
                                    projectFragment = ProjectFragment.newInstance(projectId, MainActivity.this);

                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .add(R.id.main_container, projectFragment)
                                            .commit();
                                    selectedFragment = projectFragment;

                                    setAppBarTitle(projectId);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
    }

    @Override
    public void onProjectClick(String projectId) {
        drawerLayout.closeDrawer(Gravity.START);
        ProjectFragment projectFragment = ProjectFragment.newInstance(projectId, this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, projectFragment)
                .commit();

        setAppBarTitle(projectId);
        selectedFragment = projectFragment;

    }

    @Override
    public void onTaskClick(String taskId, String projectId) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra(PROJECT_ID, projectId);
        intent.putExtra(TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void onCreateProjectButtonClick() {
        drawerLayout.closeDrawer(Gravity.START);
        newProjectFragment = NewProjectFragment.newInstance(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_container, newProjectFragment)
                .addToBackStack(null)
                .commit();
        selectedFragment = newProjectFragment;

        toolbar.setTitle(getString(R.string.create_project));

    }

    @Override
    public void onCreateTaskButtonClick(String projectId) {
        newTaskFragment = NewTaskFragment.newInstance(projectId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, newTaskFragment)
                .addToBackStack(null)
                .commit();

        selectedFragment = newTaskFragment;

        toolbar.setTitle(getString(R.string.create_task));
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment.equals(newProjectFragment)) {
            drawerLayout.openDrawer(Gravity.START);
            getSupportFragmentManager().popBackStack();
            setAppBarTitle(projectFragment.getProjectId());
        } else if (selectedFragment.equals(newTaskFragment)) {
            getSupportFragmentManager().popBackStack();
            setAppBarTitle(projectFragment.getProjectId());
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onNewProjectCreated(String projectId) {
        ProjectFragment projectFragment = ProjectFragment.newInstance(projectId, this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, projectFragment)
                .commit();

        setAppBarTitle(projectId);
    }


    public static void setAppBarTitle(String projectId) {
        projectsRef.child(projectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Project project = dataSnapshot.getValue(Project.class);
                        toolbar.setTitle(project.getTitle());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}

