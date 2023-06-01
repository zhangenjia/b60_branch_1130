package com.adayo.app.btphone.linkmanstate;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adayo.app.btphone.ui.fragment.LinkManFragment;
import com.adayo.app.btphone.view.SlideBar;

import java.util.List;

public class LinkManHFPAndA2DPConnectState implements ILinkManState{

    private static final String TAG = LinkManHFPAndA2DPConnectState.class.getSimpleName();

    private RelativeLayout mSearchRL;
    private RelativeLayout mSyncIngRL;
    private RelativeLayout mRecyclerViewRL;
    private SlideBar mSlideBar;
    private LinkManFragment mLinkManFragment;

    public LinkManHFPAndA2DPConnectState(){}

    public LinkManHFPAndA2DPConnectState(List<View> viewList,LinkManFragment fragment){
        mLinkManFragment = fragment;
        mSearchRL = (RelativeLayout) viewList.get(0);
        mSyncIngRL = (RelativeLayout) viewList.get(1);
        mRecyclerViewRL = (RelativeLayout) viewList.get(2);
        mSlideBar = (SlideBar) viewList.get(3);
    }

    @Override
    public void updateDisplayStatus() {
        Log.i(TAG,"updateDisplayStatus");
        mSearchRL.setVisibility(View.VISIBLE);
        if(mLinkManFragment.mNoLinkManInfo){
            mRecyclerViewRL.setVisibility(View.GONE);
        }else{
            mRecyclerViewRL.setVisibility(View.VISIBLE);
        }
        mSlideBar.setVisibility(View.VISIBLE);
    }
}
