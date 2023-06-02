package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.*;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.adapter.PlaybackAdapter;
import com.adayo.app.dvr.adapter.WillTotAdapter;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.entity.PlaybackEntity;
import com.adayo.app.dvr.entity.WillTotEntity;
import com.adayo.app.dvr.utils.FastClickCheck;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

public class PlaybackControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + PlaybackControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile PlaybackControl mModel = null;

    private ConstraintLayout llPlaybackBack;
    private ConstraintLayout llPlaybackOrdinaryVideo;
    private ConstraintLayout llPlaybackEmergencyVideo;
    private ConstraintLayout llPlaybackPhoto;
    private ConstraintLayout llPlaybackAtWill;
    private ConstraintLayout llPlaybackEdit;
    private ImageView ivPlaybackUp;
    private ImageView ivPlaybackDown;
    private TextView tvPlaybackCurPage;
    private TextView tvPlaybackTotPage;
    private TextView tvAtWillCurPage;
    private TextView tvAtWillTotPage;
    private TextView ivplaybackEditTitle;
    private TextView ivAtWillPlaybackEditTitle;
    private ImageView ivAtWillPlaybackUp;
    private ImageView ivAtWillPlaybackDown;
    private ImageView ivPlaybackEditImg;
    private ImageView ivAtWillPlaybackEditImg;
    private GridView gvPlayback;
    private GridView gvWillTotPlayback;
    private List<PlaybackEntity> mPlaybackEntities;
    private List<WillTotEntity> mWilltotEntities;
    private PlaybackAdapter mPlaybackAdapter;
    private WillTotAdapter mWillTotAdapter;
    private DvrStatusInfo mDvrStatusInfo;
    private ConstraintLayout clNormalPlayback;
    private ConstraintLayout clAtWillPlayback;
    private ConstraintLayout llAtWillPlaybackBack;
    private ConstraintLayout llAtWillPlaybackVideo;
    private ConstraintLayout llAtWillPlaybackPicture;
    private ConstraintLayout llAtWillPlaybackEdit;

    //回放页面停留5分钟返回主页面
    private static final long COUNTDOWN_TIME = 300000;

    PlaybackEntity playbackEntity;
    WillTotEntity willTotEntity;
    int num = 0;

    private PlaybackControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static PlaybackControl getPlaybackControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (PlaybackControl.class) {
                if (mModel == null) {
                    mModel = new PlaybackControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView: ");
        mDvrStatusInfo = DvrStatusInfo.getInstance();
        llPlaybackBack = (ConstraintLayout) findViewById(R.id.ll_playback_back);
        llPlaybackOrdinaryVideo = (ConstraintLayout) findViewById(R.id.ll_playback_ordinary_video);
        llPlaybackEmergencyVideo = (ConstraintLayout) findViewById(R.id.ll_playback_emergency_video);
        llPlaybackPhoto = (ConstraintLayout) findViewById(R.id.ll_playback_photo);
        llPlaybackAtWill = (ConstraintLayout) findViewById(R.id.ll_playback_at_will);
        llPlaybackEdit = (ConstraintLayout) findViewById(R.id.ll_playback_edit);
        ivPlaybackEditImg = (ImageView) findViewById(R.id.iv_playback_edit_img);
        ivAtWillPlaybackEditImg =(ImageView) findViewById(R.id.iv_at_will_playback_edit_img);
        ivPlaybackUp = (ImageView) findViewById(R.id.iv_playback_up);
        ivPlaybackDown = (ImageView) findViewById(R.id.iv_playback_down);
        ivAtWillPlaybackUp = (ImageView) findViewById(R.id.iv_at_will_playback_up);
        ivAtWillPlaybackDown = (ImageView) findViewById(R.id.iv_at_will_playback_down);

        tvPlaybackCurPage = (TextView) findViewById(R.id.tv_playback_cur_page);
        tvPlaybackTotPage = (TextView) findViewById(R.id.tv_playback_tot_page);
        tvAtWillCurPage = (TextView) findViewById(R.id.tv_at_will_cur_page);
        tvAtWillTotPage = (TextView) findViewById(R.id.tv_at_will_tot_page);
        ivplaybackEditTitle =(TextView) findViewById(R.id.iv_playback_edit_title);
        ivAtWillPlaybackEditTitle =(TextView) findViewById(R.id.iv_at_will_playback_edit_title);

        gvPlayback = (GridView) findViewById(R.id.gv_playback);
        mPlaybackEntities = new ArrayList<>();


        clNormalPlayback = (ConstraintLayout) findViewById(R.id.cl_normal_playback);
        clAtWillPlayback = (ConstraintLayout) findViewById(R.id.cl_at_will_playback);
        llAtWillPlaybackBack = (ConstraintLayout) findViewById(R.id.ll_at_will_playback_back);
        llAtWillPlaybackVideo = (ConstraintLayout) findViewById(R.id.ll_at_will_playback_video);
        llAtWillPlaybackPicture = (ConstraintLayout) findViewById(R.id.ll_at_will_playback_picture);
        llAtWillPlaybackEdit = (ConstraintLayout) findViewById(R.id.ll_at_will_playback_edit);

        gvWillTotPlayback = (GridView) findViewById(R.id.gv_will_tot_playback);
        mWilltotEntities = new ArrayList<>();

        //默认选中普通录像
        AAOP_HSkin
                .with(llPlaybackOrdinaryVideo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                .applySkin(false);
    }


    /**
     * 倒计时
     */
    public CountDownTimer timer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            Log.d(TAG, "onTick: 111112222233333 " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
//            Log.d(TAG, "onFinish: 111112222233333 返回开始 ");
            DvrController.getInstance().setDisplayMode(Constant.DISPLAY_MODE_SET_PREVIEW);
            mPlaybackHandler.sendEmptyMessage(3);
        }
    };

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: ");
        llPlaybackBack.setOnClickListener(this);
        llPlaybackOrdinaryVideo.setOnClickListener(this);
        llPlaybackEmergencyVideo.setOnClickListener(this);
        llPlaybackPhoto.setOnClickListener(this);
        llPlaybackAtWill.setOnClickListener(this);
        llPlaybackEdit.setOnClickListener(this);
        ivPlaybackUp.setOnClickListener(this);
        ivPlaybackDown.setOnClickListener(this);
        ivAtWillPlaybackUp.setOnClickListener(this);
        ivAtWillPlaybackDown.setOnClickListener(this);
        llAtWillPlaybackBack.setOnClickListener(this);
        llAtWillPlaybackVideo.setOnClickListener(this);
        llAtWillPlaybackPicture.setOnClickListener(this);

        llAtWillPlaybackEdit.setOnClickListener(this);

        gvPlayback.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selectthumbnail position" + position);
                //TODO 全都防抖
                if (!FastClickCheck.isFastClick()) {
                    return;
                }
                Message msgItem = new Message();
                msgItem.obj = position + 1;
                msgItem.what = SELECTED_FILE;
                mPlaybackHandler.sendMessageDelayed(msgItem,500);

                mDvrStatusInfo.setmPlayStatus(true);
                if (PLAY_TYPE_VIDEO.equals(mDvrStatusInfo.getType())){
                    mDvrStatusInfo.setmPlayBackStatus(true);
                }

            }
        });

        gvWillTotPlayback.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selectthumbnail position" + position);
                if (!FastClickCheck.isFastClick()) {
                    return;
                }
                Message msgWilItem = new Message();
                msgWilItem.obj = position + 1;
                msgWilItem.what = SELECTED_FILE;
                mPlaybackHandler.sendMessageDelayed(msgWilItem,500);
