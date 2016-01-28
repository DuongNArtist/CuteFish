package com.tvi.cutefish.entities;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameThread;

public class Food extends Actor {

    public static final int LIMIT = 10;
    public static final int[] PRICES = { 1000, 2000, 3000, 4000, 0 };
    public static final int[] COSTS = { 1, 2, 3, 5, 8 };
    public static final int[] EXPERIENCES = { 1, 2, 3, 5, 8 };
    public static final int[] HEALTHS = { 10 * GameThread.FPS,
            20 * GameThread.FPS, 30 * GameThread.FPS, 40 * GameThread.FPS,
            50 * GameThread.FPS };
    public static final float SPEED = 3;

    public int health;
    public int experience;

    public Food(int level, float x, float y) {
        this.state = level % COSTS.length;
        this.x = x;
        this.y = y;
        experience = EXPERIENCES[state];
        health = HEALTHS[state];
        alive = true;
        width = 40;
        height = 40;
        dx = 0;
        dy = SPEED;
        index = INDEX_MIN;
    }

    @Override
    public void update() {
        index = ++index % SHEET_COLUMNS;
        if (y >= Tank.HEIGHT - height / 2) {
            alive = false;
        }
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        return true;
    }

    @Override
    public void render(Canvas canvas) {
        renderBody(canvas, GameBitmap.FOOD);
    }

}
