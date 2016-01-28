package com.tvi.cutefish.entities;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.GameActivity;
import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.engine.GameView;

public class Coin extends Actor {

    public static final float SPEED = 4;
    public static final int[] VALUES = { 10, 20, 30, 40, 50, 60 };

    public int value;
    public boolean falling;

    public Coin(int level, float x, float y) {
        this.state = level % VALUES.length;
        this.x = x;
        this.y = y;
        dx = 0;
        dy = SPEED;
        value = VALUES[state];
        alive = true;
        width = 72;
        height = 72;
        dx = 0;
        dy = SPEED;
        index = INDEX_MIN;
        falling = true;
    }

    public void collect() {
        GameActivity.mGameSound.playSound(GameSound.money_0 + state);
        dy = -3 * SPEED;
        dx = dy / y * (x - Tank.WIDTH / 2);
        falling = false;
    }

    @Override
    public void update() {
        index = ++index % SHEET_COLUMNS;
        if (y >= Tank.HEIGHT - height / 2 || y <= height / 2) {
            alive = false;
            if (y >= Tank.HEIGHT - height / 2) {
                value = 0;
            }
        }
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        if (falling) {
            float ex = event.getX() * Tank.WIDTH / GameView.width;
            float ey = event.getY() * Tank.HEIGHT / GameView.height;
            if (dst.contains(ex, ey)) {
                collect();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Canvas canvas) {
        renderBody(canvas, GameBitmap.COIN);
    }

}
