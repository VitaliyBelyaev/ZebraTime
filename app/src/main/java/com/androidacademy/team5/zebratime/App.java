package com.androidacademy.team5.zebratime;

import android.app.Application;

public class App extends Application {

    Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = Timer.initInstance();
    }
}
