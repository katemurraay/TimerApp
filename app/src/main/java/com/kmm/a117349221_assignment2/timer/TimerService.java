package com.kmm.a117349221_assignment2.timer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kmm.a117349221_assignment2.IConstants;

public class TimerService extends Service {
    private final static String TAG = "TimerService";

    public static final String COUNTDOWN_BR = "com.kmm.a117349221_assignment2.timer";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt =null;

    SharedPreferences mpref;
    SharedPreferences.Editor editor;
    long timeLeft;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting timer...");

        /*Code below is based on:
        Web Article: "Android Countdown Timer Run In Background",
        https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/
         */

        mpref = getSharedPreferences(IConstants.TIMER_PREFERENCES, MODE_PRIVATE);
        editor = mpref.edit();

        long millis = mpref.getLong(IConstants.TIME_SET, 1000);
        Log.d(TAG, "Time: "+ String.valueOf(millis));
        cdt = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;


                bi.putExtra(IConstants.TIME_LEFT, millisUntilFinished);
                bi.putExtra(IConstants.TIMER_STATE, true);
                sendBroadcast(bi);
            }


            @Override
            public void onFinish() {
                bi.putExtra(IConstants.TIMER_STATE, false);
                sendBroadcast(bi);
                Log.i(TAG, "Timer finished");
            }
        };

                    cdt.start();
        //END
    }
    @Override
    public void onDestroy() {
        cdt.cancel();
        editor.putLong(IConstants.TIME_AT_PAUSE, timeLeft).apply();
        super.onDestroy();
    }


}
