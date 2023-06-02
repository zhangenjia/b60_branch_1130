package com.adayo.app.setting.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.adayo.app.base.BaseActivity;
import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.ActivityMainBinding;
import com.adayo.app.setting.manager.Controller;
import com.adayo.app.setting.manager.ShowOrHideAbstractLifeHandler;
import com.adayo.app.setting.manager.bindDataHandlerAbstract;
import com.adayo.app.setting.manager.onConfigurationChangedAbstractLifeHandler;
import com.adayo.app.setting.manager.onCreateAbstractLifeHandler;
import com.adayo.app.setting.manager.onDestroyAbstractLifeHandler;
import com.adayo.app.setting.manager.onPauseAbstractLifeHandler;
import com.adayo.app.setting.manager.onResumeAbstractLifeHandler;
import com.adayo.app.setting.manager.saveStateHandlerAbstract;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.reply.CommonReplyFragment;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.setting.utils.ConstraintUtil;
import com.adayo.app.setting.view.fragment.factory.FragmentFactory;
import com.adayo.app.setting.view.fragment.factory.FragmentUtil;
import com.adayo.app.setting.view.fragment.factory.product.DirectMediaFragment;
import com.adayo.btsetting.model.BtFinishManager;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lt.library.util.CastUtil;

import java.util.Map;
import java.util.Objects;

