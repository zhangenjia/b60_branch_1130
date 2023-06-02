package com.adayo.btsetting.view;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

/**
 * @author Y4134
 */
public class PhoneRequestPairDialog extends Dialog {
    private Context mContext;
    private RelativeLayout mView;
    private Button mBtnCancel;
    private Button mBtnSure;
    private TextView mTvTittle;
    private TextView mTvMsg;
    private ImageView mIvConnect;
    private OnPosClickListener mPosListener;
    private OnNegClickListener mNegListener;


    private static volatile PhoneRequestPairDialog mDialog;

    public static PhoneRequestPairDialog getInstance(Context context) {
        if (mDialog == null) {
            synchronized (PhoneRequestPairDialog.class) {
                if (mDialog == null) {
                    mDialog = new PhoneRequestPairDialog(context);
                }
            }
        }
        return mDialog;
    }

    public PhoneRequestPairDialog(Context context) {
        super(context, R.style.TransparentStyle);
        this.mContext = context;
        initView();
    }

    public PhoneRequestPairDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    protected PhoneRequestPairDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        initView();
    }


    public void setOnPosListener(OnPosClickListener posListener) {
        this.mPosListener = posListener;
    }

    public void setOnNegListener(OnNegClickListener negListener) {
        this.mNegListener = negListener;
    }

    @Override
    public void show() {
//        setText();
        if (mView!=null){
            AAOP_HSkin.with(mView)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.popup_background_btsetting)
                    .applySkin(false);
        }
        if (mIvConnect!=null){
            AAOP_HSkin.with(mIvConnect)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_singlerow_net)
                    .applySkin(false);
        }
        super.show();
        //showDialogAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setText(){
        if (mTvTittle!=null){
            mTvTittle.setText(R.string.conn_bt_del_dlg_title);
        }
        if (mTvMsg!=null){
            mTvMsg.setText(R.string.conn_bt_del_dlg_msg);
        }
        if (mBtnSure!=null){
            mBtnSure.setText(R.string.conn_bt_del_dlg_positive_btn);
        }
        if (mBtnCancel!=null){
            mBtnCancel.setText(R.string.conn_bt_del_dlg_negative_btn);
        }
    }

    public void setConnectIcon(){
        if (mIvConnect!=null){
            mIvConnect.setBackgroundResource(R.drawable.icon_singlerow_net);
        }
    }

    public void setBtnTxt(String firstTxt,String secondTxt){
        if (mBtnSure!=null&&mBtnCancel!=null){
            mBtnSure.setText(firstTxt);
            mBtnCancel.setText(secondTxt);
        }
    }

    public void setTitle(String title){
        if (mTvTittle!=null){
            mTvTittle.setText(title);
        }
    }

    public void setMsg(String msg){
        if (mTvMsg!=null){
            mTvMsg.setText(msg);
        }
    }

    private void initView(){
        View dialogView = AAOP_HSkin.getLayoutInflater(mContext)
                .inflate(R.layout.bt_dlg_phone_req, null);
        AAOP_HSkin
                .getInstance()
                .applySkin(dialogView , true);
        AAOP_HSkin.getWindowViewManager().addWindowView(dialogView);
        setContentView(dialogView);
        mView = findViewById(R.id.cl_container);
        mBtnCancel = findViewById(R.id.btn_cfm_negative);
        mBtnCancel.setOnClickListener(v -> {
            dismiss();
            if (mNegListener!=null){
                mNegListener.onClick();
            }
        });
        mBtnSure = findViewById(R.id.btn_cfm_positive);
        mBtnSure.setOnClickListener(v -> {
            dismiss();
            if (mPosListener!=null){
                mPosListener.onClick();
            }
        });
        mTvTittle = findViewById(R.id.tv_cfm_title);
        mTvMsg = findViewById(R.id.tv_cfm_msg);
        mIvConnect = findViewById(R.id.iv_connect);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //可以调整Dialog显示的位置
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setLayout(838, 470);
        setCanceledOnTouchOutside(true);
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
        mView.startAnimation(setAnim);
    }


    /**
     * 获取当前Dialog坐标
     */
    /*int x =0;
    int y =0;
    private void getCurrentPosition(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        x = params.x;
        y = params.y;
    }*/



    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
    public void dismissDialogAnimation(){
        Log.d("CommonDialog", "dismissDialogAnimation");
        mView.clearAnimation();
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

        mView.startAnimation(setAnim);
    }

    public interface OnPosClickListener{
        /**
         * 确认
         */
        void onClick();
    }

    public interface OnNegClickListener{
        /**
         * 取消
         */
        void onClick();
    }

}
