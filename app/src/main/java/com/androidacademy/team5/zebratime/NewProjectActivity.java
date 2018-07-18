package com.androidacademy.team5.zebratime;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidacademy.team5.zebratime.domain.Project;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProjectActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        TextInputLayout til = findViewById(R.id.text_input_ed_text);
        final EditText titleEditText = findViewById(R.id.ed_text);
        Button okBtn = findViewById(R.id.ok_button);

        til.setHint(getString(R.string.enter_title));

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String titleNewProject = titleEditText.getText().toString();

                if (titleNewProject.matches("")) {
                    showToast("Empty title");
                } else {

                    Project newProject = new Project(titleNewProject);
                    addProjectToDB(newProject);
                    finish();
                }
            }
        });
    }

    private void addProjectToDB(Project project){
        DatabaseReference projectsRef = FirebaseDatabase.getInstance()
                .getReference().child("Projects");

        project.setId(projectsRef.push().getKey());
        projectsRef.child(project.getId()).setValue(project);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
