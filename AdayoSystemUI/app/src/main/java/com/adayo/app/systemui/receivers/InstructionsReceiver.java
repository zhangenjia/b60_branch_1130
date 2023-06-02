package com.adayo.app.systemui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.business.NaviControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;

public class InstructionsReceiver extends BroadcastReceiver {
    private static final int SHOW_HVAC_PANEL = 10002;
    private static final int UPDATE_QS_PANEL = 10003;
    private static final int SHOW_VOLUME_PANEL = 10004;
    private static final int ADAYO_NAVI_SERVICE_SEND = 10008;
    private static final int KEYEVENT_ACTION_HOME = 10009;
    private static final int HIDE_PANELS = 10010;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_HVAC_PANEL:
                    if (SystemUIContent.SYSTEM_STATUS_SCREEN == SystemStatusManager.getInstance().getSystemStatus()) {
                        SystemStatusManager.getInstance().setScreenOn();
                    }
                    WindowsManager.setHvacPanelVisibility(View.VISIBLE, HVAC_FRONT, true, true);
                    break;
                case UPDATE_QS_PANEL:
                    WindowsManager.setQsPanelVisibility(View.VISIBLE);
                    break;
                case SHOW_VOLUME_PANEL:
                    WindowsManager.setVolumeDialogVisibility(msg.arg1, msg.arg2, View.VISIBLE);
                    break;
                case KEYEVENT_ACTION_HOME:
//                    QsPanel.getInstance().dismiss();
//                    HVACPanel.getInstance().setVisibility(false);
//                    AllApplicationsPanel.getInstance().close();
                    break;
                case HIDE_PANELS:
                    WindowsManager.setQsPanelVisibility(View.GONE);
                    WindowsManager.setHvacPanelVisibility(View.GONE, 0, true, true);
                    break;
                case ADAYO_NAVI_SERVICE_SEND:
                    NaviControllerImpl.getInstance().notifyNaviStatus(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }
        String action = intent.getAction();
        switch (action){
            case SystemUIContent.ACTION_SHOW_HVAC_PANEL:
                LogUtil.debugD(SystemUIContent.TAG, "Action = " + action);
                mHandler.sendEmptyMessage(SHOW_HVAC_PANEL);
                break;
            case SystemUIContent.ACTION_UPDATE_QS_PANEL:
                LogUtil.debugD(SystemUIContent.TAG, "Action = " + action);
                mHandler.sendEmptyMessage(UPDATE_QS_PANEL);
                break;
            case SystemUIContent.ACTION_SHOW_VOLUME_PANEL:
                int type = intent.getIntExtra("type", 0);
                int volume = intent.getIntExtra("volume", 0);
                LogUtil.debugD(SystemUIContent.TAG, "Action = " + action + ": type = " + type + " ; volume = " + volume);
                Message msgVolume = Message.obtain();
                msgVolume.what = SHOW_VOLUME_PANEL;
                msgVolume.arg1 = type;
                msgVolume.arg2 = volume;
                mHandler.sendMessage(msgVolume);
                break;
            case SystemUIContent.ADAYO_NAVI_SERVICE_SEND:
                int naviAppStatus = intent.getIntExtra(SystemUIContent.NAVI_APP_STATE , -1);
                LogUtil.debugD(SystemUIContent.TAG, "Action = " + action + " ; naviAppStatus = " + naviAppStatus);
                Message msgNavi = Message.obtain();
                msgNavi.what = ADAYO_NAVI_SERVICE_SEND;
                msgNavi.arg1 = naviAppStatus;
                mHandler.sendMessage(msgNavi);
                break;
            case SystemUIContent.ACTION_HIDE_PANELS:
                LogUtil.debugD(SystemUIContent.TAG, "Action = " + action);
                mHandler.sendEmptyMessage(HIDE_PANELS);
                break;
            default:
                break;
        }
    }
}
