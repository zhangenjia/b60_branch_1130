package com.adayo.app.btphone.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTConnectManager;
import com.adayo.app.btphone.callback.IBTConnectCallback;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.app.btphone.ui.fragment.CallLogFragment;
import com.adayo.app.btphone.ui.fragment.LinkManFragment;
import com.adayo.app.btphone.ui.fragment.KeyboardFragment;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends Activity {

    private static final String TAG = Constant.TAG + MainActivity.class.getSimpleName();

    private IActivitySkinEventHandler mSkinEventHandler;

    private BTConnectManager mBTConnectManager;
    private boolean mHFTState;  //电话
    private boolean mA2DPState; //音频

    private LinearLayout mBTNotConnectLL;
    private RelativeLayout mBTConnnectRL;
    private FrameLayout mFragmentContentFL;
    private FrameLayout mKeyboardFL;
    private LinearLayout mBTPhoneNotConnectLL;
    private TextView mToSetting;
    private TextView mContactsTV;
    private TextView mCallLogTV;
    private TextView mDeviceNameTV;
    private TextView mBtNotConnectTV;
    private TextView mToSettingPageTV;
    private TextView mCurrentDeviceTV;
    private TextView mBtPhoneNotConnectTV;
    private TextView mOpenPhoneSwitchTV;
    private ImageView mTopLineIV;

    private LinkManFragment mLinkManFragment;
    private CallLogFragment mCallLogFragment;
    private KeyboardFragment mKeyboardFragment;
    private String mBTDeviceName;
    private Handler mHandler;
    private RelativeLayout deviceNameRL;

    private static final String LINK_MAN = "linkman";
    private static final String CALL_LOG = "calllog";
    private static final String BT_DEVICE_NAME = "btDeviceName";
    private int linkManOrCallLog;

    private int lastConnState = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSkinEventHandler = AAOP_HSkinHelper.initActivity(this, new ActivityViewCreateListener(this));
        setContentView(R.layout.activity_main);
        onSystemUIVisibility(true);
        initManager();
        initView();
        initFragment(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mBTConnectManager) {
            mHFTState = mBTConnectManager.getHFPState();
            mA2DPState = mBTConnectManager.getA2DPState();
        }
        showPage();
        changeKeyboardDialogSkin();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
        if (null != mCallLogFragment && null != mLinkManFragment) {
            mCallLogFragment.hideInputMethod();
            mLinkManFragment.hideInputMethod();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                Map map = (HashMap) bundle.get("map");
                if (null != map) {
                    String page = (String) map.get("page");
                    Log.i(TAG, "onNewIntent page = " + page);
                    if (null != page && LINK_MAN.equals(page)) {
                        switchContactsOrCallLog(Constant.LINK_MAN);
                    } else if (null != page && CALL_LOG.equals(page)) {
                        switchContactsOrCallLog(Constant.CALL_LOG);
                    }
                }
            }
        }
    }

    public void initView() {
        mBTNotConnectLL = findViewById(R.id.bt_not_connect_ll);
        mBTConnnectRL = findViewById(R.id.bt_connect_rl);
        mFragmentContentFL = findViewById(R.id.fragment_container_fl);
        mKeyboardFL = findViewById(R.id.fragment_keyBoard_fl);
        mBTPhoneNotConnectLL = findViewById(R.id.bt_phone_not_connect_ll);
        mToSetting = findViewById(R.id.to_setting_tv);
        deviceNameRL = findViewById(R.id.device_name_rl);
        mTopLineIV = findViewById(R.id.top_line);

        mToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("setting_page", "bt_phone");
                    SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, map,
                            AppConfigType.SourceSwitch.APP_ON.getValue(),
                            AppConfigType.SourceType.UI.getValue());
                    SrcMngSwitchManager.getInstance().requestSwitchApp(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mContactsTV = findViewById(R.id.contacts_tv);
        mCallLogTV = findViewById(R.id.calllog_tv);
        mContactsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchContactsOrCallLog(Constant.LINK_MAN);
            }
        });
        mCallLogTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchContactsOrCallLog(Constant.CALL_LOG);
            }
        });
        mDeviceNameTV = findViewById(R.id.device_name_tv);
        mBtNotConnectTV = findViewById(R.id.bt_not_connect);
        mToSettingPageTV = findViewById(R.id.to_setting_page);
        mCurrentDeviceTV = findViewById(R.id.current_device);
        mBtPhoneNotConnectTV = findViewById(R.id.bt_phone_not_connect);
        mOpenPhoneSwitchTV = findViewById(R.id.open_phone_switch);
    }

    public void initFragment(final Bundle savedInstanceState) {
        Log.i(TAG, "initFragment savedInstanceState = " + savedInstanceState);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    if ("isShow".equals(bundle.getString("keyBoardDialog", ""))) {
                        noTabSelectedBg(true);
                    } else {
                        noTabSelectedBg(false);
                    }

                } else {
                    Bundle bundle = msg.getData();
                    if (null == bundle.get(BT_DEVICE_NAME)) {
                        mBTDeviceName = "";
                    } else {
                        mBTDeviceName = Objects.requireNonNull(bundle.get("btDeviceName")).toString();
                    }
                    if (!"".equals(mBTDeviceName)) {
                        mDeviceNameTV.setText(mBTDeviceName);
                    }
                }
                return false;
            }
        });
        DialogManager.getInstance().setHandler(mHandler);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (null != savedInstanceState) {
                    mLinkManFragment = (LinkManFragment) getFragmentManager().findFragmentByTag("linkManFragment");
                    mCallLogFragment = (CallLogFragment) getFragmentManager().findFragmentByTag("collLogFragment");
                    mCallLogFragment.setContext(MainActivity.this);
                    mKeyboardFragment = (KeyboardFragment) getFragmentManager().findFragmentByTag("keyboardFragment");
                    mKeyboardFragment.setHandler(mHandler);
                } else {
                    mLinkManFragment = new LinkManFragment(mHandler, MainActivity.this);
                    mCallLogFragment = new CallLogFragment(mHandler, MainActivity.this);
                    mKeyboardFragment = new KeyboardFragment(mHandler);
                    FragmentTransaction mFragmentTran = getFragmentManager().beginTransaction();
                    mFragmentTran.add(R.id.fragment_container_fl, mLinkManFragment, "linkManFragment");
                    mFragmentTran.add(R.id.fragment_container_fl, mCallLogFragment, "collLogFragment");
                    mFragmentTran.add(R.id.fragment_keyBoard_fl, mKeyboardFragment, "keyboardFragment");
                    mFragmentTran.commitAllowingStateLoss();
                }
                switchContactsOrCallLog(Constant.LINK_MAN);
            }
        });
    }

    public void initManager() {
        mBTConnectManager = new BTConnectManager(getApplicationContext());
        mBTConnectManager.registerBTConnectState(callback);
    }

    private IBTConnectCallback callback = new IBTConnectCallback() {
        @Override
        public void updateHFPState(boolean state) {
            mHFTState = state;
            showPage();
        }

        @Override
        public void updateA2DPState(boolean state) {
            mA2DPState = state;
            showPage();
        }
    };

    private void showPage() {
        Log.d(TAG, "showPage  mHFTState = " + mHFTState + " mA2DPState = " + mA2DPState);
        int connState = -1;
        //通话连接
        if (mHFTState) {
            connState = Constant.CONN_STATE_BT_CONNECTED;
        } else {
            //电话不连接 音乐连接
            if (mA2DPState) {
                connState = Constant.CONN_STATE_PHONE_NOT_CONNECTED_AUDIO_CONNECTED;
                //电话不连接 音乐不连接
            } else {
                connState = Constant.CONN_STATE_BT_NOT_CONNECTED;
            }
            if (null != mKeyboardFragment) {
                mKeyboardFragment.notifyBTNotConnect();
            }
        }
        Log.d(TAG, "showPage  lastConnState = " + lastConnState + " connState =" + connState);
        if (lastConnState == connState) {
            return;
        } else {
            Log.d(TAG, "showPage  1111");
            lastConnState = connState;
            if (lastConnState == Constant.CONN_STATE_BT_CONNECTED) {
                mBTNotConnectLL.setVisibility(View.GONE);// 蓝牙未连接 请前往系统设置-蓝牙界面，进行设置
                mBTConnnectRL.setVisibility(View.VISIBLE);// 联系人通话记录
                mFragmentContentFL.setVisibility(View.VISIBLE);// 通话列表fragment
                mBTPhoneNotConnectLL.setVisibility(View.GONE);// 蓝牙电话未连接 请打开手机端-已连接设备的“通话”开关
                noTabSelectedBg(false);
                if (null != mKeyboardFragment) {
                    mKeyboardFragment.notifyBTPhoneConnectState(true);
                }
                mContactsTV.setClickable(true);
                mCallLogTV.setClickable(true);
                mDeviceNameTV.setVisibility(View.VISIBLE);
                deviceNameRL.setVisibility(View.VISIBLE);
            } else if (lastConnState == Constant.CONN_STATE_PHONE_NOT_CONNECTED_AUDIO_CONNECTED) {
                mBTNotConnectLL.setVisibility(View.GONE);
                mBTConnnectRL.setVisibility(View.VISIBLE);
                mFragmentContentFL.setVisibility(View.GONE);
                mBTPhoneNotConnectLL.setVisibility(View.VISIBLE);
                noTabSelectedBg(true);
                if (null != mKeyboardFragment) {
                    mKeyboardFragment.notifyBTPhoneConnectState(false);
                }
                mContactsTV.setClickable(false);
                mCallLogTV.setClickable(false);
                mDeviceNameTV.setVisibility(View.INVISIBLE);
                deviceNameRL.setVisibility(View.VISIBLE);
            } else if (lastConnState == Constant.CONN_STATE_BT_NOT_CONNECTED) {
                mBTNotConnectLL.setVisibility(View.VISIBLE);
                mBTConnnectRL.setVisibility(View.GONE);
            }
        }
    }

    private void switchContactsOrCallLog(int contactsOrCallLog) {
        linkManOrCallLog = contactsOrCallLog;
        DialogManager.getInstance().dismissKeyboardDialog();
        switch (contactsOrCallLog) {
            case Constant.LINK_MAN:
                mCallLogTV.setSelected(false);
                mContactsTV.setSelected(true);
                FragmentTransaction contacts = getFragmentManager().beginTransaction();
                contacts.show(mLinkManFragment).hide(mCallLogFragment).commitAllowingStateLoss();
                break;
            case Constant.CALL_LOG:
                mContactsTV.setSelected(false);
                mCallLogTV.setSelected(true);
                FragmentTransaction callLog = getFragmentManager().beginTransaction();
                callLog.show(mCallLogFragment).hide(mLinkManFragment).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    private void onSystemUIVisibility(boolean visibility) {
        Log.d(TAG, "onTabItemSelected():visibility = " + visibility);
        View decorView = getWindow().getDecorView();
        if (visibility) {// 非全屏
            Log.i(TAG, "设置SystemUI 显示");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//导航栏不占位
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏透明
            getWindow().setNavigationBarColor(Color.TRANSPARENT);

        } else {// 全屏
            Log.i(TAG, "设置SystemUI 隐藏");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 隐藏导航栏、状态栏
        }
    }

    private static class ActivityViewCreateListener implements IViewCreateListener {
        WeakReference<MainActivity> activityWeakReference;

        public ActivityViewCreateListener(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public View beforeCreate(View parent, String name, Context context, AttributeSet attrs) {
            MainActivity activity = activityWeakReference.get();
            if (null == activity) {
                return null;
            }
//            return activity.getDelegate().createView(parent, name, context, attrs);
            //如果上面代码爆红，就把上面的代码删掉，用下面的
            return activity.onCreateView(parent, name, context, attrs);
        }

        @Override
        public void afterCreated(View view, String s, Context context, AttributeSet attributeSet) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != mCallLogFragment && null != mLinkManFragment) {
            mCallLogFragment.hideInputMethod();
            mLinkManFragment.hideInputMethod();
        }
        return super.onTouchEvent(event);
    }

    private void changeKeyboardDialogSkin() {
        if (null != DialogManager.getInstance().getKeyboardDialog(this) &&
                DialogManager.getInstance().getKeyboardDialog(this).isShowing()) {
            RelativeLayout rl = DialogManager.getInstance().getKeyboardDialog(this).findViewById(R.id.rl);
            AAOP_HSkin.with(rl)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_letter1)
                    .applySkin(true);
        }
    }

    private void noTabSelectedBg(boolean flag){
        if (flag) {
            mContactsTV.setSelected(false);
            mCallLogTV.setSelected(false);
            mTopLineIV.setVisibility(View.VISIBLE);
        } else {
            if(lastConnState == Constant.CONN_STATE_PHONE_NOT_CONNECTED_AUDIO_CONNECTED){
                return;
            }
            mTopLineIV.setVisibility(View.GONE);
            if (linkManOrCallLog == Constant.LINK_MAN) {
                mContactsTV.setSelected(true);
            } else {
                mCallLogTV.setSelected(true);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToSetting.setText(R.string.go);
        mContactsTV.setText(R.string.contact);
        mCallLogTV.setText(R.string.calllog);
        mBtNotConnectTV.setText(R.string.bt_not_connect);
        mToSettingPageTV.setText(R.string.to_setting_page);
        mCurrentDeviceTV.setText(R.string.current_device);
        mBtPhoneNotConnectTV.setText(R.string.bt_phone_not_connect);
        mOpenPhoneSwitchTV.setText(R.string.open_phone_switch);

    }
}
