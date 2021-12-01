package com.kmm.a117349221_assignment2.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kmm.a117349221_assignment2.RegPoly;

import java.util.Calendar;

public class TimerSurfaceView extends SurfaceView implements Runnable {
    private float length;
    private Thread thread;
    private boolean running= false;
    private SurfaceHolder holder;


    public TimerSurfaceView(Context context, float length) {
        super(context);
        this.length = length;
        holder = getHolder();
    }


    //methods to manage the thread
    public void onResumeClock(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public void  onPauseClock() {
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
                forePaint.setColor(Color.WHITE);
                forePaint.setStrokeWidth(2f);


                //draw the marks
                RegPoly secMarks = new RegPoly(60, getWidth()/2, getHeight()/2, length + 80, canvas, forePaint);

                RegPoly milliMarks = new RegPoly(60, 7 *getWidth()/20, 11 *getHeight()/20, length-200, canvas, forePaint);

                secMarks.drawNodes(4.5f);

                milliMarks.drawNodes(2f);

                //hourMarks.drawNodes(4);
                forePaint.setTextSize(35f);
                RegPoly poly = new RegPoly(12, getWidth()/2, getHeight()/2, length+30, canvas, forePaint);

                for (int i = 1; i <13; i++ ) {
                    String strHour = String.valueOf(i);
                    //-3 + 9
                    canvas.drawText(strHour, poly.getX(i+9), poly.getY(i+9),  forePaint);
                }
                //make 3 reg polys for the hands
                forePaint.setStrokeWidth(5f);
                RegPoly secHand = new RegPoly(60, getWidth()/2, getHeight()/2, length-20, canvas, forePaint);
                RegPoly minHand = new RegPoly(60, getWidth()/2, getHeight()/2, length-50, canvas, forePaint);
                RegPoly hourHand = new RegPoly(60, getWidth()/2, getHeight()/2, length-100, canvas, forePaint);
                RegPoly milliHand = new RegPoly(60,  7 *getWidth()/20, 11 *getHeight()/20, length-220, canvas, forePaint);
                //get hour, min, sec from calender
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                sec = calendar.get(Calendar.SECOND);
                milliseconds = calendar.get(Calendar.MILLISECOND);

                //draw three hands
                secHand.drawRadius(sec + 45);
                minHand.drawRadius(minute + 45);
                hourHand.drawRadius( 45 +(hour * 5 + minute/12));
                milliHand.drawRadius(milliseconds + 45);

                try {
                    Thread.sleep(6);
                } catch (Exception e){
                    e.printStackTrace();
                }


                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
