package com.tvi.cutefish.entities;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameThread;

public class Ally extends Actor {

    public static final float SPEED_MIN = 0.5f;
    public static final float SPEED_MAX = 1.5f;
    public static final int NUMBER = 24;
    public static final int TIME_TO_CHANGE_STATE = 10 * GameThread.FPS;
    public static final int TIME_TO_MAKE_PRODUCT = 30 * GameThread.FPS;

    public boolean flying;
    public boolean making;
    public int timeToMakeProduct;
    public int timeToChangeState;

    public Ally(int level) {
        this.level = level % NUMBER;
        state = 0;
        width = 80;
        height = 80;
        if (random.nextBoolean()) {
            direction = DIRECTION_LEFT;
            index = INDEX_MIN;
            dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
        } else {
            direction = DIRECTION_RIGHT;
            index = INDEX_MAX;
            dx = randomSpeed(SPEED_MIN, SPEED_MAX);
        }
        x = width / 2 + random.nextInt(Tank.WIDTH - width);
        if (level == 3 || level == 6 || level == 13 || level == 14
                || level == 18 || level == 19) {
            flying = false;
            y = Tank.HEIGHT - height / 2;
            dy = 0;
        } else {
            flying = true;
            y = height / 2 + random.nextInt(Tank.HEIGHT - height);
            if (random.nextBoolean()) {
                dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
            } else {
                dy = randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
        timeToChangeState = random.nextInt(TIME_TO_CHANGE_STATE);
        timeToMakeProduct = TIME_TO_MAKE_PRODUCT / 2
                + random.nextInt(TIME_TO_MAKE_PRODUCT);
    }

    public void makeProduct() {
        if (flying) {
            timeToMakeProduct--;
            if (timeToMakeProduct <= 0) {
                making = true;
                timeToMakeProduct = TIME_TO_MAKE_PRODUCT / 2
                        + random.nextInt(TIME_TO_MAKE_PRODUCT);
            }
        }
    }

    public void collectProduct(ArrayList<Coin> coins, ArrayList<Shell> shells) {
        if (!flying) {
            for (Coin coin : coins) {
                if (dst.contains(coin.x, coin.y)) {
                    coin.collect();
                }
            }
            for (Shell shell : shells) {
                if (dst.contains(shell.x, shell.y)) {
                    shell.collect();
                }
            }
        }
    }

    private void turn() {
        dx = 0;
        state = STATE_TURN;
        if (direction == DIRECTION_LEFT) {
            index = INDEX_MIN;
        } else if (direction == DIRECTION_RIGHT) {
            index = INDEX_MAX;
        }
    }

    private void swimming() {
        index = ++index % SHEET_COLUMNS;
        if ((x + dx <= width / 2) || (x + dx >= Tank.WIDTH - width / 2)) {
            turn();
        }
    }

    private void turning() {
        if (direction == DIRECTION_LEFT) {
            index++;
            if (index == INDEX_MAX) {
                state = STATE_SWIM;
                direction = DIRECTION_RIGHT;
                dx = randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        } else if (direction == DIRECTION_RIGHT) {
            index--;
            if (index == INDEX_MIN) {
                state = STATE_SWIM;
                direction = DIRECTION_LEFT;
                dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
    }

    private void updateDx() {
        if (state == STATE_SWIM) {
            swimming();
        } else if (state == STATE_TURN) {
            turning();
        }
    }

    private void updateDy() {
        if (flying) {
            if (y <= height / 2) {
                dy = randomSpeed(SPEED_MIN, SPEED_MAX);
            } else if (y >= Tank.HEIGHT - height / 2) {
                dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
    }

    private void randomState() {
        if (state == STATE_SWIM) {
            timeToChangeState--;
            if (timeToChangeState <= 0) {
                timeToChangeState = random.nextInt(TIME_TO_CHANGE_STATE);
                if (random.nextBoolean()) {
                    turn();
                } else {
                    if (flying) {
                        if (dy < 0) {
                            dy = randomSpeed(SPEED_MIN, SPEED_MAX);
                        } else {
                            dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        makeProduct();
        randomState();
        updateDx();
        updateDy();
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        return true;
    }

    @Override
    public void render(Canvas canvas) {
        if (direction == DIRECTION_LEFT) {
            renderBody(canvas, GameBitmap.ALLIES + level);
        } else if (direction == DIRECTION_RIGHT) {
            renderBody(canvas, GameBitmap.ALLIES + level + GameBitmap.FLIPPED);
        }
    }

}
