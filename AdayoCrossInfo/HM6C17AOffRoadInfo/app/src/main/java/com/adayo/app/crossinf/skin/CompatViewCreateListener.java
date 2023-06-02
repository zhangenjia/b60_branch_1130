package com.adayo.app.crossinf.skin;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;

import java.lang.ref.WeakReference;

public class CompatViewCreateListener implements IViewCreateListener {

    WeakReference<AppCompatActivity> activityWeakReference;

    public CompatViewCreateListener(AppCompatActivity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }


    @Override
    public View beforeCreate(View view, String s, Context context, AttributeSet attributeSet) {
        AppCompatActivity activity = activityWeakReference.get();
        if (null == activity) {
            return null;
        }

        return activity.getDelegate().createView(view, s, context, attributeSet);
    }

    @Override
    public void afterCreated(View view, String s, Context context, AttributeSet attributeSet) {

    }
}


