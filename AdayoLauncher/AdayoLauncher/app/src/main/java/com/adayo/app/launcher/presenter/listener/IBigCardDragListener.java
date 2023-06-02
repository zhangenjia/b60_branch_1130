package com.adayo.app.launcher.presenter.listener;

import android.view.View;

public interface IBigCardDragListener {

    void InitView(String id, int position);
    View AddView(String id, int position);
    void ChangeInfoBigCard(String id, int position);
    void addCallBackToBigCard(BigCardCallBack callBack);
    void removeBigCardCallBack();

    interface BigCardCallBack {
        void bigCardIdChange(String id,int position);
        void bigCardIdChangeToInitView(String id, int position);
        View bigCardIdChangeToAddView(String id, int position);
    }


 void ChangeInfoToBottomBigCard(String id, int position);

    void addCallBackToBottomBigCard(BottomBigCardCallBack callBack);

    void removeBottomBigCardCallBack();

    interface BottomBigCardCallBack {

        void bigCardIdChange(String id,int position);
    }
}
