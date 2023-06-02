package com.adayo.app.setting.wifi;

import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.app.setting.R;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.common.settings.bean.WifiInfoBean;
import com.adayo.common.settings.constant.EnumConstant.WIFI_STATE;
import com.lt.library.base.recyclerview.adapter.BaseAdapter;
import com.lt.library.base.recyclerview.holder.sub.EntityViewHolder;
import com.lt.library.base.recyclerview.listener.OnEntityItemClickListener;

import java.util.List;

import static android.graphics.Color.parseColor;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_WIFI;


public class NetWifiSaveAdapter extends BaseAdapter<WifiInfoBean> implements OnEntityItemClickListener {private final static String TAG = NetWifiSaveAdapter.class.getSimpleName();
    private WifiViewModel mViewModel;
    private static final int SIGNAL_MAX_LEVEL = 5;
    private Animation mRefBtnAnimation;
    private Toast mPasswordToast;
    private ScanResult mScanResult;private int mpostion = 0;
    private WifiInfoBean bean;
    private CommonHighlight mCommonHighlight;
    private AnimationDrawable mAnimationDrawable;
    private String ConnectingBssid;


    public NetWifiSaveAdapter(WifiViewModel viewModel, CommonHighlight commonHighlight) {
        super();
        LogUtil.debugD(TAG, "");
        this.mViewModel = viewModel;
        this.mCommonHighlight = commonHighlight;
        mRefBtnAnimation = AnimationUtils.loadAnimation(getAppContext(), R.anim.rotate_cw);this.setOnEntityItemClickListener(this);
    }

    @Override
    protected int getEntityLayoutRes(int viewType) {
        return R.layout.item_list_content_wifi;
    }@Override
    protected void onBindEntityView(EntityViewHolder viewHolder, WifiInfoBean dataSource, int position, int viewType) {
        super.onBindEntityView(viewHolder, dataSource, position, viewType);
        LogUtil.debugD(TAG, "postion = " + position);
        ImageView ivItemListWifi = viewHolder.findViewById(R.id.iv_item_list_wifi);
        TextView tvWifiTitle = viewHolder.findViewById(R.id.tv_item_list_title);
        ImageView ivWifiState = viewHolder.findViewById(R.id.iv_item_list_state);
        ImageView ivWlanLine = viewHolder.findViewById(R.id.iv_wlan_line);
        ImageView ivWlanLineBottom = viewHolder.findViewById(R.id.iv_wlan_line_bottom);
        if (position == 0) {
            bean = dataSource;
        }
        tvWifiTitle.setText(dataSource.getScanResult().SSID);
        int signalLevel = dataSource.getSignalLevel(SIGNAL_MAX_LEVEL);WIFI_STATE wifiState = dataSource.getState();
        tvWifiTitle.setTextColor(parseColor("#61ffffff"));
        ivWifiState.setVisibility(View.INVISIBLE);
        LogUtil.debugD(TAG, "wifiState = " + wifiState);
        switch (wifiState) {
            case NOT_CONNECT_WPA:
            case NOT_CONNECT_WPS:
            case NOT_CONNECT_WEP:
            case NOT_CONNECT_OPEN:ivWifiState.setVisibility(View.INVISIBLE);
                break;
            case SAVED:break;
            case PASSWORD_ERROR:break;
            case CONNECT:if (isWifiConnecting(dataSource, mScanResult)) {
                    mScanResult = null;
                }
                ConnectingBssid="";
                SkinUtil.setImageResource(ivWifiState, R.drawable.offroad_system_settings_icon_connect);
                ivWifiState.setVisibility(View.VISIBLE);
                tvWifiTitle.setTextColor(parseColor("#ffffff"));
                break;
            case CONNECTING:ivWifiState.setImageResource(R.drawable.anim_loading);
                mAnimationDrawable = (AnimationDrawable) ivWifiState.getDrawable();
                ivWifiState.setVisibility(View.VISIBLE);
                if (!mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                }
                break;
            default:
                break;
        }
        switch (signalLevel) {
            case 0:
                SkinUtil.setImageResource(ivItemListWifi, R.drawable.offroad_system_settings_icon_wifi_0_n);
                break;
            case 1:
                SkinUtil.setImageResource(ivItemListWifi, R.drawable.offroad_system_settings_icon_wifi_1_n);
                break;
            case 2:
                SkinUtil.setImageResource(ivItemListWifi, R.drawable.offroad_system_settings_icon_wifi_2_n);
                break;
            case 3:
                SkinUtil.setImageResource(ivItemListWifi, R.drawable.offroad_system_settings_icon_wifi_3_n);

                break;
            case 4:
                SkinUtil.setImageResource(ivItemListWifi, R.drawable.offroad_system_settings_icon_wifi_4_n);
                break;
            default:
                break;
        }
        if (position == 0) {
            SkinUtil.setImageResource(ivWlanLine, R.drawable.offroad_system_settings_line_quarter_light);
        } else {
            SkinUtil.setImageResource(ivWlanLine, R.drawable.offroad_system_settings_line_quarter_n);
        }
        viewHolder.setVisibility(R.id.iv_item_list_lock, dataSource.isEncryptedWiFi() ? View.VISIBLE : View.GONE);mpostion = position;
        if(ConnectingBssid==null) {
            return;
        }
        if(ConnectingBssid.equals(dataSource.getScanResult().BSSID)){
            ivWifiState.setImageResource(R.drawable.anim_loading);
            mAnimationDrawable = (AnimationDrawable) ivWifiState.getDrawable();
            ivWifiState.setVisibility(View.VISIBLE);
            if (!mAnimationDrawable.isRunning()) {
                mAnimationDrawable.start();
            }
        }
    }

