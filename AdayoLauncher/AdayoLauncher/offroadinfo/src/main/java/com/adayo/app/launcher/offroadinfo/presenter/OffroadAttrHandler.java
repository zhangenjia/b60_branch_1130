package com.adayo.app.launcher.offroadinfo.presenter;

import static com.adayo.app.launcher.offroadinfo.util.Constant.CURRENTTHEME;

import android.util.Log;
import android.view.View;
import com.adayo.app.launcher.offroadinfo.ui.view.HorizontalCustomView;
import com.adayo.app.launcher.offroadinfo.ui.view.PitchCustomView;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;

public class OffroadAttrHandler implements ISkinAttrHandler  {
    private static String TAG =  OffroadAttrHandler.class.getSimpleName();
    private int color = 0xff7bb368;
    private int color_1 = 0xff7bb368;//越野信息 绿色
    private int color_2 = 0xfff18410;//长途穿越 黄色

    public OffroadAttrHandler(){
        Log.d(TAG, "OffroadAttrHandler: ");
    }
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        Log.d(TAG, "apply: ");
        String simpleSkinIdentifier = iResourceManager.getSimpleSkinIdentifier();
        if (simpleSkinIdentifier == null) {
            Log.d(TAG, "apply: =null");
            color = color_1;
            CURRENTTHEME = 1;
            SkinChangeImpl.getInstance().onSkinChange(1);
        }else if (simpleSkinIdentifier.equals("2")){
            Log.d(TAG, "apply: =2");
            color = color_2;
            CURRENTTHEME = 2;
            SkinChangeImpl.getInstance().onSkinChange(2);
        }
        switch (skinAttr.mAttrName) {
            case "offrodskin":
                Log.d(TAG, "apply: pichangle"+color);
                if(view instanceof HorizontalCustomView) {
                    HorizontalCustomView cv_horizontal = (HorizontalCustomView) view;
                    cv_horizontal.setPaintColor(color);
                } else if (view instanceof PitchCustomView) {
                    PitchCustomView cv_pichangle = (PitchCustomView) view;
                    cv_pichangle.setPaintColor(color);
                }
                break;
            default:
                Log.d(TAG, "apply: skinHandler");
                break;
        }

    }
}
