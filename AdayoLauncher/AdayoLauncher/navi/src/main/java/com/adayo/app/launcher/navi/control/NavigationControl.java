package com.adayo.app.launcher.navi.control;//package externalview.control;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import com.adayo.app.launcher.navi.R;
import com.adayo.app.launcher.navi.util.ConstantsUtil;
import com.adayo.app.launcher.navi.bean.NaviBean;
import com.adayo.app.launcher.navi.listener.INavigationInfoListener;
import com.adayo.app.launcher.navi.util.NetworkUtils;
import com.adayo.proxy.navigation.navi.NaviManagerForInner;
import com.adayo.proxy.navigation.navi.aidl.CarHeadDirection;
import com.adayo.proxy.navigation.navi.aidl.GuideInfo;
import com.adayo.proxy.navigation.navi.constant.NaviConstantsDef;
import com.adayo.proxy.navigation.navi.interfaces.host.ICityChangedListener;
import com.adayo.proxy.navigation.navi.interfaces.host.IGuideInfoListener;
import com.adayo.proxy.navigation.navi.interfaces.host.INaviClientProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static com.adayo.app.launcher.navi.util.ConstantsUtil.MYRIAMETER;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.NAVI_CUIDE_STATE_ACTIVE;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.NAVI_CUIDE_STATE_NOT_ACTIVE;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.NAVI_REMAIND_DISTANCE_INT_INVALID;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.NAVI_REMAIND_TIME_INT_INVALID;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.ONEKILOMETER;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.ONEKILOMETER_AS_DOUBLE;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.RE_AUTONAVIINIT;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.SIXTYTIMES;
import static com.adayo.app.launcher.navi.util.ConstantsUtil.TENTHOUSANDMETERS;
import static com.adayo.proxy.navigation.navi.constant.NaviConstantsDef.NaviGuideStatus.End_Guide;
import static com.adayo.proxy.navigation.navi.constant.NaviConstantsDef.NaviGuideStatus.Start_Guide;
import static com.adayo.proxy.navigation.navi.constant.NaviConstantsDef.NaviGuideStatus.Stop_Guide;


