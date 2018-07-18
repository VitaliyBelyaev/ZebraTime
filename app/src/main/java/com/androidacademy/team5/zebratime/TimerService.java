package com.androidacademy.team5.zebratime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.androidacademy.team5.zebratime.domain.Task;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.androidacademy.team5.zebratime.MainActivity.PROJECT_ID;
import static com.androidacademy.team5.zebratime.MainActivity.TASK_ID;

public class TimerService extends Service {

    public TimerService() {
    }

    private static final String LOG_TAG = "ForegroundService";

    public static final int TIMER_NOTIFICATION_ID = 6312;
    private static final String CHANNEL_ID = "timer_channel";
    public Timer timer;

    public class TimerBinder extends Binder{
        TimerService getTimerService(){
            return TimerService.this;
        }
    }



    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            long workTime = 60 * 1000 * Long.valueOf(getSharedPreferences().getString(getString(R.string.work_time_key), "25"));
            long shortBreakTime = 60 * 1000 *Long.valueOf(getSharedPreferences().getString(getString(R.string.short_rest_key), "5"));
            String taskTitle = timer.getTask().getTitle();
            switch (timer.getState()) {
                case STOP:
                    showNotification(formatTime(workTime), taskTitle);
                    break;
                case WORK:
                    long passedTime = System.currentTimeMillis() - timer.getStartTime();
                    showNotification(formatTime(workTime - passedTime), taskTitle);
                    break;
                case OVERWORK:
                    showNotification(formatTime(shortBreakTime), taskTitle);
                    break;
                case PAUSE:
                    long passedBreakTime = System.currentTimeMillis() - timer.getEndTime();
                    showNotification(formatTime((shortBreakTime - passedBreakTime)), taskTitle);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("BINDING","in TimerService onCreate");
        timer = getApp().timer;
        timer.addListener(timerListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.removeListener(timerListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("BINDING","in onBind");
        return new TimerBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String serviceAction = intent.getExtras().getString("timerServiceAction");
            long workTime = 60 * 1000 * Long.valueOf(getSharedPreferences().getString(getString(R.string.work_time_key), "25"));
            long shortBreakTime = 60 * 1000 *Long.valueOf(getSharedPreferences().getString(getString(R.string.short_rest_key), "5"));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (serviceAction.equals("Start")) {
                showNotification(formatTime(workTime), timer.getTask().getTitle());
            } else {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                notificationManager.cancel(TIMER_NOTIFICATION_ID);
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    private String formatTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ZebraTime";
            String description = "Channel for foreground notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String time, String message) {

        createNotificationChannel();

        Intent resultIntent = new Intent(this, TimerActivity.class);
        Task task = timer.getTask();
        resultIntent.putExtra(PROJECT_ID, task.getProjectId());
        resultIntent.putExtra(TASK_ID, task.getId());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(time)
                .setContentText(message)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = builder.build();

        startForeground(TIMER_NOTIFICATION_ID, notification);
    }

    private App getApp(){
        return (App) getApplication();
    }
}


