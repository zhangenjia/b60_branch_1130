package com.adayo.app.setting.base;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;

import java.lang.reflect.Field;

public class EditTextCursorDrawable implements ISkinAttrHandler {
    private static final String TAG = "EditTextCursorDrawable";

    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        LogUtil.debugD(TAG, "apply");
        if (null == view
                || null == skinAttr
                || !("EditTextCursorDrawable".equals(skinAttr.mAttrName))) {
            return;
        }
        Drawable drawable = SkinAttrUtils.getDrawable(
                iResourceManager, skinAttr.mAttrValueRefId,
                skinAttr.mAttrValueTypeName, skinAttr.mAttrValueRefName);

        if (null != drawable) {
            if (view instanceof EditText) {
                try {
                    Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");fCursorDrawableRes.setAccessible(true);Field fEditor = TextView.class.getDeclaredField("mEditor");
                    fEditor.setAccessible(true);
                    Object editor = fEditor.get(view);
                    Class<?> clazz = editor.getClass();
                    Field fCursorDrawable = clazz.getDeclaredField("mDrawableForCursor");
                    fCursorDrawable.setAccessible(true);
                    fCursorDrawable.set(editor, drawable);
                } catch (Throwable ignored) {
                    LogUtil.w(TAG, "fan she shi bai"+ignored.toString());
                }
            } else {
                LogUtil.w(TAG, "apply: failed because drawable is " + drawable.getClass().getName());
            }
        }
    }
}



