package com.adayo.btsetting.view;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.systemservice.SystemServiceManager;

/**
 * 带有动画【淡入+平移】的自定义Dialog
 * @author Y4134
 */
public class DisConnectDialog extends Dialog {
    private Context mContext;
    private Button mBtnCancel;
    private Button mBtnSure;
    private TextView mTvTittle;
    private TextView mTvMsg;
    private RelativeLayout mRlContainer;
    private OnPosClickListener mPosListener;
    private OnNegClickListener mNegListener;

    private static volatile DisConnectDialog mDialog;

    public static DisConnectDialog getInstance(Context context) {
        if (mDialog == null) {
            synchronized (DisConnectDialog.class) {
                if (mDialog == null) {
                    mDialog = new DisConnectDialog(context);
                }
            }
        }
        return mDialog;
    }

    public DisConnectDialog(Context context) {
        super(context, R.style.TransparentStyle);
        this.mContext = context;
        initView();
    }

    public DisConnectDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    protected DisConnectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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

    public void setBtnTxt(String firstTxt,String secondTxt){
        if (mBtnSure!=null&&mBtnCancel!=null){
            mBtnSure.setText(firstTxt);
            mBtnCancel.setText(secondTxt);
        }
    }

    public void setText(){
        if (mRlContainer!=null){
            AAOP_HSkin.with(mRlContainer)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.popup_background_btsetting)
                    .applySkin(false);
        }
        if (mTvMsg!=null){
            mTvMsg.setText(R.string.conn_bt_disconnect_dlg_msg);
        }
        if (mBtnSure!=null){
            mBtnSure.setText(R.string.conn_bt_disconnect_dlg_positive_btn);
        }
        if (mBtnCancel!=null){
            mBtnCancel.setText(R.string.conn_bt_disconnect_dlg_negative_btn);
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

    @SuppressLint("SetTextI18n")
    private void initView(){
        View dialogView = AAOP_HSkin.getLayoutInflater(mContext)
                .inflate(R.layout.bt_dlg_disconnect, null);
        AAOP_HSkin
                .getInstance()
                .applySkin(dialogView , true);
        AAOP_HSkin.getWindowViewManager().addWindowView(dialogView);
        setContentView(dialogView);
        mRlContainer = findViewById(R.id.rl_container);
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
