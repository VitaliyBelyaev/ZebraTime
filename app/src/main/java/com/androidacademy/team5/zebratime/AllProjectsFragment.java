package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidacademy.team5.zebratime.ProjectsAdapter.ProjectOnClickHandler;
import com.androidacademy.team5.zebratime.domain.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllProjectsFragment extends Fragment {

    private Button createProjectButton;
    private RecyclerView recyclerView;
    private ProjectsAdapter adapter;
    private ProjectOnClickHandler onClickHandler;
    private DatabaseReference projectRef;
    private NewProjectHandler projectHandler;

    interface NewProjectHandler {
        void onCreateProjectButtonClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_projects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createProjectButton = view.findViewById(R.id.create_project);
        recyclerView = view.findViewById(R.id.projects_recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        projectRef = FirebaseDatabase.getInstance().getReference("Projects");
        initRecyclerView();
        projectRef.addValueEventListener(createProjectsListener());

        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectHandler.onCreateProjectButtonClick();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProjectOnClickHandler) {
            onClickHandler = (ProjectOnClickHandler) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onClickHandler = null;
    }

    private void initRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProjectsAdapter(onClickHandler);
        recyclerView.setAdapter(adapter);
    }

    private ValueEventListener createProjectsListener() {

        ValueEventListener projectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Project> projects = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for (DataSnapshot project : snapshots) {
                    try {
                        projects.add(project.getValue(Project.class));
                    } catch (Exception e) {
                    }
                }
                adapter.replaceWith(projects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        return projectsListener;
    }

    public static AllProjectsFragment newInstance(NewProjectHandler projectHandler) {
        AllProjectsFragment fragment = new AllProjectsFragment();
        fragment.projectHandler = projectHandler;
        return fragment;
    }
}
