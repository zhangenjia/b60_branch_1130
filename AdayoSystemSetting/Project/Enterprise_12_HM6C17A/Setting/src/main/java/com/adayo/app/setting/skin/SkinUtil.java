package com.adayo.app.setting.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;

import android.view.View;
import android.widget.ImageView;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.view.popwindow.harman.HarmanSurfaceView;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class SkinUtil {
    private static boolean isSkin = true;

    private SkinUtil() {

    }



    public static void setImageResource(ImageView imageView, int id) {
        if (isSkin) {
            AAOP_HSkin.with(imageView)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, id)
                    .applySkin(false);
        } else {
            imageView.setImageResource(id);
        }
    }

    public static void setBackground(View view, int drawable) {
        if (isSkin) {
            AAOP_HSkin.with(view)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, drawable)
                    .applySkin(false);
        } else {
            view.setBackgroundResource(drawable);
        }
    }

    public static void setHarmanSkinAttr(String skinAttr, View view, int drawable, Context context) {
        if (isSkin) {
            AAOP_HSkin.with(view).addViewAttrs(skinAttr, drawable).applySkin(false);
        } else {
            ((HarmanSurfaceView) view).setBitmap(BitmapFactory.decodeResource(context.getResources(), drawable));
        }
    }
    public static void setSegmentTabLayoutSkinAttrThumbDrawable(String skinAttr, View view, int drawable, Context context) {
        if (isSkin) {
            AAOP_HSkin.with(view).addViewAttrs(skinAttr, drawable).applySkin(false);
        } else {
            ((com.flyco.tablayout.SegmentTabLayout) view).setThumbDrawable(context.getResources().getDrawable(drawable));

        }
    }
    public static void setSegmentTabLayoutSkinAttrTextSelectColor(String skinAttr, View view, int color, Context context) {
        if (isSkin) {
            AAOP_HSkin.with(view).addViewAttrs(skinAttr, color).applySkin(false);
        } else {
            ((com.flyco.tablayout.SegmentTabLayout) view).setTextSelectColor(context.getResources().getColor(color));
        }
    }

}
