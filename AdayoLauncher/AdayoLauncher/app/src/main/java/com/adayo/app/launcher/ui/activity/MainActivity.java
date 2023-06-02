package com.adayo.app.launcher.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.communicationbase.SouceUtil;
import com.adayo.app.launcher.model.adapter.LauncherViewPagerAdapter;
import com.adayo.app.launcher.offroadinfo.presenter.ISkinChangeListener;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.presenter.listener.ConfigurationChangeImpl;
import com.adayo.app.launcher.presenter.listener.TouchImpl;
import com.adayo.app.launcher.presenter.manager.WindowsControllerImpl;
import com.adayo.app.launcher.presenter.manager.WindowsManager;
import com.adayo.app.launcher.presenter.shareinfo.IShareInterface;
import com.adayo.app.launcher.skin.CompatViewCreateListener;
import com.adayo.app.launcher.skin.MyAttrHandler;
import com.adayo.app.launcher.ui.fragment.AllAppFragment;
import com.adayo.app.launcher.ui.fragment.DefaultFragment;
import com.adayo.app.launcher.ui.view.CustomViewPager;
import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.util.LogUtil;
import com.adayo.app.launcher.util.MyBroadcastReceiver;
import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.app.launcher.util.SystemPropertiesUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.activity.ISkinActivity;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.proxy.infrastructure.share.ShareDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.util.MyConstantsUtil.AppTAG;
import static com.adayo.app.launcher.util.MyConstantsUtil.VEHICLECONFIGUREDKEY;

import warning.LauncherApplication;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, ISkinActivity {
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }};
    private static String TAG = MainActivity.class.getSimpleName();
    private List<Fragment> fragmentList;
    private CustomViewPager vp_launcher;
    private FragmentManager supportFragmentManager;
    private DefaultFragment defaultFragment;
    private LinearLayout ll_point_parent;
    private float mZeroPointFive = 0.5f;
    private ConfigurationChangeImpl mConfigurationChangeImpl;
    private IActivitySkinEventHandler mSkinEventHandler;
    public static final int INVALID = -1;
    private boolean mFirstTimeApplySkin = true;
    private RelativeLayout rl_root;
    private ImageView iv_point_left;
    private ImageView iv_point_right;
    private ImageView imgBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(AppTAG + TAG, "onCreate===>");
        initSkinChange();//皮肤
        SystemUIConfig(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initShareInfo();
        initCarConfiguration();
        initView();

        mConfigurationChangeImpl = ConfigurationChangeImpl.getInstance();
//
        WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
            @Override
            public void setWallPaper(Bitmap bitmap) {
                if (bitmap != null) {//todo 设置壁纸
                    //将图片显示到ImageView中
                    imgBg.setImageBitmap(bitmap);
                } else {
                    if (CURRENT_THEME==1){
                        imgBg.setImageResource(R.mipmap.app_bg);
                    }else {
                        imgBg.setImageResource(R.mipmap.app_bg_2);
                    }

                }
            }

            @Override
            public void resumeDefault() {
//                imgBg.setImageResource(R.drawable.bg_theme);
                imgBg.setImageResource(0);

                AAOP_HSkin.with(imgBg)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_theme)
                        .applySkin(false);
            }

            @Override
            public void deleteWallPaper() {

            }

        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String fileName = SystemPropertiesUtil.getInstance().getStringMethod("persist.wallpaperType", "");
                String localResourcePath = SouceUtil.getLocalResourcePath(LauncherApplication.getContext());
                Log.d(TAG, "fileName : "+fileName+"   "+localResourcePath);
                if (!"".equals(fileName)){
                    //判断sdcard是否存在
                    Bitmap bm = BitmapFactory.decodeFile(SouceUtil.getLocalResourcePath(LauncherApplication.getContext())+"/"+fileName);
                    //将图片显示到ImageView中
                    WrapperUtil.getInstance().setWallPaper(bm);
                }else {
//                    if (CURRENT_THEME==1){
//                        imgBg.setImageResource(R.mipmap.app_bg);
//                    }else {
//                        imgBg.setImageResource(R.mipmap.app_bg_2);
//                    }
                }
            }
        },0);

