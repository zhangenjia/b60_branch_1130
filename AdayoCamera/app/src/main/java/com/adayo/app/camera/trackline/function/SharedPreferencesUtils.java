package com.adayo.app.camera.trackline.function;

import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.X;
import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.Y;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adayo.app.camera.trackline.view.PointView;


public class SharedPreferencesUtils {
    private static final String TAG = "SharedPreferencesUtils";
    private static volatile SharedPreferencesUtils mSharedPreferencesUtils;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private SharedPreferencesUtils(Context context) {
        mContext = context;
        //mSharedPreferences = mContext.getSharedPreferences(POINT, Context.MODE_PRIVATE);
        mSharedPreferences = mContext.createDeviceProtectedStorageContext().getSharedPreferences(POINT, Context.MODE_PRIVATE);
    }

    private static final String POINT = "point";
    private static final String CALIBRATION = "calibration";

    public static SharedPreferencesUtils getInstance(Context context) {
        if (mSharedPreferencesUtils == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (mSharedPreferencesUtils == null) {
                    mSharedPreferencesUtils = new SharedPreferencesUtils(context);
                }
            }
        }
        return mSharedPreferencesUtils;
    }


    /**
     * 保存收音机是否静音的标志位
     * add by jcpan
     */
    public boolean savePoint(PointView pointView) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String keyX = getKey(pointView.getPoint().name(), X);
        String keyY = getKey(pointView.getPoint().name(), Y);

        editor.putInt(keyX, (int) pointView.getCurrentX());
        editor.putInt(keyY, (int) pointView.getCurrentY());
        boolean ret = editor.commit();
        Log.d("AdayoCamera", TAG + " - savePoint: " + pointView.getPoint().name() + "_pointX" + mSharedPreferences.getInt(keyX, 0));
        Log.d("AdayoCamera", TAG + " - savePoint: " + pointView.getPoint().name() + "_pointY" + mSharedPreferences.getInt(keyY, 0));
        return ret;
    }

    public PointView getPoint(PointView pointView) {
        Log.d("AdayoCamera", TAG + " - getPoint: point =" + pointView.getPoint());
        String keyX = getKey(pointView.getPoint().name(), X);
        String keyY = getKey(pointView.getPoint().name(), Y);
        pointView.setCurrentX((float) mSharedPreferences.getInt(keyX, 0));
        Log.d("AdayoCamera", TAG + " - getPoint: setCurrentx = " + (float) mSharedPreferences.getInt(keyX, 0));
        pointView.setCurrentY((float) mSharedPreferences.getInt(keyY, 0));
        Log.d("AdayoCamera", TAG + " - getPoint: setCurrenty = " + (float) mSharedPreferences.getInt(keyY, 0));
        return pointView;
    }

    private String getKey(String name, String position) {
        String key;
        key = POINT + "_" + name + "_" + position;
        return key;
    }


    public boolean isCalibration() {
        int frist = mContext.createDeviceProtectedStorageContext().getSharedPreferences(POINT, Context.MODE_PRIVATE).getInt(CALIBRATION, 0);

        /*int frist = mContext.getSharedPreferences(POINT,
                Context.MODE_PRIVATE).getInt(CALIBRATION, 0);*/
        if (frist == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean calibrationFinish() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(POINT, Context.MODE_PRIVATE).edit();
        editor.putInt(CALIBRATION, 1);
        return editor.commit();
    }


    /**
     * 清空SharedPreferences里面point.xml的数据
     */
    public void clearSP() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(POINT, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

}
