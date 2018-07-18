package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        AllProjectsFragment allProjectsFragment = AllProjectsFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_container, allProjectsFragment)
                .commit();

//        ValueEventListener projectsListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                ArrayList<Project> projects = new ArrayList<>();
//                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
//
//                for (DataSnapshot project : snapshots) {
//                    projects.add(project.getValue(Project.class));
//                }
//                if (!projects.isEmpty()) {
//                    ProjectFragment projectFragment = ProjectFragment.newInstance(projects.get(0).getId());
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main_container, projectFragment)
//                            .commit();
//
//                }
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        projectRefJ.addValueEventListener(projectsListener);
//
//    }

    }
        @Override
        public void onProjectClick (String projectId){
            drawerLayout.closeDrawer(Gravity.START);
            ProjectFragment projectFragment = ProjectFragment.newInstance(projectId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, projectFragment)
                    .commit();

        }

        @Override
        public void onTaskClick (String taskId, String projectId){
            Intent intent = new Intent(this, TimerActivity.class);
            intent.putExtra(PROJECT_ID, projectId);
            intent.putExtra(TASK_ID, taskId);
            startActivity(intent);
        }
    }

