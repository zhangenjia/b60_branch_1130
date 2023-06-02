package com.example.externalcardview;

import android.util.Log;
import android.view.View;

import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;

public class NoshortCardViewHandler implements ISkinAttrHandler {
    private static String TAG =  NoshortCardViewHandler.class.getSimpleName();
    private final NoShortcutCardView view;


    public NoshortCardViewHandler(NoShortcutCardView view){
        this.view = view;
    }
    @Override
    public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
        Log.d(TAG, "apply: ");
        String simpleSkinIdentifier = iResourceManager.getSimpleSkinIdentifier();
        if (simpleSkinIdentifier == null) {
            Log.d(TAG, "apply: =null");
            this.view.setTheme(1);
        }else if (simpleSkinIdentifier.equals("2")){
            Log.d(TAG, "apply: =2");
            this.view.setTheme(2);
        }else {
            this.view.setTheme(1);
        }

    }
}
