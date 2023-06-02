package com.adayo.app.crossinf.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.app.crossinf.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class CustomTabLayout extends LinearLayout {

    private View mView;

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initView(int isConfigWading) {
        if (this.mView != null) {
            removeView(this.mView);
            this.mView = null;
        }
        switch (isConfigWading){
            case 0:
                mView = LayoutInflater.from(getContext()).inflate(R.layout.ll_tab1, null);
                TextView tv_vehicleInfo1 = (TextView) mView.findViewById(R.id.vehicleInfo);
                TextView tv_environInfo1 = (TextView) mView.findViewById(R.id.environInfo);
                AAOP_HSkin
                        .with(tv_vehicleInfo1)
                        .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                        .applySkin(false);
                AAOP_HSkin
                        .with(tv_environInfo1)
                        .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                        .applySkin(false);
                break;
            case 1:
                mView = LayoutInflater.from(getContext()).inflate(R.layout.ll_tab2, null);
                TextView tv_vehicleInfo2 = (TextView) mView.findViewById(R.id.vehicleInfo);
                TextView tv_environInfo2 = (TextView) mView.findViewById(R.id.environInfo);
                TextView tv_wadingDetection2 = (TextView) mView.findViewById(R.id.wadingDetection);
                AAOP_HSkin
                        .with(tv_vehicleInfo2)
                        .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                        .applySkin(false);
                AAOP_HSkin
                        .with(tv_environInfo2)
                        .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                        .applySkin(false);
                AAOP_HSkin
                        .with(tv_wadingDetection2)
                        .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                        .applySkin(false);
                break;
        }

        if (this.mView != null) {
            addView(this.mView);
            this.mView = null;
        }

    }
}
