package com.tvi.cutefish.dialogs;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tvi.cutefish.GameActivity;
import com.tvi.cutefish.R;
import com.tvi.cutefish.engine.GameSound;

public class ConfirmDialog extends BaseDialog implements OnClickListener {

    public TextView mtvTitle;
    public TextView mtvMessage;
    public Button mbtYes;
    public Button mbtNo;
    public ConfirmCallback mConfirmCallback;

    public ConfirmDialog(Context context) {
        super(context);
    }

    @Override
    public int getViewId() {
        return R.layout.dialog_confirm;
    }

    @Override
    public void bindView() {
        mtvTitle = (TextView) findViewById(R.id.tv_title);
        mtvMessage = (TextView) findViewById(R.id.tv_message);
        mbtYes = (Button) findViewById(R.id.bt_yes);
        mbtNo = (Button) findViewById(R.id.bt_no);
        mbtYes.setOnClickListener(this);
        mbtNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        GameActivity.mGameSound.playSound(GameSound.click);
        view.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.anim_button));
        switch (view.getId()) {
        case R.id.bt_yes:
            mConfirmCallback.onClickYes();
            break;

        case R.id.bt_no:
            mConfirmCallback.onClickNo();
            break;

        default:
            break;
        }
    }

}
