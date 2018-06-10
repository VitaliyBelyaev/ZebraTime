package com.androidacademy.team5.zebratime;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;

public class MainActivity extends AppCompatActivity
        implements ProjectsAdapter.ProjectOnClickHandler {

    private DrawerLayout drawerLayout;

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

        getApp().timer.start();

    }

    @Override
    public void onProjectClick(String projectId) {
        drawerLayout.closeDrawer(Gravity.START);
        ProjectFragment projectFragment = ProjectFragment.newInstance("1");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, projectFragment)
                .commit();

    }

    private App getApp(){
        return (App) getApplication();
    }
}
