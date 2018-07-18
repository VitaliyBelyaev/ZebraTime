package com.androidacademy.team5.zebratime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Intent.EXTRA_TEXT;

public class NewTaskActivity extends AppCompatActivity {
    private Button btnOk;
    private EditText titleEdT;
    private EditText commentEdT;
    private TextInputLayout titleTil;
    private TextInputLayout commentTil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_screen);

        final String projectId = getIntent().getStringExtra(EXTRA_TEXT);
        btnOk = findViewById(R.id.btnOk);
        titleEdT = findViewById(R.id.textTitle);
        commentEdT= findViewById(R.id.textComment);
        titleTil = findViewById(R.id.text_input_textTitle);
        commentTil = findViewById(R.id.text_input_textComment);

        titleTil.setHint(getString(R.string.enter_title));
        commentTil.setHint(getString(R.string.enter_comment));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String taskTitle = titleEdT.getText().toString();
                final String taskComment = commentEdT.getText().toString();

                if (taskTitle.matches("")){
                    showToast("Empty title");
                } else {
                    Task task = new Task(taskTitle,taskComment,projectId);
                    addTaskToDB(task);
                    finish();
                }

                }
        });

    }

    private void addTaskToDB(Task task){
        DatabaseReference tasksRef = FirebaseDatabase.getInstance()
                .getReference().child("Tasks");

        task.setId(tasksRef.push().getKey());
        tasksRef.child(task.getId()).setValue(task);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    public static void startActivity(Activity activity, String projectId){
        Intent intent = new Intent(activity, NewTaskActivity.class);
        intent.putExtra(EXTRA_TEXT, projectId);
        activity.startActivity(intent);

    }

}
