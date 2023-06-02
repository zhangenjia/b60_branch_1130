package com.adayo.app.crossinf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.crossinf.presenter.CarSettingManager;
import com.adayo.app.crossinf.ui.customview.CustomTabLayout;
import com.adayo.app.crossinf.ui.fragment.BaseFragment;
import com.adayo.app.crossinf.ui.fragment.EnvirFragment;
import com.adayo.app.crossinf.ui.fragment.VehicleInFragment;
import com.adayo.app.crossinf.ui.fragment.WadingFragment;
import com.adayo.app.crossinf.skin.CompatViewCreateListener;
import com.adayo.app.crossinf.skin.CrossInfoAttrHandler;
import com.adayo.app.crossinf.util.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.activity.ISkinActivity;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adayo.app.crossinf.util.Constant.CROSSINF;
import static com.adayo.app.crossinf.util.Constant.ENVIRONMENTALINFORMATION;
import static com.adayo.app.crossinf.util.Constant.VEHICLEINFORMATION;
import static com.adayo.app.crossinf.util.Constant.WADINGDETECTION;
import static com.adayo.app.crossinf.util.Constant.setProperty;

public class MainActivity extends AppCompatActivity implements ISkinActivity, View.OnClickListener, IAppControlListener {

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private static final String TAG = "CrossInfoActivity";
    private IActivitySkinEventHandler mSkinEventHandler;
    private RelativeLayout rl_activity;
    private TextView vehicleInfo;
    private TextView environInfo;
    private TextView wadingDetection;
    private FragmentManager supportFragmentManager;
    private ImageView iv_line_left;
    private ImageView iv_line_right;
    private List<Fragment> fragmentList = new ArrayList<>();
    private CustomTabLayout cv_tablayout;

