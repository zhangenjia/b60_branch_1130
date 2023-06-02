package com.adayo.app.dvr.function;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.utils.SystemPropertiesPresenter;
import com.adayo.app.dvr.widget.CustomDialog;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import static com.adayo.app.dvr.constant.Constant.DVR_CB_KEY_ST_PLAYVIDEO;
import static com.adayo.app.dvr.constant.Constant.DVR_DeleteCurDocument;
import static com.adayo.app.dvr.constant.Constant.DVR_LastVideoOrPhoto;
import static com.adayo.app.dvr.constant.Constant.DVR_NextVideoOrPhoto;
import static com.adayo.app.dvr.constant.Constant.DVR_Pause;
import static com.adayo.app.dvr.constant.Constant.DVR_Play;
import static com.adayo.app.dvr.constant.Constant.DVR_Quit;
import static com.adayo.app.dvr.constant.Constant.DVR_St_ThumbnailOpera;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAYBACK;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import androidx.constraintlayout.widget.ConstraintLayout;

public class FreeshotPlayControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + FreeshotPlayControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile FreeshotPlayControl mModel = null;
    private static final String comFigHigh = "HM6C17A";
    private ConstraintLayout llFreeshotReplayBack;
    public String model ;
    private DvrController mDvrController;

    private CustomDialog mUploadDialog;

    private ImageView ivFreeshotReplayRewind,
            ivFreeshotReplayPlayOrPause, ivFreeshotReplayForward,
            ivFreeshotReplayUpload, ivFreeshotReplayDelete;

    private FreeshotPlayControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mDvrController = DvrController.getInstance();
    }

    public static FreeshotPlayControl getFreeshotPlayControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (FreeshotPlayControl.class) {
                if (mModel == null) {
                    mModel = new FreeshotPlayControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView: start");
        llFreeshotReplayBack = (ConstraintLayout) findViewById(R.id.ll_freeshot_replay_back);
        ivFreeshotReplayRewind = (ImageView) findViewById(R.id.iv_freeshot_replay_rewind);
        ivFreeshotReplayPlayOrPause = (ImageView) findViewById(R.id.iv_freeshot_replay_play_or_pause);
        ivFreeshotReplayForward = (ImageView) findViewById(R.id.iv_freeshot_replay_forward);
        ivFreeshotReplayUpload = (ImageView) findViewById(R.id.iv_freeshot_replay_upload);
        ivFreeshotReplayDelete = (ImageView) findViewById(R.id.iv_freeshot_replay_delete);
        model = SystemPropertiesPresenter.getInstance().getProperty("ro.project.name", "123456");
        if (comFigHigh.equals(model)){
            ivFreeshotReplayUpload.setVisibility(View.VISIBLE);
        }else{
            ivFreeshotReplayUpload.setVisibility(View.GONE);
        }
        Log.d(TAG, "initView: end");

    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: start");
        llFreeshotReplayBack.setOnClickListener(this);
        ivFreeshotReplayRewind.setOnClickListener(this);
        ivFreeshotReplayPlayOrPause.setOnClickListener(this);
        ivFreeshotReplayForward.setOnClickListener(this);
        ivFreeshotReplayUpload.setOnClickListener(this);
        ivFreeshotReplayDelete.setOnClickListener(this);

        Log.d(TAG, "initListener: end");
    }

    /**
     * 更新视频按钮状态
     */
    public void updatePlayOrPauseStatus(boolean fIsplay) {
        ivFreeshotReplayPlayOrPause.setVisibility(View.VISIBLE);
        if (comFigHigh.equals(model)){
            ivFreeshotReplayUpload.setVisibility(View.VISIBLE);
        }else{
            ivFreeshotReplayUpload.setVisibility(View.GONE);
        }
        if (!fIsplay) {
            AAOP_HSkin
                    .with(ivFreeshotReplayRewind)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_left_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivFreeshotReplayPlayOrPause)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_play_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivFreeshotReplayForward)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC,R.drawable.btn_right_bg)
                    .applySkin(false);
            //暂停状态，删除和移动按钮恢复正常
            AAOP_HSkin
                    .with(ivFreeshotReplayDelete)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_delete_bg)
                    .applySkin(false);
            ivFreeshotReplayDelete.setEnabled(true);
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.ATWILL)) {
                AAOP_HSkin
                        .with(ivFreeshotReplayUpload)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_upload_bg)
                        .applySkin(false);
            } else {
                AAOP_HSkin
                        .with(ivFreeshotReplayUpload)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_upload_bg)
                        .applySkin(false);
            }
            ivFreeshotReplayUpload.setEnabled(true);
        } else {
            AAOP_HSkin
                    .with(ivFreeshotReplayRewind)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_rewind_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivFreeshotReplayPlayOrPause)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_pause_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivFreeshotReplayForward)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_forward_bg)
                    .applySkin(false);
            //播放状态，删除和移动按钮失效
            AAOP_HSkin
                    .with(ivFreeshotReplayDelete)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_big_delete_dis)
                    .applySkin(false);
            ivFreeshotReplayDelete.setEnabled(false);
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.ATWILL)) {
                AAOP_HSkin
                        .with(ivFreeshotReplayUpload)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_upload_dis)
                        .applySkin(false);
            } else {
                AAOP_HSkin
                        .with(ivFreeshotReplayUpload)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_upload_dis)
                        .applySkin(false);
            }
            ivFreeshotReplayUpload.setEnabled(false);
        }
    }

    /**
     * 更新照片状态栏
     */
    private void updatePhotoStatus() {
        Log.d(TAG, "updatePhotoStatus: " + DvrStatusInfo.getInstance().getType());
       // 判断是视频还是图片
        ivFreeshotReplayPlayOrPause.setVisibility(View.GONE);
        if (comFigHigh.equals(model)){
            ivFreeshotReplayUpload.setVisibility(View.VISIBLE);
        }else {
            ivFreeshotReplayUpload.setVisibility(View.GONE);
        }


        AAOP_HSkin
                .with(ivFreeshotReplayRewind)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_left_bg)
                .applySkin(false);
        AAOP_HSkin
                .with(ivFreeshotReplayForward)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_right_bg)
                .applySkin(false);
    }
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_freeshot_replay_back:
                mDvrController.quit();
                DvrStatusInfo.getInstance().setmPlayStatus(false);
                DvrStatusInfo.getInstance().setmWillPlaystatus(false);


                break;
            case R.id.iv_freeshot_replay_rewind:
                Log.d(TAG, "onClick: currentStatus ==" + DvrStatusInfo.getInstance().getType());
                //播放状态，快退 暂停状态 上一曲
                if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {
                    //视频
                    if (!isPlay) {
                        mDvrController.prev();
                        isPlay = true;
                    } else {
                        mDvrController.rewind();
                    }
                } else {
                    //图片
                    mDvrController.prev();
                }


                break;
            case R.id.iv_freeshot_replay_play_or_pause:
                playOrPause(isPlay);
                break;
            case R.id.iv_freeshot_replay_forward:
                Log.d(TAG, "onClick: currentStatus ==" + DvrStatusInfo.getInstance().getAtWillType());
                //播放状态 快进 暂停状态 下一曲
                if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {
                    //视频
                    if (!isPlay) {
                        mDvrController.next();
                        isPlay = true;
                    } else {
                        mDvrController.fastForward();
                    }
                } else {
                    //图片
                    mDvrController.next();
                }


                break;
            case R.id.iv_freeshot_replay_upload:
                uploadDocument();
                break;
            case R.id.iv_freeshot_replay_delete:
                deleteDocument();
                break;
            default:
                break;
        }
    }

    private boolean isPlay = true;

    public void playOrPause(boolean fIsplay) {
        if (fIsplay) {
            DvrController.getInstance().pause();
            isPlay = false;
        } else {
            DvrController.getInstance().play();
            isPlay = true;
        }
    }

    private void uploadDocument() {
        int text3 = 0;
        int text4 = 0;
        int text5 = 0;

        text3 = R.string.text_replay_upload_title;
        text4 = R.string.text_edit_upload_body;


        mUploadDialog = CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL,R.layout.layout_custom_dialog)
                .setText(text3, text4, text5)
                .setOkText(R.string.text_edit_upload_confirm)
                .setCancelText(R.string.text_cancel)
                .setImageView(R.mipmap.icon_singlerow_upload)
                .initClickListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void ok() {

                        DvrController.getInstance().uploadAtWill();


                    }

                    @Override
                    public void cancel() {

                    }
                });
        mUploadDialog.showDialog();
    }

    private void deleteDocument() {
        int text = 0;
        int text1 = 0;
        if (Constant.PLAY_TYPE_ATWILL_PHOTO.equals(DvrStatusInfo.getInstance().getAtWillType())) {
            text = R.string.text_delete_current_photo;
            text1 = R.string.text_replay_delete_picture1;
        } else {
            text = R.string.text_delete_current_video;
            text1 = R.string.text_replay_delete_video;
        }
        CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL,R.layout.layout_custom_dialog_mid)
                .setText(text, text1, 0)
                .setOkText(R.string.text_replay_delete)
                .setCancelText(R.string.text_cancel)
                .setImageView(R.mipmap.icon_singlerow_delete)
                .initClickListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void ok() {
                        DvrController.getInstance().deleteCurrentDoc();
                        DvrStatusInfo.getInstance().setmFreeshotplayStatus(true);
                    }

                    @Override
                    public void cancel() {

                    }
                })
                .showDialog();
    }
    private static final int FREE_SHOT_CHANGE_PIC = 1007;
    private Handler mFreeshotPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!DvrStatusInfo.getInstance().ismSpeedError()){
                        mMainActivity.changePage(Constant.PAGE_PLAPWILL);
                    }
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case 2:
                    mMainActivity.changePage(Constant.PAGE_PLAPWILL);
                    updatePhotoStatus();
                    break;
                case 3:
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    DvrStatusInfo.getInstance().setmFreeshotplayStatus(false);
                    break;
                case 4:
                    if(!isPlay){
                        isPlay = true;
                    }
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case 5:
                    DvrController.getInstance().play();
                    break;
                case 6:
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case FREE_SHOT_CHANGE_PIC:
                    updatePhotoStatus();
                    break;
                default:
                    break;
            }

        }
    };
    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_DeleteCurDocument:
                Log.d(TAG, "notifyChange: DVR_DeleteCurDocument replay " +value);
                if(value == 1){
                    if (DvrStatusInfo.getInstance().getAtWillType().equals(Constant.PLAY_TYPE_ATWILL_VIDEO)) {
                        mFreeshotPlayHandler.sendEmptyMessage(4);
                    } else {
                        mFreeshotPlayHandler.sendEmptyMessage(FREE_SHOT_CHANGE_PIC);
                    }
                }else {
                    Log.d(TAG, "notifyChange: DVR_DeleteCurDocument==fail");
                }
                break;
            case DVR_St_ThumbnailOpera:
                mFreeshotPlayHandler.sendEmptyMessage(5);
            case DVR_Play:
                if (value == 1) {
                    if (DvrStatusInfo.getInstance().getAtWillType().equals(Constant.PLAY_TYPE_ATWILL_VIDEO)) {
                        if (!isPlay){
                            isPlay = true;
                        }
                        mFreeshotPlayHandler.sendEmptyMessage(1);
                    } else {
                        mFreeshotPlayHandler.sendEmptyMessage(2);
                    }
                } else {
                    Log.d(TAG, "notifyChange: replay==failure");
                }

                break;
            case DVR_Pause:
            case DVR_NextVideoOrPhoto:
            case DVR_LastVideoOrPhoto:
                if (value == 1) {

                    if (DvrStatusInfo.getInstance().getAtWillType().equals(Constant.PLAY_TYPE_ATWILL_VIDEO)) {
                        mFreeshotPlayHandler.sendEmptyMessage(1);
                    } else {
                        mFreeshotPlayHandler.sendEmptyMessage(2);
                    }
                } else {
                    Log.d(TAG, "notifyChange: replay==failure");
                }

                break;
            case DVR_CB_KEY_ST_PLAYVIDEO:
                if (DvrStatusInfo.getInstance().getAtWillType().equals(Constant.PLAY_TYPE_ATWILL_VIDEO)) {
                    if (value == 0) {
                        isPlay = false;
                    } else {
                        isPlay = true;
                    }
                    mFreeshotPlayHandler.sendEmptyMessage(6);
                }
                break;
            case DVR_Quit:
                if (value == 1) {
                    mFreeshotPlayHandler.sendEmptyMessage(3);
                }
                break;
            default:
                break;
        }
    }



}

