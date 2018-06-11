package com.androidacademy.team5.zebratime;

import android.os.CountDownTimer;

import com.androidacademy.team5.zebratime.entity.Session;
import com.androidacademy.team5.zebratime.entity.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.androidacademy.team5.zebratime.Timer.State.OVERWORK;
import static com.androidacademy.team5.zebratime.Timer.State.STOP;


public class Timer {
    private Task task;
    private State state;
    Session newSession;
    ArrayList<TimerListener> listeners = new ArrayList<>();

    long startTime;
    long endTime;

    enum State {
        STOP, PAUSE, WORK, OVERWORK
    }

    interface TimerListener{
        void onTick(Timer timer);
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


    final CountDownTimer internalTimer = new CountDownTimer(50000, 1000) {

        public void onTick(long millisUntilFinished) {
            notifyListeners();
        }

        public void onFinish() {
            notifyListeners();
            state = OVERWORK;
        }
    };

    final CountDownTimer breakTimer = new CountDownTimer(7000, 1000) {

        public void onTick(long millisUntilFinished) {
            notifyListeners();
        }

        public void onFinish() {
            notifyListeners();
        }
    };

    void start() {
        if(state == STOP){
            state = State.WORK;
            internalTimer.start();
            startTime = System.currentTimeMillis();
            newSession.setStartDate(startTime);
            notifyListeners();
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
            notifyListeners();
        }

    }

    void pause() {
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
            notifyListeners();
        }
    }

    void addListener(TimerListener listener){
        listeners.add(listener);
    }

    void removeListener(TimerListener listener){
        listeners.remove(listener);
    }

    private void notifyListeners(){
        for(TimerListener listener:listeners){
            listener.onTick(this);
        }
    }
}
