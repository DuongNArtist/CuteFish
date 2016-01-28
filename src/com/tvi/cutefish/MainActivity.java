package com.tvi.cutefish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tvi.cutefish.engine.GameBitmap;
import com.tvi.cutefish.engine.GamePreference;
import com.tvi.cutefish.engine.GameScreen;
import com.tvi.cutefish.engine.GameSound;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHashkey();
        new Thread(new Runnable() {

            @Override
            public void run() {
                GameActivity.mGameSound = GameSound
                        .getInstance(MainActivity.this);
                GameActivity.mGameBitmap = GameBitmap
                        .getInstance(MainActivity.this);
                GameActivity.mGamePreference = GamePreference
                        .getInstance(MainActivity.this);
                GameActivity.mGameBitmap.load();
                GameActivity.mGameSound.load();
                GameActivity.mGameScreen = new GameScreen(
                        GameActivity.mGamePreference.getLevel());
                startActivity(new Intent(MainActivity.this, GameActivity.class));
                finish();
            }
        }).start();
    }

    public String getHashkey() {
        String key = null;
        try {
            PackageInfo packageInfo;
            String packageName = getApplicationContext().getPackageName();
            packageInfo = getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            Log.e("Package Name = ", getApplicationContext().getPackageName());
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                key = new String(Base64.encode(messageDigest.digest(), 0));
                Log.e("Key Hashes = ", key);
            }
        } catch (NameNotFoundException e) {
            Log.e("NameNotFoundException", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("NoSuchAlgorithmException", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }
}
