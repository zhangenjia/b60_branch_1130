package com.adayo.app.camera.skin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.adayo.app.camera.view.ParkingView;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;

/**
 * @author Yiwen.Huan
 * created at 2022/1/5 13:41
 */
public class ImageNAttrHandler implements ISkinAttrHandler {

    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        if (null == skinAttr) {
            return;
        }
        if (!SkinAttrs.IMAGE_N.equals(skinAttr.mAttrName)) {
            return;
        }
        if (view instanceof ParkingView) {
            BitmapDrawable drawable = (BitmapDrawable) SkinAttrUtils.getDrawable(iResourceManager, skinAttr.mAttrValueRefId, skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);
            if (null != drawable) {
                Bitmap bitmap = drawable.getBitmap();
                ((ParkingView) view).setImageN(bitmap);
            }
        }
    }
}