    @Override
    protected void onBindEntityView(EntityViewHolder viewHolder, WifiInfoBean dataSource, int position, int viewType, List<Object> payloads) {
        super.onBindEntityView(viewHolder, dataSource, position, viewType, payloads);
        if ("change_state_connecting".equals(payloads.get(0))) {ImageView ivWifiState = viewHolder.findViewById(R.id.iv_item_list_state);SkinUtil.setImageResource(ivWifiState, R.drawable.anim_loading);
            AnimationDrawable mAnimationDrawable = (AnimationDrawable) ivWifiState.getDrawable();
            ivWifiState.setVisibility(View.VISIBLE);
            if (!mAnimationDrawable.isRunning()) {
                mAnimationDrawable.start();
            }
} else if ("change_state_not_connected".equals(payloads.get(0))) {
            ImageView ivWifiState = viewHolder.findViewById(R.id.iv_item_list_state);ivWifiState.setVisibility(View.INVISIBLE);
            TextView tvWifiTitle = viewHolder.findViewById(R.id.tv_item_list_title);
            tvWifiTitle.setTextColor(parseColor("#61ffffff"));
        }
    }


    private boolean isWifiConnecting(WifiInfoBean dataSource, ScanResult savedScanResult) {
        return savedScanResult != null && (dataSource.getScanResult().BSSID.equals(savedScanResult.BSSID) && dataSource.getScanResult().SSID.equals(savedScanResult.SSID));
    }


    public void notifyWifiConnecting(WifiInfoBean entity) {

        if (entity == null) {mScanResult = null;
            return;
        }
        ScanResult scanResult = entity.getScanResult();int position = findPositionByBssidAndSsid(scanResult.BSSID, scanResult.SSID);LogUtil.debugD(TAG, "position = " + position);
        if (position == -1) {LogUtil.d(TAG, "did not pass BSSID = " + scanResult.BSSID + ", SSID = " + scanResult.SSID + ", found position");
            ConnectingBssid=scanResult.BSSID;
return;
        }
        mScanResult = scanResult;
        notifyItemChanged(0, "change_state_not_connected");
        notifyItemChanged(position, "change_state_connecting");
        notifyItemMoved(position, 0);
    }



    public int findPositionByBssidAndSsid(String bssid, String ssid) {
        int result = -1;
        List<WifiInfoBean> listBeans = getEntityList();
        for (int i = 0; i < listBeans.size(); i++) {
            WifiInfoBean listBean = listBeans.get(i);
            if (listBean.getScanResult().BSSID.equals(bssid) && listBean.getScanResult().SSID.equals(ssid)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public WifiInfoBean getBean() {
        return bean;
    }

    public void setBean(WifiInfoBean bean) {
        this.bean = bean;
    }

    @Override
    public void onEntityClick(View view, int position) {
        mViewModel.mWifiRequest.requestSaveSelect(getEntityList().get(position));
        mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_WIFI);
    }
}
