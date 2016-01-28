package com.tvi.cutefish.entities;

import com.tvi.cutefish.engine.GameBitmap;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class Bullet extends Actor {

    public static final int[] DAMAGES = { 10, 15, 20, 25, 30, 35, 40, 45, 50,
            55, 60 };
    public static final int[] COSTS = { 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
    public static final int[] PRICES = { 1000, 2000, 3000, 4000, 5000, 6000,
            7000, 8000, 9000, 10000, 0 };

    public Bullet(int level, float x, float y) {
        this.state = level;
        this.x = x;
        this.y = y;
        dx = 0;
        dy = 0;
        index = INDEX_MIN;
        alive = true;
        width = 80;
        height = 80;
    }

    @Override
    public void update() {
        index++;
        if (index == INDEX_MAX) {
            alive = false;
        }
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {

        return false;
    }

    @Override
    public void render(Canvas canvas) {
        renderBody(canvas, GameBitmap.BULLET);
    }

}
