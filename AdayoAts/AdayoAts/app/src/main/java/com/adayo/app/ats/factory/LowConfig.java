package com.adayo.app.ats.factory;

import com.adayo.app.ats.R;

import java.util.HashMap;
import java.util.Map;

public class LowConfig {
    public static int mStartAngle_Low = 130;
    public static  int mValueUnit_Low = 40;
    /**
     * 数组 1 图片模式 ，目标-高亮-切图normal
     */
    public static int[] mItemNormalImages_Low = new int[]{
            R.drawable.icon_sport_n,//运动
            R.drawable.icon_eco_n,//经济
            R.drawable.icon_shushi_n,//舒适
            R.drawable.icon_xuedi_n,//雪地
            R.drawable.icon_chuanyue_n,//穿越
            R.drawable.icon_shadi_n,//沙地
            R.drawable.icon_nidi_n,//泥地
            R.drawable.icon_yanshi_n//岩石
    };

    /**
     * 数组 2 图片模式，默认-暗-切图dis
     */
    public static int[] mItemDisImages_Low = new int[]{
            R.drawable.icon_sport_dis,//运动
            R.drawable.icon_eco_dis,//经济
            R.drawable.icon_shushi_dis,//舒适
            R.drawable.icon_xuedi_dis,//雪地
            R.drawable.icon_chuanyue_dis,//穿越
            R.drawable.icon_shadi_dis,//沙地
            R.drawable.icon_nidi_dis,//泥地
            R.drawable.icon_yanshi_dis//岩石
    };

    /**
     * 数组 3 图片模式，当前-绿-切图sel
     */
    public static int[] mItemSelImages_Low = new int[]{
            R.drawable.icon_sport_sel,//运动
            R.drawable.icon_eco_sel,//经济
            R.drawable.icon_shushi_sel,//舒适
            R.drawable.icon_xuedi_sel,//雪地
            R.drawable.icon_chuanyue_sel,//穿越
            R.drawable.icon_shadi_sel,//沙地
            R.drawable.icon_nidi_sel,//泥地
            R.drawable.icon_yanshi_sel//岩石
    };

    /**
     * 数组 4 文字模式，选中，目标，默认
     */
    public static int[] mItemGlobalTexts_Low = new int[]{
            R.string.sport,
            R.string.eco,
            R.string.shushi,
            R.string.xuedi,
            R.string.chuanyue,
            R.string.shadi,
            R.string.nidi,
            R.string.yanshi,
    };

    public static int[] mbgImages_Low = new int[]{
            R.drawable.img_sports,//运动
            R.drawable.img_eco,//经济
            R.drawable.img_comfortable,//舒适
            R.drawable.img_snowfield,//雪地
            R.drawable.img_traverse,//穿越
            R.drawable.img_sand,//沙地
            R.drawable.img_muddyroad,//泥地
            R.drawable.img_stoneground//岩石
    };

    public static int[] mItemCenterTexts_Low = new int[]{
            R.string.sportmod,
            R.string.ecomod,
            R.string.shushimod,
            R.string.xuedimod,
            R.string.chuanyuemod,
            R.string.shadimod,
            R.string.nidimod,
            R.string.yanshimod,
            R.string.target,//文言: 正在准备进入
            R.string.confirm,//文言: 已开启
            R.string.setupfailed//设置失败
    };

    /**
     * 数组 5 底部提示语
     */

    public static int[] mTipsText_Low = new int[]{
            R.string.sportModTips,
            R.string.ecoModTips,
            R.string.shushiModTips,
            R.string.xuediModTips,
            R.string.chuanyueModTips,
            R.string.shadiModTips,
            R.string.nidiModTips,
            R.string.yanshiModTips
    };

    /**
     * 模式请求上报值与模式映射关系 key:上报值，value:模式值
     */
    public static final Map<Integer, Integer> ATSREQUESTMODMAP_LOW = new HashMap<Integer, Integer>() {
        {
            put(0, 0);  // 运动
            put(1, 1);  // 经济
            put(2, 2);  // 舒适
            put(3, 3);  //雪地
            put(4, 3);  //雪地(大雪)
            put(5, 4);  //穿越
            put(6, 5);  //沙地
            put(7, 6);  //泥地
            put(8, 6);  //泥地(滑泥)
            put(9, 7);  //岩石
            put(10, 0);  //无涉水
            put(11, 0); //Reserved
            put(12, 0); //Reserved
            put(13, 0); //Reserved
            put(14, 0);  //ATS sys not initialized
            put(15, 0);  //Fault
        }
    };

    /**
     * 模式确认上报值与模式映射关系 key:上报值，value:模式值
     */
    public static final Map<Integer, Integer> ATSCONFIRMMODMAP_LOW = new HashMap<Integer, Integer>() {
        {
            put(1, 0);  // 运动
            put(6, 1);  // 经济
            put(8, 2);  // 舒适
            put(2, 3);  //雪地
            put(12, 3);  //雪地(大雪)
            put(22, 4);  //穿越
            put(4, 5);  //沙地
            put(3, 6);  //泥地
            put(23, 6);  //泥地(滑泥)
            put(18, 7);  //岩石
            put(24, 0);  //泥地
            put(15, 0); //Reserved
            put(15, 0); //Reserved
            put(15, 0); //Reserved
            put(15, 0);  //ATS sys not initialized
            put(14, -1);  //Fault
        }
    };


    public static final Map<Integer, Float> ROTATIONOFFSET_LOW = new HashMap<Integer, Float>() {
        {
            put(1, 0.7f);
            put(0, 1f);
            put(2, 1f);
            put(3, 0.2f);
            put(8, -1f);//Reserved
            put(4, 0f);
            put(7, -0.8f);
            put(6, 0f);
            put(5, -2f);

            put(9, 0f);//Reserved
            put(10, 0f);//Reserved
            put(11, 0f); //Reserved
            put(12, 0f); //Reserved
            put(13, 0f); //Reserved
            put(14, 0f);  //ATS sys not initialized
            put(15, 0f);  //Fault
        }
    };
}
