package com.adayo.app.dvr.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 带有动画【淡入+平移】的自定义Dialog
 */
public class UnderCommonDialog extends Dialog {
    private View mView;
    private TextView textView;
    private ImageView imageView;
    private ImageView animImageView;
    private ConstraintLayout imageLayout;
    private Context mContext;
    private AnimationDrawable amin;
    private volatile static UnderCommonDialog commonDialog;

    private UnderCommonDialog(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public static UnderCommonDialog getCommonDialog(Context context) {
        if (commonDialog == null) {
            synchronized (UnderCommonDialog.class) {
                if (commonDialog == null) {
                    commonDialog = new UnderCommonDialog(context);
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

        mView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_under_dialog,null);
        setContentView(mView);
        imageLayout = mView.findViewById(R.id.image_constraintLayout);
        textView = mView.findViewById(R.id.tv_dialog_tip);
        animImageView = mView.findViewById(R.id.iv_anim_dialog_tip_title);
        imageView = mView.findViewById(R.id.iv_dialog_tip_title);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //可以调整Dialog显示的位置
        params.gravity = Gravity.BOTTOM;
        params.y = 0;
        params.width = 836;
        params.height = 186;
//        params.gravity = Gravity.CENTER_HORIZONTAL;
        window.setAttributes(params);
        //去除Dialog的白色背景
        window.setBackgroundDrawableResource(R.color.transparent);

        //清除灰色背景
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


        //为dialog的根View和子View应用当前皮肤样式
        AAOP_HSkin.getInstance()
                .applySkin(mView, true);

        //将Dialog添加到框架的WindowViewManager内以实现实时换肤
        // addWindowView和removeWindowView最好成对使用以减少框架压力
        AAOP_HSkin
                .getWindowViewManager()
                .addWindowView(mView);
        //点击外部区域不可取消
    }

    public UnderCommonDialog initStringAnimView(String s, int drawable){
        imageLayout.setVisibility(View.VISIBLE);
        AAOP_HSkin
                .with(animImageView)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, drawable)
                .applySkin(false);
        amin = (AnimationDrawable) animImageView.getBackground();
        animImageView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        if (!amin.isRunning()) {
            amin.start();
        }
        textView.setText(s);
        return commonDialog;
    }

    public UnderCommonDialog initStringView(String s) {
        imageView.setVisibility(View.GONE);
        animImageView.setVisibility(View.GONE);
        imageLayout.setVisibility(View.GONE);
        textView.setText(s);
        return commonDialog;
    }

    public UnderCommonDialog initStringImgView(String s,int resource) {
        imageLayout.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        animImageView.setVisibility(View.GONE);
        AAOP_HSkin
                .with(imageView)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, resource)
                .applySkin(false);
        textView.setText(s);
        //set dialog背景图片
        return commonDialog;
    }


    /**
     * showDialogAnimation
     * 淡入+平移
     */
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
                            //do Something
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




    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
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
        if (amin.isRunning()){
            amin.stop();
        }
        mView.startAnimation(setAnim);
    }

}
