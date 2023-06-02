package com.adayo.app.launcher.presenter.listener;

import android.view.View;

public class SmallCardDragImpl implements ISmallCardDragListener {

    private static SmallCardDragImpl mListener;
    private SmallCardCallBack mSmallCardCallBack;
    private BottomSmallCardCallBack mBottomSmallCardCallBack;

    public static SmallCardDragImpl getInstance() {
        if (null == mListener) {
            synchronized (SmallCardDragImpl.class) {
                if (null == mListener) {
                    mListener = new SmallCardDragImpl();
                }
            }
        }
        return mListener;
    }

    @Override
    public void ChangeIdToTopToInitView(String id, int position) {
        mSmallCardCallBack.smallCardIdChangeToInitView(id,position);
    }

    @Override
    public View ChangeIdToTopToAddView(String id, int position) {
        View view = mSmallCardCallBack.smallCardIdChangeToAddView(id, position);
        return view;
    }

    @Override
    public void hideFrameAndMask() {
        mSmallCardCallBack.hideFrameAndMask();
    }

    @Override
    public void showFrameAndMask() {
        mSmallCardCallBack.showFrameAndMask();
    }



    @Override
    public void addCallBackToSmallCard(SmallCardCallBack smallCardCallBack) {
        mSmallCardCallBack = smallCardCallBack;
    }

    @Override
    public void removeSmallCardCallBack() {

    }



    @Override
    public void ChangeIdFromCardToBottomSmallCard(String id, int position) {
        mBottomSmallCardCallBack.smallCardIdChange(id,position);
    }

    @Override
    public void addCallBackToBottomSmallCard(BottomSmallCardCallBack bottomSmallCardCallBack) {
        mBottomSmallCardCallBack = bottomSmallCardCallBack;
    }

    @Override
    public void removeBottomSmallCardCallBack() {

    }
}
