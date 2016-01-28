package com.tvi.cutefish.entities;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface Action {

    void update();

    boolean touch(MotionEvent event);

    void render(Canvas canvas);
}
