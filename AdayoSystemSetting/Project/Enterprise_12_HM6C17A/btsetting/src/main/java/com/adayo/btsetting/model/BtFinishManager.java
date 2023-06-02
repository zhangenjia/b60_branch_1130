package com.adayo.btsetting.model;


import android.util.Log;

/**
 * @author Y4134
 */
public class BtFinishManager {

    private static final String TAG = BtFinishManager.class.getSimpleName();
    private static volatile BtFinishManager mModel;
    private volatile boolean isFinish = false;
    private volatile String sourceType;

    private BtFinishManager() {
        // TODO: 2020/2/29 注册carservice回调

    }

    public static BtFinishManager getInstance() {
        if (mModel == null) {
            synchronized (BtFinishManager.class) {
                if (mModel == null) {
                    mModel = new BtFinishManager();
                }
            }
        }
        return mModel;
    }

    public boolean isFinish() {
        Log.d(TAG, "isFinish: " + isFinish);
        return isFinish;
    }

    public void setFinish(boolean finish) {
        Log.d(TAG, "setFinish: " + finish);
        isFinish = finish;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
