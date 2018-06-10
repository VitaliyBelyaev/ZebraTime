package com.androidacademy.team5.zebratime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import entity.Task;

import static android.content.Intent.EXTRA_TEXT;

public class NewTaskActivity extends AppCompatActivity {
    Button btnOk;
    EditText textTitle;
    EditText textComment;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("tasks");
    DatabaseReference projectRefJ = database.getReference("Projects");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_screen);


        //final String projectId = getIntent().getStringExtra(EXTRA_TEXT);
        btnOk = findViewById(R.id.btnOk);
        textTitle = findViewById(R.id.textTitle);
        textComment = findViewById(R.id.textComment);
        final String projectId = "-LEe_57y7rc5VoR7mub2";


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Task task = new Task(myRef.push().getKey(),textTitle.getText().toString(),textComment.getText().toString());
                    DatabaseReference projectRef = projectRefJ.child(projectId);
                    projectRef.child("tasks").child(task.getId()).setValue(task);
                   // myRef.child(task.getId()).setValue(task);
                }
        });


    }
    public static void startActivity(Activity activity, String projectId){
        Intent intent = new Intent(activity, NewTaskActivity.class);
        intent.putExtra(EXTRA_TEXT, projectId);
        activity.startActivity(intent);

    }


}
