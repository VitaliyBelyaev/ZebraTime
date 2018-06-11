package com.androidacademy.team5.zebratime;

import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Log;

import com.androidacademy.team5.zebratime.entity.Session;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.TimerTask;


enum State {
    STOP, PAUSE, WORK, OVERWORK
}

public class Timer {
    private Task task = new Task("fertyuytd", "gfghgghf", "gfsdhjhhj");
    private State state;
    Session newSession = new Session();

    long startTime;
    long endTime;

    CountDownTimer timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i("TimerTest", "seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                stop();
                state = State.OVERWORK;
                Log.i("TimerTest", "Done!");
            }
        };

    CountDownTimer timerRest = new CountDownTimer(2000, 1000) {

        public void onTick(long millisUntilFinished) {
            Log.i("TimerTest", "seconds remaining: " + millisUntilFinished / 1000);
        }

        public void onFinish() {
            state = State.STOP;
            Log.i("TimerTest", "Done!");
        }
    };
    private Timer(){}
    public static Timer initInstance(){
        return new Timer();
    }

    Task getTask(){
        return this.task;
    }

    void setTask(Task task){
        this.task = task;
    }

    void start(){
        state = State.WORK;
        timer.start();
        startTime = System.currentTimeMillis();
        newSession.setStartDate(startTime);
    }

    void stop(){
        state = State.STOP;
        timer.cancel();
        endTime = System.currentTimeMillis();
        newSession.setDuration(endTime-startTime);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("Sessions");
        newSession.setId(mRef.push().getKey());
        newSession.setIdTask(task.getId());
        mRef.child(newSession.getId()).setValue(newSession);
    }

    void pause(){
        timerRest.start();

        state = State.PAUSE;
        endTime = System.currentTimeMillis();
        newSession.setDuration(endTime-startTime);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("Sessions");
        newSession.setId(mRef.push().getKey());
        newSession.setIdTask(task.getId());
        mRef.child(newSession.getId()).setValue(newSession);
    }

}
