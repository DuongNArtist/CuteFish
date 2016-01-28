package com.tvi.cutefish.entities;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.GameActivity;
import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.engine.GameThread;

public class Fish extends Actor {

    public static final float SPEED_MIN = 0.5f;
    public static final float SPEED_MAX = 2.5f;
    public static final int LIMIT = 20;
    public static final int PRICE = 100;
    public static final int STATUS_FULL = 0;
    public static final int STATUS_EMPTY = 3;
    public static final int TIME_TO_CHANGE_STATE = 10 * GameThread.FPS;
    public static final int TIME_TO_MAKE_PRODUCT = 10 * GameThread.FPS;
    public static final int[] HEALTHS = { 20 * GameThread.FPS,
            40 * GameThread.FPS, 80 * GameThread.FPS, 100 * GameThread.FPS,
            120 * GameThread.FPS };
    public static final int[] EXPERIENCES = { 10, 20, 30, 50, 80 };
    public static final float PERCENT_TO_FIND_FOOD = 0.5f;

    public int experience;
    public int health;
    public int status;
    public int timeToChangeState;
    public int timeToMakeProduct;
    public boolean making;

    public Fish(int level) {
        this.level = level;
        alive = true;
        width = 80;
        height = 80;
        health = (int) (HEALTHS[level] * (PERCENT_TO_FIND_FOOD + random
                .nextFloat() * PERCENT_TO_FIND_FOOD));
        status = STATUS_FULL;
        state = status + STATE_SWIM;
        if (random.nextBoolean()) {
            direction = DIRECTION_LEFT;
            index = INDEX_MIN;
            dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
        } else {
            direction = DIRECTION_RIGHT;
            index = INDEX_MAX;
            dx = randomSpeed(SPEED_MIN, SPEED_MAX);
        }
        if (random.nextBoolean()) {
            dy = randomSpeed(SPEED_MIN, SPEED_MAX);
        } else {
            dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
        }
        x = width / 2 + random.nextInt(Tank.WIDTH - width);
        y = height / 2 + random.nextInt(Tank.HEIGHT - height);
        timeToChangeState = random.nextInt(TIME_TO_CHANGE_STATE);
        timeToMakeProduct = random.nextInt(TIME_TO_MAKE_PRODUCT);
    }

    public void findFood(ArrayList<Food> foods) {
        if (foods.size() > 0 && state == STATUS_EMPTY + STATE_SWIM) {
            Food targetFood = findTargetFood(foods);
            swimToFood(targetFood);
            eatFood(targetFood);
        }
    }

    public void attackedBy(ArrayList<Enemy> enemies) {
        if (health > 0) {
            for (Enemy enemy : enemies) {
                if (dst.contains(enemy.x, enemy.y)) {
                    health -= enemy.damage;
                }
            }
        }
    }

    private void turn() {
        state = status + STATE_TURN;
        dx = 0;
        if (direction == DIRECTION_LEFT) {
            index = INDEX_MIN;
        } else if (direction == DIRECTION_RIGHT) {
            index = INDEX_MAX;
        }
    }

