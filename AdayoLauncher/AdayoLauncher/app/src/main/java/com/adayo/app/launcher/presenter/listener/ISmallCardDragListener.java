package com.adayo.app.launcher.presenter.listener;

import android.view.View;

public interface ISmallCardDragListener {

    void ChangeIdToTopToInitView(String id, int position);
    View ChangeIdToTopToAddView(String id, int position);
    void hideFrameAndMask();
    void showFrameAndMask();
    void addCallBackToSmallCard(SmallCardCallBack callBack);

    void removeSmallCardCallBack();

    interface SmallCardCallBack {
        void smallCardIdChangeToInitView(String id, int position);
        View smallCardIdChangeToAddView(String id, int position);
        void hideFrameAndMask();
        void showFrameAndMask();
    }


    void ChangeIdFromCardToBottomSmallCard(String id, int position);
    void addCallBackToBottomSmallCard(BottomSmallCardCallBack callBack);
    void removeBottomSmallCardCallBack();

    interface BottomSmallCardCallBack {
        void smallCardIdChange(String id, int position);
    }
}
