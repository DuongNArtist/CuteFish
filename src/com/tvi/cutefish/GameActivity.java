package com.tvi.cutefish;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tvi.cutefish.dialogs.ConfirmCallback;
import com.tvi.cutefish.dialogs.ConfirmDialog;
import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GamePreference;
import com.tvi.cutefish.engine.GameScreen;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.engine.GameView;
import com.tvi.cutefish.entities.Bullet;
import com.tvi.cutefish.entities.Fish;
import com.tvi.cutefish.entities.Food;
import com.tvi.cutefish.entities.Tank;

public class GameActivity extends Activity implements OnClickListener {

    public static final String ACTION = "SMS_SENT";
    public static GameActivity mInstance;
    public static GameSound mGameSound;
    public static GameBitmap mGameBitmap;
    public static GameScreen mGameScreen;
    public static GamePreference mGamePreference;

    public static TextView mtvMoney;
    public static Handler mHandler;
    private FrameLayout mflGame;
    private ImageButton mbtSound;
    private ImageButton mbtMusic;
    private ImageButton mbtPause;
    private ImageButton mbtEggs;
    private ImageButton mbtFish;
    private ImageButton mbtFood;
    private ImageButton mbtGunz;
    private ImageButton mbtShop;
    private ImageButton mbtQuit;
    private TextView mtvFish;
    private TextView mtvFood;
    private TextView mtvGunz;
    private TextView mtvEggs;
    private TextView mtvLevel;
    private int mLevel;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK) {
                mGameScreen.tank.money += 10000;
                Toast.makeText(context,
                        mInstance.getString(R.string.message_successfull),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,
                        mInstance.getString(R.string.message_unsuccessfull),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);
        registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION));
        mInstance = this;
        mHandler = new Handler();
        mflGame = (FrameLayout) findViewById(R.id.fl_game);
        mbtSound = (ImageButton) findViewById(R.id.bt_sound);
        mbtMusic = (ImageButton) findViewById(R.id.bt_music);
        mbtPause = (ImageButton) findViewById(R.id.bt_pause);
        mbtEggs = (ImageButton) findViewById(R.id.bt_eggs);
        mbtFish = (ImageButton) findViewById(R.id.bt_fish);
        mbtFood = (ImageButton) findViewById(R.id.bt_food);
        mbtGunz = (ImageButton) findViewById(R.id.bt_gunz);
        mbtShop = (ImageButton) findViewById(R.id.bt_shop);
        mbtQuit = (ImageButton) findViewById(R.id.bt_quit);
        mtvLevel = (TextView) findViewById(R.id.tv_level);
        mtvFish = (TextView) findViewById(R.id.tv_fish);
        mtvFood = (TextView) findViewById(R.id.tv_food);
        mtvGunz = (TextView) findViewById(R.id.tv_gunz);
        mtvEggs = (TextView) findViewById(R.id.tv_eggs);
        mtvMoney = (TextView) findViewById(R.id.tv_money);
        mbtSound.setOnClickListener(this);
        mbtMusic.setOnClickListener(this);
        mbtPause.setOnClickListener(this);
        mbtEggs.setOnClickListener(this);
        mbtFish.setOnClickListener(this);
        mbtFood.setOnClickListener(this);
        mbtGunz.setOnClickListener(this);
        mbtEggs.setOnClickListener(this);
        mbtShop.setOnClickListener(this);
        mbtQuit.setOnClickListener(this);
        mtvFish.setText(GamePreference.parseMoney(Fish.PRICE));
        mLevel = mGamePreference.getLevel();
        mtvLevel.setText((mLevel + 1) + "");
        mGameScreen = new GameScreen(mLevel);
        updateLevel();
        mflGame.addView(new GameView(this, mGameScreen), 0);
    }

    public void updateLevel() {
        updateEggs();
        updateFood();
        updateGunz();
    }

    public static void updateText(final int money, final int time) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mtvMoney.setText(GamePreference.parseMoney(money));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        playGame();
        if (mGameScreen.tank.fighting) {
            mGameSound.playMusic(R.raw.battle);
        } else {
            mGameSound.playMusic(R.raw.tank_0 + mGameScreen.tank.tankLevel
                    % Tank.NUMBER);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
        if (mGameSound.mMusic) {
            mGameSound.mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {
        mGameSound.playSound(GameSound.click);
        view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_button));
        switch (view.getId()) {

        case R.id.bt_quit:
            onClickQuitButton();
            break;

        case R.id.bt_shop:
            onClickShopButton();
            break;

        case R.id.bt_sound:
            onClickSoundButton();
            break;

        case R.id.bt_music:
            onClickMusicButton();
            break;

        case R.id.bt_pause:
            onClickPauseButton();
            break;

        case R.id.bt_eggs:
            onClickBuyEggsButton();
            break;

        case R.id.bt_fish:
            onClickBuyFishButton();
            break;

        case R.id.bt_food:
            onClickBuyFoodButton();
            break;

        case R.id.bt_gunz:
            onClickBuyGunzButton();
            break;

        default:
            break;
        }
    }

    private void onClickQuitButton() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.mtvTitle.setText(getString(R.string.title_quit_game));
        dialog.mtvMessage.setText(getString(R.string.message_quit_game));
        dialog.mConfirmCallback = new ConfirmCallback() {

            @Override
            public void onClickYes() {
                dialog.dismiss();
                System.gc();
                System.exit(0);
            }

            @Override
            public void onClickNo() {
                dialog.dismiss();
            }
        };
        dialog.show();
    }

    private void onClickShopButton() {
        showGetCoinDialog();
    }

    private void pauseGame() {
        mGameScreen.tank.playing = false;
        mbtPause.setImageResource(R.drawable.play);
    }

    private void playGame() {
        mGameScreen.tank.playing = true;
        mbtPause.setImageResource(R.drawable.pause);
    }

    private void onClickPauseButton() {
        if (mGameScreen.tank.playing) {
            pauseGame();
        } else {
            playGame();
        }
    }

    private void onClickBuyFishButton() {
        mGameScreen.tank.buyFish();
    }

    private void onClickBuyEggsButton() {
        if (mGameScreen.tank.buyEggs()) {
            updateEggs();
        }
    }

    private void onClickBuyFoodButton() {
        if (mGameScreen.tank.buyFood()) {
            updateFood();
        }
    }

    private void onClickBuyGunzButton() {
        if (mGameScreen.tank.buyGunz()) {
            updateGunz();
        }
    }

    private void onClickMusicButton() {
        mGameSound.mMusic = !mGameSound.mMusic;
        if (mGameSound.mMusic) {
            mbtMusic.setImageResource(R.drawable.musicoff);
            if (mGameScreen.tank.fighting) {
                mGameSound.playMusic(R.raw.battle);
            } else {
                mGameSound.playMusic(R.raw.tank_0 + mGameScreen.tank.tankLevel
                        % 6);
            }
        } else {
            mbtMusic.setImageResource(R.drawable.musicon);
            mGameSound.mMediaPlayer.pause();
        }
    }

    private void onClickSoundButton() {
        mGameSound.mSound = !mGameSound.mSound;
        if (mGameSound.mSound) {
            mbtSound.setImageResource(R.drawable.soundoff);
        } else {
            mbtSound.setImageResource(R.drawable.soundon);
        }
    }

    private void updateEggs() {
        int myEggs = mGameScreen.tank.eggsLevel;
        if (myEggs < Tank.EGG_MAX) {
            mbtEggs.setImageResource(R.drawable.egg0 + myEggs);
            mtvEggs.setText(GamePreference.parseMoney((mLevel + 1)
                    * Tank.EGG_PRICE));
        } else {
            mGamePreference.setLevel(mLevel + 1);
            startActivity(new Intent(this, WinActivity.class));
            finish();
        }
    }

    private void updateFood() {
        int myFood = mGameScreen.tank.foodLevel;
        if (myFood < Food.PRICES.length - 1) {
            mbtFood.setImageResource(R.drawable.food0 + myFood);
            mtvFood.setText(GamePreference.parseMoney(Food.PRICES[myFood]));
        } else {
            mbtFood.setImageResource(R.drawable.star);
            mtvFood.setText(getResources().getString(R.string.max));
        }
    }

    private void updateGunz() {
        int myGunz = mGameScreen.tank.gunzLevel;
        if (myGunz < Bullet.PRICES.length - 1) {
            mbtGunz.setImageResource(R.drawable.gun0 + myGunz);
            mtvGunz.setText(GamePreference.parseMoney(Bullet.PRICES[myGunz]));
        } else {
            mbtGunz.setImageResource(R.drawable.star);
            mtvGunz.setText(getResources().getString(R.string.max));
        }
    }

    public static void showGetCoinDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(mInstance);
        dialog.mtvTitle.setText(mInstance.getString(R.string.title_get_coin));
        dialog.mtvMessage.setText(mInstance
                .getString(R.string.message_get_coin));
        dialog.mConfirmCallback = new ConfirmCallback() {

            @Override
            public void onClickYes() {
                String number = mInstance.getString(R.string.provider_number);
                String text = mInstance.getString(R.string.message_content)
                        + " " + mInstance.getString(R.string.ref_code);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        mInstance, 0, new Intent(ACTION), 0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, text, pendingIntent,
                        null);
                dialog.dismiss();
            }

            @Override
            public void onClickNo() {
                dialog.dismiss();
            }
        };
        dialog.show();
    }

    public static void gameOver() {
        mInstance.finish();
        mInstance.startActivity(new Intent(mInstance, OverActivity.class));
    }

}
