package com.kmm.a117349221_assignment2.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.IConstants;
import com.kmm.a117349221_assignment2.R;
import com.kmm.a117349221_assignment2.clock.ClockActivity;

import java.util.Locale;

import static com.kmm.a117349221_assignment2.IConstants.CHANNEL_TIMER_ID;
import static com.kmm.a117349221_assignment2.IConstants.END_TIME;
import static com.kmm.a117349221_assignment2.IConstants.IS_RUNNING;
import static com.kmm.a117349221_assignment2.IConstants.MILLIS_LEFT;
import static com.kmm.a117349221_assignment2.IConstants.PAUSED_TIME;

import static com.kmm.a117349221_assignment2.IConstants.TIMER_PAUSED;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PREFERENCES;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_RUNNING;
import static com.kmm.a117349221_assignment2.IConstants.TIME_LEFT;

public class TimerActivity extends AppCompatActivity {

  private BottomNavigationView bottomNavigationView;


  private long endTime;
  private Boolean timerRunning, timerPaused;
  private Button btnCancel;
  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;

    private String[] hours;
    private String[] mins;
    private Spinner spHours, spMins, spSeconds;
    private Button btnStart;
    private RelativeLayout rlTimer, rlTimerSet;
    private FrameLayout flTimer;
    private TextView tvTimeLeft;
    private Button btnPause;
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        notificationManager = NotificationManagerCompat.from(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        spHours = findViewById(R.id.spHours);
        spMins= findViewById(R.id.spMins);
        spSeconds= findViewById(R.id.spSeconds);
        btnStart = findViewById(R.id.btnStart);
        rlTimer = findViewById(R.id.rlTimer);
        rlTimerSet = findViewById(R.id.rlTimerSet);
        flTimer = findViewById(R.id.flTimer);
        tvTimeLeft = findViewById(R.id.tvTimeLeft);
        hours = getResources().getStringArray(R.array.hours);
        mins = getResources().getStringArray(R.array.minutes);
        btnCancel = findViewById(R.id.btnCancel);
        btnPause = findViewById(R.id.btnPauseResume);
        ArrayAdapter<String> adapterHours = new ArrayAdapter<>(this, R.layout.spinner_item, hours);
        ArrayAdapter<String> adapterMins = new ArrayAdapter<>(this, R.layout.spinner_item, mins);
        adapterHours.setDropDownViewResource(R.layout.dropdown_item);
        adapterMins.setDropDownViewResource(R.layout.dropdown_item);
        spSeconds.setAdapter(adapterMins);
        spMins.setAdapter(adapterMins);
        spHours.setAdapter(adapterHours);
        btnStart.setOnClickListener((v)->{
            int min = Integer.parseInt(mins[spMins.getSelectedItemPosition()]);
            int sec = Integer.parseInt(mins[spSeconds.getSelectedItemPosition()]);
            int hour = Integer.parseInt(hours[spHours.getSelectedItemPosition()]);
            sec = sec +1;
            setTimer(hour, min, sec);
            updateVisibility();
        });
        btnCancel.setOnClickListener((v)->{
        cancelTimer();
    });
        btnPause.setOnClickListener((v)->{
            if(timerPaused){
                resumeTimer();
            } else {
                pauseTime();
            }
        });

    }

    private void updateVisibility(){
        if(timerRunning){
            rlTimer.setVisibility(View.GONE);
            rlTimerSet.setVisibility(View.VISIBLE);

        } else{
            rlTimerSet.setVisibility(View.GONE);
            rlTimer.setVisibility(View.VISIBLE);
            spHours.setSelection(0);
            spMins.setSelection(0);
            spSeconds.setSelection(0);
        }
    }

    private void resumeView(){
        if (timerPaused){
            prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            long millisLeft = prefs.getLong(PAUSED_TIME, 0);
            int hours   = (int) ((millisLeft / (1000*60*60)) % 24);
            int minutes = (int) (millisLeft / (1000*60)) % 60;
            int seconds = (int) (millisLeft / 1000) % 60;
            String timeLeftFormatted = new String();
            if(hours ==0) {
                timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            } else{
                timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

            }
            btnPause.setText(getResources().getString(R.string.btn_restart));
            tvTimeLeft.setText(timeLeftFormatted);
        } else if(endTime>0 && tvTimeLeft.getText().toString().equals("")){
            prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
            long timeLeft = endTime - System.currentTimeMillis();
            editor = prefs.edit();
            editor.putLong(TIME_LEFT, timeLeft).apply();
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()) {
                        case R.id.nav_clock:
                            Intent intent = new Intent(TimerActivity.this, ClockActivity.class);
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
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.COUNTDOWN_BR));

    }

    private void resumeTimer(){
        //https://gist.github.com/codinginflow/61e9cec706e7fe94b0ca3fffc0253bf2
        prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
      long millisLeft = prefs.getLong(PAUSED_TIME, 0);
      editor = prefs.edit();
      editor.clear().apply();
      editor.putLong(MILLIS_LEFT, millisLeft).apply();
        editor.putBoolean(TIMER_RUNNING, true).apply();
      Intent intent = new Intent(getApplicationContext(), TimerService.class);

        startService(intent);
        btnPause.setText(getResources().getString(R.string.btn_pause));
        timerPaused = false;




    }

    private void pauseTime(){
        prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        btnPause.setText(getResources().getString(R.string.btn_restart));
        timerPaused = true;
        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        stopService(intent);
        editor.clear().apply();
        editor.putBoolean(TIMER_RUNNING, true).apply();
        editor.putBoolean(TIMER_PAUSED, true).apply();


    }

    private void cancelTimer(){
        timerRunning = false;
        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        stopService(intent);
        editor = prefs.edit();
        editor.clear().apply();
        updateVisibility();
    }

    private void setTimer(int hours, int mins, int secs){

        long milliHours = hours * 60 * 60 * 1000;
        long milliMins = mins * 60 * 1000;
        long milliSecs = secs * 1000;
        long millisLeft = milliHours + milliMins + milliSecs;


        timerRunning = true;

        editor = prefs.edit();
        if(millisLeft>0) {
            editor.putLong(MILLIS_LEFT, millisLeft).apply();
            editor.putBoolean(TIMER_RUNNING, timerRunning).commit();
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        timerRunning = prefs.getBoolean(TIMER_RUNNING, false);
        timerPaused = prefs.getBoolean(TIMER_PAUSED, false);
        endTime = prefs.getLong(END_TIME, 0);
        updateVisibility();
        resumeView();


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        boolean isRunning = intent.getBooleanExtra(IS_RUNNING, false);
        prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        if(isRunning){
        long millisTime = intent.getLongExtra(TIME_LEFT, 0);

        int hours   = (int) ((millisTime / (1000*60*60)) % 24);
        int minutes = (int) (millisTime / (1000*60)) % 60;
        int seconds = (int) (millisTime / 1000) % 60;
        String timeLeftFormatted = new String();
        if(hours ==0) {
              timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else{
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        }

            timerRunning = true;
            tvTimeLeft.setText(timeLeftFormatted);
            updateVisibility();

        } else{
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

    }



}




