package com.kmm.a117349221_assignment2.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.IConstants;
import com.kmm.a117349221_assignment2.R;
import com.kmm.a117349221_assignment2.clock.ClockActivity;

import static com.kmm.a117349221_assignment2.IConstants.CHANNEL_TIMER_ID;
import static com.kmm.a117349221_assignment2.IConstants.END_TIME;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PAUSED;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PREFERENCES;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_RUNNING;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_STATE;
import static com.kmm.a117349221_assignment2.IConstants.TIME_AT_PAUSE;
import static com.kmm.a117349221_assignment2.IConstants.TIME_SET;

public class TimerSetActivity extends AppCompatActivity {
TimerSurfaceView timer = null;

    private NotificationManagerCompat notificationManager;
    private Boolean timerRunning, timerPaused;
    private Button btnPause, btnCancel;
    private FrameLayout flTimer;
    private RelativeLayout rlTimerSet;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_set);
        btnCancel = findViewById(R.id.btnCancel);
        btnPause = findViewById(R.id.btnPauseResume);
        rlTimerSet = findViewById(R.id.rlTimerSet);
        flTimer = findViewById(R.id.flTimer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        btnPause.setOnClickListener((v)->{
            if(timerPaused){
                resumeTimer();
            } else{
                pauseTime();
            }
        });
        btnCancel.setOnClickListener((v)->{
            cancelTimer();
        });
        notificationManager = NotificationManagerCompat.from(this);
        SharedPreferences preferences = getSharedPreferences(IConstants.TIMER_PREFERENCES, MODE_PRIVATE);
        long endTime = preferences.getLong(IConstants.END_TIME, 0);
        long millisLeft = endTime - System.currentTimeMillis();
        timer = new TimerSurfaceView(this, 350, endTime, millisLeft);
        flTimer.removeAllViews();
        flTimer.addView(timer);
        flTimer.addView(rlTimerSet);
        setContentView(flTimer);

    }

    @Override
    protected void onResume(){
        super.onResume();
        timer.onResumeTimer();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.COUNTDOWN_BR));

    }
    @Override
    protected void onPause(){
        super.onPause();
        timer.onPauseTimer();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        boolean isRunning = intent.getBooleanExtra(TIMER_STATE, false);
        SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if(isRunning){
             timerRunning = true;
        } else{
            //set notification
            timerRunning =false;
            String title = "TIMER";
            String message= "Timer Completed";

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_TIMER_ID)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();
            notificationManager.notify(1, notification);
            cancelTimer();
        }
        editor.putBoolean(TIMER_RUNNING, timerRunning).apply();
    }
        private void resumeTimer(){
            //https://gist.github.com/codinginflow/61e9cec706e7fe94b0ca3fffc0253bf2

            SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            long millisLeft = prefs.getLong(TIME_AT_PAUSE, 1000);
            long endTime = millisLeft + System.currentTimeMillis();
            editor = prefs.edit();
            editor.clear().apply();
            editor.putLong(END_TIME, endTime).apply();
            editor.putBoolean(TIMER_RUNNING, true).apply();
            editor.putBoolean(TIMER_PAUSED, false).apply();
            editor.putLong(TIME_SET, millisLeft).apply();
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);
            btnPause.setText(getResources().getString(R.string.btn_pause));
            timerPaused = false;
            timer.setTime(endTime, millisLeft);
            timer.onResumeTimer();




        }

        private void pauseTime(){
            SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            long timeLeft = prefs.getLong(END_TIME, 0);
            timeLeft = timeLeft - System.currentTimeMillis();
            Log.d("pauseTime", String.valueOf(timeLeft));
            btnPause.setText(getResources().getString(R.string.btn_restart));
            timerPaused = true;
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            stopService(intent);
            editor.clear().apply();
            editor.putBoolean(TIMER_RUNNING, true).apply();
            editor.putBoolean(TIMER_PAUSED, timerPaused).apply();
            editor.putLong(TIME_AT_PAUSE, timeLeft).apply();
            timer.onPauseTimer();


        }

        private void cancelTimer(){
            timerRunning = false;
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            stopService(intent);
            SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
            Intent activity = new Intent(TimerSetActivity.this, TimerActivity.class);
            startActivity(activity);
            overridePendingTransition(0,0);
            finish();

        }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()) {
                        case R.id.nav_clock:
                            Intent intent = new Intent(TimerSetActivity.this, ClockActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();
                            break;

                        case R.id.nav_timer:
                            break;

                    }


                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        timerRunning = preferences.getBoolean(TIMER_RUNNING, false);
        timerPaused = preferences.getBoolean(TIMER_PAUSED, false);
    }
}