//        if (wallpaperType)
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.broadcast.home.bgchange");
        registerReceiver(myBroadcastReceiver, filter);

    }


    private void initShareInfo() {
        IShareInterface iShareInterface = new IShareInterface();
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        shareDataManager.registerShareDataListener(14, iShareInterface);
    }

    private void initSkinChange() {
        //TODO move to application
//        AAOP_HSkin
//                .getInstance()
//                .registerSkinAttrHandler("lottie", new MyAttrHandler());
//        AAOP_HSkinHelper
//                .init(getApplicationContext(), true, "AdayoLauncher");
        mSkinEventHandler = AAOP_HSkin.newActivitySkinEventHandler()
                .setSwitchSkinImmediately(isSwitchSkinImmediately())
                .setSupportSkinChange(isSupportSkinChange())
                .setWindowBackgroundResource(getWindowBackgroundResource())
                .setNeedDelegateViewCreate(isNeedDelegateViewCreate());
        //如果继承 AppCompatActivity ，需要打开下面注释
        mSkinEventHandler.setViewCreateListener(new CompatViewCreateListener(this));
        mSkinEventHandler.onCreate(this);
    }

    /**
     * 覆盖systemUi导航栏
     */
    private void SystemUIConfig(boolean visibility) {
        View decorView = getWindow().getDecorView();
        Window window = getWindow();
        if (visibility) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//导航栏透明，可覆盖
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 隐藏导航栏、状态栏
        }
    }


    //todo 异常情况考虑一下Launcher意外崩溃是否对此有影响
    private void initCarConfiguration() {
        SystemPropertiesUtil systemPropertiesUtil = SystemPropertiesUtil.getInstance();
        String configured = systemPropertiesUtil.getStringMethod(VEHICLECONFIGUREDKEY, "");//获取上一次开机的
        ConfigFunction configFunction = ConfigFunction.getInstance(this);
        String offLineConfiguration = configFunction.getOffLineConfiguration();//低配还是高配
        String apaConfigured = configFunction.isApaConfigured() == true ? "1" : "0";//本次
        String avmConfigured = configFunction.isAvmConfigured() == true ? "1" : "0";//本次
        String dvrConfigured = configFunction.isDvrConfigured() == true ? "1" : "0";
        LogUtil.d(AppTAG + TAG, " configured = " + configured + "apaConfigured = " + apaConfigured + "avmConfigured = " + avmConfigured + " dvrConfigured =" + dvrConfigured);
        if (configured.equals("")) { //首次
            systemPropertiesUtil.setProperty(VEHICLECONFIGUREDKEY, (offLineConfiguration + apaConfigured + avmConfigured + dvrConfigured));//存储本次配置
        } else if (!configured.equals(offLineConfiguration + apaConfigured + avmConfigured + dvrConfigured)) {//如果配置字改变了,那么清空之前存的卡片顺序的系统属性
            systemPropertiesUtil.setProperty(MyConstantsUtil.LAUNCHER_ALLAPPCARD_KEY, "");//清空allapp卡片顺序
            systemPropertiesUtil.setProperty(MyConstantsUtil.LAUNCHER_BIGCARD_KEY, "");//清空顶部大卡片顺序
            systemPropertiesUtil.setProperty(MyConstantsUtil.LAUNCHER_SMALLCARD_KEY, "");//清空顶部小卡片顺序
            systemPropertiesUtil.setProperty(MyConstantsUtil.LAUNCHER_BOTTOM_BIGCARD_KEY, "");//清空底部弹框大卡片顺序
            systemPropertiesUtil.setProperty(MyConstantsUtil.LAUNCHER_BOTTOM_SMALLCARD_KEY, "");//清空底部弹框小卡片顺序
            systemPropertiesUtil.setProperty(VEHICLECONFIGUREDKEY, (offLineConfiguration + apaConfigured + avmConfigured + dvrConfigured));//存储本次配置
        }
    }

    /**
     * 初始化View
     *
     * @param
     */
    protected void initView() {
        vp_launcher = (CustomViewPager) findViewById(R.id.vp_launcher);
        ll_point_parent = (LinearLayout) findViewById(R.id.ll_point_parent);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        iv_point_left = (ImageView) findViewById(R.id.iv_point_left);
        iv_point_right = (ImageView) findViewById(R.id.iv_point_right);
        imgBg = (ImageView) findViewById(R.id.imgBg);

        initViewPager();
        AAOP_HSkin.with(ll_point_parent).addViewAttrs(new DynamicAttr("lottie")).applySkin(false);


        AAOP_HSkin.with(imgBg)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_theme)
                .applySkin(false);
    }


    /**
     * 初始化View-ViewPager
     *
     * @param
     */
    private void initViewPager() {
        iv_point_left.setSelected(true);
        iv_point_right.setSelected(false);
        fragmentList = new ArrayList<>();
        defaultFragment = new DefaultFragment();
        fragmentList.add(defaultFragment);
        fragmentList.add(new AllAppFragment());
        vp_launcher.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (v <= 0) {
                    return;
                }

                Log.d(TAG, "onPageScrolled: v = " + v);
                defaultFragment.ViewPagerChange(i1);//高斯模糊
                if (v <= mZeroPointFive) {
                    vp_launcher.setAlpha(1 - v);
                } else {
                    vp_launcher.setAlpha(v);
                }
            }

            @Override
            public void onPageSelected(int i) {//切换ViewPager页码回调
                Log.d(TAG, "onPageScrolled: v bbb= ");
                getPoint(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.d(TAG, "onPageScrolled: v ccc= " + i);
                if (i == 0) {
                    vp_launcher.setAlpha(1);
                }
            }
        });
        supportFragmentManager = getSupportFragmentManager();
        vp_launcher.setAdapter(new LauncherViewPagerAdapter(supportFragmentManager, fragmentList));
    }

    /**
     * View-ViewPager页码切换回调，设置Launcher页码圆点
     *
     * @param index
     */
    private void getPoint(int index) {
        if (index == 0) {

            iv_point_left.setSelected(true);
            iv_point_right.setSelected(false);
        } else if (index == 1) {

            iv_point_left.setSelected(false);
            iv_point_right.setSelected(true);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;

    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int i = WindowsManager.getInstance().geBottomDialogVisibility();
        Log.d(TAG, "dispatchTouchEventasdfasdf: " + i);
        if (i == 0) {
            vp_launcher.setIsScanScroll(false);
            return super.dispatchTouchEvent(ev);
        }
        try {
            if (TouchImpl.getInstance().onTouchEvent(ev)) {
                vp_launcher.setIsScanScroll(false);
            } else {
                vp_launcher.setIsScanScroll(super.dispatchTouchEvent(ev));
            }
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d(TAG, "");
        super.onConfigurationChanged(newConfig);
        mConfigurationChangeImpl.configurationChange();
    }


    @Override
    protected void onRestart() {
        LogUtil.d(TAG, "");
        super.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            LogUtil.d(TAG, "intent != null: ");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                LogUtil.d(TAG, "bundle != null: ");
                Map map = (HashMap) bundle.get("map");
                if (map != null) {
                    String value = (String) map.get("PARAMETER_KEY");
                    LogUtil.d(TAG, "map != null: ");
                    if (value == null) {
                        LogUtil.d(TAG, "value == null: ");
                        return;
                    }
                    Log.d(TAG, "onPageScrolled: v === ");
                    if (value.equals("APPS")) {
                        vp_launcher.setCurrentItem(1);
                        WindowsControllerImpl.getInstance().notifyCallbacks(View.GONE);
                    } else if (value.equals("CART")) {
                        defaultFragment.ViewPagerChange(0);
                        vp_launcher.setCurrentItem(0);
                    }
                }
            }
        }
        LogUtil.d(TAG, "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            LogUtil.d(TAG, "");
            vp_launcher.setCurrentItem(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * ============================================================================================>skin start
     */
    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "");
        mSkinEventHandler.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //皮肤相关，此通知放在此处，尽量让子类的view都添加到view树内
        if (mFirstTimeApplySkin) {
            mSkinEventHandler.onViewCreated();
            mFirstTimeApplySkin = false;
        }
        mSkinEventHandler.onResume();
        LogUtil.d(TAG, "");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.d(TAG, "");
        mSkinEventHandler.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "");
        mSkinEventHandler.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "");
        mSkinEventHandler.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "");
        mSkinEventHandler.onDestroy();
    }


    @Override
    public boolean isSupportSkinChange() {
        //告知当前界面是否支持换肤：true支持换肤，false不支持
        return true;
    }

    @Override
    public boolean isNeedDelegateViewCreate() {
        //告知当前界面是否需要在框架内代理View创建
        //该Activity是否支持换肤在框架内代理创建View，一般返回true即可
        return true;
    }

    @Override
    public boolean isSwitchSkinImmediately() {//告知当切换皮肤时，是否立刻刷新当前界面；true立刻刷新，false表示在界面onResume时刷新；
        return false;
    }


    @Override
    public void handleSkinChange() {
        //当前界面在换肤时收到的回调，可以在此回调内做一些其他事情；
        //比如：通知WebView内的页面切换到夜间模式等
    }

    @Override
    public int getWindowBackgroundResource() {
        //告知当前Activity的Window的background资源，换肤时会寻找对应的资源替换
        //INVALID（-1）为无效值，即不设置该属性
        return INVALID;
    }

    /**
     * ============================================================================================>skin end
     */


}
