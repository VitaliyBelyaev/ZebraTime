package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;

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
        TasksAdapter.TaskOnClickHandler {

    private DrawerLayout drawerLayout;
    public static final String PROJECT_ID = "projectId";
    public static final String TASK_ID = "taskId";

    private ProjectFragment projectFragment;

    private FirebaseDatabase database;
    private DatabaseReference tasksRef;
    private DatabaseReference sessionsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);

        database = FirebaseDatabase.getInstance();
        tasksRef = database.getReference("Tasks");
        sessionsRef = database.getReference("Sessions");

        AllProjectsFragment allProjectsFragment = AllProjectsFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_container, allProjectsFragment)
                .commit();

        setLastProject();
    }


    private void setLastProject() {
        Log.i("FB", "in getLastProjectId");
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

                String taskId = sessions.get(sessions.size() - 1).getIdTask();

                tasksRef.child(taskId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Task task = dataSnapshot.getValue(Task.class);
                                Log.i("FB", "Just before creating instance of projectFragment in valueListener");
                                projectFragment = ProjectFragment.newInstance(task.getProjectId());

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.main_container, projectFragment)
                                        .commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
        ProjectFragment projectFragment = ProjectFragment.newInstance(projectId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, projectFragment)
                .commit();

    }

    @Override
    public void onTaskClick(String taskId, String projectId) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra(PROJECT_ID, projectId);
        intent.putExtra(TASK_ID, taskId);
        startActivity(intent);
    }
}

