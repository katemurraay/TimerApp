package com.kmm.a117349221_assignment2;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

public class TimerApp extends Application {

    /* Code below is based on:
    Github Repository: "Notifications Tutorial: Part 1,
    codinginflow,
    https://gist.github.com/codinginflow/33e2ef8270892acca1ce7cab955ee3d3
     */
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
                    ); //END


            timerChannel.setDescription("Timer Completed");

         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(timerChannel);
        }
    }
}
