package com.adayo.app.launcher.presenter.factory;

import static com.adayo.app.launcher.util.MyConstantsUtil.HIGH_CONFIG_VEHICLE;
import static com.adayo.app.launcher.util.MyConstantsUtil.LOW_CONFIG_VEHICLE;

import android.util.Log;

import com.adayo.app.launcher.model.bean.AllAppBean;

import java.util.List;

/**
 * 目前根据系统属性判断车型一共两种 HN6C17A 高配
 * HN66C17低配 实际根据配置字判断
 * 配置字
 * 高
 * 中
 * 低
 * 目前只做了根据首次启动根据系统属性高低配返回不同卡片的逻辑，但卡片数量暂时返回相同，暂时返回高配(实际根据配置字判断)
 * 没有做刷新配置字后加载新车型卡片操作，重刷配置字
 * 目前会不生效，因为系统属性没清空，
 * 不会走判断配置字，读取卡片映射的代码。
 */

//todo  用的时候直接get成员变量
public class CardMappingFactory {

    private static final String TAG = "CardMappingFactory";
    private String mDefaultSmallCardResMapping = null;
    private String mBottomDefaultSmallCardResource = null;
    private String mBottomDefaultBigCardResource = null;
    private List<AllAppBean> mAllAppCardResource = null;
    private String mAllAppCardMapping = null;
    private static CardMappingFactory mCardMappingFactory;
    private String allBigCardMapping;

    /**
     * 首次调用在MainActivity中
     *
     * @return
     */
    public static CardMappingFactory getInstance() {
        if (null == mCardMappingFactory) {
            synchronized (CardMappingFactory.class) {
                if (null == mCardMappingFactory) {
                    mCardMappingFactory = new CardMappingFactory();
                }
            }
        }
        return mCardMappingFactory;
    }

    private CardMappingFactory() {

    }


    /**
     * 获取默认的4个小卡 //
     */
    public String getDefaultSmallCardResMapping(String getOffLineConfigInfo) {
        if (mDefaultSmallCardResMapping != null) {
            return mDefaultSmallCardResMapping;
        }
        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            mDefaultSmallCardResMapping = HeightConfigCarMapping.getInstance().getDefaultSmallCardMapping();
            return mDefaultSmallCardResMapping;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            mDefaultSmallCardResMapping = LowConfigCarMapping.getInstance().getDefaultSmallCardMapping();
            return mDefaultSmallCardResMapping;
        }
        mDefaultSmallCardResMapping = HeightConfigCarMapping.getInstance().getDefaultSmallCardMapping();
        return mDefaultSmallCardResMapping;
    }

    /**
     * 获取底部首次展示默认小卡映射  //首次展示
     */
    public String getBottomDefaultSmallCardResource(String getOffLineConfigInfo) {
        Log.d(TAG, "getBottomDefaultSmallCardResource: "+mBottomDefaultSmallCardResource);
        if (mBottomDefaultSmallCardResource != null) {
            return mBottomDefaultSmallCardResource;
        }

        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            mBottomDefaultSmallCardResource = HeightConfigCarMapping.getInstance().getBottomDefaultSmallCardMapping();
            Log.d(TAG, "getBottomDefaultSmallCardResource:: "+mBottomDefaultSmallCardResource);
            return mBottomDefaultSmallCardResource;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            mBottomDefaultSmallCardResource = LowConfigCarMapping.getInstance().getBottomDefaultSmallCardMapping();
            Log.d(TAG, "getBottomDefaultSmallCardResource::: "+mBottomDefaultSmallCardResource);
            return mBottomDefaultSmallCardResource;
        }
        mBottomDefaultSmallCardResource = HeightConfigCarMapping.getInstance().getBottomDefaultSmallCardMapping();
        Log.d(TAG, "getBottomDefaultSmallCardResource::: "+mBottomDefaultSmallCardResource);
        return mBottomDefaultSmallCardResource;
    }

    /**
     * 获取底部首次展示默认大卡映射 //首次展示
     */
    public String getBottomDefaultBigCardResource(String getOffLineConfigInfo) {
        if (mBottomDefaultBigCardResource != null) {
            Log.d(TAG, "a getBottomDefaultBigCardResource: "+mBottomDefaultBigCardResource);
            return mBottomDefaultBigCardResource;

        }
        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            mBottomDefaultBigCardResource = HeightConfigCarMapping.getInstance().getBottomDefaultBigCardMapping();
            Log.d(TAG, "b getBottomDefaultBigCardResource:: "+mBottomDefaultBigCardResource);
            return mBottomDefaultBigCardResource;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            mBottomDefaultBigCardResource = LowConfigCarMapping.getInstance().getBottomDefaultBigCardMapping();
            Log.d(TAG, "c getBottomDefaultBigCardResource::: "+mBottomDefaultBigCardResource);
            return mBottomDefaultBigCardResource;
        }
        mBottomDefaultBigCardResource = HeightConfigCarMapping.getInstance().getBottomDefaultBigCardMapping();
        Log.d(TAG, "d getBottomDefaultBigCardResource:::: "+mBottomDefaultBigCardResource);
        return mBottomDefaultBigCardResource;
    }

    /**
     * 获取AllApp显示卡片资源
     */
    public List<AllAppBean> getAllAppCardResource(String getOffLineConfigInfo) {
        if (mAllAppCardResource != null) {
            return mAllAppCardResource;
        }
        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            mAllAppCardResource = HeightConfigCarMapping.getInstance().getAllAppCardResource();
            return mAllAppCardResource;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            mAllAppCardResource = LowConfigCarMapping.getInstance().getAllAppCardResource();
            return mAllAppCardResource;
        }
        mAllAppCardResource = HeightConfigCarMapping.getInstance().getAllAppCardResource();
        return mAllAppCardResource;
    }

    /**
     * 获取AllApp显示卡片映射
     */
    public String getAllAppCardMapping(String getOffLineConfigInfo) {
        if (mAllAppCardMapping != null) {
            return mAllAppCardMapping;
        }
        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            mAllAppCardMapping = HeightConfigCarMapping.getInstance().getAllAppCardMapping();
            return mAllAppCardMapping;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            mAllAppCardMapping = LowConfigCarMapping.getInstance().getAllAppCardMapping();
            return mAllAppCardMapping;
        }
        mAllAppCardMapping = HeightConfigCarMapping.getInstance().getAllAppCardMapping();
        return mAllAppCardMapping;
    }

    public String getAllBigCardMapping(String getOffLineConfigInfo) {
        if (getOffLineConfigInfo.equals(HIGH_CONFIG_VEHICLE)) {
            allBigCardMapping = HeightConfigCarMapping.getInstance().getAllBigCardMapping();
            return allBigCardMapping;
        } else if (getOffLineConfigInfo.equals(LOW_CONFIG_VEHICLE)) {
            allBigCardMapping = LowConfigCarMapping.getInstance().getAllBigCardMapping();
            return allBigCardMapping;
        }
        allBigCardMapping = HeightConfigCarMapping.getInstance().getAllBigCardMapping();
        return allBigCardMapping;
    }
}
