package com.adayo.app.launcher.presenter.function;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.adayosource.TecentSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.example.BcmImpl;

import java.util.HashMap;
import java.util.Map;

import static com.adayo.app.launcher.offroadinfo.util.Constant.CURRENT_TAB;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AIQUTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_APA;

import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AVM;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_CARBIT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_DVR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_HAVC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MUSIC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MYCAR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_NAVI;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_PICTURE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_RADIO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_SETTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_TEL;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_VIDEO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WEATHER;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WECHAT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_YUEYEQUAN;
import static com.adayo.app.launcher.util.MyConstantsUtil.AppTAG;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_BT_AUDIO;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_CARBIT;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_USB;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI_AUDIO;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI;

public class OpenAppFunction {

    private static final String TAG = AppTAG + "OpenAppFunction";
    private Context context;
    private SrcMngSwitchManager switchManager;
    private static OpenAppFunction mOpenAppFunction;

    public static OpenAppFunction getInstance() {
        if (null == mOpenAppFunction) {
            synchronized (OpenAppFunction.class) {
                if (null == mOpenAppFunction) {
                    mOpenAppFunction = new OpenAppFunction();
                }
            }
        }
        return mOpenAppFunction;
    }

    private OpenAppFunction() {
        switchManager = SrcMngSwitchManager.getInstance();

    }

    public void mOpenApp(String id, Context context) {

        this.context = context;
        switch (id) {
            case ID_VIDEO:
                mOpenVideoApp();
                break;
            case ID_TEL:
                mOpenTel();
                break;
            case ID_PICTURE:
                mOpenPictureApp();
                break;
            case ID_SETTING:
                mOpenSettingApp();
                break;
            case ID_MYCAR:
                mOpenMyCar();
                break;
            case ID_DVR:
                mOpenDvr();
                break;
            case ID_MUSIC:
                mOpenMusic();
                break;

            case ID_NAVI:
                mOpenNavi(context);
                break;
            case ID_AVM:
                mOpenAVM();
                break;
            case ID_OFFROADINFO:
                Bundle bundle = new Bundle();
                OpenAppFunction.getInstance().mOpenOffRoadInfo(bundle);
                break;
            case ID_APA:
                mOpenApa(context);
                break;
            case ID_RADIO:
                mOpenRadioApp();
                break;
            case ID_YUEYEQUAN:
                mOpenyueyequan(context);
                break;
            case ID_WITHTENCENT:
                mOpenWithTecent(context);
                break;
            case ID_AIQUTING:
                mOpenAiquting();
                break;
            case ID_WECHAT:
                mOpenWechat();
                break;
            case ID_HAVC:
                mOpenHvac(context);
                break;
            case ID_WEATHER:
                mOpenWeather(context);
                break;
            case ID_CARBIT:
                OpenAppFunction.getInstance().mOpenCarbit(context);
                break;
            default:
                break;
        }
    }

    /**
     * 打开图片
     */
    public void mOpenPictureApp() {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_USB_PHOTO, AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
        switchManager.requestSwitchApp(info);
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_USB_PHOTO, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        Log.d(TAG, "mOpenPictureApp: ");
    }

    /**
     * 打开视频
     */
    public void mOpenVideoApp() {

        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_USB_VIDEO, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenVideoApp: ");
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_USB_VIDEO, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);

    }

    /**
     * 打开Setting
     */
    public void mOpenSettingApp() {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        Log.d(TAG, "mOpenSettingApp: ");
    }

    /**
     * 打开Radio
     */
    public void mOpenRadioApp() {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_RADIO, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);

