package com.adayo.btsetting.view;
import android.app.Dialog;
import android.content.Context;
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
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;


/**
 * @author Y4134
 */
public class ToastDialog extends Dialog {
    Context mContext;
    private RelativeLayout mLlContainer;
    private TextView mTvName;
    private static final String TAG = ToastDialog.class.getSimpleName();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static volatile ToastDialog mDialog;

    public static ToastDialog getInstance(Context context) {
        if (mDialog == null) {
            synchronized (ToastDialog.class) {
                if (mDialog == null) {
                    mDialog = new ToastDialog(context);
                }
            }
        }
        return mDialog;
    }

    public ToastDialog(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public ToastDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    protected ToastDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        initView();
    }


    @Override
    public void show() {
        super.show();
        if (mLlContainer!=null){
            AAOP_HSkin.with(mLlContainer)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.popup_background_small_btsetting)
                    .applySkin(false);
        }
        showDialogAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void showToast(){
        show();
        mHandler.postDelayed(this::dismiss,2000);
    }

    public void setText(String txt){
        if (mTvName!=null){
            mTvName.setText(txt);
        }
    }

    private void initView(){
        Log.d(TAG, "initView: ");


        View dialogView = AAOP_HSkin.getLayoutInflater(mContext)
                .inflate(R.layout.bt_dlg_toast, null);
        AAOP_HSkin
                .getInstance()
                .applySkin(dialogView , true);
        AAOP_HSkin.getWindowViewManager().addWindowView(dialogView);
        mLlContainer = dialogView.findViewById(R.id.pic_animat);
        mTvName= dialogView.findViewById(R.id.tv_name);
        setContentView(dialogView);
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
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //点击外部区域不可取消
        setCanceledOnTouchOutside(false);
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
        animAlpha.setDuration(1000);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);
        if (mLlContainer!=null){
            mLlContainer.startAnimation(setAnim);
        }else {
            Log.e(TAG, "showDialogAnimation: mLlContainer = null");
        }

    }


    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
    public void dismissDialogAnimation(){
        Log.d("CommonDialog", "dismissDialogAnimation");
        mLlContainer.clearAnimation();
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1.0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0f);
        animAlpha.setDuration(1000);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);

        setAnim.setAnimationListener(new Animation.AnimationListener(){
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

        mLlContainer.startAnimation(setAnim);
    }

}
