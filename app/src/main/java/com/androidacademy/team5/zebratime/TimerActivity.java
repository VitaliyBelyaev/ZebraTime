package com.androidacademy.team5.zebratime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidacademy.team5.zebratime.domain.Session;
import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.androidacademy.team5.zebratime.MainActivity.PROJECT_ID;
import static com.androidacademy.team5.zebratime.MainActivity.TASK_ID;

public class TimerActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView timeTextView;
    private Button actionButton;
    private TextView taskTitleTextView;
    private TextView taskDurationTextView;
    private Task task;
    private Timer timer;
    private Button endTaskButton;
    private long workTime;
    private long shortBreakTime;
    private long longBreakTime;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("Tasks");
    private DatabaseReference sessionsRef = database.getReference("Sessions");

    public static final String TIMER_SERVICE_ACTION = "timerServiceAction";


    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            switch (timer.getState()) {
                case STOP:
                    actionButton.setText("Start");
                    timeTextView.setText(formatTime(workTime));
                    break;
                case WORK:
                    actionButton.setText("Stop");
                    long passedTime = System.currentTimeMillis() - timer.getStartTime();
                    timeTextView.setText(formatTime(workTime - passedTime));
                    break;
                case OVERWORK:
                    actionButton.setText("Take break");
                    timeTextView.setText(formatTime(shortBreakTime));
                    break;
                case PAUSE:
                    actionButton.setText("START");
                    long passedBreakTime = System.currentTimeMillis() - timer.getEndTime();
                    timeTextView.setText(formatTime(shortBreakTime - passedBreakTime));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        synchronizePreferredTimes();

        timeTextView = findViewById(R.id.tv_time);
        actionButton = findViewById(R.id.action_button);
        taskTitleTextView = findViewById(R.id.tv_timer_task_title);
        taskDurationTextView = findViewById(R.id.tv_timer_task_duration);
        endTaskButton = findViewById(R.id.end_task_button);

        timer = getApp().timer;

        switch (timer.getState()) {
            case STOP:
                actionButton.setText("Start");
                timeTextView.setText(formatTime(workTime));
                break;
            case WORK:
                actionButton.setText("Stop");
                long passedTime = System.currentTimeMillis() - timer.getStartTime();
                timeTextView.setText(formatTime(workTime - passedTime));
                break;
            case OVERWORK:
                actionButton.setText("Take break");
                timeTextView.setText(formatTime(shortBreakTime));
                break;
            case PAUSE:
                actionButton.setText("START");
                long passedBreakTime = System.currentTimeMillis() - timer.getEndTime();
                timeTextView.setText(formatTime(shortBreakTime - passedBreakTime));
                break;
        }


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
            task = timer.getTask();
            showTaskInfo(task);
        }

        endTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getBaseContext(), TimerService.class);
                serviceIntent.putExtra(TIMER_SERVICE_ACTION, "Stop");
                startService(serviceIntent);
                timer.stop();
                timer.setTask(null);
                Intent activityIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(activityIntent);
                finish();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (timer.getState()) {
                    case STOP:
                        Intent i = new Intent(getBaseContext(), TimerService.class);
                        i.putExtra(TIMER_SERVICE_ACTION, "Start");
                        startService(i);
                        timer.start(workTime);
                        break;
                    case WORK:
                        timer.stop();
                        break;
                    case OVERWORK:
                        timer.startBreak(shortBreakTime);
                        break;
                    case PAUSE:
                        timer.stopBreak();
                        timer.start(workTime);
                        break;
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer.addListener(timerListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.removeListener(timerListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.work_time_key))) {
            String workTime = sharedPreferences.getString(key, "25");
            this.workTime = 60 * 1000 * Long.valueOf(workTime);
            timeTextView.setText(formatTime(this.workTime));
        }

        if (key.equals(getString(R.string.short_rest_key))) {
            String workTime = sharedPreferences.getString(key, "5");
            this.shortBreakTime = 60 * 1000 * Long.valueOf(workTime);
        }
    }

    private void showTaskInfo(Task task) {
        taskTitleTextView.setText(task.getTitle());
        sessionsRef.orderByChild("idTask")
                .equalTo(task.getId())
                .addValueEventListener(createSessionsListener());

        timeTextView.setText(formatTime(workTime));
    }

    private ValueEventListener createSessionsListener() {

        ValueEventListener sessionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Session> sessions = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for (DataSnapshot snapshot : snapshots) {
                    sessions.add(snapshot.getValue(Session.class));
                }
                long duration = 0;
                for (Session session : sessions) {
                    duration = duration + session.getDuration();
                }

                taskDurationTextView.setText(formatDurationTime(duration));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        return sessionsListener;
    }

    private String formatTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
    }

    private String formatDurationTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh'h' mm'm'", Locale.getDefault());
        return timeFormat.format(time);
    }


    private void synchronizePreferredTimes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        workTime = 60 * 1000 * Long.valueOf(preferences.getString(getString(R.string.work_time_key), "25"));
        shortBreakTime = 60 * 1000 * Long.valueOf(preferences.getString(getString(R.string.short_rest_key), "5"));
    }


    private App getApp() {
        return (App) getApplication();
    }


}
