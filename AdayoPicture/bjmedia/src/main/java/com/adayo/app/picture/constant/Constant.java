package com.adayo.app.picture.constant;


/**
 * 常量类
 *
 * @author sth
 * @version 1.0 2018-03-13
 */
public class Constant {
    public static final int FRAGMENT_PICTURE_LIST = 1;//显示图片列表界面
    public static final int FRAGMENT_PICTURE_PLAY = 2;//显示全屏状态
    public static final int PICTURE_LOADING_VIEW_V = 3;//显示过场动画（加载中。。。）
    public static final int PICTURE_LOADING_VIEW_G = 4;//隐藏过场动画（加载中。。。）
    public static final int PICTURE_MSG_V = 5;//显示提示界面
    public static final int PICTURE_MSG_G = 6;//隐藏提示界面
    public static final int PICTURE_APP_STATE_FINISH = 100;//关闭app
    public static final int FRAGMENT_PICTURE_PLAY_PAUSE = 7;//全屏状态暂停幻灯片播放
    public static final int FRAGMENT_PICTURE_PLAY_PREV = 8;//全屏状态切换上一张照片
    public static final int FRAGMENT_PICTURE_PLAY_NEXT = 9;//全屏状态切换下一张照片
    public static final int FRAGMENT_PICTURE_PLAY_PLAY_OR_STOP = 10;//全屏幻灯片播放或者暂停照片播放
    public static final int FRAGMENT_PICTURE_PLAY_ROTATE = 11;//全屏旋转图片
  //  public static boolean IS_TIMER_STATE = true;//隐藏提示界面
    public static final String MEDIA_CONTROL = "VR_MEDIA_CONTROL";
    public static final String ACTION_PREV = "com.media.widget.PREV";
    public static final String ACTION_PLAY_PAUSE = "com.media.widget.PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.media.widget.NEXT";
    public static final String ACTION_START_APP = "com.media.widget.start_app";

}
