package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import static com.androidacademy.team5.zebratime.Timer.State.OVERWORK;
import static com.androidacademy.team5.zebratime.Timer.State.STOP;

public class TimerActivity extends AppCompatActivity {

    private TextView timeTextView;
    private Button actionButton;
    private TextView taskTitleTextView;
    private TextView taskDurationTextView;
    private Task task;
    private Timer timer;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("Tasks");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timer = getApp().timer;
        timeTextView = findViewById(R.id.tv_time);
        actionButton = findViewById(R.id.action_button);
        taskTitleTextView = findViewById(R.id.tv_timer_task_title);
        taskDurationTextView = findViewById(R.id.tv_timer_task_duration);

        if (timer.getTask() == null) {
            String projectId = getIntent().getStringExtra(PROJECT_ID);
            final String taskId = getIntent().getStringExtra(TASK_ID);

            DatabaseReference taskRef = tasksRef.child(taskId);

            ValueEventListener taskListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    task = dataSnapshot.getValue(Task.class);
                    Log.i("TASK_TEST", "Task: " + task);
                    showTaskInfo(task);
                    timer.setTask(task);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                }
            };
            taskRef.addListenerForSingleValueEvent(taskListener);

        } else {
            task = getApp().timer.getTask();
            showTaskInfo(task);
        }

        final CountDownTimer internalTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("TimerTest", "seconds remaining: " + millisUntilFinished / 1000);
                timeTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer.setState(OVERWORK);
                actionButton.setText("Take break");
                Log.i("TimerTest", "Done!");
            }
        };

        final CountDownTimer breakTimer = new CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("TimerTest", "seconds remaining: " + millisUntilFinished / 1000);
                timeTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer.setState(STOP);
                Log.i("TimerTest", "Done!");
            }
        };

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(timer.getState()){
                    case STOP:
                        actionButton.setText("STOP");
                        timer.start(internalTimer);
                        break;
                    case WORK:
                        actionButton.setText("START");
                        timer.stop();
                        break;
                    case OVERWORK:
                        actionButton.setText("START");
                        timer.pause(breakTimer);
                    case PAUSE:
                        actionButton.setText("STOP");
                        timer.start(internalTimer);
                }
            }
        });
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

    private void showTaskInfo(Task task) {
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

    private App getApp() {
        return (App) getApplication();
    }
}
