package com.androidacademy.team5.zebratime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
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
import static com.androidacademy.team5.zebratime.Timer.State.WORK;
import static com.androidacademy.team5.zebratime.TimerActivity.TIMER_SERVICE_ACTION;

public class TimerService extends Service {

    public TimerService() {
    }

    private static final String LOG_TAG = "ForegroundService";


    public static final String WITHOUT_SOUND = "withoutSound";
    public static final String WITH_SOUND = "withSound";
    public static final int TIMER_NOTIFICATION_ID = 6312;
    public static final String NOTIFICATION_AB_CLICK = "actionButtonClick";
    private static final String CHANNEL_ID = "timer_channel";
    public Timer timer;

    private long workTime;
    private long shortBreakTime;

    private Timer.TimerListener timerListener = new Timer.TimerListener() {
        @Override
        public void onTick(Timer timer) {

            synchronizePreferredTimes();

            String taskTitle = timer.getTask().getTitle();
            switch (timer.getState()) {
                case STOP:
                    if (timer.getPrevState() == WORK) {
                        updateNotification(formatTime(workTime), taskTitle, WITHOUT_SOUND);
                    } else {
                        updateNotification(formatTime(workTime), taskTitle, WITH_SOUND);
                    }
                    break;
                case WORK:
                    long passedTime = System.currentTimeMillis() - timer.getStartTime();
                    updateNotification(formatTime(workTime - passedTime), taskTitle, WITHOUT_SOUND);
                    break;
                case OVERWORK:
                    updateNotification(formatTime(shortBreakTime), taskTitle, WITH_SOUND);
                    break;
                case PAUSE:
                    long passedBreakTime = System.currentTimeMillis() - timer.getEndTime();
                    updateNotification(formatTime((shortBreakTime - passedBreakTime)), taskTitle, WITHOUT_SOUND);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
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

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra(TIMER_SERVICE_ACTION)) {
            String serviceAction = intent.getExtras().getString(TIMER_SERVICE_ACTION);
            synchronizePreferredTimes();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (serviceAction.equals("Start")) {
                updateNotification(formatTime(workTime), timer.getTask().getTitle(), WITHOUT_SOUND);
            } else {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                notificationManager.cancel(TIMER_NOTIFICATION_ID);
                stopSelf();
            }
        } else if (intent.hasExtra(NOTIFICATION_AB_CLICK)
                && intent.getBooleanExtra(NOTIFICATION_AB_CLICK, false)) {

            switch (timer.getState()) {
                case STOP:
                    timer.start(workTime);
                    break;
                case WORK:
                    timer.stop();
                    break;
                case OVERWORK:
                    timer.startBreak(shortBreakTime);
                    break;
                case PAUSE:
                    timer.stopBreak();
                    timer.start(workTime);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private String formatTime(long time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return timeFormat.format(time);
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


    private void updateNotification(String time, String message, String mode) {

        createNotificationChannel();

        Intent resultIntent = new Intent(this, TimerActivity.class);
        Task task = timer.getTask();
        resultIntent.putExtra(PROJECT_ID, task.getProjectId());
        resultIntent.putExtra(TASK_ID, task.getId());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent actionButtonIntent = new Intent(this, TimerService.class);
        actionButtonIntent.putExtra(NOTIFICATION_AB_CLICK, true);
        PendingIntent aBPendingIntent =
                PendingIntent.getService(this, 0, actionButtonIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(time)
                .setContentText(message)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setShowWhen(false);

        switch (timer.getState()) {
            case STOP:
                builder.addAction(R.drawable.ic_stat_access_time,
                        getString(R.string.start),
                        aBPendingIntent);
                break;
            case WORK:
                builder.addAction(R.drawable.ic_stat_access_time,
                        getString(R.string.stop),
                        aBPendingIntent);
                break;
            case OVERWORK:
                builder.addAction(R.drawable.ic_stat_access_time,
                        getString(R.string.take_break),
                        aBPendingIntent);
                break;
            case PAUSE:
                builder.addAction(R.drawable.ic_stat_access_time,
                        getString(R.string.start),
                        aBPendingIntent);
                break;
        }


        switch (mode) {
            case WITHOUT_SOUND:
                break;
            case WITH_SOUND:
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
                break;
        }

        Notification notification = builder.build();
        startForeground(TIMER_NOTIFICATION_ID, notification);
    }


    private void synchronizePreferredTimes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        workTime = 60 * 1000 * Long.valueOf(preferences.getString(getString(R.string.work_time_key), "25"));
        shortBreakTime = 60 * 1000 * Long.valueOf(preferences.getString(getString(R.string.short_rest_key), "5"));
    }

    private App getApp() {
        return (App) getApplication();
    }
}


