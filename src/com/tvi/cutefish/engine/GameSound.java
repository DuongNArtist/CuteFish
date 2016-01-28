package com.tvi.cutefish.engine;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class GameSound {

    private static GameSound mInstance;

    public static int ally;
    public static int bubble;
    public static int buy_no;
    public static int buy_yes;
    public static int click;
    public static int enemy_new;
    public static int enemy_die;
    public static int fish_new;
    public static int fish_eat;
    public static int fish_grow;
    public static int fish_die;
    public static int food;
    public static int gun_00;
    public static int gun_01;
    public static int gun_02;
    public static int gun_03;
    public static int gun_04;
    public static int gun_05;
    public static int gun_06;
    public static int gun_07;
    public static int gun_08;
    public static int gun_09;
    public static int gun_10;
    public static int money_0;
    public static int money_1;
    public static int money_2;
    public static int money_3;
    public static int money_4;
    public static int money_5;

    public static final String FOLDER = "sfx/";
    public static final String EXT = ".ogg";

    private Context mContext;
    private AudioManager mAudioManager;
    private SoundPool mSoundPool;
    public MediaPlayer mMediaPlayer;
    public boolean mMusic = true;
    public boolean mSound = true;

    @SuppressWarnings("deprecation")
    private GameSound(Context context) {
        this.mContext = context;
        mSoundPool = new SoundPool(30, AudioManager.STREAM_MUSIC, 0);
        mAudioManager = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), 0);
    }

    public static GameSound getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GameSound(context);
        }
        return mInstance;
    }

    public void playMusic(int id) {
        if (mMusic) {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                mMediaPlayer.release();
            }
            mMediaPlayer = MediaPlayer.create(mContext, id);
            mMediaPlayer.start();
        }
    }

    public void load() {
        AssetManager assetManager = mContext.getAssets();
        try {
            ally = mSoundPool.load(assetManager.openFd(FOLDER + "ally" + EXT),
                    1);
            bubble = mSoundPool.load(
                    assetManager.openFd(FOLDER + "bubble" + EXT), 1);
            buy_no = mSoundPool.load(
                    assetManager.openFd(FOLDER + "buy_no" + EXT), 1);
            buy_yes = mSoundPool.load(
                    assetManager.openFd(FOLDER + "buy_yes" + EXT), 1);
            click = mSoundPool.load(
                    assetManager.openFd(FOLDER + "click" + EXT), 1);
            enemy_new = mSoundPool.load(
                    assetManager.openFd(FOLDER + "enemy_new" + EXT), 1);
            enemy_die = mSoundPool.load(
                    assetManager.openFd(FOLDER + "enemy_die" + EXT), 1);
            fish_new = mSoundPool.load(
                    assetManager.openFd(FOLDER + "fish_new" + EXT), 1);
            fish_eat = mSoundPool.load(
                    assetManager.openFd(FOLDER + "fish_eat" + EXT), 1);
            fish_grow = mSoundPool.load(
                    assetManager.openFd(FOLDER + "fish_grow" + EXT), 1);
            fish_die = mSoundPool.load(
                    assetManager.openFd(FOLDER + "fish_die" + EXT), 1);
            food = mSoundPool.load(assetManager.openFd(FOLDER + "food" + EXT),
                    1);
            gun_00 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_00" + EXT), 1);
            gun_01 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_01" + EXT), 1);
            gun_02 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_02" + EXT), 1);
            gun_03 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_03" + EXT), 1);
            gun_04 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_04" + EXT), 1);
            gun_05 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_05" + EXT), 1);
            gun_06 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_06" + EXT), 1);
            gun_07 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_07" + EXT), 1);
            gun_08 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_08" + EXT), 1);
            gun_09 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_09" + EXT), 1);
            gun_10 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "gun_10" + EXT), 1);
            money_0 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_0" + EXT), 1);
            money_1 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_1" + EXT), 1);
            money_2 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_2" + EXT), 1);
            money_3 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_3" + EXT), 1);
            money_4 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_4" + EXT), 1);
            money_5 = mSoundPool.load(
                    assetManager.openFd(FOLDER + "money_5" + EXT), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playSound(int idSound) {
        if (mSound) {
            mSoundPool.play(idSound, 1, 1, 1, 0, 1);
        }
    }
}
