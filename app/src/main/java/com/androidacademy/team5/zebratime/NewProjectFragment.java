package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidacademy.team5.zebratime.domain.Project;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProjectFragment extends Fragment {

    private EditText editText;
    private Button okButton;
    CreatedProjectHandler createdProjectHandler;

    interface CreatedProjectHandler{
        void onNewProjectCreated(String projectId);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_new_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editText = view.findViewById(R.id.project_title_editText);
        okButton = view.findViewById(R.id.project_ok_button);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newProjectTitle = editText.getText().toString();

                if (newProjectTitle.matches("")) {
                    showToast("Empty title");
                } else {

                    Project newProject = new Project(newProjectTitle);
                    String projectId = addProjectToDB(newProject);

                    hideKeyboard();

                    createdProjectHandler.onNewProjectCreated(projectId);
                }

            }
        });
    }



    private String addProjectToDB(Project project) {
        DatabaseReference projectsRef = FirebaseDatabase.getInstance()
                .getReference().child("Projects");

        String projectId = projectsRef.push().getKey();
        project.setId(projectId);
        projectsRef.child(projectId).setValue(project);
        return projectId;
    }

    private void hideKeyboard() {
        View keyboardView = getActivity().getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static NewProjectFragment newInstance(CreatedProjectHandler handler) {
        NewProjectFragment fragment = new NewProjectFragment();
        fragment.createdProjectHandler = handler;
        return fragment;
    }
}
