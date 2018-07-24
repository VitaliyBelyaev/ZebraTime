package com.androidacademy.team5.zebratime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskFragment extends Fragment {

    private Button okButton;
    private EditText titleEditText;
    private EditText commentEditText;
    private String projectId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        titleEditText = view.findViewById(R.id.task_title_editText);
        commentEditText = view.findViewById(R.id.task_comment_editText);
        okButton = view.findViewById(R.id.task_ok_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newTaskTitle = titleEditText.getText().toString();
                String newTaskComment = commentEditText.getText().toString();

                if (newTaskTitle.matches("")) {
                    showToast("Empty title");
                } else {

                    Task task = new Task(newTaskTitle, newTaskComment, projectId);
                    addTaskToDB(task);

                    getActivity().getSupportFragmentManager().popBackStack();

                    MainActivity.setAppBarTitle(projectId);
                }

            }
        });
    }

    private void addTaskToDB(Task task) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance()
                .getReference().child("Tasks");

        task.setId(tasksRef.push().getKey());
        tasksRef.child(task.getId()).setValue(task);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static NewTaskFragment newInstance(String projectId) {
        NewTaskFragment fragment = new NewTaskFragment();
        fragment.projectId = projectId;
        return fragment;
    }

}
