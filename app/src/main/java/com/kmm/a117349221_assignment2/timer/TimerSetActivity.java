package com.kmm.a117349221_assignment2.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.IConstants;
import com.kmm.a117349221_assignment2.R;
import com.kmm.a117349221_assignment2.clock.ClockActivity;

import java.util.Locale;
import java.util.function.LongBinaryOperator;

import static com.kmm.a117349221_assignment2.IConstants.CHANNEL_TIMER_ID;
import static com.kmm.a117349221_assignment2.IConstants.END_TIME;
import static com.kmm.a117349221_assignment2.IConstants.IMAGE_IN_VIEW;
import static com.kmm.a117349221_assignment2.IConstants.STATIC_TIMER;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PAUSED;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PREFERENCES;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_RUNNING;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_STATE;
import static com.kmm.a117349221_assignment2.IConstants.TIME_AT_PAUSE;
import static com.kmm.a117349221_assignment2.IConstants.TIME_SET;

public class TimerSetActivity extends AppCompatActivity {
    TimerSurfaceView timer = null;


    private Boolean timerRunning, timerPaused, imageTimer;
    private Button btnPause, btnCancel;
    private FrameLayout flTimer;
    private RelativeLayout rlTimerSet;
    private BottomNavigationView bottomNavigationView;
    private TextView tvTimer;
    private long millisLeft;


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
        tvTimer = findViewById(R.id.tvTimer);
        btnPause.setOnClickListener((v)->{
            if(timerPaused){
                resumeTimer();
                updateTextView(true);
            } else{
                pauseTime();
            }
        });
        btnCancel.setOnClickListener((v)->{
            cancelTimer();
        });

        SharedPreferences preferences = getSharedPreferences(IConstants.TIMER_PREFERENCES, MODE_PRIVATE);
        millisLeft = preferences.getLong(IConstants.END_TIME, 0);
        long endTime = millisLeft + System.currentTimeMillis();

        timer = new TimerSurfaceView(this, 350, endTime);
        flTimer.removeAllViews();
        flTimer.addView(timer);
        flTimer.addView(rlTimerSet);
        setContentView(flTimer);

    }

    @Override
    protected void onResume(){
        super.onResume();
        timer.onResumeTimer(timerPaused);
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
//https://gist.github.com/codinginflow/33e2ef8270892acca1ce7cab955ee3d3
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, IConstants.CHANNEL_TIMER_ID)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(this);
            notificationCompat.notify(123, builder.build());
             cancelTimer();
        }
        editor.putBoolean(TIMER_RUNNING, timerRunning).apply();
    }
        private void resumeTimer(){
            //https://gist.github.com/codinginflow/61e9cec706e7fe94b0ca3fffc0253bf2

            SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            millisLeft = prefs.getLong(TIME_AT_PAUSE, 1000);
            long endTime = millisLeft + System.currentTimeMillis();
            editor = prefs.edit();
            editor.clear().apply();
            editor.putLong(END_TIME, endTime).apply();
            editor.putBoolean(TIMER_RUNNING, true).apply();
            editor.putBoolean(TIMER_PAUSED, false).apply();
            editor.putLong(TIME_SET, millisLeft).apply();
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);
            timerPaused = false;
            timer.setTime(endTime);
            timer.onResumeTimer(timerPaused);
            updateButtons(false);
                    }


         private  void updateButtons(boolean pause){
        if(pause){
            btnPause.setText(getResources().getString(R.string.btn_restart));

        } else{
            btnPause.setText(getResources().getString(R.string.btn_pause));


        }
                    }
        private void pauseTime(){
            SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            millisLeft = prefs.getLong(END_TIME, 0);
            long timeLeft = millisLeft - System.currentTimeMillis();
            timerPaused = true;
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            stopService(intent);
            editor.clear().apply();
            editor.putBoolean(TIMER_RUNNING, true).apply();
            editor.putBoolean(TIMER_PAUSED, timerPaused).apply();
            editor.putLong(TIME_AT_PAUSE, timeLeft).apply();
            editor.putLong(END_TIME, millisLeft).apply();
            timer.onPauseTimer();
            SharedPreferences staticPrefs= getSharedPreferences(STATIC_TIMER, MODE_PRIVATE);
            SharedPreferences.Editor editor1 = staticPrefs.edit();
            editor1.putBoolean(IMAGE_IN_VIEW, false).apply();
            updateButtons(true);

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
        SharedPreferences staticprefs = getSharedPreferences(STATIC_TIMER, MODE_PRIVATE);
        timerRunning = preferences.getBoolean(TIMER_RUNNING, false);
        timerPaused = preferences.getBoolean(TIMER_PAUSED, false);
        imageTimer = staticprefs.getBoolean(IMAGE_IN_VIEW, true);
        updateButtons(timerPaused);
        updateTextView(imageTimer);
    }

    private void updateTextView(boolean imageTimer){
        if(imageTimer){
            tvTimer.setText("");
            tvTimer.setVisibility(View.GONE);
        } else{
        tvTimer.setVisibility(View.VISIBLE);
        int hours   = (int) ((millisLeft / (1000*60*60)) % 24);
        int minutes = (int) (millisLeft / (1000*60)) % 60;
        int seconds = (int) (millisLeft / 1000) % 60;
        String timeLeftFormatted;
        if(hours ==0) {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else{
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        }
        tvTimer.setText(timeLeftFormatted);
    }
    }
}