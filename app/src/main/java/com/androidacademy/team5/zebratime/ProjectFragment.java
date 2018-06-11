package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidacademy.team5.zebratime.TasksAdapter.TaskOnClickHandler;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProjectFragment extends Fragment {

    private TasksAdapter adapter;
    private RecyclerView recyclerView;
    private TaskOnClickHandler onClickHandler;
    private FloatingActionButton fab;
    private String projectId;
    public static final String ARG_PROJECT_ID = "projectId";


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("tasks");
    private DatabaseReference projectRefJ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.projectId = getArguments().getString(ARG_PROJECT_ID);
        this.projectRefJ = database.getReference("Projects").child(projectId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_project,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.tasks_recycler_view);
        fab = view.findViewById(R.id.fab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TasksAdapter(onClickHandler,projectId);
        recyclerView.setAdapter(adapter);

        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Task> tasks = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.child("tasks").getChildren();

                for(DataSnapshot project:snapshots){
                    tasks.add(project.getValue(Task.class));
                }
                adapter.replaceWith(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };

        projectRefJ.addValueEventListener(tasksListener);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskActivity.startActivity(getActivity(),projectId);
            }
        });
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
