package com.tvi.cutefish.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.tvi.cutefish.R;

public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getAttributes().windowAnimations = R.style.MyPopupTheme;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mContext = context;
        setContentView(getViewId());
        bindView();
    }

    public abstract int getViewId();

    public abstract void bindView();
}
