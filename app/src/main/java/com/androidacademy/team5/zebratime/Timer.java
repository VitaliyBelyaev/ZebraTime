package com.androidacademy.team5.zebratime;

import android.os.CountDownTimer;

import com.androidacademy.team5.zebratime.entity.Session;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.androidacademy.team5.zebratime.Timer.State.OVERWORK;
import static com.androidacademy.team5.zebratime.Timer.State.STOP;


public class Timer {
    private Task task;
    private State state;
    Session newSession;
    CountDownTimer internalTimer;
    CountDownTimer breakTimer;

    long startTime;
    long endTime;

    enum State {
        STOP, PAUSE, WORK, OVERWORK
    }

    private Timer() {
    }

    public static Timer initInstance() {
        Timer timer = new Timer();
        timer.task = null;
        timer.state = STOP;
        timer.newSession = new Session();

        return timer;
    }

    Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    void start(CountDownTimer internalTimer) {
        this.internalTimer = internalTimer;
        if(state == STOP){
            state = State.WORK;
            internalTimer.start();
            startTime = System.currentTimeMillis();
            newSession.setStartDate(startTime);
        }
    }

    void stop() {
        if(state != STOP){
            state = STOP;
            internalTimer.cancel();
            endTime = System.currentTimeMillis();
            newSession.setDuration(endTime - startTime);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference().child("Sessions");
            newSession.setId(mRef.push().getKey());
            newSession.setIdTask(task.getId());
            mRef.child(newSession.getId()).setValue(newSession);
        }

    }

    void pause(CountDownTimer breakTimer) {
        this.breakTimer = breakTimer;
        if(state == OVERWORK){
            breakTimer.start();

            state = State.PAUSE;
            endTime = System.currentTimeMillis();
            newSession.setDuration(endTime - startTime);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference().child("Sessions");
            newSession.setId(mRef.push().getKey());
            newSession.setIdTask(task.getId());
            mRef.child(newSession.getId()).setValue(newSession);
        }
    }

}
