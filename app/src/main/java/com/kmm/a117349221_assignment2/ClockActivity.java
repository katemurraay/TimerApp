package com.kmm.a117349221_assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kmm.a117349221_assignment2.clock.ClockSurfaceView;

import java.util.List;
import java.util.Vector;

public class ClockActivity extends AppCompatActivity {

    private ClockSurfaceView clock = null;
    private Button btnPause;
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    private  Button btnClock;
    private  Button btnTimer;

    private String[] tabs;
    private String[] images;

private TabLayout tabLayout;
private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabs = new String[]{"Clock", "Timer"};
        images = new String[]{"clock.png", "timer.png"};
        clock = new ClockSurfaceView(this, 300);
        frameLayout = findViewById(R.id.frame_layout);
        relativeLayout = findViewById(R.id.relative_layout);
       bottomNavigationView = findViewById(R.id.bottom_navigation);
       bottomNavigationView.setSelectedItemId(R.id.nav_clock);
       bottomNavigationView.setOnNavigationItemSelectedListener(navListener);









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
                            //https://stackoverflow.com/a/16146179
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            break;

                    }


                    return true;
                }
            };
}
