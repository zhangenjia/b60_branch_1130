package com.adayo.app.ats.util;

import com.adayo.app.ats.R;

public class Constants {

    public static final String ATS_VERSION = "AtsService_2022/11/17";
    public static final String CURRENTMOD = "persist.current_mod";
    public static final int PICTUREMOD = 0;
    public static final int TEXTMOD = 1;


    /**
     * 数组 4 文字模式，选中，目标，默认
     */
    public static int[] mItemGlobalTexts_High = new int[]{
            R.string.sport,
            R.string.eco,
            R.string.shushi,
            R.string.xuedi,
            R.string.chuanyue,
            R.string.shadi,
            R.string.nidi,
            R.string.yanshi,
            R.string.sheshui
    };

    public static int[] mbgImages_High = new int[]{
            R.drawable.img_sports,//运动
            R.drawable.img_eco,//经济
            R.drawable.img_comfortable,//舒适
            R.drawable.img_snowfield,//雪地
            R.drawable.img_traverse,//穿越
            R.drawable.img_sand,//沙地
            R.drawable.img_muddyroad,//泥地
            R.drawable.img_stoneground,//岩石
            R.drawable.img_wading//涉水
    };

    public static int[] mItemCenterTexts_High = new int[]{
            R.string.sportmod,
            R.string.ecomod,
            R.string.shushimod,
            R.string.xuedimod,
            R.string.chuanyuemod,
            R.string.shadimod,
            R.string.nidimod,
            R.string.yanshimod,
            R.string.sheshuimod,
            R.string.target,//文言: 正在准备进入
            R.string.confirm,//文言: 已开启
            R.string.setupfailed//设置失败
    };

    /**
     * 底部提示语
     */
    public static int[] mTipsText_High = new int[]{
            R.string.sportModTips,
            R.string.ecoModTips,
            R.string.shushiModTips,
            R.string.xuediModTips,
            R.string.chuanyueModTips,
            R.string.shadiModTips,
            R.string.nidiModTips,
            R.string.yanshiModTips,
            R.string.sheshuiModTips
    };


}
