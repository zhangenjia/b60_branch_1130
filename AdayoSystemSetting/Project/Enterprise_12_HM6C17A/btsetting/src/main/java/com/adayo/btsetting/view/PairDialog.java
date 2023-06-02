package com.adayo.btsetting.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

/**
 * @author Y4134
 */
public class PairDialog extends Dialog {
    Context mContext;
    private RelativeLayout mRlContainer;
    private TextView mTvName;
    private TextView mTvName1;
    private ImageView mIvPairing;
    private static final String TAG = PairDialog.class.getSimpleName();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static volatile PairDialog mDialog;

    public static PairDialog getInstance(Context context) {
        if (mDialog == null) {
            synchronized (PairDialog.class) {
                if (mDialog == null) {
                    mDialog = new PairDialog(context);
                }
            }
        }
        return mDialog;
    }

    public PairDialog(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }



    public PairDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    protected PairDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        initView();
    }


    @Override
    public void show() {
        super.show();
        if (mRlContainer!=null){
            AAOP_HSkin.with(mRlContainer)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.popup_background_small_btsetting)
                    .applySkin(false);
        }
        showDialogAnimation();
        startAnim();
    }


    @Override
    public void dismiss() {
        clearAnim();
        super.dismiss();
    }

    public void showToast() {
        show();
        mHandler.postDelayed(this::dismiss, 2000);
    }

    public void setText(String txt) {
        if (mTvName != null) {
            mTvName.setText(txt);
        }
    }

    public void setText1(String txt) {
        if (mTvName1 != null) {
            mTvName1.setText(txt);
        }
    }

    public void startAnim() {
        Log.d(TAG, "startAnim: mIvPairing" + mIvPairing);
        if (mIvPairing != null) {
            AnimationDrawable animationDrawable = (AnimationDrawable) mIvPairing.getDrawable();
            if (animationDrawable != null) {
                animationDrawable.start();
            }
        }
    }

    public void clearAnim() {
        if (mIvPairing != null) {
            AnimationDrawable animationDrawable = (AnimationDrawable) mIvPairing.getDrawable();
            if (animationDrawable != null) {
                animationDrawable.start();
            }
        }
    }

    private void initView() {
        Log.d(TAG, "initView: ");
        LayoutInflater inflater = AAOP_HSkin.getLayoutInflater(mContext);
        Log.d(TAG, "initView: inflater " + inflater);
        View dialogView = inflater.inflate(R.layout.bt_dlg_bt_pairing, null);
        Log.d(TAG, "initView: dialogView " + dialogView);
        AAOP_HSkin
                .getInstance()
                .applySkin(dialogView , true);
        AAOP_HSkin.getWindowViewManager().addWindowView(dialogView);
        setContentView(dialogView);
        mRlContainer = dialogView.findViewById(R.id.rl_container);
        mIvPairing = dialogView.findViewById(R.id.iv_pairing);
        mTvName = dialogView.findViewById(R.id.textView2);
        mTvName1 = dialogView.findViewById(R.id.textView1);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //可以调整Dialog显示的位置
        params.gravity = Gravity.BOTTOM;
        //params.gravity = Gravity.CENTER_HORIZONTAL;
        window.setAttributes(params);
        //去除Dialog的白色背景
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setLayout(838, 188);
        //清除灰色背景
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //点击外部区域不可取消
        setCanceledOnTouchOutside(false);
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
        if (mRlContainer != null) {
            mRlContainer.startAnimation(setAnim);
        } else {
            Log.e(TAG, "showDialogAnimation: mLlContainer = null");
        }

    }


    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
    public void dismissDialogAnimation() {
        Log.d("CommonDialog", "dismissDialogAnimation");
        mRlContainer.clearAnimation();
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

        mRlContainer.startAnimation(setAnim);
    }

}
