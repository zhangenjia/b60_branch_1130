package com.adayo.app.setting.base;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;

public class kswBackDrawable implements ISkinAttrHandler {
    private final static String TAG = kswBackDrawable.class.getSimpleName();
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        LogUtil.debugD(TAG,"apply");
        if(null == view
                || null == skinAttr
                || !("kswBackDrawable".equals(skinAttr.mAttrName))) {
            return;
        }
        Drawable drawable = SkinAttrUtils.getDrawable(
                iResourceManager, skinAttr.mAttrValueRefId,
                skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);

        if(null != drawable) {
            if (view instanceof SwitchButton) {
                ((SwitchButton)view).setBackDrawable(drawable);
            }
            if (view instanceof com.kyleduo.switchbutton.SwitchButton) {
                ((com.kyleduo.switchbutton.SwitchButton)view).setBackDrawable(drawable);
            }
        }
    }
}



