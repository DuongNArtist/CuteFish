package com.tvi.cutefish.engine;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class GameBitmap {

    public static final String ALLIES = "allies/ally";
    public static final String ENEMIES = "enemies/enemy";
    public static final String FISHES = "fishes/fish";
    public static final String TANKS = "tanks/tank";
    public static final String BULLET = "items/bullet";
    public static final String FOOD = "items/food";
    public static final String COIN = "items/coin";
    public static final String SHELL = "items/shell";
    public static final String FLIPPED = "flipped";

    private static GameBitmap mInstance;
    private HashMap<String, Bitmap> mBitmap;
    private AssetManager mAssetManager;

    private GameBitmap(Context context) {
        mBitmap = new HashMap<String, Bitmap>();
        mAssetManager = context.getAssets();
    }

    public static GameBitmap getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GameBitmap(context);
        }
        return mInstance;
    }

    public void load() {
        for (int i = 0; i < 24; i++) {
            String name = ALLIES + i;
            put(name, decode(name));
            put(ALLIES + i + FLIPPED, flip(get(name)));
        }
        for (int i = 0; i < 8; i++) {
            String name = ENEMIES + i;
            put(name, decode(name));
            put(ENEMIES + i + FLIPPED, flip(get(name)));
        }
        for (int i = 0; i < 5; i++) {
            String name = FISHES + i;
            put(name, decode(name));
            put(FISHES + i + FLIPPED, flip(get(name)));
        }
        for (int i = 0; i < 6; i++) {
            put(TANKS + i, decode(TANKS + i));
        }
        put(BULLET, decode(BULLET));
        put(FOOD, decode(FOOD));
        put(COIN, decode(COIN));
        put(SHELL, decode(SHELL));
    }

    public void release() {
        mBitmap.clear();
        mBitmap = null;
    }

    public Bitmap get(String name) {
        if (mBitmap != null && mBitmap.containsKey(name)) {
            return mBitmap.get(name);
        }
        return null;
    }

    public void put(String name, Bitmap bitmap) {
        mBitmap.put(name, bitmap);
    }

    public Bitmap decode(String name) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = mAssetManager
                    .open("gfx/" + name + ".png");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return bitmap;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Bitmap flip(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);

    }
}
