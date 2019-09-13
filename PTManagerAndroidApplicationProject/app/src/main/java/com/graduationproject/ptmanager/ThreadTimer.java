package com.graduationproject.ptmanager;

/**
 * Created by crystal on 2018-03-16.
 */

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Study on 2017-11-27.
 */

public class ThreadTimer extends Thread {
    private Timer timer;
    private TimerTask countTask;

    int delay = 0;
    int period = 10;

    private Handler parentHandler;
    private Handler timerHandler;

    public ThreadTimer(Handler handler) {
        this.parentHandler = handler;
    }

    public Handler getTimerHandler() {
        return timerHandler;
    }


    public void run() {
        Looper.prepare();
        timerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        timer.cancel();
                        timer.purge();
                        break;
                    case 1:
                        timer = new Timer(true);
                        taskCountMaker();
                        timer.schedule(countTask, delay, period);
                        break;
                }
            }
        };
        Looper.loop();
    }

    private void taskCountMaker() {
        countTask = new TimerTask() {
            @Override
            public void run() {
                parentHandler.sendEmptyMessage(period);
            }
        };
    }
}
