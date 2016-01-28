package com.tvi.cutefish;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.tvi.cutefish.dialogs.ConfirmCallback;
import com.tvi.cutefish.dialogs.ConfirmDialog;
import com.tvi.cutefish.engine.GameSound;
import com.tvi.cutefish.entities.Ally;

public class WinActivity extends Activity implements OnClickListener {

    private ImageView mivEgg;
    private ImageView mivAlly;
    private Button mbtShare;
    private Button mbtPlay;
    private Button mbtQuit;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    private int mAlly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mAlly = (GameActivity.mGamePreference.getLevel() - 1) % Ally.NUMBER;
        mivEgg = (ImageView) findViewById(R.id.iv_egg);
        mivAlly = (ImageView) findViewById(R.id.iv_ally);
        mivAlly.setImageResource(R.drawable.ally00 + mAlly);
        mivAlly.setVisibility(ImageView.GONE);
        ((AnimationDrawable) mivEgg.getDrawable()).start();
        GameActivity.mGameSound.playMusic(R.raw.egg);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mivEgg.setVisibility(ImageView.GONE);
                mivAlly.setVisibility(ImageView.VISIBLE);

            }
        }, 3000);
        mbtQuit = (Button) findViewById(R.id.bt_quit);
        mbtPlay = (Button) findViewById(R.id.bt_play);
        mbtShare = (Button) findViewById(R.id.bt_share);
        mbtQuit.setOnClickListener(this);
        mbtPlay.setOnClickListener(this);
        mbtShare.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GameActivity.mGameSound.playMusic(R.raw.win);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (GameActivity.mGameSound.mMusic) {
            GameActivity.mGameSound.mMediaPlayer.pause();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {
        GameActivity.mGameSound.playSound(GameSound.click);
        view.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_button));
        switch (view.getId()) {
        case R.id.bt_quit:
            onClickQuit();
            break;

        case R.id.bt_play:
            onClickPlay();
            break;

        case R.id.bt_share:
            onClickShare();
            break;

        default:
            break;
        }
    }

    private void onClickPlay() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    private void onClickQuit() {
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

    private void onClickShare() {
        mbtShare.setVisibility(Button.GONE);
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        mLoginManager = LoginManager.getInstance();
        mLoginManager.logInWithPublishPermissions(this, permissionNeeds);
        mLoginManager.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        sharePhotoToFacebook();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                    }
                });

    }

    private void sharePhotoToFacebook() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(getBitmap(R.drawable.ally00 + mAlly))
                .setCaption(getString(R.string.message_congratulations))
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(
                photo).build();
        ShareApi.share(content, null);
    }

    public Bitmap getBitmap(int id) {
        return BitmapFactory.decodeStream(getResources().openRawResource(id));
    }

}
