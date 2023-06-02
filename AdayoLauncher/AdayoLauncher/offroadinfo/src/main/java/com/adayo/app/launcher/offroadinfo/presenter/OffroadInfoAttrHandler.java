package com.adayo.app.launcher.offroadinfo.presenter;

import android.util.Log;
import android.view.View;
import com.adayo.app.launcher.offroadinfo.ui.view.HorizontalCustomView;
import com.adayo.app.launcher.offroadinfo.ui.view.PitchCustomView;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;

public class OffroadInfoAttrHandler  implements ISkinAttrHandler  {
    private static String TAG =  OffroadInfoAttrHandler.class.getSimpleName();
    private int color = 0xff7bb368;
    private int color_1 = 0xff7bb368;//越野信息 绿色
    private int color_2 = 0xfff18410;//长途穿越 黄色
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        String simpleSkinIdentifier = iResourceManager.getSimpleSkinIdentifier();
        if (simpleSkinIdentifier == null) {
            Log.d(TAG, "apply: =null");
            color = color_1;
        }else if (simpleSkinIdentifier.equals("2")){
            Log.d(TAG, "apply: =2");
            color = color_2;
        }
        switch (skinAttr.mAttrName) {
            case "pichangle":
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

//        if ("horizontal".equals(skinAttr.mAttrName)) {
////            view.getId()方式一
//            //方式二
//            LottieAttr attr = (LottieAttr) skinAttr.mDynamicAttr;
//            String json = attr.json;
//            if (!iResourceManager.isDefault()) {
//                json = json.replace(".json", iResourceManager.getSimpleSkinIdentifier() + ".json");
//            }
//            ((LottieAnimationView) view).setAnimation(json);
//        }

    }
}
