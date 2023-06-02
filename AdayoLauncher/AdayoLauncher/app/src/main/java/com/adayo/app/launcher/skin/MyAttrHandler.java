package com.adayo.app.launcher.skin;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.navi.util.ConstantsUtil;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.airbnb.lottie.LottieAnimationView;
import com.example.externalcardview.LottieAttr;

public class MyAttrHandler implements ISkinAttrHandler {
    private static final String TAG = MyAttrHandler.class.getSimpleName();

    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {

        String simpleSkinIdentifier = iResourceManager.getSimpleSkinIdentifier();
        Log.d(TAG, "apply: " + simpleSkinIdentifier);
        if (simpleSkinIdentifier == null) {
            ConstantUtil.CURRENT_THEME = 1;
        }else if (simpleSkinIdentifier.equals("2")){
            ConstantUtil.CURRENT_THEME = 2;
        }
        if ("lottie".equals(skinAttr.mAttrName)) {
//            view.getId()方式一

//            //方式二
//            LottieAttr attr = (LottieAttr) skinAttr.mDynamicAttr;
//            String json = attr.json;
//            if (!iResourceManager.isDefault()) {
//                json = json.replace(".json", iResourceManager.getSimpleSkinIdentifier() + ".json");
//            }
//            ((LottieAnimationView) view).setAnimation(json);
        }

    }
}
