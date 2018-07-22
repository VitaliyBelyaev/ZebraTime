package com.androidacademy.team5.zebratime;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class App extends Application{

    Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = Timer.newInstance();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
