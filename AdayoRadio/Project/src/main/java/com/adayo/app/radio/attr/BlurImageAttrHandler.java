package com.adayo.app.radio.attr;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.adayo.app.radio.R;
import com.adayo.app.radio.utils.BlurBitmapCache;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.app.radio.utils.Utils;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;
import com.adayo.proxy.aaop_hskin.utils.SkinUtils;
import com.airbnb.lottie.LottieAnimationView;

/**
 * @author jwqi
 */
public class BlurImageAttrHandler implements ISkinAttrHandler {
    private static final String TAG = BlurImageAttrHandler.class.getName();

    public BlurImageAttrHandler(){

    }

    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        if (null == view || null == skinAttr
                || !(Utils.ATTR_BLUR_IMAGE.equals(skinAttr.mAttrName))) {
            return;
        }


        Drawable drawable = SkinAttrUtils.getDrawable(iResourceManager, skinAttr.mAttrValueRefId, skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);
        if (drawable instanceof BitmapDrawable) {
            BlurBitmapCache.getInstance().setBitmap(view.getContext().getApplicationContext(),((BitmapDrawable)drawable).getBitmap(),16, 0.5f, 20, false);
        }
    }
}
