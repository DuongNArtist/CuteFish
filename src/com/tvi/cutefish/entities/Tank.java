package com.tvi.cutefish.entities;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.tvi.cutefish.GameActivity;
import com.tvi.cutefish.R;
import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.engine.GameThread;
import com.tvi.cutefish.engine.GameView;

public class Tank implements Action {

    public static final int NUMBER = 6;
    public static final int EGG_MAX = 3;
    public static final int MONEY = 20000000;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final int EGG_PRICE = 10000;
    public static final int TIME_TO_BATTLE = 60 * GameThread.FPS;

    public ArrayList<Enemy> enemies;
    public ArrayList<Ally> allies;
    public ArrayList<Fish> fishes;
    public ArrayList<Food> foods;
    public ArrayList<Coin> coins;
    public ArrayList<Shell> shells;
    public ArrayList<Bullet> bullets;
    public Random random;
    public int money;
    public int tankLevel;
    public int foodLevel;
    public int gunzLevel;
    public int eggsLevel;
    public int enemyLevel;
    public boolean buyingFood;
    public boolean buyingFish;
    public boolean buyingBullet;
    public boolean fighting;
    public boolean playing;
    public float x;
    public float y;
    public int timeToBattle;

    public Tank(int level) {
        tankLevel = level % NUMBER;
        eggsLevel = 0;
        foodLevel = 0;
        enemyLevel = 0;
        gunzLevel = 0;
        money = MONEY;
        timeToBattle = TIME_TO_BATTLE;
        buyingFood = false;
        buyingBullet = false;
        buyingFish = false;
        fighting = false;
        playing = true;
        random = new Random();
        enemies = new ArrayList<Enemy>();
        foods = new ArrayList<Food>();
        coins = new ArrayList<Coin>();
        shells = new ArrayList<Shell>();
        bullets = new ArrayList<Bullet>();
        allies = new ArrayList<Ally>();
        fishes = new ArrayList<Fish>();
        if (level > 0) {
            for (int i = 0; i < level; i++) {
                allies.add(new Ally(i % Ally.NUMBER));
            }
        }
        for (int i = 0; i < 2; i++) {
            fishes.add(new Fish(0));
        }
    }

    public boolean buyFish() {
        if (playing && money >= Fish.PRICE && fishes.size() < Fish.LIMIT) {
            money -= Fish.PRICE;
            buyingFish = true;
            GameActivity.mGameSound.playSound(GameSound.fish_new);
        } else {
            GameActivity.mGameSound.playSound(GameSound.buy_no);
            GameActivity.showGetCoinDialog();
        }
        return buyingFish;
    }

    public boolean buyEggs() {
        int price = (tankLevel + 1) * EGG_PRICE;
        if (playing && eggsLevel < EGG_MAX && money >= price) {
            money -= price;
            eggsLevel++;
            GameActivity.mGameSound.playSound(GameSound.buy_yes);
            return true;
        }
        GameActivity.mGameSound.playSound(GameSound.buy_no);
        GameActivity.showGetCoinDialog();
        return false;
    }

    public boolean buyFood() {
        int price = Food.PRICES[foodLevel];
        if (playing && foodLevel < Food.PRICES.length - 1 & money >= price
                && foods.size() < Food.LIMIT) {
            money -= price;
            foodLevel++;
            GameActivity.mGameSound.playSound(GameSound.buy_yes);
            return true;
        }
        GameActivity.mGameSound.playSound(GameSound.buy_no);
        GameActivity.showGetCoinDialog();
        return false;
    }

    public boolean buyGunz() {
        int price = Bullet.PRICES[gunzLevel];
        if (playing && gunzLevel < Bullet.PRICES.length - 1 & money >= price) {
            money -= price;
            gunzLevel++;
            GameActivity.mGameSound.playSound(GameSound.buy_yes);
            return true;
        }
        GameActivity.mGameSound.playSound(GameSound.buy_no);
        GameActivity.showGetCoinDialog();
        return false;
    }

