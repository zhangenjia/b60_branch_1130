package com.adayo.app.setting.base;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.view.popwindow.harman.HarmanSurfaceView;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;

public class LoadDrawableAttrHandler implements ISkinAttrHandler {
    private static final String TAG = "LoadDrawableAttrHandler";
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        if(null == view
                || null == skinAttr
                || !("LoadDrawable".equals(skinAttr.mAttrName))) {
            return;
        }
        Drawable drawable = SkinAttrUtils.getDrawable(
                iResourceManager, skinAttr.mAttrValueRefId,
                skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);


        if(null != drawable) {
            if (drawable instanceof BitmapDrawable) {
                if (view instanceof HarmanSurfaceView) {
           }
            }else {
                LogUtil.w(TAG, "apply: failed because drawable is "+drawable.getClass().getName());
            }
        }
    }
}



