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
import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


//Uses to list tasks of certain project
public class ProjectFragment extends Fragment {

    private TasksAdapter adapter;
    private RecyclerView recyclerView;
    private TaskOnClickHandler onClickHandler;
    private FloatingActionButton fab;
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

        return inflater.inflate(R.layout.fragment_project, container, false);
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

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TasksAdapter(onClickHandler, projectId);
        recyclerView.setAdapter(adapter);

        tasksRef.orderByChild("projectId")
                .equalTo(projectId)
                .addValueEventListener(createTasksListener());


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewTaskFragment fragment = NewTaskFragment.newInstance(projectId);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskOnClickHandler) {
            onClickHandler = (TaskOnClickHandler) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onClickHandler = null;
    }

    private ValueEventListener createTasksListener() {

        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Task> tasks = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for (DataSnapshot snapshot : snapshots) {
                    tasks.add(snapshot.getValue(Task.class));
                }
                adapter.replaceWith(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        return tasksListener;
    }


    public static ProjectFragment newInstance(String projectId) {
        ProjectFragment fragment = new ProjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
}