    @Override
    public void update() {
        if (playing) {
            if (!fighting) {
                timeToBattle--;
                if (timeToBattle == 9 * GameThread.FPS) {
                    GameActivity.mGameSound.playMusic(R.raw.danger);
                }
                if (timeToBattle <= 0) {
                    fighting = true;
                    enemies.add(new Enemy(enemyLevel));
                    GameActivity.mGameSound.playMusic(R.raw.battle);
                }
            }
            for (int index = 0; index < enemies.size(); index++) {
                Enemy enemy = enemies.get(index);
                enemy.update();
                if (!enemy.alive) {
                    enemies.remove(index);
                    coins.add(new Coin(Coin.VALUES.length - 1, enemy.x, enemy.y));
                    timeToBattle = TIME_TO_BATTLE;
                    fighting = false;
                    enemyLevel++;
                    if (GameActivity.mGameSound.mMusic) {
                        GameActivity.mGameSound.mMediaPlayer.pause();
                    }
                    GameActivity.mGameSound.playMusic(R.raw.tank_0 + tankLevel);
                    GameActivity.mGameSound.playSound(GameSound.enemy_die);
                }
            }
            for (Ally ally : allies) {
                ally.update();
                ally.collectProduct(coins, shells);
                if (ally.making) {
                    ally.making = false;
                    foods.add(new Food(ally.level, ally.x - ally.width / 2,
                            ally.y));
                    shells.add(new Shell(ally.level, ally.x + ally.width / 2,
                            ally.y));
                }
            }
            if (buyingFish) {
                buyingFish = false;
                fishes.add(new Fish(0));
            }
            for (int index = 0; index < fishes.size(); index++) {
                Fish fish = fishes.get(index);
                fish.findFood(foods);
                fish.attackedBy(enemies);
                fish.update();
                if (fish.making) {
                    fish.making = false;
                    coins.add(new Coin(fish.level, fish.x, fish.y));
                }
                if (!fish.alive) {
                    fishes.remove(index);
                    if (fishes.size() == 0) {
                        GameActivity.gameOver();
                    }
                }
            }
            if (buyingFood) {
                buyingFood = false;
                foods.add(new Food(foodLevel, x, y));
                GameActivity.mGameSound.playSound(GameSound.food);
            }
            for (int index = 0; index < foods.size(); index++) {
                Food food = foods.get(index);
                food.update();
                if (!food.alive) {
                    foods.remove(index);
                }
            }
            for (int index = 0; index < coins.size(); index++) {
                Coin coin = coins.get(index);
                coin.update();
                if (!coin.alive) {
                    if (money + coin.value < Integer.MAX_VALUE) {
                        money += coin.value;
                    }
                    coins.remove(index);
                }
            }
            for (int index = 0; index < shells.size(); index++) {
                Shell shell = shells.get(index);
                shell.update();
                if (!shell.alive) {
                    if (money + shell.value < Integer.MAX_VALUE) {
                        money += shell.value;
                    }
                    shells.remove(index);
                }
            }
            if (buyingBullet) {
                buyingBullet = false;
                bullets.add(new Bullet(gunzLevel, x, y));
                GameActivity.mGameSound.playSound(GameSound.gun_00 + gunzLevel);
            }
            for (int index = 0; index < bullets.size(); index++) {
                Bullet bullet = bullets.get(index);
                bullet.update();
                if (!bullet.alive) {
                    bullets.remove(index);
                }
            }
            GameActivity.updateText(money, timeToBattle / GameThread.FPS);
        }
    }

    @Override
    public boolean touch(MotionEvent event) {
        if (playing) {
            if (enemies.size() == 0) {
                boolean touched = false;
                if (!touched) {
                    for (Coin coin : coins) {
                        touched = coin.touch(event);
                        if (touched) {
                            break;
                        }
                    }
                }
                if (!touched) {
                    for (Shell shell : shells) {
                        touched = shell.touch(event);
                        if (touched) {
                            break;
                        }
                    }
                }
                if (!touched) {
                    int price = Food.COSTS[foodLevel];
                    if (money >= price) {
                        money -= price;
                        buyingFood = true;
                        x = event.getX() * Tank.WIDTH / GameView.width;
                        y = event.getY() * Tank.HEIGHT / GameView.height;
                    } else {
                        GameActivity.mGameSound.playSound(GameSound.buy_no);
                        GameActivity.showGetCoinDialog();
                    }
                }
            } else {
                int price = Bullet.COSTS[gunzLevel];
                if (money >= price) {
                    for (Enemy enemy : enemies) {
                        if (enemy.touch(event)) {
                            x = event.getX() * Tank.WIDTH / GameView.width;
                            y = event.getY() * Tank.HEIGHT / GameView.height;
                            money -= price;
                            buyingBullet = true;
                            enemy.attackedBy(Bullet.DAMAGES[gunzLevel]);
                            break;
                        }
                    }
                } else {
                    GameActivity.mGameSound.playSound(GameSound.buy_no);
                    GameActivity.showGetCoinDialog();
                }
            }
        }
        return true;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawBitmap(
                GameActivity.mGameBitmap.get(GameBitmap.TANKS + tankLevel % 6),
                0, 0, null);
        for (Food food : foods) {
            food.render(canvas);
        }
        for (Fish fish : fishes) {
            fish.render(canvas);
        }
        for (Ally ally : allies) {
            ally.render(canvas);
        }
        for (Enemy enemy : enemies) {
            enemy.render(canvas);
        }
        for (Coin coin : coins) {
            coin.render(canvas);
        }
        for (Shell shell : shells) {
            shell.render(canvas);
        }
        for (Bullet bullet : bullets) {
            bullet.render(canvas);
        }
    }

}
