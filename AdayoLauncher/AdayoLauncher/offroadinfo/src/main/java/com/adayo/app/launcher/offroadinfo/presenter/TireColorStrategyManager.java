package com.adayo.app.launcher.offroadinfo.presenter;

import static android.car.VehicleAreaWheel.WHEEL_LEFT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_REAR;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_REAR;
import android.util.Log;
import androidx.annotation.IntDef;

import com.adayo.app.launcher.offroadinfo.model.bean.TireLevelBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author ADAYO-21
 */
public final class TireColorStrategyManager {

    private static final String TAG = "TireColorStrategyManager";

    private static volatile TireColorStrategyManager mInstance;
    private volatile List<TireLevelBean> mList = new ArrayList<>();

    private final Object mLock = new Object();


    public static final int TYPE_TIRE_SLIP = 0;
    public static final int TYPE_TIRE_TEMP = 1;
    public static final int TYPE_TIRE_PRESSURE = 2;


    public static final int LEVEL_HIGH = 0;
    public static final int LEVEL_MIDDLE = 1;
    public static final int LEVEL_LOW = 2;

    public IColorChangeListener mListener;

    public interface IColorChangeListener {
        void changeColor(@TireLevel int level, @TirePosition int position);
    }

    public void registerColorChangeListener(IColorChangeListener listener) {
        this.mListener = listener;
    }

    public void unRegisterColorChangeListener() {
        this.mListener = null;
    }

    @IntDef({TYPE_TIRE_SLIP, TYPE_TIRE_TEMP, TYPE_TIRE_PRESSURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TireColorChangeType {
    }


    @IntDef({WHEEL_LEFT_FRONT, WHEEL_LEFT_REAR, WHEEL_RIGHT_FRONT, WHEEL_RIGHT_REAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TirePosition {
    }

    @IntDef({LEVEL_HIGH, LEVEL_MIDDLE, LEVEL_LOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TireLevel {
    }

    private TireColorStrategyManager() {

    }

    public static TireColorStrategyManager getInstance() {
        if (mInstance == null) {
            synchronized (TireColorStrategyManager.class) {
                if (mInstance == null) {
                    mInstance = new TireColorStrategyManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置轮胎特殊的颜色
     *
     * @param type
     */
    public void setTireSpecialColor(@TireColorChangeType int type, @TirePosition int position) {
        synchronized (mLock) {
            Log.d(TAG, "setTireSpecialColor: type = " + type + " position = " + position);
            int result = 0;
            int level;

            if (type == TYPE_TIRE_SLIP) {//轮胎打滑
                level = LEVEL_HIGH;
            } else if (type == TYPE_TIRE_TEMP || type == TYPE_TIRE_PRESSURE) {//胎压 温度异常登
                level = LEVEL_MIDDLE;
            } else {
                level = LEVEL_MIDDLE;
            }

            TireLevelBean tireLevelBean = new TireLevelBean();
            tireLevelBean.setLevel(level);
            tireLevelBean.setType(type);
            tireLevelBean.setPosition(position);

            boolean isNeedSetColor = true;

            for (TireLevelBean levelBean : mList) {
                //如果栈中已有的等级比当前的高 数越小等级越高 等级相当需要设置比如车轮打滑设计的颜色多个
                if (levelBean.getLevel() < level && levelBean.getPosition() == position) {
                    Log.d(TAG, "setTireSpecialColor: levelBean.getLevel() " + levelBean.getLevel());
                    isNeedSetColor = false;
                    break;
                }
            }

            if (isNeedSetColor && mListener != null) {
                Log.d(TAG, "releaseSpeech: isNeedSetColor");
                Log.d(TAG, "setTireNormalColor: 1168103 cccccccc");
                mListener.changeColor(tireLevelBean.getLevel(), tireLevelBean.getPosition());
            }

            boolean isContain = false;

            for (TireLevelBean levelBean : mList) {
                if (levelBean.getPosition() == position
                        && levelBean.getLevel() == level
                        && levelBean.getType() == type) {
                    isContain = true;
                    break;
                }
            }

            if (!isContain) {
                mList.add(tireLevelBean);
                Collections.sort(mList);
            }
            Log.d(TAG, "setTireSpecialColor: size " + mList.size());
        }
    }

    /**
     * 设置轮胎正常的颜色
     *
     * @return
     */
    public void setTireNormalColor(@TireColorChangeType int type, @TirePosition int position) {
        Log.d(TAG, "test===>: setTireNormalColor");
        synchronized (mLock) {
            Log.d(TAG, "setTireNormalColor: type = "+type+" position = "+position);

            for (int i = mList.size() - 1; i >= 0; i--) {
                TireLevelBean levelBean = mList.get(i);
                if (levelBean.getType() == type && levelBean.getPosition() == position) {
                    mList.remove(levelBean);
                }
            }

            boolean curPosHasColor = false;
            for (TireLevelBean levelBean : mList) {
                //当前轮胎还有颜色
                if (levelBean.getPosition() == position) {
                    curPosHasColor = true;
                    break;
                }
            }
            if (!curPosHasColor && mListener != null) {
                mListener.changeColor(LEVEL_LOW, position);
            }else {
                for (TireLevelBean levelBean : mList) {
                    if (levelBean.getPosition() ==position){
                        Log.d(TAG, "test===>: setTireNormalColor "+levelBean.getType()+"  "+levelBean.getPosition());
                        setTireSpecialColor(levelBean.getType(),levelBean.getPosition());
                    }
                }
            }
        }
    }


}
