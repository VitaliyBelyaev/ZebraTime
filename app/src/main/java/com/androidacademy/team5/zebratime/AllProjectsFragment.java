package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.content.Intent;
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
import com.androidacademy.team5.zebratime.entity.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllProjectsFragment extends Fragment {

    private static final int VERTICAL_ITEM_SPACE = 20;
    private ProjectsAdapter adapter;
    private RecyclerView recyclerView;
    private ProjectOnClickHandler onClickHandler;
    private Button createProjectButton;


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference projectRefJ = database.getReference("Projects");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_projects,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createProjectButton = view.findViewById(R.id.create_project);
        recyclerView = view.findViewById(R.id.projects_recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecorator(VERTICAL_ITEM_SPACE));
        recyclerView.addItemDecoration(
                new ItemDevider(getActivity(), R.drawable.divider));
        adapter = new ProjectsAdapter(onClickHandler);
        recyclerView.setAdapter(adapter);

        ValueEventListener projectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Project> projects = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for(DataSnapshot project:snapshots){
                    try {
                        projects.add(project.getValue(Project.class));
                    } catch (Exception e) {}
                }
                adapter.replaceWith(projects);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };

        projectRefJ.addValueEventListener(projectsListener);

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
