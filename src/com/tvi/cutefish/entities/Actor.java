package com.tvi.cutefish.entities;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.tvi.cutefish.GameActivity;

public abstract class Actor implements Action {

    public static final int SHEET_COLUMNS = 10;
    public static final int INDEX_MAX = 9;
    public static final int INDEX_MIN = 0;
    public static final int STATE_SWIM = 0;
    public static final int STATE_TURN = 1;
    public static final int STATE_EAT = 2;
    public static final int STATE_DIE = 6;
    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_RIGHT = 1;

    public float x;
    public float y;
    public float dx;
    public float dy;
    public int width;
    public int height;
    public int state;
    public int direction;
    public int level;
    public int index;
    public boolean alive;
    public Rect src;
    public RectF dst;
    public Random random;

    public Actor() {
        src = new Rect();
        dst = new RectF();
        random = new Random();
    }

    public float randomSpeed(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public void updateBody() {
        x += dx;
        y += dy;
        src.set(index * width, state * height, (index + 1) * width, (state + 1)
                * height);
        dst.set(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
    }

    public void renderBody(Canvas canvas, String name) {
        canvas.drawBitmap(GameActivity.mGameBitmap.get(name), src, dst, null);
    }
}
