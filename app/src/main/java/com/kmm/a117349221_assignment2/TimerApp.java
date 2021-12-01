package com.kmm.a117349221_assignment2;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class TimerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel timerChannel = new NotificationChannel(IConstants.CHANNEL_TIMER_ID,
                    "Timer",
                    NotificationManager.IMPORTANCE_HIGH
                    );
            timerChannel.setDescription("Timer Completed");
         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(timerChannel);
        }
    }
}
