package com.adayo.app.btphone.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;


public class SyncFailDialog extends Dialog {

    private View mView;

    public SyncFailDialog(Context context) {
        super(context);
        mView = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.layout_dialog_sync_fail,null);
        setContentView(mView);
        initParam();
        AAOP_HSkin.getWindowViewManager().addWindowView(mView);
    }

    private void initParam(){
        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.height = 188;
        mWindowParams.width = 838;
        mWindowParams.x = 542;
        mWindowParams.y = 751;
        Window window = getWindow();
        window.setAttributes(mWindowParams);
        window.setBackgroundDrawableResource(R.color.vifrification);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void show() {
        super.show();
        showDialogAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialogAnimation();
            }
        },2000);
    }

    /**
     * showDialogAnimation
     * 淡入+平移
     */
    public void showDialogAnimation(){
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(0f, 1.0f);
        animAlpha.setDuration(500);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);
        mView.startAnimation(setAnim);
    }

    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
    public void dismissDialogAnimation(){
        Log.d("CommonDialog", "dismissDialogAnimation");
        mView.clearAnimation();
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1.0f);
        animTranslate.setDuration(633);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0f);
        animAlpha.setDuration(633);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);

        setAnim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) { dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mView.startAnimation(setAnim);
    }
}