    private void turning() {
        if (direction == DIRECTION_LEFT) {
            index++;
            if (index == INDEX_MAX) {
                state = status + STATE_SWIM;
                direction = DIRECTION_RIGHT;
                dx = randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        } else if (direction == DIRECTION_RIGHT) {
            index--;
            if (index == INDEX_MIN) {
                state = status + STATE_SWIM;
                direction = DIRECTION_LEFT;
                dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
            }
        }
    }

    private void eating() {
        if (direction == DIRECTION_LEFT) {
            index++;
            if (index == INDEX_MAX) {
                state -= 2;
            }
        } else if (direction == DIRECTION_RIGHT) {
            index--;
            if (index == INDEX_MIN) {
                state -= 2;
            }
        }
    }

    private void swimming() {
        index = ++index % SHEET_COLUMNS;
        if (x + dx <= width / 2) {
            turn();
        } else if (x + dx >= Tank.WIDTH - width / 2) {
            turn();
        }
    }

    private void dying() {
        if (direction == DIRECTION_LEFT) {
            if (index < INDEX_MAX) {
                index++;
            }
        } else if (direction == DIRECTION_RIGHT) {
            if (index > INDEX_MIN) {
                index--;
            }
        }
    }

    private void updateDx() {
        if (state == status + STATE_SWIM) {
            swimming();
        } else if (state == status + STATE_TURN) {
            turning();
        } else if (state == status + STATE_EAT) {
            eating();
        } else if (state == STATE_DIE) {
            dying();
        }
    }

    private void updateDy() {
        if (state == STATE_DIE) {
            if (y >= Tank.HEIGHT - height / 2) {
                alive = false;
            }
        } else {
            if (y <= height / 2) {
                dy = Math.abs(randomSpeed(SPEED_MIN, SPEED_MAX));
            } else if (y >= Tank.HEIGHT - height / 2) {
                dy = -Math.abs(randomSpeed(SPEED_MIN, SPEED_MAX));
            }
        }
    }

    private void randomState() {
        if (state == STATUS_FULL + STATE_SWIM) {
            timeToChangeState--;
            if (timeToChangeState <= 0) {
                timeToChangeState = random.nextInt(TIME_TO_CHANGE_STATE);
                if (random.nextBoolean()) {
                    turn();
                } else {
                    if (dy < 0) {
                        dy = randomSpeed(SPEED_MIN, SPEED_MAX);
                    } else {
                        dy = -randomSpeed(SPEED_MIN, SPEED_MAX);
                    }
                }
            }
        }
    }

    private void updateHealth() {
        if (state != STATE_DIE) {
            health--;
            state -= status;
            if (health <= 0) {
                GameActivity.mGameSound.playSound(GameSound.fish_die);
                state = STATE_DIE;
                dx = 0;
                dy = SPEED_MIN + SPEED_MAX;
                if (direction == DIRECTION_LEFT) {
                    index = INDEX_MIN;
                } else if (direction == DIRECTION_RIGHT) {
                    index = INDEX_MAX;
                }
            } else if (health < HEALTHS[level] * PERCENT_TO_FIND_FOOD) {
                status = STATUS_EMPTY;
                state += status;
            } else {
                status = STATUS_FULL;
                state += status;
            }
        }
    }

    private void makeProduct() {
        if (state != STATE_DIE) {
            timeToMakeProduct--;
            if (timeToMakeProduct <= 0) {
                timeToMakeProduct = random.nextInt(TIME_TO_MAKE_PRODUCT);
                making = true;
            }
        }
    }

    private Food findTargetFood(ArrayList<Food> foods) {
        Food dstFood = foods.get(0);
        float dstDistance = calculateDistance(dstFood.x, dstFood.y, x, y);
        for (int index = 1; index < foods.size(); index++) {
            Food food = foods.get(index);
            if (food.alive) {
                float distance = calculateDistance(food.x, food.y, x, y);
                if (distance < dstDistance) {
                    dstDistance = distance;
                    dstFood = food;
                }
            }
        }
        return dstFood;
    }

    private float calculateDistance(float x0, float y0, float x1, float y1) {
        return (float) Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
    }

    private void eatFood(Food food) {
        if (dst.contains(food.x, food.y)) {
            GameActivity.mGameSound.playSound(GameSound.fish_eat);
            food.alive = false;
            state = status + STATE_EAT;
            if (direction == DIRECTION_LEFT) {
                index = INDEX_MIN;
                dx = -randomSpeed(SPEED_MIN, SPEED_MAX);
            } else if (direction == DIRECTION_RIGHT) {
                index = INDEX_MAX;
                dx = randomSpeed(SPEED_MIN, SPEED_MAX);
            }
            if (health + food.health > HEALTHS[level]) {
                health = HEALTHS[level];
            } else {
                health += food.health;
            }
            if (experience + food.experience > EXPERIENCES[level]) {
                experience = EXPERIENCES[level];
            } else {
                experience += food.experience;
            }
            if (experience >= EXPERIENCES[level]
                    && level < EXPERIENCES.length - 1) {
                level++;
                GameActivity.mGameSound.playSound(GameSound.fish_grow);
            }
        }
    }

    private void swimToFood(Food food) {
        if (food.x < x && direction == DIRECTION_RIGHT) {
            turn();
        } else if (food.x >= x && direction == DIRECTION_LEFT) {
            turn();
        }
        if (food.x < x) {
            dx = -Math.abs(dx);
        } else {
            dx = Math.abs(dx);
        }
        if (food.y < y) {
            dy = -Math.abs(dy);
        } else {
            dy = Math.abs(dy);
        }
        float deltaX = food.x - x;
        float deltaY = food.y - y;
        float targetSpeed = (float) Math
                .sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        float targetTime = (float) Math.sqrt(Math.pow(food.x - x, 2)
                + Math.pow(food.y - y, 2))
                / targetSpeed;
        float speedX = deltaX / targetTime;
        float speedY = deltaY / targetTime;
        float newSpeedX = (float) (2 * speedX / Math.abs(speedX));
        float newSpeedY = (float) (2 * speedY / Math.abs(speedY));
        dx = newSpeedX;
        dy = newSpeedY;
    }

    @Override
    public void update() {
        makeProduct();
        randomState();
        updateDx();
        updateDy();
        updateHealth();
        updateBody();
    }

    @Override
    public boolean touch(MotionEvent event) {
        return true;
    }

    @Override
    public void render(Canvas canvas) {
        if (direction == DIRECTION_LEFT) {
            renderBody(canvas, GameBitmap.FISHES + level);
        } else if (direction == DIRECTION_RIGHT) {
            renderBody(canvas, GameBitmap.FISHES + level + GameBitmap.FLIPPED);
        }
    }
}
