package com.kmm.a117349221_assignment2.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.IConstants;
import com.kmm.a117349221_assignment2.R;
import com.kmm.a117349221_assignment2.clock.ClockActivity;

import java.util.Locale;

import static com.kmm.a117349221_assignment2.IConstants.END_TIME;
import static com.kmm.a117349221_assignment2.IConstants.IMAGE_IN_VIEW;
import static com.kmm.a117349221_assignment2.IConstants.STATIC_TIME;
import static com.kmm.a117349221_assignment2.IConstants.STATIC_TIMER;
import static com.kmm.a117349221_assignment2.IConstants.TIME_SET;

import static com.kmm.a117349221_assignment2.IConstants.TIMER_PREFERENCES;
import static com.kmm.a117349221_assignment2.IConstants.TIMER_RUNNING;
import static com.kmm.a117349221_assignment2.IConstants.TIME_LEFT;

public class TimerActivity extends AppCompatActivity {

  private BottomNavigationView bottomNavigationView;


  private Boolean timerRunning;


    private String[] hours;
    private String[] mins;
    private Spinner spHours, spMins, spSeconds;
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

            sec = sec +1;

            setTimer(hour, min, sec);

        });


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





    private void setTimer(int hours, int mins, int secs){

        long milliHours = hours * 60 * 60 * 1000;
        long milliMins = mins * 60 * 1000;
        long milliSecs = secs * 1000;
        long millisLeft = milliHours + milliMins + milliSecs;

        SharedPreferences prefs = getSharedPreferences(TIMER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences staticPrefs = getSharedPreferences(STATIC_TIMER, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        SharedPreferences.Editor editor1 = staticPrefs.edit();
        timerRunning = true;

        editor = prefs.edit();
        if(millisLeft>1000) {
            editor.putLong(TIME_SET, millisLeft).apply();
            editor.putLong(END_TIME, millisLeft).apply();
            editor.putBoolean(TIMER_RUNNING, timerRunning).apply();
            editor1.putLong(STATIC_TIME, millisLeft).apply();
            editor1.putBoolean(IMAGE_IN_VIEW, timerRunning).apply();

            Intent intent = new Intent(getApplicationContext(), TimerService.class);
            startService(intent);
            Intent activity = new Intent(TimerActivity.this, TimerSetActivity.class);
            startActivity(activity);
            overridePendingTransition(0,0);
            finish();

        }
    }











}




