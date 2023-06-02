package com.adayo.btsetting.view;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

/**
 * @author Y4134
 */
public class DisConnectAndConnectOtherDialog extends Dialog {
    private Context mContext;
    private RelativeLayout mView;
    private Button mBtnCancel;
    private Button mBtnSure;
    private TextView mTvTittle;
    private TextView mTvMsg;
    private ImageView mIvConnect;
    private RelativeLayout mRlContainer;
    private OnPosClickListener mPosListener;
    private OnNegClickListener mNegListener;

    private static volatile DisConnectAndConnectOtherDialog mDialog;

    public static DisConnectAndConnectOtherDialog getInstance(Context context) {
        if (mDialog == null) {
            synchronized (DisConnectAndConnectOtherDialog.class) {
                if (mDialog == null) {
                    mDialog = new DisConnectAndConnectOtherDialog(context);
                }
            }
        }
        return mDialog;
    }

    public DisConnectAndConnectOtherDialog(Context context) {
        super(context, R.style.TransparentStyle);
        this.mContext = context;
        initView();
    }

    public DisConnectAndConnectOtherDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    protected DisConnectAndConnectOtherDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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
        setText();
        super.show();
        //showDialogAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setText(){
        if (mIvConnect!=null){
            AAOP_HSkin.with(mIvConnect)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_singlerow_disconnects)
                    .applySkin(false);
        }
        if (mRlContainer!=null){
            AAOP_HSkin.with(mRlContainer)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.popup_background_btsetting)
                    .applySkin(false);
        }
        if (mTvMsg!=null){
            mTvMsg.setText(R.string.conn_bt_connect_new_dlg_msg);
        }
        if (mBtnSure!=null){
            mBtnSure.setText(R.string.conn_bt_connect_new_dlg_positive_btn);
        }
        if (mBtnCancel!=null){
            mBtnCancel.setText(R.string.conn_bt_connect_new_dlg_negative_btn);
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
                .inflate(R.layout.bt_dlg_cfm, null);
        AAOP_HSkin
                .getInstance()
                .applySkin(dialogView , true);
        AAOP_HSkin.getWindowViewManager().addWindowView(dialogView);
        setContentView(dialogView);
        mRlContainer = findViewById(R.id.rl_container);
        mIvConnect = findViewById(R.id.iv_connect);
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


        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //可以调整Dialog显示的位置
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setLayout(838, 418);
        setCanceledOnTouchOutside(true);
        /**
         *         //去除Dialog的白色背景
         *         //window.setBackgroundDrawableResource(R.color.transpare);
         *
         *         //清除灰色背景
         *         //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
         *         //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
         *         //点击外部区域不可取消
         */
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
