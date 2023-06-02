package com.adayo.app.radio.attr;

import android.util.Log;
import android.view.View;

import com.adayo.app.radio.R;
import com.adayo.app.radio.ui.controller.RadioDataMng;
import com.adayo.app.radio.ui.fragment.RadioInfoFragment;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.app.radio.utils.Utils;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;

/**
 * @author ADAYO-06
 */
public class JsonAttrHandler implements ISkinAttrHandler {
    private static final String TAG = JsonAttrHandler.class.getName();
    private static final String SECOND_THEME = "2";

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
        Log.d(RadioAPPLog.TAG,TAG+  " apply: iResourceManager.getSimpleSkinIdentifier() = "+ iResourceManager.getSimpleSkinIdentifier());
        Log.d(RadioAPPLog.TAG,TAG+  " apply: lottie.getFrame() = "+ lottie.getFrame());
        Log.d(RadioAPPLog.TAG,TAG+  " apply: lottie.getMaxFrame() = "+ lottie.getMaxFrame());
        if (SECOND_THEME.equals(iResourceManager.getSimpleSkinIdentifier())) {
            if (view.getId() == R.id.lav_fm){
                lottie.setMinAndMaxFrame(0, 205);
                lottie.setAnimation(R.raw.anim_radio);
                Log.d(RadioAPPLog.TAG,TAG+  " apply: 2222222222");
            }else {
                lottie.setMinAndMaxFrame(0, 119);
                lottie.setAnimation(R.raw.anim_radio_am);
            }
        }else {
            if (view.getId() == R.id.lav_fm){
                lottie.setMinAndMaxFrame(0, 205);
                lottie.setAnimation(R.raw.anim_radio_2);
                Log.d(RadioAPPLog.TAG,TAG+  " apply: 11111111111");
            }else {
                lottie.setMinAndMaxFrame(0, 119);
                lottie.setAnimation(R.raw.anim_radio_am_2);
            }

        }
    }
}
