package com.adayo.app.dvr.widget;

import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.app.dvr.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class CustomDialog extends Dialog {

    private static final String TAG = APP_TAG + CustomDialog.class.getSimpleName();
    public static final int TYPE_NORMAL = 0;

    private Context mContext;
    private static CustomDialog mCustomDialog;
    private View mView;
    private TextView dialogTitle;
    private TextView dialogText1;
    private TextView dialogOk;
    private TextView dialogCancel;

    private ImageView dialogTitleIcon;

    public CustomDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public static CustomDialog getInstance(Context context) {
        if (mCustomDialog == null) {
            mCustomDialog = new CustomDialog(context);
        }
        return mCustomDialog;
    }

    public CustomDialog initView(int type, int resource) {
        Log.d(TAG, "initView: type = " + type);
        switch (type) {
            case TYPE_NORMAL:
                mView = AAOP_HSkin.getLayoutInflater(mContext).inflate(resource, null);
                setContentView(mView);
                dialogTitle = mView.findViewById(R.id.dialog_title);
                dialogText1 = mView.findViewById(R.id.dialog_text1);
                dialogOk = mView.findViewById(R.id.dialog_ok);
                dialogCancel = mView.findViewById(R.id.dialog_cancel);
                dialogTitleIcon = mView.findViewById(R.id.iv_title);

                Window window = getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                //可以调整Dialog显示的位置
                params.gravity = Gravity.CENTER_HORIZONTAL;
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


                break;
            default:
                break;
        }
//        mCustomDialog.setCancelable(false);
        mCustomDialog.setCanceledOnTouchOutside(true);
        return mCustomDialog;
    }

    public CustomDialog setText(int title, int text1, int text2) {
        Log.d(TAG, "initView: title = " + title + ";text1 ="
                + text1 + ";text2 = " + text2);
        AAOP_HSkin.with(dialogTitle)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,title)
                .applyLanguage(false);
        AAOP_HSkin.with(dialogText1)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,text1)
                .applyLanguage(false);

        return mCustomDialog;
    }

    public CustomDialog setOkText(int text) {
        AAOP_HSkin.with(dialogOk)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,text)
                .applyLanguage(false);

        return mCustomDialog;
    }

    public CustomDialog setCancelText(int text) {
        AAOP_HSkin.with(dialogCancel)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,text)
                .applyLanguage(false);
        return mCustomDialog;
    }

    public CustomDialog setImageView(int resource) {

        AAOP_HSkin
                .with(dialogTitleIcon)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, resource)
                .applySkin(false);

        return mCustomDialog;
    }

    private OnClickListener mClickListener;

    public CustomDialog initClickListener(OnClickListener onClickListener) {
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.ok();

                }
                dismiss();
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.cancel();
                }
                dismiss();
            }
        });
        mClickListener = onClickListener;
        return mCustomDialog;
    }

    public interface OnClickListener {
        //点击确定方法
        void ok();
        //取消方法
        void cancel();

    }

    public void showDialog() {
        Log.d(TAG, "show: ");
        if (mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog.show();
    }

    public void dismissDialog() {
        Log.d(TAG, "dismiss: ");
        if (mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

}
