package com.kmm.a117349221_assignment2.timer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kmm.a117349221_assignment2.IConstants;

import java.util.Calendar;

public class TimerService extends Service {
    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.kmm.a117349221_assignment2.timer";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt =null;
    private Handler handler = new Handler();
    Calendar calendar;
    SharedPreferences mpref;
    SharedPreferences.Editor editor;
    private long initial_time;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting timer...");
        //https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/
        mpref = getSharedPreferences(IConstants.TIMER_PREFERENCES, MODE_PRIVATE);
        editor = mpref.edit();
        long millis = mpref.getLong(IConstants.MILLIS_LEFT, 1000);
        cdt = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra(IConstants.TIME_LEFT, millisUntilFinished);
                editor.putLong(IConstants.MILLIS_LEFT, millisUntilFinished).apply();
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        };

        cdt.start();
    }
    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

}
