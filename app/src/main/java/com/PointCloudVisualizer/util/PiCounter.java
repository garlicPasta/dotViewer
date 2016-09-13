package com.PointCloudVisualizer.util;

import android.util.Log;

public class PiCounter {

    float speed;

    public PiCounter(){
        speed = 1;
    }

    public PiCounter(float speed){
        this.speed = speed;
    }

    public float getTimeValue(){
        double currentTimeStamp = (double)System.nanoTime() / Math.pow(10d,9d);
        return (float) ((speed * currentTimeStamp) % (2* Math.PI));
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