    private VehicleInFragment mVehicleFragment;
    private EnvirFragment mEnvirFragment;
    private WadingFragment mWadingFragment;
    private int mWadingConfig;
    int[] recordTransaction = {0, 0, 0};
    private String value;
    private Bundle savedInstanceState;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        initSkinChange();
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setSystemUIVisibility(true);
        ActivityContentViewManager.getInstance().setActivity(this);
        initSourceManagerData();
        mWadingConfig = CarConfigManager.getInstance(this).isConfigWading_Induction_Sys();
        ActivityImpController.getInstance().addCallBack(this);
    }

    public void initMainInflate(final View view) {
        setContentView(view);
        Log.d(TAG, "initMainInflate: ");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initView(view);
                int tab = 0;
                if (value != null) {
                    tab = Integer.parseInt(value);

                }
                updateFragment(tab);
            }
        }, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("  start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        ActivityManager.getInstance().setAppVisibility(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        ActivityManager.getInstance().setAppVisibility(false);
    }

    /**
     * 覆盖systemUi导航栏
     */
    private void setSystemUIVisibility(boolean visibility) {
        View decorView = getWindow().getDecorView();
        if (visibility) {// 非全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏透明，可覆盖
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//导航栏透明，可覆盖
        } else {// 全屏
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 隐藏导航栏、状态栏
        }
    }

    private void initView(View view) {

        cv_tablayout = (CustomTabLayout) view.findViewById(R.id.cv_tablayout);
        cv_tablayout.initView(mWadingConfig);
        cv_tablayout.setVisibility(View.INVISIBLE);
        rl_activity = (RelativeLayout) view.findViewById(R.id.rl_activity);
        vehicleInfo = (TextView) view.findViewById(R.id.vehicleInfo);
        environInfo = (TextView) view.findViewById(R.id.environInfo);
        iv_line_left = (ImageView) view.findViewById(R.id.iv_line_left);
        vehicleInfo.setOnClickListener(this);
        environInfo.setOnClickListener(this);
//        iv_line_left.setVisibility(View.INVISIBLE);
        vehicleInfo.setSelected(true);
        environInfo.setSelected(false);
        AAOP_HSkin
                .with(vehicleInfo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector1)
                .applySkin(false);
        AAOP_HSkin
                .with(rl_activity)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.app_bg)
                .applySkin(false);

        if (checkWadingFunction()) {
            wadingDetection = (TextView) findViewById(R.id.wadingDetection);
            iv_line_right = (ImageView) findViewById(R.id.iv_line_right);
            wadingDetection.setOnClickListener(this);
            iv_line_right.setVisibility(View.VISIBLE);
            wadingDetection.setSelected(false);
            AAOP_HSkin
                    .with(wadingDetection)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector3)
                    .applySkin(false);
            AAOP_HSkin
                    .with(environInfo)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector2)
                    .applySkin(false);
        } else {
            AAOP_HSkin
                    .with(environInfo)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector3)
                    .applySkin(false);
        }
    }


    private void updateFragment(int currentTab) {
        fragmentList.clear();

        Log.d(TAG, "rj5d1jsf30j8s0f36h "+savedInstanceState);
        if (supportFragmentManager == null) {
            supportFragmentManager = getSupportFragmentManager();
        }

        if (null != savedInstanceState) {
            mVehicleFragment = (VehicleInFragment) getSupportFragmentManager().findFragmentByTag("1");
            if (mVehicleFragment == null) {
                mVehicleFragment = new VehicleInFragment();
            }
            fragmentList.add(mVehicleFragment);
        } else {
            Log.d(TAG, "updateFragment 4: " + "  ");
            mVehicleFragment = new VehicleInFragment();
            fragmentList.add(mVehicleFragment);
        }


        if (null != savedInstanceState) {
            mEnvirFragment = (EnvirFragment) getSupportFragmentManager().findFragmentByTag("2");
            if (mEnvirFragment == null) {
                mEnvirFragment = new EnvirFragment();
            }
            fragmentList.add(mEnvirFragment);
        } else {
            mEnvirFragment = new EnvirFragment();
            fragmentList.add(mEnvirFragment);
        }


        if (null != savedInstanceState) {
            mWadingFragment = (WadingFragment) getSupportFragmentManager().findFragmentByTag("3");
            if (mWadingFragment == null) {
                mWadingFragment = new WadingFragment();
            }
            fragmentList.add(mWadingFragment);
        } else {
            mWadingFragment = new WadingFragment();
            fragmentList.add(mWadingFragment);
        }


        switch (currentTab) {
            case 0:
                mVehicleFragment.registerOnResumeListener(new BaseFragment.OnFragmentResumeListener() {
                    @Override
                    public void onFragmentResume() {
                        cv_tablayout.setVisibility(View.VISIBLE);
                    }
                });
                vehicleInfo.setSelected(true);
                environInfo.setSelected(false);
                if (checkWadingFunction()) {
                    wadingDetection.setSelected(false);
                }

                break;
            case 1:
                mEnvirFragment.registerOnResumeListener(new BaseFragment.OnFragmentResumeListener() {
                    @Override
                    public void onFragmentResume() {
                        cv_tablayout.setVisibility(View.VISIBLE);
                    }
                });
                vehicleInfo.setSelected(false);
                environInfo.setSelected(true);
                if (checkWadingFunction()) {
                    wadingDetection.setSelected(false);
                }
                break;
            case 2:
                mWadingFragment.registerOnResumeListener(new BaseFragment.OnFragmentResumeListener() {
                    @Override
                    public void onFragmentResume() {
                        cv_tablayout.setVisibility(View.VISIBLE);
                    }
                });
                vehicleInfo.setSelected(false);
                environInfo.setSelected(false);
                if (checkWadingFunction()) {
                    wadingDetection.setSelected(true);
                }
                break;
        }

        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Log.d(TAG, "updateFragment: size" + fragmentList.size());
        fragmentTransaction.add(R.id.fl_layout, fragmentList.get(currentTab), "1");
        recordTransaction[currentTab] = 1;
        fragmentTransaction.commitAllowingStateLoss();
        ActivityManager.getInstance().setAppVisibility(true);
    }


    private void initSourceManagerData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        Map map = (HashMap) bundle.get("map");
        if (map == null) {
            return;
        }
        if (map != null) {
            init(map);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(" intent =  " + intent);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            LogUtil.d(" bundle =  " + bundle);
            if (bundle != null) {
                Map map = (HashMap) bundle.get("map");
                if (map != null) {
                    init(map);
                }
            }
        }
    }

    private void init(Map map) {
        value = (String) map.get(CROSSINF);
        LogUtil.d(" value ======> " + value);
        if (value != null) {
            if (VEHICLEINFORMATION.equals(value)) {
                showVehicleInfo();
            } else if (ENVIRONMENTALINFORMATION.equals(value)) {
                showEnvironInfo();
            } else if (WADINGDETECTION.equals(value)) {
                showWadingDetection();
            }

        }


    }

    public void setHandler(Handler handler) {
        LogUtil.d("");
        CarSettingManager communication = CarSettingManager.getInstance();
        communication.setHandler(handler);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        vehicleInfo.setText(R.string.tab1);
        environInfo.setText(R.string.tab2);
        if (wadingDetection != null) {
            wadingDetection.setText(R.string.tab3);
        }


    }

    @Override
    protected void onDestroy() {//todo 如果销毁页面没走这个方法会有哪些bug
        LogUtil.d("onDestroy");
        super.onDestroy();
        ActivityManager.getInstance().setAppVisibility(false);
    }

    @Override
    protected void onPause() { //弹出弹窗会有bug 弹出弹窗也会走onPause
        super.onPause();
        overridePendingTransition(0, 0);
        LogUtil.d("onPause");
    }


    private void initSkinChange() {
        AAOP_HSkinHelper
                .init(getApplicationContext(), true, "AdayoCrossInfo");

        AAOP_HSkin
                .getInstance()
                .registerSkinAttrHandler("pichangle", new CrossInfoAttrHandler());

        mSkinEventHandler = AAOP_HSkin.newActivitySkinEventHandler()
                .setSwitchSkinImmediately(isSwitchSkinImmediately())
                .setSupportSkinChange(isSupportSkinChange())
                .setWindowBackgroundResource(getWindowBackgroundResource())
                .setNeedDelegateViewCreate(isNeedDelegateViewCreate());
        //如果继承 AppCompatActivity ，需要打开下面注释
        mSkinEventHandler.setViewCreateListener(new CompatViewCreateListener(this));
        mSkinEventHandler.onCreate(this);
        LogUtil.d("");
    }

    @Override
    public boolean isSwitchSkinImmediately() {
        return true;
    }

    @Override
    public boolean isSupportSkinChange() {
        return true;
    }

    @Override
    public boolean isNeedDelegateViewCreate() {
        return true;
    }

    @Override
    public int getWindowBackgroundResource() {
        return -1;
    }

    @Override
    public void handleSkinChange() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicleInfo:
                showVehicleInfo();
                break;
            case R.id.environInfo:
                showEnvironInfo();
                break;
            case R.id.wadingDetection:
                showWadingDetection();
                break;
        }
    }

    private void showVehicleInfo() {
        Log.d(TAG, "showVehicleInfo: ");
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (recordTransaction[0] == 0) {
            Log.d(TAG, "fragmentTransaction.add 222: ");
            fragmentTransaction.add(R.id.fl_layout, fragmentList.get(0), "1");
            recordTransaction[0] = 1;
        }
        iv_line_left.setVisibility(View.INVISIBLE);
        vehicleInfo.setSelected(true);
        environInfo.setSelected(false);
        if (checkWadingFunction()) {
            iv_line_right.setVisibility(View.VISIBLE);
            wadingDetection.setSelected(false);
        }
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == 0) {
                fragmentTransaction.show(fragmentList.get(i));
            } else {
                fragmentTransaction.hide(fragmentList.get(i));
            }
        }
        fragmentTransaction.commit();
        setProperty("crossinf", VEHICLEINFORMATION);
    }

    private void showEnvironInfo() {
        Log.d(TAG, "showEnvironInfo: ");
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

        if (recordTransaction[1] == 0) {
            fragmentTransaction.add(R.id.fl_layout, fragmentList.get(1), "2");
            recordTransaction[1] = 1;
        }
        iv_line_left.setVisibility(View.INVISIBLE);
        vehicleInfo.setSelected(false);
        environInfo.setSelected(true);
        if (checkWadingFunction()) {
            iv_line_right.setVisibility(View.INVISIBLE);
            wadingDetection.setSelected(false);
        }
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == 1) {
                fragmentTransaction.show(fragmentList.get(i));
            } else {
                fragmentTransaction.hide(fragmentList.get(i));
            }
        }
        fragmentTransaction.commit();
        setProperty("crossinf", ENVIRONMENTALINFORMATION);
    }

    private void showWadingDetection() {
        LogUtil.d("showWadingDetection");
        if (fragmentList.size() < 2) {
            return;
        }
        if (supportFragmentManager == null) {
            supportFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (recordTransaction[2] == 0) {
            fragmentTransaction.add(R.id.fl_layout, fragmentList.get(2), "3");
            recordTransaction[2] = 1;
        }
        iv_line_left.setVisibility(View.VISIBLE);
        iv_line_right.setVisibility(View.INVISIBLE);

        vehicleInfo.setSelected(false);
        environInfo.setSelected(false);
        wadingDetection.setSelected(true);
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == 2) {
                fragmentTransaction.show(fragmentList.get(i));
            } else {
                fragmentTransaction.hide(fragmentList.get(i));
            }
        }
        fragmentTransaction.commit();
        setProperty("crossinf", WADINGDETECTION);
    }


    @Override
    public void switchWadding() {
        Log.d(TAG, "launchWadingFragment: ");
        showWadingDetection();
    }

    @Override
    public void finishWadding() {
        Log.d(TAG, "finishApp: ");
        moveTaskToBack(true);
    }

    private boolean checkWadingFunction() {
        return mWadingConfig == 1;
    }


}
