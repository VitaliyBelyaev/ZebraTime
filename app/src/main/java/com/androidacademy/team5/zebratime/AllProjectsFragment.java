package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidacademy.team5.zebratime.domain.Project;

import java.util.ArrayList;
import java.util.List;
import com.androidacademy.team5.zebratime.ProjectsAdapter.ProjectOnClickHandler;

public class AllProjectsFragment extends Fragment {

    private ProjectsAdapter adapter;
    private RecyclerView recyclerView;
    private ProjectOnClickHandler onClickHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_projects,container,false);

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
        projects.add(new Project("1","Project 1"));
        projects.add(new Project("2","Project 2"));
        projects.add(new Project("3","Project 3"));
        projects.add(new Project("4","Project 4"));

        adapter.replaceWith(projects);
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
