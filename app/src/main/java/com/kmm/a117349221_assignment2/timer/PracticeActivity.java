package com.kmm.a117349221_assignment2.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kmm.a117349221_assignment2.R;

public class PracticeActivity extends AppCompatActivity {
TimerSurfaceView timer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long endTime= System.currentTimeMillis() + 50000;
        timer = new TimerSurfaceView(this, 350, endTime);
        setContentView(timer);

    }

    @Override
    protected void onResume(){
        super.onResume();
        timer.onResumeTimer();

    }
    @Override
    protected void onPause(){
        super.onPause();
        timer.onPauseTimer();

    }
}