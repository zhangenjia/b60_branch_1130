package com.adayo.app.music.bt.btmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adayo.app.music.bt.R;
import com.adayo.app.music.bt.btmusic.callback.IBTMusicCallBack;
import com.adayo.app.music.bt.btmusic.manager.BTMusicManager;
import com.adayo.app.music.bt.btmusic.model.BTMusicModel;
import com.adayo.app.music.bt.btmusic.util.AppUtils;
import com.adayo.app.music.bt.btmusic.util.DateUtil;
import com.adayo.app.music.bt.btmusic.util.SPUtils;
import com.adayo.app.music.bt.btmusic.view.CustomSeekBar;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchProxy;
import com.lt.library.util.context.ContextUtil;
import com.nforetek.bt.base.jar.NforeBtBaseJar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BTMusicFragment extends Fragment {

    private ImageView mBtnNext;
    private ImageView mBtnPrev;
    private ImageView mBtnPlayPause;
    private ImageView mBtnList;
    private TextView mTvTitle;
    private TextView mTvArtist;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private RelativeLayout mRlContainer;
    private LinearLayout mLlBtContainer;
    private LinearLayout mLlA2dpContainer;
    private Button mBtnGoSetting;
    private CustomSeekBar mSeekBar;
    private final IBTMusicCallBack callBack = new IBTMusicCallBack() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onMusicStop() {
            playStatus = PAUSE;
            mBtnPlayPause.setBackground(mContext.getResources().getDrawable(R.drawable.selector_detail_pause_btn));
//            mObjectAnimator.pause();
            Log.d(TAG, "onMusicStop: playStatus = " + playStatus);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onMusicPlay() {
            playStatus = PLAY;
            mBtnPlayPause.setBackground(mContext.getResources().getDrawable(R.drawable.selector_detail_play_btn));
//            mObjectAnimator.resume();
            Log.d(TAG, "onMusicPlay: playStatus = " + playStatus);
        }

        @Override
        public void onId3Info(String artist, String album, String title) {
            mTvTitle.setText(title);
            mTvArtist.setText(artist);
        }

        @Override
        public void onPlayTimeStatus(long totalTime, long currentTime) {
            mTvCurrentTime.setText(DateUtil.formatTime(currentTime));
            mTvTotalTime.setText(DateUtil.formatTime(totalTime));
            int maxProgress = (int) (totalTime / 1000);
            mSeekBar.setMax(maxProgress);
            int progress = (int) (currentTime / 1000);
            mSeekBar.setProgress(progress);
//            if (totalTime > 0) {
//                mObjectAnimator.setDuration(1000);
//                mObjectAnimator.setCurrentPlayTime((progress / maxProgress) * 1000);
//            }
        }

        @Override
        public void notifyA2dpDisconnect() {
            Log.d(TAG, "notifyA2dpDisconnect: ");
            mLlBtContainer.setVisibility(View.GONE);
            mLlA2dpContainer.setVisibility(View.VISIBLE);
            mRlContainer.setVisibility(View.GONE);

        }

        @Override
        public void notifyA2dpConnect() {
            Log.d(TAG, "notifyA2dpConnect: ");
            mLlBtContainer.setVisibility(View.GONE);
            mLlA2dpContainer.setVisibility(View.GONE);
            mRlContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void notifyBluetoothDisconnect() {
            Log.d(TAG, "notifyBluetoothDisconnect: ");
            mLlBtContainer.setVisibility(View.VISIBLE);
            mLlA2dpContainer.setVisibility(View.GONE);
            mRlContainer.setVisibility(View.GONE);
        }
    };
    private BTMusicManager mBTMusicManager;
    private BTMusicModel mBTMusicModel;
    private static final int PLAY = 1;
    private static final int PAUSE = 0;
    private volatile int playStatus = PAUSE;
    private static final String TAG = BTMusicFragment.class.getSimpleName()+"_0518";

    public static BTMusicFragment newInstance() {
        Bundle args = new Bundle();
        BTMusicFragment fragment = new BTMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //    private ObjectAnimator mObjectAnimator;
    private Context mContext;

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if (mBTMusicModel!=null){
            mBTMusicModel.init();
        }
        AAOP_HSkin.with(mSeekBar)
                  .addViewAttrs(AAOP_HSkin.ATTR_THUMB,R.drawable.icon_schedule)
                  .addViewAttrs(AAOP_HSkin.ATTR_PROGRESS_DRAWABLE,R.drawable.layer_detail_progress_sb)
                  .applySkin(true);
        SrcMngSwitchManager.getInstance().notifyServiceUIChange(AdayoSource.ADAYO_SOURCE_BT_AUDIO,requireContext().getPackageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = ContextUtil.getAppContext();
        View view = inflater.inflate(R.layout.fragment_bt_music, container, false);
        initView(view);
        initEvent();
        initData();
        AAOP_HSkin.getInstance().applySkin(view, true);
        return view;
    }

    private void initView(View view) {
        mBtnPrev = view.findViewById(R.id.btn_prev);
        mBtnPlayPause = view.findViewById(R.id.btn_play_pause);
        mBtnNext = view.findViewById(R.id.btn_next);
        mBtnList = view.findViewById(R.id.btn_list);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvArtist = view.findViewById(R.id.tv_artist);
        mTvCurrentTime = view.findViewById(R.id.tv_cur_time);
        mTvTotalTime = view.findViewById(R.id.tv_total_time);
        mRlContainer = view.findViewById(R.id.rl_container);
        mLlBtContainer = view.findViewById(R.id.ll_bt_not_connect);
        mLlA2dpContainer = view.findViewById(R.id.ll_a2dp_not_connect);
        mBtnGoSetting = view.findViewById(R.id.btn_go_setting);
        mSeekBar = view.findViewById(R.id.sb_detail_progress);
    }

    private void initData() {
        mBTMusicManager = BTMusicManager.getInstance(mContext);
        mBTMusicManager.registerCallBack(callBack);
        mBTMusicModel = BTMusicModel.getInstance(mContext);
        mBTMusicModel.setBTMusicPresenter(mBTMusicManager);
    }

    private void initEvent() {
        mBtnPrev.setOnClickListener(v -> {
            if (mBTMusicManager != null)
                mBTMusicManager.playPrev();
        });
        mBtnNext.setOnClickListener(v -> {
            if (mBTMusicManager != null) {
                mBTMusicManager.playNext();
            }
        });
        mBtnPlayPause.setOnClickListener(v -> {
            if (mBTMusicManager != null) {
                if (playStatus == PAUSE) {
                    SPUtils.setNeedPlay(mContext,true);
                    mBTMusicManager.play();
                } else {
                    SPUtils.setNeedPlay(mContext,false);
                    mBTMusicManager.pause();
                }
            }

        });
        // TODO: 2021/7/16 需要设置提供调到蓝牙画面接口
        mBtnGoSetting.setOnClickListener(v -> {
            AppUtils.startBTSetting();
        });
        // TODO: 2021/7/16 需要设置提供调到音效画面接口
        mBtnList.setOnClickListener(v -> {
            AppUtils.startEQSetting(mContext);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}