import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C17A;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnTabSelectListener {
    private final static String TAG = "SettingMainActivity";
    private int mTabItemSelPos = 0;
    private int[] mTabTitles;
    private Fragment directMediaFragment;
    private Fragment replyFragment;
    private Controller mController;
    private ConstraintUtil constraintUtil;
    private CommonHighlight mCommonHighlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mController = Controller.getInstance();
        mCommonHighlight = ViewModelProviders.of(this).get(CommonHighlight.class);
        LogUtil.d(TAG, "mCommonHighlight ="+mCommonHighlight.mCommonHighlightRequest);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected ActivityMainBinding bindView(View view) {
        return ActivityMainBinding.bind(view);

    }


    @Override
    protected void inflateView(@Nullable Bundle savedInstanceState) {
        super.inflateView(savedInstanceState);
        LogUtil.d(TAG, "");
getWindow().setBackgroundDrawable(AAOP_HSkin.getInstance().getResourceManager().getDrawable(R.drawable.com_bg));
        mController.setBaseActivity(this);
        mController.onNotifyLifeState(new onCreateAbstractLifeHandler());
        mController.onNotifySystemState(true, mController.isInflateReady);
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C17A) {
            mController.createFunction(ParamConstant.FILE_CONFIG_HM6C17A);
        } else {
            mController.createFunction(ParamConstant.FILE_CONFIG_HM6C18A);
        }
        mController.onNotifyInitViewModel();
        ConfigurationManager.getInstance().setActivity(this);

    }

    public void initMainInflate(View view) {
        ActivityonInflateFinished(view);
        initView();
        initData();
        initEvent();
        mController.getDevice(ParamConstant.COMMON).setViewBinding(mViewBinding);
        mController.getDevice(ParamConstant.DRIVE).setViewBinding(mViewBinding.vsCommon.fragCommonDrive);
        mController.getDevice(ParamConstant.LANGUAGE).setViewBinding(mViewBinding.vsCommon.fragCommonLanguage);
        mController.getDevice(ParamConstant.UNIT).setViewBinding(mViewBinding.vsCommon.fragCommonUnit);
        mController.getDevice(ParamConstant.DEVNM).setViewBinding(mViewBinding.vsCommon);
        mController.getDevice(ParamConstant.BLUETOOTH).setViewBinding(mViewBinding.vsCommon);
        mController.getDevice(ParamConstant.BLUETOOTH).setView(mViewBinding.vsCommon.fragConnBluetooth);
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C17A) {
            mController.getDevice(ParamConstant.LOCAL).setViewBinding(mViewBinding.vsCommon.fragCommonLocal);
            mController.getDevice(ParamConstant.HOTSPOT).setViewBinding(mViewBinding.vsCommon.fragConnHotspot);
            mController.getDevice(ParamConstant.WIFI).setViewBinding(mViewBinding.vsCommon.fragConnWifi);
        }
        mController.onNotifySystemState(mController.isActivityReady, true);
        mViewBinding.stlMain.setTabSelectWithEvent(mTabItemSelPos);
        addDirectMedia();
        addReply();
    }



    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        FragmentUtil.setFragmentFactory(new FragmentFactory());
        LogUtil.d(TAG, "bindData: " + mTabItemSelPos);
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C17A) {
            mTabTitles = new int[]{R.string.tab_item_common_title, R.string.tab_item_sound_title, R.string.tab_item_display_title,R.string.tab_item_voice_title, R.string.tab_item_system_title};
        } else {
            mTabTitles = new int[]{R.string.tab_item_common_title, R.string.tab_item_sound_title, R.string.tab_item_display_title, R.string.tab_item_system_title};
        }
        if (Objects.nonNull(arguments)) {
            parseArguments(arguments);
        }
        if (Objects.nonNull(savedInstanceState)) {
            mTabItemSelPos = savedInstanceState.getInt(ParamConstant.BDL_KEY_TAB_ITEM_SEL_POS);
            for (int i = 0; i < mTabTitles.length; i++) {
                String tabTag = Objects.requireNonNull(String.valueOf(mTabTitles[i]), "tab not set tag")
                        .toString();
                FragmentUtil.removeFragment(tabTag, getSupportFragmentManager());

            }
            LogUtil.debugD(TAG, "savedInstanceState: " + mTabItemSelPos);
            directMediaFragment = getSupportFragmentManager()
                    .findFragmentByTag("direct");
            replyFragment = getSupportFragmentManager()
                    .findFragmentByTag("reply");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(directMediaFragment);
            fragmentTransaction.remove(replyFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        mController.onNotifyLifeState(new bindDataHandlerAbstract(arguments, savedInstanceState));

    }


    @Override
    protected void initView() {
        LogUtil.d(TAG, "");
        SkinUtil.setSegmentTabLayoutSkinAttrThumbDrawable("thumb_drawable", mViewBinding.stlMain, R.drawable.img_select_center, getApplicationContext());
        SkinUtil.setSegmentTabLayoutSkinAttrTextSelectColor("textSelectColor", mViewBinding.stlMain, R.color.tab_selected, getApplicationContext());
        String[] tabTitles;
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C17A) {
            tabTitles = new String[]{this.getString(R.string.tab_item_common_title), this.getString(R.string.tab_item_sound_title), this.getString(R.string.tab_item_display_title), this.getString(R.string.tab_item_voice_title), this.getString(R.string.tab_item_system_title)};

        } else {
            tabTitles = new String[]{getString(R.string.tab_item_common_title), getString(R.string.tab_item_sound_title), getString(R.string.tab_item_display_title),getString(R.string.tab_item_system_title)};
            mViewBinding.vsCommon.fragConnHotspot.clHospot.setVisibility(View.GONE);
            mViewBinding.vsCommon.fragConnWifi.flWifi.setVisibility(View.GONE);
            mViewBinding.vsCommon.fragCommonLocal.clDisplaySetBglocal.setVisibility(View.GONE);
            initUnit();
            initDrive();
            initLanguage();
            initBluetooth();
            initDevnm();
        }
        mViewBinding.stlMain.setTabData(tabTitles);
        mViewBinding.background.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initEvent() {
        LogUtil.d(TAG, "");
        mViewBinding.stlMain.setOnTabSelectListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    protected void saveState(@NonNull Bundle outState) {
        outState.putInt(ParamConstant.BDL_KEY_TAB_ITEM_SEL_POS, mTabItemSelPos);
        LogUtil.d(TAG, "saveState: " + mTabItemSelPos);
        mController.onNotifyLifeState(new saveStateHandlerAbstract(outState));
        mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "");
        Bundle bundle = intent.getExtras();
        if (Objects.nonNull(bundle)) {
            parseArguments(bundle);}
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "");
        mController.onNotifyLifeState(new onResumeAbstractLifeHandler());
        mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
        if (mViewBinding != null) {
            mViewBinding.stlMain.setTabSelectWithEvent(mTabItemSelPos);
        }
        LogUtil.debugD(TAG, "mTabItemSelPos: " + mTabItemSelPos);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "");
        mController.onNotifyLifeState(new onDestroyAbstractLifeHandler());
        mController.startSumbit();
        mController.isActivityReady=false;
        mController.isInflateReady=false;
        ConfigurationManager.getInstance().setActivity(null);
}


    @Override
    public void handleSkinChange() {
}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.d(TAG, "");
        AAOP_HSkin.getInstance().refreshLanguage();
        mController.onNotifyLifeState(new onConfigurationChangedAbstractLifeHandler(newConfig));
        mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
        String[] tabTitles;
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C17A) {
            tabTitles = new String[]{this.getString(R.string.tab_item_common_title), this.getString(R.string.tab_item_sound_title), this.getString(R.string.tab_item_display_title), this.getString(R.string.tab_item_voice_title), this.getString(R.string.tab_item_system_title)};
        } else {
            tabTitles = new String[]{getString(R.string.tab_item_common_title), getString(R.string.tab_item_sound_title), getString(R.string.tab_item_display_title),getString(R.string.tab_item_system_title)};
        }
        mViewBinding.stlMain.setTabData(tabTitles);

    }


    private void parseArguments(@NonNull Bundle arguments) {
        LogUtil.d(TAG, "");
        Map<String, String> sourceInfo = CastUtil.obj2HashMap(arguments.get(ParamConstant.BDL_KEY_SOURCE_MNG),
                String.class, String.class);
        String settingPage = sourceInfo.get(ParamConstant.BDL_KEY_SETTING_PAGE);
        if (Objects.isNull(settingPage)) {
            return;
        }
        LogUtil.d(TAG, "settingPage = " + settingPage);
        switch (settingPage) {
            case ParamConstant.BDL_VALUE_SETTING_PAGE_BT:mTabItemSelPos = 0;
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_BT);
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_BT_MUSIC:mTabItemSelPos = 0;
                sendMsgToBt(ParamConstant.BDL_VALUE_SETTING_PAGE_BT_MUSIC);
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_BT);
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_BT_PHONE:mTabItemSelPos = 0;
                sendMsgToBt(ParamConstant.BDL_VALUE_SETTING_PAGE_BT_PHONE);
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_BT);
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_WIFI:mTabItemSelPos = 0;
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_WIFI);
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_HOTSPOT:mTabItemSelPos = 0;
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_HOTSPOT);
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_TIME:
                mTabItemSelPos = 2;
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_EQ:
                mTabItemSelPos = 1;
                break;
            case ParamConstant.BDL_VALUE_SETTING_PAGE_UPG:
                mTabItemSelPos = 4;
                break;
            default:
                LogUtil.w(TAG, "unexpected value = " + settingPage);
                return;
        }
        LogUtil.debugD(TAG, "parseArguments: " + mTabItemSelPos);
    }

    private void sendMsgToBt(String sourceParams) {
        BtFinishManager.getInstance().setFinish(true);
        if (ParamConstant.BDL_VALUE_SETTING_PAGE_BT_PHONE.equals(sourceParams)){
            BtFinishManager.getInstance().setSourceType(AdayoSource.ADAYO_SOURCE_BT_PHONE);
        }else if (ParamConstant.BDL_VALUE_SETTING_PAGE_BT_MUSIC.equals(sourceParams)){
            BtFinishManager.getInstance().setSourceType(AdayoSource.ADAYO_SOURCE_BT_AUDIO);
        }
    }


    @Override
    public void onTabSelect(int position) {
        LogUtil.d(TAG, "onTabSelected: " + mTabItemSelPos);
        if (position == 0) {
            mController.onNotifyLifeState(new ShowOrHideAbstractLifeHandler(true));
            mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
        } else {
            LogUtil.d(TAG, "mViewBinding.vsCommon: " + mViewBinding.vsCommon);
            mController.onNotifyLifeState(new ShowOrHideAbstractLifeHandler(false));
            mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
            mViewBinding.vsCommon.llCommon.setVisibility(View.GONE);
            mViewBinding.flMainInfo.setVisibility(View.VISIBLE);
            String tabTag = Objects.requireNonNull(String.valueOf(mTabTitles[position]), "tab not set tag")
                    .toString();
            LogUtil.debugD(TAG, "tabTag =" + tabTag);
            FragmentUtil.switchFragment(tabTag,
                    new int[]{R.id.fl_main_info},
                    R.id.fl_main_info,
                    this.getSupportFragmentManager());}

        LogUtil.d(TAG, "ivWelcome = " + position);
        mTabItemSelPos = position;

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
        LogUtil.d(TAG);
        mController.onNotifyLifeState(new onPauseAbstractLifeHandler());
        mController.onNotifySystemState(mController.isActivityReady, mController.isInflateReady);
    }

    private void addReply() {
        LogUtil.d(TAG);
if (replyFragment == null) {
            replyFragment = new CommonReplyFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frag_conn_reply, replyFragment, "reply");
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frag_conn_reply, replyFragment, "reply");
            fragmentTransaction.commitAllowingStateLoss();
}


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    private void addDirectMedia() {
LogUtil.d(TAG, "directMediaFragment =" + directMediaFragment);
        if (directMediaFragment == null) {
            directMediaFragment = new DirectMediaFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frag_direct_media, directMediaFragment, "direct");
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
fragmentTransaction.add(R.id.frag_direct_media, directMediaFragment, "direct");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void initUnit() {
        constraintUtil = new ConstraintUtil(mViewBinding.vsCommon.clCommon);
        ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();
        begin.Top_toBottomOf(mViewBinding.getRoot().findViewById(R.id.frag_common_unit).getId(), R.id.frag_common_drive);
        begin.commit();
    }

    private void initDrive() {
        constraintUtil = new ConstraintUtil(mViewBinding.vsCommon.clCommon);
        ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();
        begin.Top_toBottomOf(mViewBinding.getRoot().findViewById(R.id.frag_common_drive).getId(), R.id.frag_conn_devnm);
        begin.setMarginTop(mViewBinding.getRoot().findViewById(R.id.frag_common_drive).getId(), 24);
        begin.commit();

    }

    private void initLanguage() {
        constraintUtil = new ConstraintUtil(mViewBinding.vsCommon.clCommon);
        ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();
        begin.Top_toBottomOf(mViewBinding.getRoot().findViewById(R.id.frag_common_language).getId(), R.id.frag_direct_media);
        begin.Top_toBottomOf(mViewBinding.getRoot().findViewById(R.id.frag_conn_reply).getId(), R.id.frag_common_language);
        begin.commit();
    }

    private void initBluetooth() {
        constraintUtil = new ConstraintUtil(mViewBinding.vsCommon.clCommon);
        ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();
        begin.Left_toLeftOf(mViewBinding.getRoot().findViewById(R.id.frag_conn_bluetooth).getId(), mViewBinding.getRoot().findViewById(R.id.gl_main_left5).getId());
        begin.Bottom_toBottomOf(mViewBinding.getRoot().findViewById(R.id.frag_conn_bluetooth).getId(), mViewBinding.getRoot().findViewById(R.id.frag_common_unit).getId());
        begin.commit();
    }


    private void initDevnm() {
        constraintUtil = new ConstraintUtil(mViewBinding.vsCommon.clCommon);
        ConstraintUtil.ConstraintBegin begin = constraintUtil.beginWithAnim();
        begin.Right_toLeftOf(mViewBinding.getRoot().findViewById(R.id.frag_conn_devnm).getId(), mViewBinding.vsCommon.glMainLeft4.getId());
        begin.commit();
    }

}