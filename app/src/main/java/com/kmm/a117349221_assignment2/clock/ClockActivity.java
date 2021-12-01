package com.kmm.a117349221_assignment2.clock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmm.a117349221_assignment2.R;
import com.kmm.a117349221_assignment2.timer.TimerActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockActivity extends AppCompatActivity {

    private ClockSurfaceView clock = null;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView tvTime;
    private Calendar c;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = new ClockSurfaceView(this, 300);
        frameLayout = findViewById(R.id.frame_layout);
        relativeLayout = findViewById(R.id.relative_layout);
       bottomNavigationView = findViewById(R.id.bottom_navigation);
       bottomNavigationView.setSelectedItemId(R.id.nav_clock);
       bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
       tvTime = findViewById(R.id.tvTime);



//https://stackoverflow.com/a/4776556
      timer = new Timer();
      timerTask = new TimerTask() {
          long millis;

          @Override
            public void run() {
               c = Calendar.getInstance();
               Date time = c.getTime();
                String pattern = "HH:mm";
                SimpleDateFormat dateFormat =  new SimpleDateFormat(pattern);

              String pattern1 = "ss";
              SimpleDateFormat secFormat =  new SimpleDateFormat(pattern1);
              int seconds = Integer.parseInt(secFormat.format(time));
              millis = seconds * 1000;

              String strTime = dateFormat.format(time);
              tvTime.setText(strTime);

                Log.d("TIMER", "CALLED");
            }
        };
        timer.schedule(timerTask, 1000, 1000);












        frameLayout.removeAllViews();
        frameLayout.addView(clock);

        frameLayout.addView(relativeLayout);
        setContentView(frameLayout);



    }

    @Override
    protected void onResume(){
        super.onResume();
        clock.onResumeClock();

    }
    @Override
    protected void onPause(){
        super.onPause();
        clock.onPauseClock();
        timer.cancel();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()) {
                        case R.id.nav_clock:

                            break;
                        case R.id.nav_timer:
                            Intent intent = new Intent(ClockActivity.this, TimerActivity.class);
                            startActivity(intent);
                             overridePendingTransition(0,0);
                            break;

                    }


                    return true;
                }
            };
}
