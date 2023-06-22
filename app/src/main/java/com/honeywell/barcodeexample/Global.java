package com.honeywell.barcodeexample;

import android.app.Application;

public class Global extends Application {
    private boolean timer;
    private int maxCount;

    public Global() {
        timer=false;
        maxCount=-1;
    }

    public void setTimer(boolean on) {
        timer = on;
    }

    public boolean isTimerOn() {
        return timer;
    }

    public void setMaxCount(int count) {
        this.maxCount = count;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
