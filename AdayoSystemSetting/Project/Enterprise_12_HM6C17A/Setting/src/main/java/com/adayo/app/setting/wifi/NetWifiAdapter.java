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

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_WIFI;


public class NetWifiAdapter extends BaseAdapter<WifiInfoBean> implements OnEntityItemClickListener {private final static String TAG = NetWifiAdapter.class.getSimpleName();
    private static final int SIGNAL_MAX_LEVEL = 5;
    private WifiViewModel mViewModel;
    private String mConnectingBssid;
    private Animation mRefBtnAnimation;
    private Toast mPasswordToast;
    private ScanResult mScanResult;private CommonHighlight mCommonHighlight;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mivWifiState;

    public NetWifiAdapter(WifiViewModel viewModel, CommonHighlight commonHighlight) {
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
        ImageView ivItemListWifi = viewHolder.findViewById(R.id.iv_item_list_wifi);
        TextView tvWifiTitle = viewHolder.findViewById(R.id.tv_item_list_title);
        ImageView ivWifiState = viewHolder.findViewById(R.id.iv_item_list_state);
        ImageView ivWlanLine = viewHolder.findViewById(R.id.iv_wlan_line);
        ImageView ivWlanLineBottom = viewHolder.findViewById(R.id.iv_wlan_line_bottom);
        ivWifiState.setVisibility(View.GONE);
        if (position == 0) {
            SkinUtil.setImageResource(ivWlanLine, R.drawable.offroad_system_settings_line_quarter_light);
            ivWlanLineBottom.setVisibility(View.VISIBLE);
        } else if (position == getItemCount()) {
            SkinUtil.setImageResource(ivWlanLine,R.drawable.offroad_system_settings_line_quarter_n);
            ivWlanLineBottom.setVisibility(View.GONE);
        } else {
            ivWlanLineBottom.setVisibility(View.VISIBLE);
            SkinUtil.setImageResource(ivWlanLine,R.drawable.offroad_system_settings_line_quarter_n);
        }
        int signalLevel = dataSource.getSignalLevel(SIGNAL_MAX_LEVEL);tvWifiTitle.setText(dataSource.getScanResult().SSID);
        WIFI_STATE wifiState = dataSource.getState();
        LogUtil.debugD(TAG, "wifiState = " + wifiState);
        switch (signalLevel) {
            case 0:
                SkinUtil.setImageResource(ivItemListWifi,R.drawable.offroad_system_settings_icon_wifi_0_n);
                break;
            case 1:
                SkinUtil.setImageResource(ivItemListWifi,R.drawable.offroad_system_settings_icon_wifi_1_n);
                break;
            case 2:
                SkinUtil.setImageResource(ivItemListWifi,R.drawable.offroad_system_settings_icon_wifi_2_n);
                break;
            case 3:
                SkinUtil.setImageResource(ivItemListWifi,R.drawable.offroad_system_settings_icon_wifi_3_n);
                break;
            case 4:
                SkinUtil.setImageResource(ivItemListWifi,R.drawable.offroad_system_settings_icon_wifi_4_n);
         break;
            default:
                break;
        }

        viewHolder.setVisibility(R.id.iv_item_list_lock, dataSource.isEncryptedWiFi() ? View.VISIBLE : View.GONE);}

    @Override
    protected void onBindEntityView(EntityViewHolder viewHolder, WifiInfoBean dataSource, int position, int viewType, List<Object> payloads) {
        super.onBindEntityView(viewHolder, dataSource, position, viewType, payloads);

    }


    private boolean isWifiConnecting(WifiInfoBean dataSource, ScanResult savedScanResult) {
        return savedScanResult != null && (dataSource.getScanResult().BSSID.equals(savedScanResult.BSSID) && dataSource.getScanResult().SSID.equals(savedScanResult.SSID));
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

    @Override
    public void onEntityClick(View view, int position) {
        mViewModel.mWifiRequest.requestSelect(position);
        mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_WIFI);

    }
}
