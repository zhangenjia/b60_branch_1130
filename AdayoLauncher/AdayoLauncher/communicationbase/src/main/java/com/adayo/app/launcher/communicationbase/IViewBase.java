package com.adayo.app.launcher.communicationbase;

import android.view.View;

import java.util.List;

public interface IViewBase {

    /**
     *在各自的构造方法中初始化view注册监听
     */

    /**
     * 开始更新数据并返回卡片
     *
     * @param id
     * @param type 大卡还是小卡
     * @param type1 区分入场，还是拖拽上屏
     * @return
     */

    View initCardView(String id, String type,String type1);

    /**
     * 哪个卡片停止更新数据
     *
     * @param id
     * @param type
     */
    void unInitCardView(String id, String type);//停止更新数据


    /**
     * 卡片对象==null 布局== null 注册解除
     */

    void releaseResource();


    /**
     * 播放小卡动画
     *
     * @param id
     * @param delay
     */
    void playAnimation(String id, int delay);

    /**
     * 语言更新通知
     */
    void onConfigurationChanged();

    /**
     * 通知Launcher加载完成
     */

    void launcherLoadComplete();
    /**
     * 通知Launcher更新
     */
    void launcherAnimationUpdate(int i);
}