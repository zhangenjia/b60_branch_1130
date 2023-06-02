package com.adayo.app.crossinf.skin;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.crossinf.presenter.ISkinChangeListener;
import com.adayo.app.crossinf.presenter.SkinChangeImpl;
import com.adayo.app.crossinf.ui.customview.HorizontalArcView;
import com.adayo.app.crossinf.ui.customview.PitchArcView;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;

import static com.adayo.app.crossinf.util.Constant.CURRENTTHEME;

public class CrossInfoAttrHandler implements ISkinAttrHandler {
    private int color = 0xff7bb368;
    private int color_1 = 0xff7bb368;//越野信息 绿色
    private int color_2 = 0xfff18410;//长途穿越 黄色
    private static final String TAG = CrossInfoAttrHandler.class.getSimpleName();

    public CrossInfoAttrHandler(){

    }
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        String simpleSkinIdentifier = iResourceManager.getSimpleSkinIdentifier();
        if (simpleSkinIdentifier == null) {
            Log.d(TAG, "apply: =null");
            color = color_1;
            CURRENTTHEME = 1;
            SkinChangeImpl.getInstance().onSkinChange(1);
        }else if ("2".equals(simpleSkinIdentifier)){
            Log.d(TAG, "apply: =2");
            color = color_2;
            CURRENTTHEME = 2;
            SkinChangeImpl.getInstance().onSkinChange(2);
        }

        switch (skinAttr.mAttrName) {
            case "pichangle":
                Log.d(TAG, "apply: pichangle"+color);
                if(view instanceof HorizontalArcView) {
                    HorizontalArcView cv_horizontal = (HorizontalArcView) view;
                    cv_horizontal.setPaintColor(color);
                } else if (view instanceof PitchArcView) {
                    PitchArcView cv_pichangle = (PitchArcView) view;
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
