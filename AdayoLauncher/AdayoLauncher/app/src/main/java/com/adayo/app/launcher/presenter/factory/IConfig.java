package com.adayo.app.launcher.presenter.factory;

import com.adayo.app.launcher.model.bean.AllAppBean;

import java.util.List;

public interface IConfig {

    /**
     * 获取默认的4个小卡Id
     */
    String getDefaultSmallCardMapping();

    /**
     * 获取底部默认小卡Id
     */
    String getBottomDefaultSmallCardMapping();

    /**
     * 获取底部默认大卡Id
     */
    String getBottomDefaultBigCardMapping();

    /**
     * 获取AllApp显示卡片及对应资源Id
     * @return
     */
    List<AllAppBean> getAllAppCardResource();

    /**
     * 获取AllApp显示卡片Id
     * @return
     */
    String getAllAppCardMapping();

    String getAllBigCardMapping();
}
