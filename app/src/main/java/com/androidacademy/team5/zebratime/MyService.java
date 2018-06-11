package com.androidacademy.team5.zebratime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 1;

    private static final String LOG_TAG = "ForegroundService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Start service:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (intent.getAction().equals("Start")) {
                Log.i(LOG_TAG, "Received Start Foreground Intent");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                String channelId = "some_channel_id";
                CharSequence channelName = "Some Channel";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(notificationChannel);

                Notification notification = new Notification.Builder(this)
                        .setContentTitle("Some Message")
                        .setContentText("You've received new messages!")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setChannelId(channelId)
                        .build();

                startForeground(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification);
            } else {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        } else {
            if (intent.getAction().equals("Start")) {
                Log.i(LOG_TAG, "Received Start Foreground Intent");

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                        .setContentTitle("ZebraTime")
                        .setContentText("MyZebra")
                        .setSmallIcon(R.drawable.ic_launcher_foreground);

                startForeground(101, notification.build());
            } else {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

}
