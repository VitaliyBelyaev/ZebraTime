package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidacademy.team5.zebratime.ProjectsAdapter.ProjectOnClickHandler;

import java.util.ArrayList;

import entity.Project;

public class AllProjectsFragment extends Fragment {

    private ProjectsAdapter adapter;
    private RecyclerView recyclerView;
    private ProjectOnClickHandler onClickHandler;
    private Button createProjectButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_projects,container,false);

        createProjectButton = view.findViewById(R.id.create_project);
        recyclerView = view.findViewById(R.id.projects_recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProjectsAdapter(onClickHandler);
        recyclerView.setAdapter(adapter);

        ArrayList<Project> projects = new ArrayList<>();
        projects.add(new Project("sahdjs"));
        projects.add(new Project("safsdgh"));
        projects.add(new Project("safgasgg"));
        projects.add(new Project("sagasf"));

        adapter.replaceWith(projects);

        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() ,NewProjectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ProjectOnClickHandler){
            onClickHandler = (ProjectOnClickHandler) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onClickHandler = null;
    }


    public static AllProjectsFragment newInstance() {
        AllProjectsFragment fragment = new AllProjectsFragment();
        return fragment;
    }
}