public class NavigationControl implements IGuideInfoListener {
    private static final String TAG = NavigationControl.class.getSimpleName();
    private final Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RE_AUTONAVIINIT:
                    autoNaviInit();
                    break;
                default:
                    break;
            }
        }
    };

    private NaviManagerForInner mNaviManagerForInner = null;
    private NaviBean mNaviBean = new NaviBean();
    private int mRemaindTime = NAVI_REMAIND_TIME_INT_INVALID;           //剩余时间
    private int mRemaindDistance = NAVI_REMAIND_DISTANCE_INT_INVALID;   //剩余距离
    private int mSegRemainDis = NAVI_REMAIND_DISTANCE_INT_INVALID;      //下一段路的距离
    private NaviConstantsDef.NaviGuideStatus mNaviGuidStatus = NaviConstantsDef.NaviGuideStatus.Compass;
    private int n = 0;
    private byte currentStatus;
    INavigationInfoListener mNaviInfoListener = null;
    public static NavigationControl mNavigationControl = null;
    private INaviClientProxy naviClient;
    private GuideInfo info;

    public static NavigationControl getInstance(Context context) {
        if (null == mNavigationControl) {
            synchronized (NavigationControl.class) {
                if (null == mNavigationControl) {
                    mNavigationControl = new NavigationControl(context);
                }
            }
        }
        return mNavigationControl;
    }

    private NavigationControl(Context context) {
        mContext = context;
    }

    //构造块中进行赋初值操作
    {
        mNaviBean.setNavRouteState(NAVI_CUIDE_STATE_NOT_ACTIVE);
        //导航状态（0代表not_active“未规划路线”， 1代表active显示TBT界面，2代表calculating算路中，3代表finished导航完成）
        mNaviBean.setNavArrow((byte) 255);                            //转向信息（使用无效值，默认不显示）
        mNaviBean.setNextRoadName(ConstantsUtil.NAVI_ROAD_NAME_INVALID);           //下一个路名
        mNaviBean.setDistanceNextRoad(ConstantsUtil.NAVI_ROAD_NAME_INVALID);       //距离下一段路的距离，转向距离信息
        mNaviBean.seteTA(ConstantsUtil.NAVI_TIME_INVALID);                         //剩余时间
        mNaviBean.setdTD(ConstantsUtil.NAVI_DISTANCE_INVALID);                   //剩余距离
        mNaviBean.setCurrentRoadName(ConstantsUtil.NAVI_ROAD_NAME_INVALID);         //当前路名
    }

    /**
     * 注册
     */
    public void autoNaviInit() { //注册
        mNaviManagerForInner = NaviManagerForInner.getNaviManagerForInner();
        naviClient = mNaviManagerForInner.getNaviClient();
        naviClient.registerCityChangedListener(mCityChangedListener);
        naviClient.requestCurrentCity();//todo 需要间隔请求
        boolean ret = naviClient.registerGuideInfoListener(this);
        ret = naviClient.onRequestNaviGuideStatus();

        NetworkUtils.getInstance(mContext).setonNetWorkChangeListener(new NetworkUtils.OnNetWorkChangeListener() {
            @Override
            public void onNetChange(boolean isConnected) {
                Log.d(TAG,"onNetChange = "+isConnected);
                if (isConnected){
                    naviClient.requestCurrentCity();
                    mHandler.postDelayed(runnable, 1000);
                }else {
                    if (runnable!=null){
                        Log.d(TAG,"removeCallbacks  1");
                        mHandler.removeCallbacks(runnable);
                    }
                }
            }
        });
        if (!ret) {
            n++;
            Message message = new Message();
            message.what = RE_AUTONAVIINIT;
            mHandler.sendMessageDelayed(message, 5000);
        }
    }

    //响应请求以注册回调返回城市区域信息
    ICityChangedListener mCityChangedListener = new ICityChangedListener() {
        @Override
        public void onReportCityChanged(String country, String province1, String cityInfo) {
            mNaviBean.setCityInfo(cityInfo);
            if (mNavigationControl != null) {
                Log.d(TAG,"removeCallbacks  2");
                boolean getCityInfo = mNaviInfoListener.onReportGuideInfo(mNaviBean);
                Log.d(TAG," getCityInfo = "+getCityInfo);
                if (getCityInfo){
                    if (runnable!=null){
                        Log.d(TAG,"removeCallbacks  3");
                        mHandler.removeCallbacks(runnable);
                    }

                }

            }
        }
    };

    /**
     * 解注册
     */
    public void autoNaviDeInit() {//
//        if (mNaviManagerForInner != null) {
//            mNaviManagerForInner.getNaviClient().unregisterGuideInfoListener(this);
//        }
    }

    /**
     * 退出导航
     */
    public void requestExitGuide() {
        if (naviClient != null) {
            Log.d(TAG, "requestExitGuide: ");
            naviClient.requestExitGuide();
        }
    }

    /**
     * 导航状态
     *
     * @param status
     */
    @Override
    public void onReportNaviGuideStatus(NaviConstantsDef.NaviGuideStatus status) {
        Log.d(TAG, "onReportNaviGuideStatus: " + status);
        if (status == Start_Guide) {
        }
        //如果TimerTerminal还是true，说明是在2S内收到了终止导航的通知，则过滤掉。
        if ((status == Stop_Guide) || (status == End_Guide)) {
            mNaviInfoListener.oReportNaviGuideStatus(1);//未导航
        }
        if (mNaviGuidStatus != status) {
            //存储数据更新
            mNaviGuidStatus = status;
            //数据处理
            currentStatus = naviGuideStatusConvert(status);
            if (currentStatus == NAVI_CUIDE_STATE_ACTIVE) {
                mNaviBean.setNavRouteState(currentStatus);
                if (mNaviInfoListener != null) {
                    mNaviInfoListener.oReportNaviGuideStatus(0);//导航中
                }
            } else if ((currentStatus == NAVI_CUIDE_STATE_NOT_ACTIVE) && (mNaviBean.getNavRouteState() == NAVI_CUIDE_STATE_ACTIVE)) {
                mNaviBean.setNavRouteState(currentStatus);
                if (mNaviInfoListener != null) {
                    mNaviInfoListener.oReportNaviGuideStatus(1);//未导航
                }
                //todo
            }
        }
    }

    @Override
    public void onReportCompass(NaviConstantsDef.CompassType type) {
    }

    @Override
    public void onReportWarningInfo(NaviConstantsDef.WARNING_TYPE warning, String content) {
    }

    @Override
    public void onReportDriveWayInfo(String json) {
    }

    @Override
    public void onReportSafeDrivingInfo(boolean isDisplay, int type) {
    }

    @Override
    public void onReportTunnel(int tunnelType, boolean enter) {
    }

    @Override
    public void onReportCarHeadDirection(CarHeadDirection carHeadDirection) {
    }

    /**
     * 导航信息
     *
     * @param info
     */
    @Override
    public void onReportGuideInfo(GuideInfo info) {
        this.info = info;
        Log.d(TAG, "onReportGuideInfo: " + info);
        if (null == info) {
            return;
        }
        if (info.getGuideType() == 2) {//巡航
            return;
        }

        //剩余时间
        if (mRemaindTime != info.getRouteRemainTime()) {
            mRemaindTime = info.getRouteRemainTime();
            mNaviBean.seteTA(remaindTimeDisplayConvert(mRemaindTime));
        }

        //剩余时里程
        if (mRemaindDistance != info.getRouteRemainDis()) {
            mRemaindDistance = info.getRouteRemainDis();
            mNaviBean.setdTD(remaindDistanceDisplayConvert(mRemaindDistance));
        }

        //下一路口距离
        if (mSegRemainDis != info.getSegRemainDis()) {
            mSegRemainDis = info.getSegRemainDis();
            mNaviBean.setDistanceNextRoad(distanceNextRoadDisplayConvert(mSegRemainDis));
        }

        //下一路口名
        String nextRoad = info.getNextRoadName();
        if ((nextRoad != null) && (!nextRoad.equals(mNaviBean.getNextRoadName()))) {
            naviClient.requestCurrentCity();//todo 下一路口名变了，请求定位信息，需要确认这样做是否可以
            mNaviBean.setNextRoadName(nextRoad);
        }
        //导航箭头
        int icon = info.getIcon();
        if (mNaviInfoListener != null) {
            mNaviBean.setNavArrow(icon);
            mNaviInfoListener.onReportGuideInfo(mNaviBean);
        }
    }

    private byte naviGuideStatusConvert(NaviConstantsDef.NaviGuideStatus status) {
        byte naviRouteState;
        switch (status) {
            case On_Route_Near:
            case On_Route_Far:
            case Off_Route:
                naviRouteState = NAVI_CUIDE_STATE_NOT_ACTIVE;
                break;
            case Start_Guide:
            case During_Guide:
                naviRouteState = NAVI_CUIDE_STATE_ACTIVE;
                break;
            case Stop_Guide:
            case End_Guide:
                naviRouteState = NAVI_CUIDE_STATE_NOT_ACTIVE;
                break;
            default:
                naviRouteState = NAVI_CUIDE_STATE_NOT_ACTIVE;
                break;
        }

        return naviRouteState;
    }

    //距下一条道路的距离数值的显示转换
    //distanceNextRoadInt：单位是米
    public String distanceNextRoadDisplayConvert(int distanceNextRoadInt) {

        String distanceNextRoad = new String();
        if (distanceNextRoadInt >= MYRIAMETER) {
            //距离大于10公里，显示距离distanceNextRoadInt/1000 取整，单位公里
            long distanceIntValue = Math.round((double) distanceNextRoadInt / ONEKILOMETER_AS_DOUBLE);
            distanceNextRoad = String.valueOf(distanceIntValue);
            mNaviBean.setDistanceNextRoadUnit(mContext.getResources().getString(R.string.km));
        } else if ((distanceNextRoadInt >= ONEKILOMETER) && (distanceNextRoadInt < MYRIAMETER)) {
            //距离在1公里到10公里之间，显示距离distanceNextRoadInt/1000.0，保留一位小数，单位公里
            //如果处理后数据为8.0公里，则需要做特殊显示，显示为8公里，其他情况则正常显示。
            double distanceNextRoaddouble = (double) distanceNextRoadInt / ONEKILOMETER_AS_DOUBLE;
            Double distanceNextRoadDouble = distanceNextRoaddouble;
            BigDecimal bigDecimal = new BigDecimal(distanceNextRoadDouble);
            distanceNextRoadDouble = bigDecimal.setScale(1, RoundingMode.HALF_UP).doubleValue();
            if (distanceNextRoadDouble.intValue() - distanceNextRoadDouble == 0) {
                //处理后获取到的数据小数点后一位为0，需要转换
                distanceNextRoad = String.valueOf(distanceNextRoadDouble.intValue());
            } else {
                distanceNextRoad = String.valueOf(distanceNextRoadDouble);
            }
            mNaviBean.setDistanceNextRoadUnit(mContext.getResources().getString(R.string.km));
        } else if (distanceNextRoadInt < ONEKILOMETER && distanceNextRoadInt >= 0) {
            //距离小于1公里，数据不用处理，显示单位米
            distanceNextRoad = String.valueOf(distanceNextRoadInt);
            if (distanceNextRoad.length() == 2) {
                distanceNextRoad = String.valueOf(distanceNextRoadInt);
            } else if (distanceNextRoad.length() == 1) {
                distanceNextRoad = String.valueOf(distanceNextRoadInt);
            } else {
                distanceNextRoad = String.valueOf(distanceNextRoadInt);
            }
            mNaviBean.setDistanceNextRoadUnit(mContext.getResources().getString(R.string.m));
        }
        return distanceNextRoad;
    }

    //剩余时间的数值显示转换
    //time：单位是秒
    public String remaindTimeDisplayConvert(int time) {
        String eTA = new String();
        time = (int) Math.floor((double) time / 60.0);
        int eTAOfHours = time / SIXTYTIMES;
        String eTAOfHoursStr = eTAOfHours + "";
        int eTAOfMinutes = time % SIXTYTIMES;
        String eTAOfMinutesStr = eTAOfMinutes + "";
        if (!TextUtils.isEmpty(eTAOfHoursStr) && (eTAOfHoursStr.length() == 1) && (!TextUtils.isEmpty(eTAOfMinutesStr) && (eTAOfMinutesStr.length() == 1))) {
//            eTA = "0" + eTAOfHours + ":" + "0" + eTAOfMinutes;
        } else if (!TextUtils.isEmpty(eTAOfHoursStr) && (eTAOfHoursStr.length() == 2) && (!TextUtils.isEmpty(eTAOfMinutesStr) && (eTAOfMinutesStr.length() == 1))) {
//            eTA = eTAOfHours + ":" + "0" + eTAOfMinutes;
        } else if (!TextUtils.isEmpty(eTAOfHoursStr) && (eTAOfHoursStr.length() == 1) && (!TextUtils.isEmpty(eTAOfMinutesStr) && (eTAOfMinutesStr.length() == 2))) {
//            eTA = "0" + eTAOfHours + ":" + eTAOfMinutes;
        } else if (!TextUtils.isEmpty(eTAOfHoursStr) && (eTAOfHoursStr.length() == 2) && (!TextUtils.isEmpty(eTAOfMinutesStr) && (eTAOfMinutesStr.length() == 2))) {
//            eTA = eTAOfHours + ":" + eTAOfMinutes;
        }
        eTA = eTAOfHours + mContext.getResources().getString(R.string.h) + eTAOfMinutes + mContext.getResources().getString(R.string.minute);
        return eTA;
    }

    //剩余里程的数值显示转换
    //dist：单位是米
    public String remaindDistanceDisplayConvert(int dist) {
        String dTD = new String();
        if (dist > TENTHOUSANDMETERS) {
            int floor = (int) Math.floor(dist / ONEKILOMETER_AS_DOUBLE);

            dTD = String.valueOf(floor) + mContext.getResources().getString(R.string.km);
        } else if (dist >= MYRIAMETER && dist < TENTHOUSANDMETERS) {
//            //显示距离dTDInt/1000 取整，单位公里
            double dTDdouble = (double) dist / ONEKILOMETER_AS_DOUBLE;
            Double dtDDouble = dTDdouble;
            BigDecimal bigDecimal = new BigDecimal(dtDDouble);
            dtDDouble = bigDecimal.setScale(1, RoundingMode.HALF_UP).doubleValue();
            if (dtDDouble.intValue() - dtDDouble == 0) {
                //处理后获取到的数据小数点后一位为0，需要转换
                dTD = String.valueOf(dtDDouble.intValue()) + mContext.getResources().getString(R.string.km);
            } else {
                dTD = String.valueOf(dtDDouble) + mContext.getResources().getString(R.string.km);
            }

        } else if (dist >= ONEKILOMETER && dist < MYRIAMETER) {
            //显示距离dTDInt/1000.0，保留一位小数，单位公里，如果处理后数据为8.0公里，则需要做特殊显示，显示为8公里，其他情况则正常显示
            double dTDdouble = (double) dist / ONEKILOMETER_AS_DOUBLE;
            Double dtDDouble = dTDdouble;
            BigDecimal bigDecimal = new BigDecimal(dtDDouble);
            dtDDouble = bigDecimal.setScale(1, RoundingMode.HALF_UP).doubleValue();
            if (dtDDouble.intValue() - dtDDouble == 0) {
                //处理后获取到的数据小数点后一位为0，需要转换
                dTD = String.valueOf(dtDDouble.intValue()) + mContext.getResources().getString(R.string.km);
            } else {
                dTD = String.valueOf(dtDDouble) + mContext.getResources().getString(R.string.km);
            }
        } else if (dist < ONEKILOMETER) {
            //数据不用处理，显示单位米
            dTD = String.valueOf(dist) + mContext.getResources().getString(R.string.m);
        }
        return dTD;
    }


    //腾讯转向标志Map（低配）
    private final Map<Integer, Integer> mLowTencentTurnIconMap = new HashMap<Integer, Integer>() {
        {
            put(0, 255);  //0：什么也不显示，仪表识别到无效数值，不显示
            put(1, 0);    //直行
            put(2, 3);    //左转
            put(3, 4);    //右转
            put(4, 7);    //左转掉头
            put(5, 2);    //进入环岛左行，终点在环岛内
            put(6, 1);    //二岔路驶入主路（H型路口），靠左，驶入主路
            put(7, 2);    //二岔路驶入辅路（H型路口），靠右，驶入辅路
            put(8, 0);    //普通三分歧中央
            put(10, 1);   //左前方转弯
            put(11, 1);   //二岔路驶入主路，靠左，驶入主路
            put(12, 1);   //左侧
            put(13, 1);   //普通三分歧左侧
            put(14, 3);   //左侧三岔路走最左
            put(15, 1);   //普通三分歧中央
            put(16, 1);   //四岔路口走最左侧岔路
            put(17, 1);   //四岔路口走左侧第二岔路
            put(20, 2);   //右前方转弯
            put(21, 2);   //二岔路驶入辅路，靠右，驶入辅路
            put(22, 2);   //右侧
            put(23, 2);   //普通三分歧右侧
            put(24, 4);   //右侧三岔路走最右
            put(25, 2);   //右侧三岔路走中间
            put(26, 2);   //四岔路口走右侧第二岔路
            put(27, 2);   //四岔路口走最右侧岔路
            put(30, 5);   //左后方转弯
            put(31, 5);   //左后方转弯
            put(32, 5);   //八方向左转+随后靠左
            put(33, 1);   //八方向左转+随后靠右
            put(34, 5);   //八方向左转+随后靠最左
            put(35, 3);   //八方向左转+随后沿中间
            put(36, 1);   //八方向左转+随后靠最右
            put(37, 1);   //左前方转弯
            put(40, 6);   //右后方转弯
            put(41, 6);   //右后方转弯
            put(42, 2);   //八方向右转+随后靠左
            put(43, 6);   //八方向右转+随后靠右
            put(44, 2);   //八方向右转+随后靠最左
            put(45, 4);   //八方向右转+随后沿中间
            put(46, 6);   //八方向右转+随后靠最右
            put(47, 2);   //右前方转弯
            put(51, 41);  //进入环岛，右转并从第1个出口驶出
            put(52, 41);  //进入环岛，右转并从第2个出口驶出
            put(53, 41);  //进入环岛，右转并从第3个出口驶出
            put(54, 41);  //进入环岛，右转并从第4个出口驶出
            put(55, 41);  //进入环岛，右转并从第5个出口驶出
            put(56, 41);  //进入环岛，右转并从第6个出口驶出
            put(57, 41);  //进入环岛，右转并从第7个出口驶出
            put(58, 41);  //进入环岛，右转并从第8个出口驶出
            put(59, 41);  //进入环岛，右转并从第9个出口驶出
            put(60, 34);  //到达终点
            put(61, 34);  //到达终点
            put(62, 34);  //到达终点
            put(63, 35);  //途径点
            put(64, 0);   //进入隧道
            put(65, 0);   //进入隧道
            put(66, 39);  //收费站
        }
    };

    //腾讯转向标志Map（高配）
    private final Map<Integer, Integer> mHighTencentTurnIconMap = new HashMap<Integer, Integer>() {
        {
            put(0, 0);    //无效状态，什么也不显示
            put(1, 69);   //直行
            put(2, 62);   //左转
            put(3, 63);   //右转
            put(4, 68);   //左转掉头
            put(5, 16);   //进入环岛左行，终点在环岛内
            put(6, 50);   //二岔路驶入主路（H型路口），靠左，驶入主路
            put(7, 16);   //二岔路驶入辅路（H型路口），靠右，驶入辅路
            put(8, 69);   //普通三分歧中央
            put(10, 64);  //左前方转弯
            put(11, 27);  //二岔路驶入主路，靠左，驶入主路
            put(12, 27);  //左侧
            put(18, 28);  //二岔路走左侧
            put(13, 27);  //普通三分歧左侧
            put(14, 28);  //左侧三岔路走最左
            put(15, 27);  //普通三分歧中央
            put(16, 28);  //四岔路口走最左侧岔路
            put(17, 27);  //四岔路口走左侧第二岔路
            put(20, 65);  //右前方转弯
            put(21, 23);  //二岔路驶入辅路，靠右，驶入辅路
            put(22, 23);  //右侧
            put(28, 23);
            put(23, 22);  //普通三分歧右侧
            put(24, 22);  //右侧三岔路走最右
            put(25, 23);  //右侧三岔路走中间
            put(26, 23);  //四岔路口走右侧第二岔路
            put(27, 22);  //四岔路口走最右侧岔路
            put(30, 66);  //左后方转弯
            put(31, 66);  //左后方转弯
            put(32, 66);  //八方向左转+随后靠左
            put(33, 28);  //八方向左转+随后靠右
            put(34, 31);  //八方向左转+随后靠最左
            put(35, 62);  //八方向左转+随后沿中间
            put(36, 28);  //八方向左转+随后靠最右
            put(37, 64);  //左前方转弯
            put(38, 64);  //左前方转弯
            put(40, 67);  //右后方转弯
            put(41, 67);  //右后方转弯
            put(42, 22);  //八方向右转+随后靠左
            put(43, 20);  //八方向右转+随后靠右
            put(44, 23);  //八方向右转+随后靠最左
            put(45, 63);  //八方向右转+随后沿中间
            put(46, 20);  //八方向右转+随后靠最右
            put(47, 65);  //右前方转弯
            put(48, 65);  //右前方转弯
            put(51, 85);   //进入环岛，右转并从第1个出口驶出
            put(52, 86);   //进入环岛，右转并从第2个出口驶出
            put(53, 87);   //进入环岛，右转并从第3个出口驶出
            put(54, 88);  //进入环岛，右转并从第4个出口驶出
            put(55, 89);  //进入环岛，右转并从第5个出口驶出
            put(56, 90);  //进入环岛，右转并从第6个出口驶出
            put(57, 91);  //进入环岛，右转并从第7个出口驶出
            put(58, 92);  //进入环岛，右转并从第8个出口驶出
            put(59, 93);  //进入环岛，右转并从第9个出口驶出
            put(60, 77);  //到达终点
            put(61, 77);  //到达终点
            put(62, 77);  //到达终点
            put(63, 70);  //途径点
            put(64, 78);  //进入隧道
            put(65, 78);  //进入隧道
            put(66, 76);  //收费站
            put(81, 69);
            put(82, 69);
            put(83, 69);
            put(84, 69);
            put(85, 68);
            put(86, 68);
            put(87, 83);
            put(88, 83);
            put(89, 83);
            put(90, 84);
        }
    };

    private byte naviHighMeterTurnIconConvert(byte icontype, int roundNumber) {
        byte iconConverValue = 0;
        //获取导航类型

        if (mHighTencentTurnIconMap.containsKey(new Integer((int) icontype))) {
            iconConverValue = (byte) (mHighTencentTurnIconMap.get(new Integer((int) icontype)).intValue());
        } else {
        }

        return iconConverValue;
    }

    /**
     * 注册给LauncherNaviCardView 传递数据的回调
     *
     * @param listener
     */
    public void registerNavigationInfoListener(INavigationInfoListener listener) {
        mNaviInfoListener = listener;
    }

    /**
     * 0 进入地图导航到家
     * 1 进入地图导航到家
     *
     * @param value
     */
    public void NavigateToHomeOrCompany(int value) {
        Log.d(TAG, "NavigateToHomeOrCompany: ");
        Intent intent = new Intent("ADAYO_NAVI_SERVICE_RECV");
        intent.putExtra("CMD_TYPE", "GO_HOME_OR_COMPANY");
        intent.putExtra("TYPE", value);//0:回家 1:回公司
        mContext.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(android.os.Process.myUid()));
    }

    /**
     * 进入导航APP搜索界面
     */
    public void intoSearchPage() {
        Log.d(TAG, "intoSearchPage: ");
        Intent intent = new Intent("ADAYO_NAVI_SERVICE_RECV");
        intent.putExtra("CMD_TYPE", "INTO_SEARCH_PAGE");
        mContext.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(android.os.Process.myUid()));
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //要执行的方法
            boolean result = naviClient.requestCurrentCity();
            Log.d(TAG, " requestcityinfo result =  " + result);
            mHandler.postDelayed(this, 5 * 1000);// todo  Exception Memory Problem
        }
    };


    public void onConfigChange(){
        //剩余时间
        mRemaindTime = -1;
        mRemaindDistance = -1;
        mSegRemainDis = -1;
        mRemaindTime = -1;
        mNaviBean.setNextRoadName("");

        if (info==null){
            return;
        }

        if (mRemaindTime != info.getRouteRemainTime()) {
            mRemaindTime = info.getRouteRemainTime();
            mNaviBean.seteTA(remaindTimeDisplayConvert(mRemaindTime));
        }

        //剩余时里程
        if (mRemaindDistance != info.getRouteRemainDis()) {
            mRemaindDistance = info.getRouteRemainDis();
            mNaviBean.setdTD(remaindDistanceDisplayConvert(mRemaindDistance));
        }

        //下一路口距离
        if (mSegRemainDis != info.getSegRemainDis()) {
            mSegRemainDis = info.getSegRemainDis();
            mNaviBean.setDistanceNextRoad(distanceNextRoadDisplayConvert(mSegRemainDis));
        }

        //下一路口名
        String nextRoad = info.getNextRoadName();
        if ((nextRoad != null) && (!nextRoad.equals(mNaviBean.getNextRoadName()))) {
            naviClient.requestCurrentCity();//todo 下一路口名变了，请求定位信息，需要确认这样做是否可以
            mNaviBean.setNextRoadName(nextRoad);
        }
        //导航箭头
        int icon = info.getIcon();
        if (mNaviInfoListener != null) {
            mNaviBean.setNavArrow(icon);
            mNaviInfoListener.onReportGuideInfo(mNaviBean);
            Log.d(TAG, "onConfigChange: mNaviInfoListener");
        }else {
            Log.d(TAG, "onConfigChange: mNaviInfoListener null");
        }

    }

}