//                DvrController.getInstance().selectThumbnail(position + 1);
                mDvrStatusInfo.setmPlayStatus(true);
                if (PLAY_TYPE_ATWILL_VIDEO.equals(mDvrStatusInfo.getAtWillType())){
                    mDvrStatusInfo.setmWillPlaystatus(true);
                }
            }
        });

    }

    public void replyVideo(){
        mDvrStatusInfo.setType(Constant.PLAY_TYPE_VIDEO);
        mDvrStatusInfo.setMode(Constant.NORMAL);


        AAOP_HSkin
                .with(llPlaybackOrdinaryVideo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                .applySkin(false);
        AAOP_HSkin
                .with(llPlaybackEmergencyVideo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                .applySkin(false);
        AAOP_HSkin
                .with(llPlaybackPhoto)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                .applySkin(false);
    }

    @Override
    public void onClick(View v) {
        //TODO 全都防抖
        if (!FastClickCheck.isFastClick()) {
            return;
        }
        //TODO 防抖处理 end
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_playback_back:
                //点击回放-返回
                DvrController.getInstance().setDisplayMode(PREVIEW_MODE);
                mPlaybackHandler.sendEmptyMessageDelayed(3,600);
                //点击回放-普通录像
                mDvrStatusInfo.setType(Constant.PLAY_TYPE_VIDEO);
                mDvrStatusInfo.setMode(Constant.NORMAL);
                mDvrStatusInfo.setmWillPlaystatus(false);

                AAOP_HSkin
                        .with(llPlaybackOrdinaryVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackEmergencyVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackPhoto)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);

                break;
            case R.id.ll_at_will_playback_back:
                //点击随心拍-返回
                DvrController.getInstance().enterVideo();
                clNormalPlayback.setVisibility(View.VISIBLE);
                clAtWillPlayback.setVisibility(View.GONE);
                mDvrStatusInfo.setType(Constant.PLAY_TYPE_VIDEO);
                mDvrStatusInfo.setAtWillType(Constant.PLAY_TYPE_ATWILL_VIDEO);
                mDvrStatusInfo.setMode(Constant.NORMAL);
                mDvrStatusInfo.setmAtWillTypeStatus(false);
                AAOP_HSkin
                        .with(llPlaybackOrdinaryVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackEmergencyVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackPhoto)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                mPlaybackHandler.sendEmptyMessageDelayed(1, 600);
                break;
            case R.id.ll_at_will_playback_video:
                //点击随心拍-视频
                mDvrStatusInfo.setAtWillType(Constant.PLAY_TYPE_ATWILL_VIDEO);
                mDvrStatusInfo.setMode(Constant.ATWILL);

                AAOP_HSkin
                        .with(llAtWillPlaybackVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llAtWillPlaybackPicture)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);


                DvrController.getInstance().freeshotPhotoOrVedio(1);

                break;
            case R.id.ll_at_will_playback_picture:
                //点击随心拍-图片
                mDvrStatusInfo.setAtWillType(Constant.PLAY_TYPE_ATWILL_PHOTO);
                mDvrStatusInfo.setMode(Constant.ATWILL);

                AAOP_HSkin
                        .with(llAtWillPlaybackPicture)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llAtWillPlaybackVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);

                DvrController.getInstance().freeshotPhotoOrVedio(0);
                break;
            case R.id.ll_playback_ordinary_video:
                //点击回放-普通录像
                mDvrStatusInfo.setType(Constant.PLAY_TYPE_VIDEO);
                mDvrStatusInfo.setMode(Constant.NORMAL);


                AAOP_HSkin
                        .with(llPlaybackOrdinaryVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackEmergencyVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackPhoto)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                DvrController.getInstance().enterVideo();

                mPlaybackHandler.sendEmptyMessageDelayed(1, 600);
                break;
            case R.id.ll_playback_emergency_video:
                //点击回放-紧急录像
                mDvrStatusInfo.setType(Constant.PLAY_TYPE_VIDEO);
                mDvrStatusInfo.setMode(Constant.EMERGENCY);

                AAOP_HSkin
                        .with(llPlaybackEmergencyVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llPlaybackOrdinaryVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llPlaybackPhoto)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);

                DvrController.getInstance().enterEmergencyVideo();

                mPlaybackHandler.sendEmptyMessageDelayed(1, 600);
                break;
            case R.id.ll_playback_photo:
                //点击回放-照片
                mDvrStatusInfo.setType(Constant.PLAY_TYPE_PHOTO);
                mDvrStatusInfo.setMode(NORMAL);

                AAOP_HSkin
                        .with(llPlaybackPhoto)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llPlaybackEmergencyVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                AAOP_HSkin
                        .with(llPlaybackOrdinaryVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);

                DvrController.getInstance().enterGraph();

                mPlaybackHandler.sendEmptyMessageDelayed(1, 600);
                break;
            case R.id.ll_playback_at_will:
                mDvrStatusInfo.setAtWillType(Constant.PLAY_TYPE_ATWILL_VIDEO);
                mDvrStatusInfo.setMode(Constant.ATWILL);
                mDvrStatusInfo.setmAtWillTypeStatus(true);
                DvrController.getInstance().freeshotPhotoOrVedio(1);
                //点击回放-随心拍
                AAOP_HSkin
                        .with(llAtWillPlaybackVideo)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.frame_sel)
                        .applySkin(false);

                AAOP_HSkin
                        .with(llAtWillPlaybackPicture)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.transparent)
                        .applySkin(false);
                break;
            case R.id.ll_playback_edit:
                //点击回放-编辑
                mDvrStatusInfo.setmEditStatus(true);
                DvrController.getInstance().enterEditMode();

                break;
            case R.id.ll_at_will_playback_edit:
                //点击随心拍-编辑
                mDvrStatusInfo.setMode(ATWILL);
                mDvrStatusInfo.setmEditWillStatus(true);
                DvrController.getInstance().enterEditMode();
                break;
            case R.id.iv_playback_up:
                //点击回放-上翻页
                DvrController.getInstance().pageUp();
                break;
            case R.id.iv_playback_down:
                //点击回放-下翻页
                DvrController.getInstance().pageDown();
                break;
            case R.id.iv_at_will_playback_up:

                //点击随心拍-上翻页
                DvrController.getInstance().pageUp();
                break;
            case R.id.iv_at_will_playback_down:
                //点击随心拍-下翻页
                DvrController.getInstance().pageDown();
                break;
            default:
                break;
        }
    }

    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_EnterEditMode:
                if (value == 1) {
                    EditControl.getEditControlInstance(mMainActivity).updateEditMode();
                } else {
                    mPlaybackHandler.sendEmptyMessage(5);
                }
                break;
            case DVR_Will_EnterEditMode:
                if(value == 1){
                    EditWillControl.getEditControlInstance(mMainActivity).updateWillEditMode();
                }else {
                    mPlaybackHandler.sendEmptyMessage(5);
                }
                break;
            case DVR_St_DisplayMode:
                if (value == PLAYBACK_MODE) {
                    Log.d(TAG, "notifyChange: DVR_St_DisplayMode start");

                    mPlaybackHandler.sendEmptyMessage(1);

                }

                break;

            case DVR_St_CurrentThumbnail:
                if(DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                    num = value;
                    Log.d(TAG, "notifyChange: playback thumbnail1111111 value=" + num);
                    willTotEntity = new WillTotEntity();
                    mWilltotEntities.clear();
                    for (int i = 0; i < num; i++) {

                        willTotEntity.setCheck(true);
                        mWilltotEntities.add(willTotEntity);

                    }

                    mWillTotAdapter = new WillTotAdapter(mMainActivity, mWilltotEntities);

                    mPlaybackHandler.sendEmptyMessage(10);
                }else{
                    num = value;
                    Log.d(TAG, "notifyChange: playback thumbnail1111111 value=" + num);
                    playbackEntity = new PlaybackEntity();
                    mPlaybackEntities.clear();
                    for (int i = 0; i < num; i++) {

                        playbackEntity.setCheck(true);
                        mPlaybackEntities.add(playbackEntity);

                    }

                    mPlaybackAdapter = new PlaybackAdapter(mMainActivity, mPlaybackEntities);
                    Log.d(TAG, "notifyChange: playback thumbnail2222222 value=" + num);
                    mPlaybackHandler.sendEmptyMessage(4);
                }
                break;

            case DVR_St_TotalPage:
                Message msgtota = new Message();
                msgtota.obj = value;
                if (DvrStatusInfo.getInstance().getMode().equals(ATWILL)){
                    msgtota.what = 11;
                    mPlaybackHandler.sendMessage(msgtota);
                }else{
                    msgtota.what = 6;
                    mPlaybackHandler.sendMessage(msgtota);
                }
                break;
            case DVR_St_CurrentPage:
                Message msgCur = new Message();
                msgCur.obj = value;
                if (DvrStatusInfo.getInstance().getMode().equals(ATWILL)){
                    msgCur.what = 12;
                    mPlaybackHandler.sendMessage(msgCur);
                }else{
                    msgCur.what = 7;
                    mPlaybackHandler.sendMessage(msgCur);
                }

                break;
            case DVR_TakeVideoAtWill:
            case DVR_TakePhotosAtWill:
                if( value == 1 ){
                    Log.d(TAG, "notifyChange: DVR_TakeVideoAtWill success" );
                    mPlaybackHandler.sendEmptyMessage(9);
                }else{
                    Log.d(TAG, "notifyChange: DVR_TakeVideoAtWill fail" );
                }

                break;
            default:
                break;

        }

    }

    /**
     * 更新照片播放状态
     */
    public void updatePlayStatus() {
        Log.d(TAG, "updatePlayStatus: " + mPlaybackEntities.size());
        for (PlaybackEntity playbackEntity : mPlaybackEntities) {
            mPlaybackAdapter.setPlayStatus(true);
        }


    }
    /**
     * 回放上翻页
     */
    public void pageUp(int totPage) {

        if (1 == totPage || mDvrStatusInfo.getTotalPage() == 0) {
            ivPlaybackUp.setImageResource(R.mipmap.icon_up_dis);
            ivPlaybackUp.setEnabled(false);
            ivPlaybackUp.setClickable(false);
        } else {
            ivPlaybackUp.setImageResource(R.mipmap.icon_up_n);
            ivPlaybackUp.setEnabled(true);
            ivPlaybackUp.setClickable(true);
        }


    }

    /**
     * 回放下翻页
     */
    public void pageDown(int curPage) {
        if ( mDvrStatusInfo.getTotalPage() == curPage || mDvrStatusInfo.getTotalPage() == 0) {
            ivPlaybackDown.setImageResource(R.mipmap.icon_down_dis);
            ivPlaybackDown.setEnabled(false);
            ivPlaybackDown.setClickable(false);
        } else {
            ivPlaybackDown.setImageResource(R.mipmap.icon_down_n);
            ivPlaybackDown.setEnabled(true);
            ivPlaybackDown.setClickable(true);
        }


    }

    /**
     * 随心拍上翻页
     */
    public void pageWillUp(int totPage) {

        if (1 == totPage || mDvrStatusInfo.getTotalPage() == 0) {
            ivAtWillPlaybackUp.setImageResource(R.mipmap.icon_up_dis);
            ivAtWillPlaybackUp.setEnabled(false);
            ivAtWillPlaybackUp.setClickable(false);
        } else {
            ivAtWillPlaybackUp.setImageResource(R.mipmap.icon_up_n);
            ivAtWillPlaybackUp.setEnabled(true);
            ivAtWillPlaybackUp.setClickable(true);
        }


    }

    /**
     * 随心拍下翻页
     */
    public void pageWillDown(int curPage) {
        if ( mDvrStatusInfo.getTotalPage() == curPage || mDvrStatusInfo.getTotalPage() == 0) {
            ivAtWillPlaybackDown.setImageResource(R.mipmap.icon_down_dis);
            ivAtWillPlaybackDown.setEnabled(false);
            ivAtWillPlaybackDown.setClickable(false);
        } else {
            ivAtWillPlaybackDown.setImageResource(R.mipmap.icon_down_n);
            ivAtWillPlaybackDown.setEnabled(true);
            ivAtWillPlaybackDown.setClickable(true);
        }


    }
    private static final int SELECTED_FILE = 30001;
    private Handler mPlaybackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("mPlaybackHandler", " handleMessage. what = " + msg.what);
            switch (msg.what) {
                case 1:
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    if (mPlaybackAdapter != null) {
                        if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_VIDEO) {
                            mPlaybackAdapter.setPlayStatus(true);
                        } else if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                            mPlaybackAdapter.setPlayStatus(false);
                        }
                        mPlaybackAdapter.notifyDataSetChanged();
                    }
                    break;
                case 3:
                    mDvrStatusInfo.setmPlayStatus(false);
                    mDvrStatusInfo.setmEditStatus(false);
                    mDvrStatusInfo.setmClickStatus(false);
                    mDvrStatusInfo.setmEditWillStatus(false);
                    mDvrStatusInfo.setmRefreshStatus(false);
                    clNormalPlayback.setVisibility(View.VISIBLE);
                    clAtWillPlayback.setVisibility(View.GONE);
                    mMainActivity.changePage(PAGE_RECORD);
                    mMainActivity.closeDialog();
                    break;
                case 4:
                    if (mPlaybackAdapter != null) {
                        gvPlayback.setAdapter(mPlaybackAdapter);

                        if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_VIDEO) {
                            mPlaybackAdapter.setPlayStatus(true);
                        } else if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                            mPlaybackAdapter.setPlayStatus(false);
                        }
                        mPlaybackAdapter.notifyDataSetChanged();
                    }
