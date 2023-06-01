package com.adayo.app.btphone.linkmanstate;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adayo.app.btphone.ui.fragment.LinkManFragment;
import com.adayo.app.btphone.view.SlideBar;

import java.util.List;

public class LinkManSyncSuccessState implements ILinkManState{

    private static final String TAG = LinkManSyncSuccessState.class.getSimpleName();

    private RelativeLayout mSearchRL;
    private RelativeLayout mSyncIngRL;
    private RelativeLayout mRecyclerViewRL;
    private SlideBar mSlideBar;
    private LinkManFragment mLinkManFragment;
    private LinearLayout mSyncFailedLL;

    public LinkManSyncSuccessState(){}

    public LinkManSyncSuccessState(List<View> viewList,LinkManFragment linkManFragment){
        mLinkManFragment = linkManFragment;
        mSearchRL = (RelativeLayout) viewList.get(0);
        mSyncIngRL = (RelativeLayout) viewList.get(1);
        mRecyclerViewRL = (RelativeLayout) viewList.get(2);
        mSlideBar = (SlideBar) viewList.get(3);
        mSyncFailedLL = (LinearLayout) viewList.get(4);
    }

    @Override
    public void updateDisplayStatus() {
        Log.i(TAG,"updateDisplayStatus()");
        if(mLinkManFragment.mNoLinkManInfo){
            mRecyclerViewRL.setVisibility(View.GONE);
            return;
        }
        mSearchRL.setVisibility(View.VISIBLE);
        mSyncIngRL.setVisibility(View.GONE);
        mSyncFailedLL.setVisibility(View.GONE);
        mRecyclerViewRL.setVisibility(View.VISIBLE);
        mSlideBar.setVisibility(View.VISIBLE);
    }
}
