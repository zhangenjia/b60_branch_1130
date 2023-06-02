package com.adayo.app.launcher.presenter.function;

import android.util.Log;

import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.app.launcher.util.SystemPropertiesUtil;

public class SysPropFunction {

    private static final String TAG = "SysPropFunction";
    private static SysPropFunction mSysPropFunction;

    public static SysPropFunction getInstance() {
        if (null == mSysPropFunction) {
            synchronized (SysPropFunction.class) {
                if (null == mSysPropFunction) {
                    mSysPropFunction = new SysPropFunction();
                }
            }
        }
        return mSysPropFunction;
    }


    /**
     * 从系统属性获得 大卡映射
     *
     * @param
     */
    public String getTopBigCardIdFromProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_BIGCARD_KEY, "");
        return string;
    }

    /**
     * 设置大卡映射的系统属性
     *
     * @param
     */
    public void setTopBigCardIdToProperties(String cardid) {
        Log.d(TAG, "setTopBigCardIdToProperties: "+cardid);
        SystemPropertiesUtil.getInstance().setProperty(MyConstantsUtil.LAUNCHER_BIGCARD_KEY, cardid);
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_BIGCARD_KEY, "");
        Log.d(TAG, "setTopBigCardIdToProperties: "+string);
    }

    /**
     * 从系统属性获得 小卡映射
     *
     * @param
     */
    public String getSmallCardIdFromProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_SMALLCARD_KEY, "");
        return string;
    }

    /**
     * 设置小卡映射的系统属性
     *
     * @param
     */
    public void setSmallCardIdToProperties(String value) {
        SystemPropertiesUtil.getInstance().setProperty(MyConstantsUtil.LAUNCHER_SMALLCARD_KEY, value);
    }
/**
 * ================================================================================================>
 * CustomDialog
 */
    /**
     * 获取大卡映射
     * @param
     */
    public String getBottomBigCardIdFromSysProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_BOTTOM_BIGCARD_KEY, "");
        return string;
    }

    /**
     * 设置大卡映射
     * @param id
     */
    public void setBottomBigCardIdToSysProperties(String id) {
        Log.d(TAG, "setBottomBigCardIdToSysProperties: "+id);
        SystemPropertiesUtil.getInstance().setProperty(MyConstantsUtil.LAUNCHER_BOTTOM_BIGCARD_KEY, id);
    }

    /**
     * 获取小卡映射
     * @param
     */
    public String getSmallCardInFoFromSysProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_BOTTOM_SMALLCARD_KEY, "");
        return string;
    }

    /**
     * 设置小卡映射
     * @param id
     */
    public void setSmallCardInFoToSysProperties(String id) {
        SystemPropertiesUtil.getInstance().setProperty(MyConstantsUtil.LAUNCHER_BOTTOM_SMALLCARD_KEY, id);
    }
}
