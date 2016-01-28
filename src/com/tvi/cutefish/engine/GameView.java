package com.tvi.cutefish.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameView.class.getSimpleName();

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    public static int width;
    public static int height;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Rect mRectGame;
    private Rect mRectScreen;
    private GameThread mGameThread;
    private GameScreen mGameScreen;

    public GameView(Context context, GameScreen gameScreen) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        mGameScreen = gameScreen;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);
        mRectGame = new Rect(0, 0, WIDTH, HEIGHT);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        mRectScreen = new Rect(0, 0, width, height);
        mGameThread = new GameThread(this);
        mGameThread.setRunning(true);
        mGameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                mGameThread.setRunning(false);
                mGameThread.join();
                retry = false;
            } catch (InterruptedException ie) {
                Log.i(TAG, ie.toString());
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mGameScreen.touch(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    public void draw(Canvas canvas) {
        mGameScreen.update();
        mGameScreen.render(mCanvas);
        canvas.drawBitmap(mBitmap, mRectGame, mRectScreen, null);
    }

    public Bitmap getBitmap() {
        synchronized (mBitmap) {
            return mBitmap;
        }
    }
}
