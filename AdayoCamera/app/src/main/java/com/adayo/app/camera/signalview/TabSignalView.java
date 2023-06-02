package com.adayo.app.camera.signalview;

import android.view.View;

import com.adayo.proxy.aaop_camera.controlcenter.ControlCenter;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/8/20 8:32
 */
public class TabSignalView extends BaseSignalView implements TabLayout.OnTabSelectedListener {

    @Override
    public void setView(View view, boolean clickable) {
        this.viewWeakReference = new WeakReference<>(view);
        if (view instanceof TabLayout) {
            ((TabLayout) view).addOnTabSelectedListener(this);
        }
    }


    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (view instanceof TabLayout && SignalViewState.INVALID != state.eventTriggeredSelectedTabAt) {
            TabLayout.Tab tab = ((TabLayout) view).getTabAt(state.eventTriggeredSelectedTabAt);
            if (null != tab) {
                tab.select();
            }
        }
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (view instanceof TabLayout && SignalViewState.INVALID != state.signalTriggeredSelectedTabAt) {
            TabLayout.Tab tab = ((TabLayout) view).getTabAt(state.signalTriggeredSelectedTabAt);
            if (null != tab) {
                tab.select();
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (eventStateMap.isEmpty()) {
            return;
        }
        int pos = tab.getPosition();
        for (SignalViewState state : eventStateMap.values()) {
            if (state.eventTriggeredSelectedTabAt == pos) {
                ControlCenter.getInstance().notifyEventComing(state.eventId);
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
