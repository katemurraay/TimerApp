package com.kmm.a117349221_assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.timer.TimerService;

import java.util.Locale;

import static com.kmm.a117349221_assignment2.IConstants.END_TIME;
import static com.kmm.a117349221_assignment2.IConstants.MILLIS_LEFT;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_PREFERENCES;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_RUNNING;

public class TimerActivity extends AppCompatActivity {

  private BottomNavigationView bottomNavigationView;

  private long millisLeft;
  private long endTime;
  private Boolean timerRunning;
  private CountDownTimer cdt;
  private SharedPreferences prefs;
  private SharedPreferences.Editor editor;
    private static final long START_TIME_IN_MILLIS = 600000;
    private String[] hours;
    private String[] mins;
    private Spinner spHours, spMins, spSeconds;
    private TextView tvHour, tvMin, tvSec;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        spHours = findViewById(R.id.spHours);
        spMins= findViewById(R.id.spMins);
        spSeconds= findViewById(R.id.spSeconds);
        tvHour = findViewById(R.id.tvHour);
        tvMin = findViewById(R.id.tvMin);
        tvSec = findViewById(R.id.tvSec);
        btnStart = findViewById(R.id.btnStart);
        hours = getResources().getStringArray(R.array.hours);
        mins = getResources().getStringArray(R.array.minutes);
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
    setTimer(hour, min, sec);
});

        startService(new Intent(this, TimerService.class));
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
     
    }

    private void startTimer(){
       endTime = System.currentTimeMillis() + millisLeft;


    }

    private void pauseTime(){
        cdt.cancel();
        timerRunning = false;

    }


    private void setTimer(int hours, int mins, int secs){
        long milliHours = hours * 60 * 60 * 1000;
        long milliMins = mins * 60 * 1000;
        long milliSecs = secs * 1000;
        millisLeft = milliHours + milliMins + milliSecs;
        if(millisLeft>0) {
            editor.putLong(MILLIS_LEFT, millisLeft).commit();
            editor.putBoolean(TIMER_RUNNING, true).commit();
            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);
        }
    }
    private void updateCountDownText() {
        int hours   = (int) ((millisLeft / (1000*60*60)) % 24);
        int minutes = (int) (millisLeft / 1000) / 60;
        int seconds = (int) (millisLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

       // mTextViewCountDown.setText(timeLeftFormatted);
    }
    @Override
    protected void onStart() {
        super.onStart();
        prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        millisLeft = prefs.getLong(MILLIS_LEFT, START_TIME_IN_MILLIS);
        timerRunning = prefs.getBoolean(TIMER_RUNNING, false);
        try {
            millisLeft = prefs.getLong(MILLIS_LEFT, START_TIME_IN_MILLIS);
            if (timerRunning) {
                endTime = prefs.getLong(END_TIME, 0);
               millisLeft= endTime - System.currentTimeMillis();

                if (millisLeft < 0) {
                   millisLeft= 0;
                    timerRunning = false;
                    //updateCountDownText();
                    //updateButtons();
                } else {
                   // startTimer();
                }
            }}
             catch (Exception e) {
            e.printStackTrace();

        }


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        int time = intent.getIntExtra("time", 0);

        Log.d("Hello", "Time " + time);

        int mins = time / 60;
        int secs = time % 60;
       // timerValue.setText("" + mins + ":"
        //        + String.format("%02d", secs));
    }



}




