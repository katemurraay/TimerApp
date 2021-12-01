package com.kmm.a117349221_assignment2;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RegPoly {
    private int n;
    private float x0, y0, r;
    private float[] x,y;
    private Canvas canvas;
    private Paint paint;


    public RegPoly(int n, float x0, float y0, float r, Canvas canvas, Paint paint) {
        this.n = n;
        this.x0 = x0;
        this.y0 = y0;
        this.r = r;
        this.canvas = canvas;
        this.paint = paint;

    //calculate x[] and y[]
        this.x = new float[this.n];
        this.y = new float[this.n];

        for (int i=0; i<n; i ++){
            this.x[i] = (float) (x0+ r *Math.cos(2 * Math.PI * i/n));
            this.y[i] = (float) (y0+ r *Math.sin(2 * Math.PI * i/n));
        }
    }

    //getters for co-ordinates
    public float getX(int i){return x[i%n];}
    public float getY(int i){return y[i%n];}

    //drawers
    public void drawRadius(int i){
        canvas.drawLine(x0, y0, getX(i), getY(i), paint);
    }

    public void drawNodes(float radius){
        for(int i=0; i<n; i++){
            canvas.drawCircle(getX(i), getY(i),  radius, paint);
        }
    }

    public void drawCircle (float radius){
        canvas.drawCircle( x0, y0, radius, paint);
    }
    public void drawText(String text){
        canvas.drawText(text, x0, y0, paint );
    }
}
