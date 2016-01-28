package com.tvi.cutefish.engine;

import android.graphics.Canvas;
import android.util.Log;

public class GameThread extends Thread {

    public static final int FPS = 30;
    public static final int PER = 1000 / FPS;

    private GameView mGameView;
    private boolean mRunning;

    public GameThread(GameView gameView) {
        mRunning = false;
        mGameView = gameView;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void run() {
        long startTime = 0;
        long sleepTime = 0;
        while (mRunning) {
            Canvas canvas = null;
            try {
                canvas = mGameView.getHolder().lockCanvas();
                if (canvas != null) {
                    synchronized (mGameView.getHolder()) {
                        startTime = System.currentTimeMillis();
                        mGameView.draw(canvas);
                        sleepTime = PER
                                - (System.currentTimeMillis() - startTime);
                        Log.i("SLEEP", sleepTime + "");
                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } finally {
                if (canvas != null) {
                    mGameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
