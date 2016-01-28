package com.tvi.cutefish.entities;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.GameActivity;
import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.engine.GameThread;
import com.tvi.cutefish.engine.GameView;

public class Enemy extends Actor {

    public static final float SPEED_MIN = 2.0f;
    public static final float SPEED_MAX = 5.0f;
    public static final int TIME_TO_CHANGE_STATE = 5 * GameThread.FPS;
    public static final int HEALTH = 200;
    public static final int NUMBER = 8;
    public static final int DAMAGE = 5 * GameThread.FPS;

    public boolean flying;
    public boolean alive;
    public int health;
    public int damage;
    public int timeToChangeState;

    public Enemy(int level) {
        GameActivity.mGameSound.playSound(GameSound.enemy_new);
        this.level = level % NUMBER;
        state = STATE_SWIM;
        width = 160;
        height = 160;
        health = (level + 1) * HEALTH;
        damage = (level + 1) * DAMAGE;
        alive = true;
        if (random.nextBoolean()) {
            direction = DIRECTION_LEFT;
            index = INDEX_MIN;
            dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
        } else {
            direction = DIRECTION_RIGHT;
            index = INDEX_MIN;
            dx = randomSpeed(SPEED_MIN, SPEED_MAX);
        }
        x = width / 2 + random.nextInt(Tank.WIDTH - width);
        if (level == 2 || level == 3 || level == 6 || level == 7) {
            flying = false;
            y = Tank.HEIGHT - height / 2;
            dy = 0;
        } else {
            flying = true;
            y = random.nextInt(Tank.HEIGHT - height);
            if (random.nextBoolean()) {
                dy = randomSpeed(SPEED_MIN, SPEED_MAX);
            } else {
                dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
        timeToChangeState = random.nextInt(TIME_TO_CHANGE_STATE);
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

    private void updateDy() {
        if (flying) {
            if (y <= height / 2) {
                dy = randomSpeed(SPEED_MIN, SPEED_MAX);
            } else if (y >= Tank.HEIGHT - height / 2) {
                dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
    }

    public void attackedBy(int damage) {
        health -= damage;
        if (health <= 0) {
            alive = false;
        }
    }

    @Override
    public void update() {
        randomState();
        updateDx();
        updateDy();
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        float ex = event.getX() * Tank.WIDTH / GameView.width;
        float ey = event.getY() * Tank.HEIGHT / GameView.height;
        if (dst.contains(ex, ey)) {
            return true;
        }
        return false;
    }

    @Override
    public void render(Canvas canvas) {
        if (direction == DIRECTION_LEFT) {
            renderBody(canvas, GameBitmap.ENEMIES + level);
        } else if (direction == DIRECTION_RIGHT) {
            renderBody(canvas, GameBitmap.ENEMIES + level + GameBitmap.FLIPPED);
        }
    }

}
