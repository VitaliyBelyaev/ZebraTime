package com.androidacademy.team5.zebratime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Intent.EXTRA_TEXT;

public class NewTaskActivity extends AppCompatActivity {
    Button btnOk;
    EditText textTitle;
    EditText textComment;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Tasks");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_screen);




        final String projectId = getIntent().getStringExtra(EXTRA_TEXT);
        btnOk = findViewById(R.id.btnOk);
        textTitle = findViewById(R.id.textTitle);
        textComment = findViewById(R.id.textComment);

        TextInputLayout til1 = findViewById(R.id.text_input_textTitle);
        EditText etl = findViewById(R.id.textTitle);
        til1.setHint(getString(R.string.enter_title));

        TextInputLayout til2 = findViewById(R.id.text_input_textComment);
        EditText etl2 = findViewById(R.id.textComment);
        til2.setHint(getString(R.string.enter_comment));



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Task task = new Task(myRef.push().getKey(),textTitle.getText().toString(),textComment.getText().toString());
                    task.setIdProject(projectId);
                    myRef.child(task.getId()).setValue(task);
                    finish();
                }
        });

    }
    public static void startActivity(Activity activity, String projectId){
        Intent intent = new Intent(activity, NewTaskActivity.class);
        intent.putExtra(EXTRA_TEXT, projectId);
        activity.startActivity(intent);

    }

}
