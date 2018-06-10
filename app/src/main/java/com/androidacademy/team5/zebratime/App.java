package com.androidacademy.team5.zebratime;

import android.app.Application;
import android.os.Bundle;

public class App extends Application {

    Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = Timer.initInstance();
    }
}
