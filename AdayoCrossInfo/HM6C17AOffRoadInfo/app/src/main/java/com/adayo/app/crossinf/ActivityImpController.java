package com.adayo.app.crossinf;

import static com.adayo.app.crossinf.util.Constant.CROSSINF;
import static com.adayo.app.crossinf.util.Constant.FINISH_WADING_FUNCTION;
import static com.adayo.app.crossinf.util.Constant.START_WADING_FUNCTION;
import static com.adayo.app.crossinf.util.Constant.WADINGDETECTION;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI;

import android.util.Log;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityImpController {

    private static final String TAG = "ActivityImpController";
    private static ActivityImpController mActivityImpController;
    private List<IAppControlListener> list = new ArrayList<>();

    public static ActivityImpController getInstance() {
        if (null == mActivityImpController) {
            synchronized (ActivityImpController.class) {
                if (null == mActivityImpController) {
                    mActivityImpController = new ActivityImpController();
                }
            }
        }
        return mActivityImpController;
    }

    private IAppControlListener listener;

    public void addCallBack(IAppControlListener listener) {
        list.add(listener);
    }

    public void updateActivityStatus(int function) {
        Log.d(TAG, "updateActivityStatus: " + function);
        switch (function) {
            case FINISH_WADING_FUNCTION:
                Log.d(TAG, "updateActivityStatus: finish_app_function  " + list.size());
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        IAppControlListener iAppControlListener = list.get(i);
                        if (iAppControlListener != null) {
                            iAppControlListener.finishWadding();
                        }
                    }

                }
                break;
            case START_WADING_FUNCTION:
                Log.d(TAG, "updateActivityStatus: start_wading_fragment  " + list.size());
                if (isAppRunning()) {
                    for (int i = 0; i < list.size(); i++) {
                        IAppControlListener iAppControlListener = list.get(i);
                        if (iAppControlListener != null) {
                            iAppControlListener.switchWadding();
                        }
                    }
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(CROSSINF, WADINGDETECTION);//参数 (表明要打开涉水检测Tab)
                    SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_CAR_ASSISTANT, map,
                            AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
                    SrcMngSwitchManager.getInstance().requestSwitchApp(info);

                }
                break;

        }

    }


    public boolean isAppRunning() {
//        @SuppressLint("ServiceCast")
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
//        if(list.size() <= 0) {
//            return false;
//        }
//
//        if( list.get(0).topActivity.getClassName().equals("需要比较的activity的包名") ) {
//            return true;
//        } else {
//            return false;
//        }
        boolean appVisibility = ActivityManager.getInstance().getAppVisibility();
        Log.d(TAG, "isAppRunning: "+appVisibility);
        return appVisibility;
    }

}
