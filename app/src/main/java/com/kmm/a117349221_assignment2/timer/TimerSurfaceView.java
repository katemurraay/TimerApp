package com.kmm.a117349221_assignment2.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kmm.a117349221_assignment2.RegPoly;

import java.util.Calendar;
import java.util.Locale;

public class TimerSurfaceView extends SurfaceView implements Runnable {
    private float length;
    private Thread thread;
    private boolean running= false;
    private SurfaceHolder holder;
    private long time;


    public TimerSurfaceView(Context context, float length, long time) {
        super(context);
        this.length = length;
        holder = getHolder();
        this.time =time;


    }


    //methods to manage the thread
    public void onResumeTimer(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public void  onPauseTimer() {
        running = false;
        boolean retry = true;
        while (retry){
            try {
                thread.join();
                retry =false;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        int sec =0;
        int minute =0;
        int hour =0;
        int milliseconds =0;
        while (running){
            if (holder.getSurface().isValid()){
                //get canvas
                Canvas canvas = holder.lockCanvas();
                //draw the clock
                Paint backPaint = new Paint();
                backPaint.setColor(Color.BLACK);
                canvas.drawPaint(backPaint);
                Paint forePaint = new Paint();
                forePaint.setColor(Color.BLUE);
                forePaint.setStyle(Paint.Style.STROKE);
                forePaint.setStrokeWidth(5f);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(28);
                //paint.setTypeface(typeface);
                paint.setTextAlign(Paint.Align.CENTER);

                //draw circle

                  RegPoly timer = new RegPoly(60, getWidth()/2, getHeight()/2, length, canvas, forePaint);
                  RegPoly text = new RegPoly(60, getWidth()/2, getHeight()/2, length, canvas, paint);

                  long millisLeft = time;
                  if(millisLeft>=0){
                  int hours   = (int) ((millisLeft / (1000*60*60)) % 24);
                  int minutes = (int) (millisLeft / (1000*60)) % 60;
                  int seconds = (int) (millisLeft / 1000) % 60;
                  String timeLeftFormatted = new String();
                  if(hours ==0) {
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                  } else{
                    timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

                  }
                    text.drawText(timeLeftFormatted);

                  }     timer.drawCircle(length);






                try {
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }


                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
