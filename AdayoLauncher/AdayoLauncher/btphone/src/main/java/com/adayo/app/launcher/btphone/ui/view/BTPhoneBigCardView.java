package com.adayo.app.launcher.btphone.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.launcher.btphone.R;
import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI;

public class BTPhoneBigCardView extends RelativeLayout implements View.OnClickListener, IViewBase {

    private static final String TAG = BTPhoneBigCardView.class.getSimpleName();

    private static final int SHARE_BLUETOOTH = 27;
    private Context mContext;
    private View mCardView;
    private TextView mDeviceName;
    private RelativeLayout mLinkManAndCallLogRL;
    private ImageView mLinkManIV;
    private ImageView mCallLogIV;

    private ShareDataManager mShareDataMgr;
    private ImageView iv_bt_frame;
    private TextView device_name_tv;
    private TextView tv_bigcardname;
    private BlurTransitionView bt_blur;
    private RelativeLayout parent_layout;

    public BTPhoneBigCardView(Context context) {
        super(context);
        mContext = context;
        initView();
        mShareDataMgr = ShareDataManager.getShareDataManager();
        mShareDataMgr.registerShareDataListener(SHARE_BLUETOOTH, mCallBack);
        String s = mShareDataMgr.getShareData(SHARE_BLUETOOTH);
        btConnectState(s);
    }

    public BTPhoneBigCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private void initView() {
        mCardView = LayoutInflater.from(mContext).inflate(R.layout.big_card_layout, null);
        parent_layout = (RelativeLayout) mCardView.findViewById(R.id.parent_layout);
        iv_bt_frame = (ImageView) mCardView.findViewById(R.id.iv_bt_frame);
        bt_blur = (BlurTransitionView) mCardView.findViewById(R.id.bt_blur);
        mDeviceName = mCardView.findViewById(R.id.device_name_tv);
        mLinkManAndCallLogRL = mCardView.findViewById(R.id.linkman_calllog_rl);
        mLinkManIV = mCardView.findViewById(R.id.linkman_iv);
        mLinkManIV.setOnClickListener(this);
        mCallLogIV = mCardView.findViewById(R.id.call_log_iv);
        mCallLogIV.setOnClickListener(this);
        device_name_tv = (TextView) mCardView.findViewById(R.id.device_name_tv);
        tv_bigcardname = (TextView) mCardView.findViewById(R.id.tv_bigcardname);
        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(mCardView);
        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(parent_layout, true);
        AAOP_HSkin.with(iv_bt_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                .applySkin(false);
    }

    @Override
    public void onClick(View v) {
        Map<String, String> map = new HashMap<>();
        if (v.getId() == R.id.linkman_iv) {
            map.put("page", "linkman");
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_PHONE, map, AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
            SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        } else if (v.getId() == R.id.call_log_iv) {
            map.put("page", "calllog");
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_PHONE, map, AppConfigType.SourceSwitch.APP_ON.getValue(), UI.getValue());
            SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        }
    }

    IShareDataListener mCallBack = new IShareDataListener() {
        @Override
        public void notifyShareData(int i, String s) {
            btConnectState(s);
        }
    };



    @Override
    public View initCardView(String id, String type, String type1) {
        Log.i(TAG, "initCardView type = " + type);

        if (type.equals(TYPE_BIGCARD)) {
            WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
                @Override
                public void setWallPaper(Bitmap bitmap) {
                    bt_blur.setBitmap(5, 0.5f);
                    bt_blur.show(0);
                }

                @Override
                public void resumeDefault() {
                    bt_blur.setBitmap(5, 0.5f);
                    bt_blur.show(0);
                }

                @Override
                public void deleteWallPaper() {
                    bt_blur.setBitmap(5, 0.5f);
                    bt_blur.show(0);
                }


            });

            return mCardView;//返回大卡
        } else if (type.equals(TYPE_SMALLCARD)) {
            //返回小卡
        }
        if (type1.equals(DEFAULT_INITCARD)){

        }else if (type1.equals(DEFAULT_INITCARD)){

        }

            return null;
    }


    @Override
    public void unInitCardView(String id, String type) {

    }

    @Override
    public void releaseResource() {

    }

    /**
     * 播小卡动画
     *
     * @param id
     */
    @Override
    public void playAnimation(String id, int delay) {

    }

    @Override
    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: big");
        if (device_name_tv != null) {
            device_name_tv.setText(mContext.getResources().getString(R.string.connect_bt_device));
        }
        if (tv_bigcardname != null) {
            tv_bigcardname.setText(mContext.getResources().getString(R.string.phone));
        }
    }

    @Override
    public void launcherLoadComplete() {
        Log.d(TAG, "launcherLoadComplete: weather");
        bt_blur.post(new Runnable() {
            @Override
            public void run() {
                bt_blur.setBitmap(5, 0.5f);
                bt_blur.show(300);
            }
        });

    }

    @Override
    public void launcherAnimationUpdate(int i) {
        if (i == 1) {
            bt_blur.show(300);
        } else {
            if (bt_blur.getIsShow()) {
                bt_blur.hide(0);
            }
        }
    }

    public void btConnectState(String s) {
        if (s == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject(s);
            if (object == null) {
                return;
            }
            boolean connect = object.optBoolean("is_hfp_connected", false);
            if (connect) {
                String deviceName = object.optString("phoneName");
                mLinkManAndCallLogRL.setVisibility(View.VISIBLE);
                mDeviceName.setText(deviceName);
            } else {
                mLinkManAndCallLogRL.setVisibility(View.GONE);
                mDeviceName.setText(getResources().getText(R.string.connect_bt_device));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}