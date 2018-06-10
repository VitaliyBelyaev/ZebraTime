package com.androidacademy.team5.zebratime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import entity.Task;

public class NewTaskActivity extends AppCompatActivity {
    Button btnOk;
    EditText textTitle;
    EditText textComment;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Task");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_screen);


        btnOk = findViewById(R.id.btnOk);
        textTitle = findViewById(R.id.textTitle);
        textComment = findViewById(R.id.textComment);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Task task = new Task(myRef.push().getKey(),textTitle.getText().toString(),textComment.getText().toString());
                    myRef.child(task.getId()).setValue(task);
                }
        });
    }
}
