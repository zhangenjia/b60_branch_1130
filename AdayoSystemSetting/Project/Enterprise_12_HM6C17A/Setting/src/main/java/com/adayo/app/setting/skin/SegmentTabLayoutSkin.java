package com.adayo.app.setting.skin;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;

public class SegmentTabLayoutSkin implements ISkinAttrHandler {
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        LogUtil.d("SegmentTabLayoutSkin", "apply");
        if (null == view
                || null == skinAttr
        ) {
            LogUtil.d("SegmentTabLayoutSkin", "RETUREN skinAttr.mAttrName=" + skinAttr.mAttrName);
            return;

        }
        if ("thumb_drawable".equals(skinAttr.mAttrName)) {
            Drawable drawable = SkinAttrUtils.getDrawable(
                    iResourceManager, skinAttr.mAttrValueRefId,
                    skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);
            if (null != drawable) {
                if (view instanceof com.flyco.tablayout.SegmentTabLayout) {
                    ((com.flyco.tablayout.SegmentTabLayout) view).setThumbDrawable(drawable);
                }
            }
        }
        if ("textSelectColor".equals(skinAttr.mAttrName)) {
            int color = iResourceManager.getColor(
                    skinAttr.mAttrValueRefId);
            LogUtil.d("SegmentTabLayoutSkin", " color =" + color);
            if (view instanceof com.flyco.tablayout.SegmentTabLayout) {
                ((com.flyco.tablayout.SegmentTabLayout) view).setTextSelectColor(color);
            }
        }
    }
}
