package com.androidacademy.team5.zebratime;

import android.os.CountDownTimer;

import com.androidacademy.team5.zebratime.domain.Session;
import com.androidacademy.team5.zebratime.domain.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.androidacademy.team5.zebratime.Timer.State.OVERWORK;
import static com.androidacademy.team5.zebratime.Timer.State.PAUSE;
import static com.androidacademy.team5.zebratime.Timer.State.STOP;
import static com.androidacademy.team5.zebratime.Timer.State.WORK;


public class Timer {

    private Task task;
    private State state;
    private State prevState;
    private long startTime;
    private long endTime;
    private Session session;
    private ArrayList<TimerListener> listeners = new ArrayList<>();
    private static final long COUNT_DOWN_INTERVAL = 1000;
    private CountDownTimer workTimer;
    private CountDownTimer breakTimer;

    enum State {
        STOP, PAUSE, WORK, OVERWORK
    }

    interface TimerListener {
        void onTick(Timer timer);
    }

    private Timer() {
    }


    public static Timer newInstance() {
        Timer timer = new Timer();
        timer.task = null;
        timer.prevState = null;
        timer.state = STOP;
        timer.session = new Session();

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

    public State getPrevState() {
        return prevState;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }




    void start(long workTimeInMillis) {
        if (state == STOP) {
            prevState = getState();
            state = WORK;
            breakTimer = null;
            workTimer = getWorkTimer(workTimeInMillis).start();
            startTime = System.currentTimeMillis();
            session.setStartDate(startTime);
            notifyListeners();
        }
    }

    void stop() {
        if (state != STOP) {
            prevState = getState();
            state = STOP;
            workTimer.cancel();
            workTimer = null;
            endTime = System.currentTimeMillis();
            session.setDuration(endTime - startTime);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference().child("Sessions");
            session.setId(mRef.push().getKey());
            session.setIdTask(task.getId());
            mRef.child(session.getId()).setValue(session);
            notifyListeners();
        }

    }

    void startBreak(long breakTimeInMillis) {
        if (state == OVERWORK) {
            prevState = getState();
            state = PAUSE;
            breakTimer = getBreakTimer(breakTimeInMillis);
            breakTimer.start();
            endTime = System.currentTimeMillis();
            session.setDuration(endTime - startTime);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference().child("Sessions");
            session.setId(mRef.push().getKey());
            session.setIdTask(task.getId());
            mRef.child(session.getId()).setValue(session);
            notifyListeners();
        }
    }

    void stopBreak(){
        if(state == PAUSE){
            prevState = getState();
            state = STOP;
            breakTimer.cancel();
            notifyListeners();
        }
    }

    void addListener(TimerListener listener) {
        listeners.add(listener);
    }

    void removeListener(TimerListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (TimerListener listener : listeners) {
            listener.onTick(this);
        }
    }

    private CountDownTimer getWorkTimer(long workTimeInMillis){
        return new CountDownTimer(workTimeInMillis, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                notifyListeners();
            }

            @Override
            public void onFinish() {
                prevState = getState();
                state = OVERWORK;
                notifyListeners();
            }
        };
    }

    private CountDownTimer getBreakTimer(long breakTimeInMillis){
        return new CountDownTimer(breakTimeInMillis, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                notifyListeners();
            }

            @Override
            public void onFinish() {
                prevState = getState();
                state = STOP;
                notifyListeners();
            }
        };
    }

}
