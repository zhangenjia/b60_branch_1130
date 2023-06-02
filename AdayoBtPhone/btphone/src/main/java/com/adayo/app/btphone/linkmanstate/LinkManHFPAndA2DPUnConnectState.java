package com.adayo.app.btphone.linkmanstate;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adayo.app.btphone.view.SlideBar;

import java.util.List;

public class LinkManHFPAndA2DPUnConnectState implements ILinkManState{

    private static final String TAG = LinkManHFPAndA2DPUnConnectState.class.getSimpleName();

    private RelativeLayout mSearchRL;
    private RelativeLayout mSyncIngRL;
    private RelativeLayout mRecyclerViewRL;
    private SlideBar mSlideBar;

    public LinkManHFPAndA2DPUnConnectState(){}

    public LinkManHFPAndA2DPUnConnectState(List<View> viewList){
        mSearchRL = (RelativeLayout) viewList.get(0);
        mSyncIngRL = (RelativeLayout) viewList.get(1);
        mRecyclerViewRL = (RelativeLayout) viewList.get(2);
        mSlideBar = (SlideBar) viewList.get(3);
    }

    @Override
    public void updateDisplayStatus() {
        Log.i(TAG,"updateDisplayStatus()");
        mSearchRL.setVisibility(View.GONE);
        mSyncIngRL.setVisibility(View.GONE);
        mSlideBar.setVisibility(View.GONE);
        mRecyclerViewRL.setVisibility(View.GONE);
    }
}
