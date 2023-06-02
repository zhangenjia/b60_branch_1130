package com.adayo.app.launcher.communicationbase;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WrapperUtil {
    private static final String TAG = "WrapperUtil";
    private static WrapperUtil mWrapperUtil;
    private BgChangeCallBack bgChangeCallBack;
    private List<BgChangeCallBack> list = new ArrayList<>();

    public static WrapperUtil getInstance() {
        if (null == mWrapperUtil) {
            synchronized (WrapperUtil.class) {
                if (null == mWrapperUtil) {
                    mWrapperUtil = new WrapperUtil();
                }
            }
        }
        return mWrapperUtil;
    }

    public void registerBgChangeCallBack(BgChangeCallBack bgChangeCallBack) {
        Log.d(TAG, "registerBgChangeCallBack:  "+"   "+list.size());
        if (!list.contains(bgChangeCallBack)) {
            list.add(bgChangeCallBack);
        }
    }

    public interface BgChangeCallBack {
        void setWallPaper(Bitmap bitmap );
        void resumeDefault();
        void deleteWallPaper();//todo 删除是否要直接恢复
    }

    public void setWallPaper(Bitmap bitmap) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                list.get(i).setWallPaper(bitmap);
            }
        }
    }

    public void resumeDefault( ) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                list.get(i).resumeDefault();
            }
        }
    }


}
