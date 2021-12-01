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
        //https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/

        mpref = getSharedPreferences(IConstants.TIMER_PREFERENCES, MODE_PRIVATE);
        editor = mpref.edit();

        long millis = mpref.getLong(IConstants.MILLIS_LEFT, 1000);
         Log.d("MILLIS", String.valueOf(millis));
        cdt = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                timeLeft = millisUntilFinished;
                long endtime = System.currentTimeMillis() + millisUntilFinished;
                editor.putLong(IConstants.END_TIME, endtime).apply();
                bi.putExtra(IConstants.TIME_LEFT, millisUntilFinished);
                bi.putExtra(IConstants.IS_RUNNING, true);
                sendBroadcast(bi);
            }


            @Override
            public void onFinish() {
                bi.putExtra(IConstants.IS_RUNNING, false);
                sendBroadcast(bi);
                Log.i(TAG, "Timer finished");
            }
        };

                    cdt.start();

    }
    @Override
    public void onDestroy() {
        cdt.cancel();
        editor.putLong(IConstants.PAUSED_TIME, timeLeft).apply();

        super.onDestroy();
    }


}
