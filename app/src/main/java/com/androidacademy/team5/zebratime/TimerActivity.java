package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.androidacademy.team5.zebratime.MainActivity.PROJECT_ID;
import static com.androidacademy.team5.zebratime.MainActivity.TASK_ID;
import static com.androidacademy.team5.zebratime.Timer.State.STOP;

public class TimerActivity extends AppCompatActivity {

    private TextView timeTextView;
    private Button actionButton;
    private TextView taskTitleTextView;
    private TextView taskDurationTextView;
    private Task task;
    private Timer timer;
    private Button endTaskButton;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("Tasks");
    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            long workTime = 60 * 1000 * getSharedPreferences().getLong("workTime",25);
            long shortBreakTime = 60 * 1000 * getSharedPreferences().getLong("shortTime",5);

            switch(timer.getState()){
                case STOP:
                    actionButton.setText("Start");
                    timeTextView.setText(formatTime(workTime));
                    break;
                case WORK:
                    actionButton.setText("Stop");
                    long passedTime = System.currentTimeMillis() - timer.startTime;
                    timeTextView.setText(formatTime(workTime - passedTime));
                    break;
                case OVERWORK:
                    actionButton.setText("Take break");
                    timeTextView.setText(formatTime(shortBreakTime));
                    break;
                case PAUSE:
                    actionButton.setText("START");
                    long passedBreakTime = System.currentTimeMillis() - timer.endTime;
                    timeTextView.setText(String.valueOf((shortBreakTime - passedBreakTime)));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timer = getApp().timer;
        timer.addListener(timerListener);

        timeTextView = findViewById(R.id.tv_time);
        actionButton = findViewById(R.id.action_button);
        taskTitleTextView = findViewById(R.id.tv_timer_task_title);
        taskDurationTextView = findViewById(R.id.tv_timer_task_duration);
        endTaskButton = findViewById(R.id.end_task_button);

        actionButton.setText("Start");

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



        endTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MyService.class);
                i.putExtra("timerServiceAction", "Stop");
                startService(i);
                timer.setTask(null);
                if(timer.getState() != STOP){
                    timer.stop();
                }
                finish();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (timer.getState()){
                    case STOP:
                        Intent i = new Intent(getBaseContext(), MyService.class);
                        i.putExtra("timerServiceAction", "Start");
                        startService(i);
                        timer.start();
                        break;
                    case WORK:
                        timer.stop();
                        break;
                    case OVERWORK:
                        timer.pause();
                        break;
                    case PAUSE:
                        timer.start();
                        break;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.removeListener(timerListener);
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

    private String formatTime(long time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
    }

    private String formatDurationTime(long time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh'h' mm'm'", Locale.getDefault());
        return timeFormat.format(time);
    }

    private SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private App getApp() {
        return (App) getApplication();
    }
}
