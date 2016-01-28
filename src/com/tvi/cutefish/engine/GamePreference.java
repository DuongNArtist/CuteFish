package com.tvi.cutefish.engine;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;

public class GamePreference {

    public static final String NAME = "TVi";
    public static final String LEVEL = "Level";

    private static GamePreference mInstance;

    private SharedPreferences mSharedPreferences;

    public static GamePreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GamePreference(context);
        }
        return mInstance;
    }

    public static String parseMoney(int money) {
        Locale locale = new Locale("en", "us");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        String strMoney = numberFormat.format(money).substring(1,
                numberFormat.format(money).length() - 3);
        return strMoney.replace(",", ".");
    }

    private GamePreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
    }

    public void setLevel(int level) {
        mSharedPreferences.edit().putInt(LEVEL, level).commit();
    }

    public int getLevel() {
        return mSharedPreferences.getInt(LEVEL, 0);
    }
}
