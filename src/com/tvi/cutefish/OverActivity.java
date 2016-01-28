package com.tvi.cutefish;

import com.tvi.cutefish.dialogs.ConfirmCallback;
import com.tvi.cutefish.dialogs.ConfirmDialog;
import com.tvi.cutefish.engine.GameSound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class OverActivity extends Activity implements OnClickListener {

    private Button mbtReplay;
    private Button mbtQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over);
        mbtQuit = (Button) findViewById(R.id.bt_quit);
        mbtReplay = (Button) findViewById(R.id.bt_replay);
        mbtQuit.setOnClickListener(this);
        mbtReplay.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GameActivity.mGameSound.playMusic(R.raw.over);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (GameActivity.mGameSound.mMusic) {
            GameActivity.mGameSound.mMediaPlayer.pause();
        }
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

        case R.id.bt_replay:
            onClickReplay();
            break;

        default:
            break;
        }
    }

    private void onClickReplay() {
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

}