//                    onListener(num);
                    break;

                case 5:
                    if (mPlaybackAdapter != null) {
                        mPlaybackAdapter.setPlayStatus(false);
                    }
                    break;
                case 6:
                    tvPlaybackTotPage.setText(msg.obj.toString());
                    break;
                case 7:
                    tvPlaybackCurPage.setText(msg.obj.toString());
                    pageUp((Integer) msg.obj);
                    pageDown((Integer) msg.obj);
                    break;
                case 8:
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    break;
                case 9:
                    clNormalPlayback.setVisibility(View.GONE);
                    clAtWillPlayback.setVisibility(View.VISIBLE);
                    if (mWillTotAdapter != null) { ;
                        if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {
                            mWillTotAdapter.setPlayStatus(true);
                        } else if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                            mWillTotAdapter.setPlayStatus(false);
                        }
                        mWillTotAdapter.notifyDataSetChanged();
                    }
                    break;
                case 10:
                    if (mWillTotAdapter != null) {
                        gvWillTotPlayback.setAdapter(mWillTotAdapter);
                        if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {
                            mWillTotAdapter.setPlayStatus(true);
                        } else if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                            mWillTotAdapter.setPlayStatus(false);
                        }
                        mWillTotAdapter.notifyDataSetChanged();
                    }
//                    onListener(num);
                    break;
                case 11:
                    tvAtWillTotPage.setText(msg.obj.toString());
                    break;
                case 12:
                    tvAtWillCurPage.setText(msg.obj.toString());
                    pageWillUp((Integer) msg.obj);
                    pageWillDown((Integer) msg.obj);
                    break;
                case SELECTED_FILE:
                    DvrController.getInstance().selectThumbnail((Integer) msg.obj);
                default:
                    break;
            }
        }
    };


}
