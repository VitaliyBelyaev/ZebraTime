package com.androidacademy.team5.zebratime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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
import java.util.List;
import java.util.Locale;

import static com.androidacademy.team5.zebratime.MainActivity.PROJECT_ID;
import static com.androidacademy.team5.zebratime.MainActivity.TASK_ID;

public class TimerActivity extends AppCompatActivity {

    private TextView timeTextView;
    private Button actionButton;
    private TextView taskTitleTextView;
    private TextView taskDurationTextView;
    private Task task;
    private Timer timer;
    private Button endTaskButton;
    private boolean bound = false;
    private TimerService tService;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tasksRef = database.getReference("Tasks");
    public static final String TIMER_SERVICE_ACTION = "timerServiceAction";

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            Log.i("BINDING", "in onserviceConnected");
            tService = binder.getTimerService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            long workTime = 60 * 1000 * Long.valueOf(getSharedPreferences().getString(getString(R.string.work_time_key), "25"));
            long shortBreakTime = 60 * 1000 *Long.valueOf(getSharedPreferences().getString(getString(R.string.short_rest_key), "5"));

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
                    timeTextView.setText(String.valueOf((shortBreakTime - passedBreakTime)));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Log.i("BINDING", "TA onCreate");
        Intent intent = new Intent(getBaseContext(), TimerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        timeTextView = findViewById(R.id.tv_time);
        actionButton = findViewById(R.id.action_button);
        taskTitleTextView = findViewById(R.id.tv_timer_task_title);
        taskDurationTextView = findViewById(R.id.tv_timer_task_duration);
        endTaskButton = findViewById(R.id.end_task_button);

        actionButton.setText("Start");

        timer = getApp().timer;

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
                Intent i = new Intent(getBaseContext(), TimerService.class);
                i.putExtra(TIMER_SERVICE_ACTION, "Stop");
                startService(i);
                timer.stop();
                timer.setTask(null);
                finish();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long workTime = 60 * 1000 * Long.valueOf(getSharedPreferences().getString(getString(R.string.work_time_key), "25"));
                long shortBreakTime = 60 * 1000 *Long.valueOf(getSharedPreferences().getString(getString(R.string.short_rest_key), "5"));
                switch (timer.getState()) {
                    case STOP:
                        timer.addListener(timerListener);
                        Intent i = new Intent(getBaseContext(), TimerService.class);
                        i.putExtra(TIMER_SERVICE_ACTION, "Start");
                        startService(i);
                        timer.start(workTime);
                        break;
                    case WORK:
                        timer.stop();
                        break;
                    case OVERWORK:
                        timer.pause(shortBreakTime);
                        break;
                    case PAUSE:
                        timer.start(workTime);
                        break;
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("BINDING", "TA onStart, tService: "+tService);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        bound = false;
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

    private String formatTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
    }

    private String formatDurationTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh'h' mm'm'", Locale.getDefault());
        return timeFormat.format(time);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private App getApp(){
        return (App) getApplication();
    }

}
