package com.adayo.app.setting.view.popwindow;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.PopupWindow;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.PopupVoiceWakeFreeEleBinding;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.app.base.LogUtil;


public class VoiceWakeFreeEleWindow extends PopupWindow {
    private final static String TAG = VoiceWakeFreeEleWindow.class.getSimpleName();
    private PopupVoiceWakeFreeEleBinding mPopViewBinding;
    private Context mContext;

    public VoiceWakeFreeEleWindow(Context context){
        super(context);
       LogUtil.debugD(TAG,"");
        mContext = context;
        initView();
        initEvent();
    };

    public void initView(){
       LogUtil.debugD(TAG,"");
        mPopViewBinding = PopupVoiceWakeFreeEleBinding.inflate(AAOP_HSkin.getLayoutInflater(mContext));
        this.setContentView(mPopViewBinding.getRoot());
        this.setWidth((int)(mContext.getResources().getDimension(R.dimen.popup_big_width)));
        this.setHeight((int)(mContext.getResources().getDimension(R.dimen.popup_big_height)));
        this.setFocusable(true);
        this.setClippingEnabled(false);
        this.setBackgroundDrawable(null);
    }

    public void initEvent(){
        mPopViewBinding.btnPopupVoiceWakefreeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LogUtil.debugD(TAG,"btnPopupVoiceWakefreeClose");
                new Handler().postDelayed(VoiceWakeFreeEleWindow.super::dismiss, mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
            }
        });
    }
}
