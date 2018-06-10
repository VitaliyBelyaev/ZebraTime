package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidacademy.team5.zebratime.domain.Project;
import com.androidacademy.team5.zebratime.TasksAdapter.TaskOnClickHandler;
import com.androidacademy.team5.zebratime.domain.Task;

import java.util.ArrayList;

public class ProjectFragment extends Fragment {

    private TasksAdapter adapter;
    private RecyclerView recyclerView;
    private TaskOnClickHandler onClickHandler;
    private String projectId;
    public static final String ARG_PROJECT_ID = "projectId";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.projectId = getArguments().getString(ARG_PROJECT_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project,container,false);

        recyclerView = view.findViewById(R.id.tasks_recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TasksAdapter(onClickHandler);
        recyclerView.setAdapter(adapter);

        ArrayList<Task> tasks= new ArrayList<>();
        tasks.add(new Task("1","Task 1"));
        tasks.add(new Task("2","Project 2"));
        tasks.add(new Task("3","Project 3"));
        tasks.add(new Task("4","Project 4"));

        adapter.replaceWith(tasks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof TaskOnClickHandler){
            onClickHandler = (TaskOnClickHandler) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onClickHandler = null;
    }


    public static ProjectFragment newInstance(String projectId) {
        ProjectFragment fragment = new ProjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
}
