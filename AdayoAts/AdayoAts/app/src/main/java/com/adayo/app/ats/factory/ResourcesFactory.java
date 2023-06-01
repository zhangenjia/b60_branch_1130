package com.adayo.app.ats.factory;

import android.content.Context;

import com.adayo.app.ats.function.ConfigFunction;

import java.util.Map;
//
//   * Demo class
//     *
//             * @author jcshao
//        * @date 2016/10/31
//        */



public class ResourcesFactory {

    private static ResourcesFactory mResourcesFactory;
    private final Context context;
    private  boolean isConfigWading;

    

    public static ResourcesFactory getInstance(Context context) {
        if (null == mResourcesFactory) {
            synchronized (ResourcesFactory.class) {
                if (null == mResourcesFactory) {
                    mResourcesFactory = new ResourcesFactory(context);
                }
            }
        }
        return mResourcesFactory;
    }

    private ResourcesFactory(Context context) {
        this.context = context;
        ConfigFunction mConfigFunction = ConfigFunction.getInstance(context);
        isConfigWading = mConfigFunction.isConfigWadingInductionSys();
    }

    public boolean isConfigWading() {
        return isConfigWading;
    }

    public int getValueUnit(){
        if (isConfigWading) {
            return HighConfig.mValueUnit_High;
        } else {
            return LowConfig.mValueUnit_Low;
        }
    }

    public int getAngleValue() {
        if (isConfigWading) {
            return HighConfig.mStartAngle_High;
        } else {
            return LowConfig.mStartAngle_Low;
        }
    }

    public int[] getNormalImages() {
        if (isConfigWading) {
            return HighConfig.mItemNormalImages_High;
        } else {
            return LowConfig.mItemNormalImages_Low;
        }
    }

    public int[] getDisImages() {

        if (isConfigWading) {
            return HighConfig.mItemDisImages_High;
        } else {
            return LowConfig.mItemDisImages_Low;
        }
    }

    public int[] getSelImages() {
        if (isConfigWading) {
            return HighConfig.mItemSelImages_High;
        } else {
            return LowConfig.mItemSelImages_Low;
        }
    }

    public int[] getGlobalTexts() {
        if (isConfigWading) {
            return HighConfig.mItemGlobalTexts_High;
        } else {
            return LowConfig.mItemGlobalTexts_Low;
        }
    }

    public int[] getBgImages() {
        if (isConfigWading) {
            return HighConfig.mbgImages_High;
        } else {
            return LowConfig.mbgImages_Low;
        }
    }

    public int[] getCenterTexts() {

        if (isConfigWading) {
            return HighConfig.mItemCenterTexts_High;
        } else {
            return LowConfig.mItemCenterTexts_Low;
        }
    }

    public int[] getTipsText() {
        if (isConfigWading) {
            return HighConfig.mTipsText_High;
        } else {
            return LowConfig.mTipsText_Low;
        }
    }

    public Map<Integer, Integer> getRequestModMap() {
        if (isConfigWading) {
            return HighConfig.ATSREQUESTMODMAP_HIGH;
        } else {
            return LowConfig.ATSREQUESTMODMAP_LOW;
        }
    }

    public Map<Integer, Integer> getConfirmModMap() {
        if (isConfigWading) {
            return HighConfig.ATSCONFIRMMODMAP_HIGH;
        } else {
            return LowConfig.ATSCONFIRMMODMAP_LOW;
        }
    }

    public Map<Integer, Float> getRotationOffset() {
        if (isConfigWading) {
            return HighConfig.ROTATIONOFFSET_HIGH;
        } else {
            return LowConfig.ROTATIONOFFSET_LOW;
        }
    }
}
