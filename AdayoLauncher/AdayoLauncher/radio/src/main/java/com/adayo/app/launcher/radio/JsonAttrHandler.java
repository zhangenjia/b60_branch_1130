package com.adayo.app.launcher.radio;

import android.util.Log;
import android.view.View;

import com.adayo.app.launcher.radio.constant.Utils;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;

public class JsonAttrHandler implements ISkinAttrHandler {
    private static final String TAG = JsonAttrHandler.class.getName();

    public JsonAttrHandler(){

    }

    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        if (null == view || null == skinAttr
                || !(Utils.ATTR_JSON_IMG_VIEW.equals(skinAttr.mAttrName))) {
            return;
        }

        if (!(view instanceof LottieAnimationView)) {
            return;
        }

        LottieAnimationView lottie = (LottieAnimationView) view;
        Log.d(TAG,  " apply: iResourceManager.getSimpleSkinIdentifier() = "+ iResourceManager.getSimpleSkinIdentifier());
        if ("2".equals(iResourceManager.getSimpleSkinIdentifier())) {
            if (view.getId() == R.id.lav_fm_car){
//                lottie.setAnimation(R.raw.anim_radio_2);
                lottie.setMinAndMaxFrame(0, 205);
                LottieComposition composition = LottieComposition.Factory.fromFileSync(view.getContext(),"anim_car_rado_fm_2.json");
                lottie.setComposition(composition);
                Log.d(TAG,  " apply: 2222222222");
            }else {
                //lottie.setAnimation(R.raw.anim_radio_am_2);
                lottie.setMinAndMaxFrame(0, 119);
                LottieComposition composition = LottieComposition.Factory.fromFileSync(view.getContext(),"anim_car_radio_am_2.json");
                lottie.setComposition(composition);
            }
        }else {
            if (view.getId() == R.id.lav_fm_car){
                //lottie.setAnimation(R.raw.anim_radio);
                lottie.setMinAndMaxFrame(0, 205);
                LottieComposition composition = LottieComposition.Factory.fromFileSync(view.getContext(),"anim_car_rado_fm.json");
                lottie.setComposition(composition);
                Log.d(TAG,  " apply: 11111111111");
            }else {
               // lottie.setAnimation(R.raw.anim_radio_am);
                lottie.setMinAndMaxFrame(0, 119);
                LottieComposition composition = LottieComposition.Factory.fromFileSync(view.getContext(),"anim_car_radio_am.json");
                lottie.setComposition(composition);
            }
        }
    }
}