//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_RADIO, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        Log.d(TAG, "mOpenRadioApp: ");
    }

    /**
     * 打开我的车
     */
    public void mOpenMyCar() {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BCM, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenMyCar: ");
    }

    /**
     * 打开全景影像
     */
    public void mOpenAVM() {





            if (BcmImpl.getInstance().getPowerStatus() != 0) {
                SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_AVM, AppConfigType.SourceSwitch.SERVICE_ON.getValue());
                switchManager.requestSwitchApp(info);
                Log.d(TAG, "mOpenAVM: ");
            }else {
                Log.d(TAG, "mOpenAVM: "+BcmImpl.getInstance().getPowerStatus()+"  "+BcmImpl.getInstance().getTrailerMode());
            }


    }

    /**
     * 打开APA
     */
    public void mOpenApa(Context context) {
        if (BcmImpl.getInstance().getTrailerMode() != null) {

            if (BcmImpl.getInstance().getNewEngineStatus() != 0 && "false".equals(BcmImpl.getInstance().getTrailerMode())) {
                SourceInfo info = new SourceInfo("ADAYO_SOURCE_APA", AppConfigType.SourceSwitch.SERVICE_ON.getValue());
                switchManager.requestSwitchApp(info);
                Log.d(TAG, "mOpenApa: ");
            }else {
                Log.d(TAG, "mOpenApa: "+BcmImpl.getInstance().getNewEngineStatus()+"  "+BcmImpl.getInstance().getTrailerMode());
            }
        } else {
            Log.d(TAG, "mOpenApa: getTrailerMode null");
        }


    }

    /**
     * 打开蓝牙电话
     */
    public void mOpenTel() {

        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_PHONE, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_PHONE, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        Log.d(TAG, "mOpenTel: ");
    }

    /**
     * 打开爱趣听
     */
    public void mOpenAiquting() {
        SourceInfo info = new SourceInfo(TecentSource.TECENT_WECAR_FLOW, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenAiquting: ");
    }

    /**
     * 打开腾讯随行 (打开腾讯随行主界面区别于打开导航)
     */
    public void mOpenWithTecent(Context context) {
        Intent intent = new Intent("ADAYO_NAVI_SERVICE_RECV");
        intent.putExtra("MAP_TYPE", "TxMap");//表明处理腾讯地图请求
        intent.putExtra("CMD_TYPE", "OPEN_OR_CLOSE_WECAR_APP");//表明命令类型：打开或关闭腾讯地图
        intent.putExtra("TYPE", 2);//控制APP类型 1： 腾讯智驾地图 2：随行应用
        intent.putExtra("VALUE", 1);//执行动作 0：关闭APP 1：打开 APP
        context.sendBroadcast(intent);
        Log.d(TAG, "mOpenWithTecent: ");
//        Intent intent = new Intent("ADAYO_NAVI_SERVICE_RECV");
//        intent.putExtra("CMD_TYPE", "INTO_SEARCH_PAGE");
//        context.sendBroadcastAsUser(intent, UserHandle.getUserHandleForUid(android.os.Process.myUid()));
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_NAVI, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
//        Log.d(TAG, "mOpenWithTecent: ");
    }

    /**
     * 打开微信
     */
    public void mOpenWechat() {
        SourceInfo info = new SourceInfo(TecentSource.TECENT_WECHAT, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
//        SourceInfo info = new SourceInfo(TecentSource.TECENT_WECHAT, null,map,AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue(),bundle);
//        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        Log.d(TAG, "mOpenWechat: ");
    }

    /**
     * 打开usb/蓝牙音乐
     */
    public void mOpenMusic() {
        if (MyConstantsUtil.CURRENT_AUDIOID.equals(ADAYO_SOURCE_USB)) {
            Map<String, String> map = new HashMap<>();
            map.put("SourceType", ADAYO_SOURCE_USB);
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_MULTIMEDIA, map,
                    AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
            switchManager.requestSwitchApp(info);
            Log.d(TAG, "mOpenMusic: ADAYO_SOURCE_USB");
        } else if (MyConstantsUtil.CURRENT_AUDIOID.equals(ADAYO_SOURCE_BT_AUDIO)) {
            Map<String, String> map = new HashMap<>();
            map.put("SourceType", ADAYO_SOURCE_BT_AUDIO);
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_MULTIMEDIA, map,
                    AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
            switchManager.requestSwitchApp(info);
            Log.d(TAG, "mOpenMusic: ADAYO_SOURCE_BT_AUDIO");
        }
    }

    /**
     * 打开小程序
     */
    public void mOpenApplets(Context context) {
        Intent intent = new Intent("com.tencent.wecar.intentapi");
        intent.setData(Uri.parse("wecar" + "://" + "openAIBoard"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("launchSource", 1);
        context.startActivity(intent);
        Log.d(TAG, "mOpenApplets: ");
    }

    /**
     * 打开DVR
     */
    public void mOpenDvr() {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_DVR, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenDvr: ");
    }

    /**
     * 打开越野信息
     */
    public void mOpenOffRoadInfo(Bundle bundle) {
        switchManager = SrcMngSwitchManager.getInstance();
        Map<String, String> map = new HashMap<>();
//        map.put("crossinf", CURRENT_TAB);
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_CAR_ASSISTANT, map,
                AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenOffRoadInfo: " + CURRENT_TAB);
    }


    /**
     * 打开悦野圈
     */

    public void mOpenyueyequan(Context context) {
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_NET_MUSIC, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenyueyequan: ");
    }

    /**
     * 打开空调
     */
    public void mOpenHvac(Context context) {
//        Intent intent = new Intent();
//        intent.setAction("ADAYO_HVAC");
//        intent.putExtra("Type", "launcher");
//        context.sendBroadcast(intent);
    }

    /**
     * 打开导航  主界面
     */
    public void mOpenNavi(Context context) {
//        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_NAVI, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
//        switchManager.requestSwitchApp(info);
        Intent intent = new Intent("ADAYO_NAVI_SERVICE_RECV");
        intent.putExtra("MAP_TYPE", "TxMap");//表明处理腾讯地图请求
        intent.putExtra("CMD_TYPE", "OPEN_OR_CLOSE_WECAR_APP");//表明命令类型：打开或关闭腾讯地图
        intent.putExtra("TYPE", 1);//控制APP类型 1： 腾讯智驾地图 2：随行应用
        intent.putExtra("VALUE", 1);//执行动作 0：关闭APP 1：打开 APP
        context.sendBroadcast(intent);
        Log.d(TAG, "mOpenNavi: ");
    }

    /**
     * 打开天气
     */
    public void mOpenWeather(Context context) {
        boolean state = !NetworkUtils.isNetworkAvailable(context);
        Log.d(TAG, "mOpenWeather: " + state);
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "mOpenWeather: ");
            Map<String, String> map = new HashMap<>();
            map.put("setting_page", "wifi");
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, map,
                    AppConfigType.SourceSwitch.APP_ON.getValue(),
                    AppConfigType.SourceType.UI.getValue());
            SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        }
    }

    /**
     * 打开互联
     */
    public void mOpenCarbit(Context context) {
        SourceInfo info = new SourceInfo(ADAYO_SOURCE_CARBIT, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        switchManager.requestSwitchApp(info);

    }

    /**
     * 打开电子手册
     */
    public void mOpenUserGuide() {
        Map<String, String> map = new HashMap<>();
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_CAR_NEW, map,
                AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
        switchManager.requestSwitchApp(info);
        Log.d(TAG, "mOpenUserGuide: ");
    }

}
