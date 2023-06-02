package com.adayo.app.launcher.offroadinfo.presenter;

public class SkinChangeImpl implements ISkinChangeListener {

    private static SkinChangeImpl mTouchImpl;

    public static SkinChangeImpl getInstance() {
        if (null == mTouchImpl) {
            synchronized (SkinChangeImpl.class) {
                if (null == mTouchImpl) {
                    mTouchImpl = new SkinChangeImpl();
                }
            }
        }
        return mTouchImpl;
    }

    private CallBack mCallBack;

    @Override
    public void onSkinChange(int skin) {
        if (mCallBack != null) {
             mCallBack.skinChangeCallBack(skin);
        }
    }

    @Override
    public void addCallBack(CallBack callBack) {

        mCallBack = callBack;
    }

}
