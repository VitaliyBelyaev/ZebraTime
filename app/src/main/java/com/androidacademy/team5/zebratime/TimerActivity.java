package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidacademy.team5.zebratime.entity.Session;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.androidacademy.team5.zebratime.MainActivity.PROJECT_ID;
import static com.androidacademy.team5.zebratime.MainActivity.TASK_ID;

public class TimerActivity extends AppCompatActivity {

    private TextView timeTextView;
    private Button actionButton;
    private Button stopButton;
    private TextView taskTitleTextView;
    private TextView taskDurationTextView;
    private Task task;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("tasks");
    private DatabaseReference projectRefJ = database.getReference("Projects");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timeTextView = findViewById(R.id.tv_time);
        actionButton = findViewById(R.id.action_button);
        stopButton = findViewById(R.id.stop_button);
        taskTitleTextView = findViewById(R.id.tv_timer_task_title);
        taskDurationTextView = findViewById(R.id.tv_timer_task_duration);

        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MyService.class);
                intent.setAction("Start");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }
                else startService(intent);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MyService.class);
                intent.setAction("Stop");
                startService(intent);
            }
        });

        String projectId = getIntent().getStringExtra(PROJECT_ID);
        final String taskId = getIntent().getStringExtra(TASK_ID);

        DatabaseReference taskRef = projectRefJ.child(projectId).child("tasks").child(taskId);


        ValueEventListener taskListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                task = dataSnapshot.getValue(Task.class);
                Log.i("TASK_TEST", "Task: " + task);
                taskTitleTextView.setText(task.getTitle());
                List<Session> sessions = task.getSessions();
                long duration = 0;
                if (sessions != null) {
                    for (Session session : sessions) {
                        duration = duration + session.getDuration();
                    }
                }

                taskDurationTextView.setText(String.valueOf(duration));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        taskRef.addListenerForSingleValueEvent(taskListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timer_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, TimerSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
