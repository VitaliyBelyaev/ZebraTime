package com.androidacademy.team5.zebratime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyService extends Service {

    public MyService() {
    }

    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 6312;

    private static final String LOG_TAG = "ForegroundService";
    private Timer timer;

    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            long workTime = 60 * 1000 * getSharedPreferences().getLong("workTime", 25);
            long shortBreakTime = 60 * 1000 * getSharedPreferences().getLong("shortTime", 5);
            String taskTitle = timer.getTask().getTitle();
            switch (timer.getState()) {
                case STOP:
                    showNotification(formatTime(workTime), taskTitle);
                    break;
                case WORK:
                    long passedTime = System.currentTimeMillis() - timer.startTime;
                    showNotification(formatTime(workTime - passedTime), taskTitle);
                    break;
                case OVERWORK:
                    showNotification(formatTime(shortBreakTime), taskTitle);
                    break;
                case PAUSE:
                    long passedBreakTime = System.currentTimeMillis() - timer.endTime;
                    showNotification(formatTime((shortBreakTime - passedBreakTime)), taskTitle);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timer = ((App) getApplication()).timer;
        timer.addListener(timerListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.removeListener(timerListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            long workTime = 60 * 1000 * getSharedPreferences().getLong("workTime", 25);
            String arg = intent.getExtras().getString("timerServiceAction");
            long shortBreakTime = 60 * 1000 * getSharedPreferences().getLong("shortTime", 5);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Start service:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (arg.equals("Start")) {
                    showNotification(formatTime(workTime), timer.getTask().getTitle());
                } else {
                    Log.i(LOG_TAG, "Received Stop Foreground Intent");
                    stopForeground(true);
                    notificationManager.cancel(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID);
                    stopSelf();
                }
            } else {
                if (arg.equals("Start")) {
                    showNotification(formatTime(workTime), timer.getTask().getTitle());
                } else {
                    Log.i(LOG_TAG, "Received Stop Foreground Intent");
                    stopForeground(true);
                    notificationManager.cancel(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID);
                    stopSelf();
                }
            }
        }
        return START_STICKY;
    }

    private String formatTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
    }

    private SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void showNotification(String time, String message) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(LOG_TAG, "Received Start Foreground Intent");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel("some_channel_id", "Some Channel", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle(time)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setOngoing(true)
                    .setContentIntent(resultPendingIntent)
                    .setChannelId("some_channel_id")
                    .build();
            notificationManager.notify(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification);
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentTitle(time)
                    .setContentText(message)
                    .setOngoing(true)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification.build());
        }
    }



}


