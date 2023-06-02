package com.adayo.app.picture.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.picture.MyViewModel;
import com.adayo.app.picture.R;
import com.adayo.app.picture.ui.fragment.PictureListFragment;
import com.adayo.app.picture.ui.fragment.PicturePlayFragment;
import com.adayo.bpresenter.picture.presenters.PictureWndManagerPresenter;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.app.picture.ui.base.LogUtil;

import java.lang.ref.WeakReference;

import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_LIST;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY;
import static com.adayo.app.picture.constant.Constant.PICTURE_APP_STATE_FINISH;
import static com.adayo.app.picture.constant.Constant.PICTURE_LOADING_VIEW_G;
import static com.adayo.app.picture.constant.Constant.PICTURE_LOADING_VIEW_V;
import static com.adayo.app.picture.constant.Constant.PICTURE_MSG_G;
import static com.adayo.app.picture.constant.Constant.PICTURE_MSG_V;

public class MediaActivity extends AppCompatActivity {
    private final static String TAG = "Picture" + MediaActivity.class.getSimpleName();
    private IActivitySkinEventHandler mSkinEventHandle;
    private TextView tvPictur;//标签
    private FragmentTransaction fragmentTransaction;
    private RelativeLayout mLoadingLinear;
    private Fragment mPictureListFragment;
    private FrameLayout mMsgRl;
    private PictureWndManagerPresenter mPictureWndManagerPresenter;
    private MyViewModel mMyViewModel;
    private TextView mMsgTv;
    private FrameLayout flPlay, flList;
    private View decorView;
    private Fragment mPicturePlayFragment;
    private ImageView merrorIv, loadingiv;
    private View mUnPupportedPopView;
    private PopupWindow mPupportedPopWindow;
    private final Handler mPopHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPupportedPopWindow.dismiss();
        }
    };
    private TextView mPopTv;

    private TextView loadingTv;
    private AnimationDrawable mAnimationDrawable;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtil.v(TAG, "hasFocus = " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.v(TAG, "onConfigurationChanged: " + newConfig.toString());
    }

    @Override
    public void onStart() {
        LogUtil.v(TAG, "");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "");
        mMyViewModel.updateList();

    }

    @Override
    public void onPause() {
        LogUtil.v(TAG, "");
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtil.v(TAG, "");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "");
        super.onDestroy();
        mSkinEventHandle.onDestroy();

    }


    private void initView() {
        LogUtil.i(TAG, " ");
        onSystemUIVisibility(true);
        tvPictur = findViewById(R.id.picture_iv);
        mLoadingLinear = findViewById(R.id.loading_linear);
        loadingTv = findViewById(R.id.loading_tv);
        loadingiv = findViewById(R.id.loading_iv);
        flPlay = findViewById(R.id.frame_layout_play);
        flList = findViewById(R.id.frame_layout_list);
        mMsgRl = findViewById(R.id.error_msg);
        mMsgTv = findViewById(R.id.error_tv);
        merrorIv = findViewById(R.id.error_iv);
    }

    private void initMyViewModel() {
        LogUtil.i(TAG, " ");
        mMyViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        mMyViewModel.setContext(this);
    }

    private void tagChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getTag().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.d(TAG, "Tag integer = " + integer);
                switch (integer) {
                    case FRAGMENT_PICTURE_LIST:
                        showList();
                        break;
                    case FRAGMENT_PICTURE_PLAY:
                        showPlay();
                        break;
                    case PICTURE_LOADING_VIEW_V:
                        showLoadingView();
                        break;
                    case PICTURE_LOADING_VIEW_G:
                        hideLoadingView();
                        break;
                    case PICTURE_MSG_V:
                        showMsg();
                        break;
                    case PICTURE_MSG_G:
                        hideMsg();
                        break;
                    default:
                        break;
                }

            }
        });
    }


    private void typeChanged() {//媒体扫描相关改变触发的回调：设备拔出。。。
        LogUtil.i(TAG, " ");
        mMyViewModel.getType().observe(this, new Observer<com.adayo.common.picture.constant.Constant.MSG_TYPE>() {
            @Override
            public void onChanged(@Nullable com.adayo.common.picture.constant.Constant.MSG_TYPE type) {
                LogUtil.d(TAG, " Type MSG_TYPE = " + type);
                switch (type) {
                    case MSG_UNMOUNTED://拔出U盘
                        mMyViewModel.setIsctrlbar(true);
                        showNoDevice();
                        break;
                    case MSG_NO_FILE:
                        mMyViewModel.setIsctrlbar(true);
                        showNoFile();
                        break;
                    case MSG_NO_DEVICE://U盘未插入
                        mMyViewModel.setIsctrlbar(true);
                        showNoDevice();
                        break;
                    case MSG_FILE_UNSUPPORTED:     //文件播放不支持，图片应该无所谓，因为有个错误图片图片标使
                        break;
                    case MSG_LOADING:
                        showLoadingView();
                        break;
                    default:
                        break;

                }

            }
        });
    }

    private void appstateChanged() {//app生命周期状态改变回调，目前只有finish
        LogUtil.i(TAG, " ");
        mMyViewModel.getAppstate().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer appstate) {
                LogUtil.d(TAG, "appstate = " + appstate);
                switch (appstate) {
                    case PICTURE_APP_STATE_FINISH:
                        finishAPP();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void newListFragments() {
        LogUtil.i(TAG, " ");
        mPictureListFragment = new PictureListFragment();
    }

    private void newPlayFragments() {
        LogUtil.i(TAG, " ");
        mPicturePlayFragment = new PicturePlayFragment();
    }

    private void initFragment(Fragment fragment, int id, String s) {
        LogUtil.i(TAG, " ");
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(id, fragment, s);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void onSystemUIVisibility(boolean visibility) {
        View decorView = getWindow().getDecorView();
        if (visibility) {// 非全屏
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//导航栏不占位
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏透明
        } else {// 全屏
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 隐藏导航栏、状态栏
        }
    }

    /**
     * 初始化p层管理
     */
    private void initWndPresenters() {
        LogUtil.i(TAG, " ");
        mPictureWndManagerPresenter = PictureWndManagerPresenter.getInstance(this);
        mMyViewModel.setPictureWndManagerPresenter(mPictureWndManagerPresenter);
    }

    @Override
    public void onBackPressed() {
        LogUtil.i(TAG, " ");
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        finish();
    }

    /**
     * 显示全屏图片状态
     */
    private void showPlay() {
        LogUtil.i(TAG, " ");
        flPlay.setVisibility(View.VISIBLE);
        tvPictur.setVisibility(View.GONE);


    }


    /**
     * 显示加载
     */
    private void showLoadingView() {
        LogUtil.i(TAG, "");
        mLoadingLinear.setVisibility(View.VISIBLE);
            mAnimationDrawable = (AnimationDrawable) loadingiv.getDrawable();
            mAnimationDrawable.start();
        if (flPlay.getVisibility() == View.VISIBLE) {
            flPlay.setVisibility(View.GONE);
        }
        if (flList.getVisibility() == View.VISIBLE) {
            flList.setVisibility(View.GONE);
        }
        if (tvPictur.getVisibility() == View.VISIBLE) {
            tvPictur.setVisibility(View.VISIBLE);
        }


        mMsgRl.setVisibility(View.GONE);
    }

    private void hideLoadingView() {
        LogUtil.i(TAG, "");
        mLoadingLinear.setVisibility(View.GONE);
    }

    private void showMsg() {
        LogUtil.i(TAG, "");
        mMsgRl.setVisibility(View.VISIBLE);
    }

    private void hideMsg() {
        LogUtil.i(TAG, "");
        mMsgRl.setVisibility(View.GONE);
    }

    private void showList() {
        LogUtil.i(TAG, "");
        hideMsg();
        flPlay.setVisibility(View.GONE);
        tvPictur.setVisibility(View.VISIBLE);
        mLoadingLinear.setVisibility(View.GONE);
        flList.setVisibility(View.VISIBLE);
        mMyViewModel.updateListAll();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, " ");
        mSkinEventHandle = AAOP_HSkinHelper.initActivity(this, new ActivityViewCreateListener(this));
        super.onCreate(savedInstanceState);
        //   primingImmersion();// 启动沉浸式
        setContentView(R.layout.activity_main);
        initView();
        initMyViewModel();
        typeChanged();
        tagChanged();
        appstateChanged();
        LogUtil.d(TAG, "savedInstanceState = " + savedInstanceState);
        if (savedInstanceState != null) {
            mPictureListFragment = getSupportFragmentManager()
                    .findFragmentByTag("list");
            mPicturePlayFragment = getSupportFragmentManager()
                    .findFragmentByTag("play");
        } else {
            newListFragments();
            newPlayFragments();
            initFragment(mPictureListFragment, R.id.frame_layout_list, "list");
            initFragment(mPicturePlayFragment, R.id.frame_layout_play, "play");
        }
        mMyViewModel.initIPhotoListView();
        mMyViewModel.initPresenter();
        initWndPresenters();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    private void showNoDevice() {
        LogUtil.i(TAG, " ");
        mMsgRl.setVisibility(View.VISIBLE);
        AAOP_HSkin.with(merrorIv)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.com_icon_big_usb)
                .applySkin(false);
        mMsgTv.setText(R.string.picture_no_device);
        tvPictur.setVisibility(View.VISIBLE);
        flPlay.setVisibility(View.GONE);
        flList.setVisibility(View.GONE);
        mLoadingLinear.setVisibility(View.GONE);
    }

    private void showNoFile() {
        LogUtil.i(TAG, " ");
        tvPictur.setVisibility(View.VISIBLE);
        AAOP_HSkin.with(merrorIv)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.com_icon_big_no_file)
                .applySkin(false);
        mMsgTv.setText(R.string.picture_no_file);
        flList.setVisibility(View.GONE);
        mMsgRl.setVisibility(View.VISIBLE);
        mLoadingLinear.setVisibility(View.GONE);
        flPlay.setVisibility(View.GONE);
    }

    private void finishAPP() {
        LogUtil.i(TAG, " ");
        finish();
    }


    private static class ActivityViewCreateListener implements IViewCreateListener {
        WeakReference<MediaActivity> activityWeakReference;

        public ActivityViewCreateListener(MediaActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public View beforeCreate(View parent, String name, Context context, AttributeSet attrs) {
            MediaActivity activity = activityWeakReference.get();
            if (null == activity) {
                return null;
            }
            return activity.getDelegate().createView(parent, name, context, attrs);

        }

        @Override
        public void afterCreated(View view, String s, Context context, AttributeSet attributeSet) {

        }
    }

}