package com.adayo.app.setting.view.popwindow;

import android.app.Dialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.skin.SkinUtil;

import java.util.Timer;
import java.util.TimerTask;


public class CommonVoiceDialog extends Dialog {
    private String TAG= CommonVoiceDialog.class.getName();
    private LinearLayout mView;
private ImageView mImage;
    private TextView textView;
    private volatile static CommonVoiceDialog commonDialog;

    private CommonVoiceDialog(Context context) {
        super(context);
        LogUtil.i(TAG,"");
        init();
    }

    public static CommonVoiceDialog getCommonDialog(Context context) {
        if (commonDialog == null) {
            synchronized (CommonVoiceDialog.class) {
                if (commonDialog == null) {
                    commonDialog = new CommonVoiceDialog(context);
                }
            }
        }
        return commonDialog;
    }


    @Override
    public void show() {
        super.show();
        showDialogAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void init() {
        LogUtil.i(TAG,"");
        setContentView(R.layout.toast_hint_voice);
        mImage=findViewById(R.id.iv_icon);
        mView = findViewById(R.id.ll_toast);
        textView = findViewById(R.id.tv_hint_info);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

    public void initIntView(int id,int icon) {
        textView.setText(id);
        LogUtil.i(TAG,"");
        SkinUtil.setImageResource(mImage,icon);

    }

    public void initStringView(String s,int id) {
        textView.setText(s);
        LogUtil.i(TAG,"");
        SkinUtil.setImageResource(mImage,id);
    }



    public void showDialogAnimation() {
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(0f, 1.0f);
        animAlpha.setDuration(1000);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);
        mView.startAnimation(setAnim);
        setAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            dismissDialogAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }







    public void dismissDialogAnimation() {
        Log.d("CommonDialog", "dismissDialogAnimation");
        mView.clearAnimation();
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0f);
        animAlpha.setDuration(1000);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);

        setAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mView.startAnimation(setAnim);
    }

}
