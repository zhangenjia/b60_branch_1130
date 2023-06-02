package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.DVR_CB_KEY_ST_PLAYVIDEO;
import static com.adayo.app.dvr.constant.Constant.DVR_DeleteCurDocument;
import static com.adayo.app.dvr.constant.Constant.DVR_LastVideoOrPhoto;
import static com.adayo.app.dvr.constant.Constant.DVR_MoveCurDocumentToNoneDeleteArea;
import static com.adayo.app.dvr.constant.Constant.DVR_NextVideoOrPhoto;
import static com.adayo.app.dvr.constant.Constant.DVR_Pause;
import static com.adayo.app.dvr.constant.Constant.DVR_Play;
import static com.adayo.app.dvr.constant.Constant.DVR_Quit;
import static com.adayo.app.dvr.constant.Constant.DVR_St_ThumbnailOpera;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAYBACK;
import static com.adayo.app.dvr.constant.Constant.PLAY_TYPE_VIDEO;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.widget.CustomDialog;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class ReplayControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + ReplayControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile ReplayControl mModel = null;

    private ConstraintLayout llReplayBack;
    private DvrController mDvrController;
    private ImageView ivReplayRewind,
            ivReplayPlayOrPause, ivReplayForward,
            ivReplayMove, ivReplayDelete;

    private CarPropertyManager mCarPropertyManager;
    private Car mCar;

    private ReplayControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mDvrController = DvrController.getInstance();
    }

    public static ReplayControl getReplayControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (ReplayControl.class) {
                if (mModel == null) {
                    mModel = new ReplayControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    private void initCarPropertyManager() {
        Log.d(TAG, " initCarPropertyManager() begin");

        if (mMainActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            mCar = Car.createCar(mMainActivity, mCarServiceConnection);
            mCar.connect();
        }

        Log.d(TAG, " initCarPropertyManager() end");
    }

    private final ServiceConnection mCarServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                Log.d(TAG, " onServiceConnected() begin");
                mCarPropertyManager = (CarPropertyManager) mCar.getCarManager(Car.PROPERTY_SERVICE);
                initCarPropertyListener(); //初始化Listener
                Log.d(TAG, " onServiceConnected() end");
            } catch (CarNotConnectedException e) {
                Log.e(TAG, " onServiceConnected() CarNotConnectedException e = " + e.toString());
            } catch (Exception e) {
                Log.e(TAG, " onServiceConnected() Exception e = " + e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, " onServiceDisconnected()");
            unRegisterCarPropertyListener();
            mCarPropertyManager = null;
        }
    };
    private static final int FREE_REPLAY_SHOT_CHANGE_PIC = 1007;
    private Handler mReplayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("mUIHandler", " handleMessage. what = " + msg.what);
            switch (msg.what) {

                case 1:
                    if (!DvrStatusInfo.getInstance().ismSpeedError()){
                        mMainActivity.changePage(Constant.PAGE_REPLAY);
                    }
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case 2:
                    mMainActivity.changePage(Constant.PAGE_REPLAY);
                    updatePhotoStatus();
                    break;
                case 3:
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    DvrStatusInfo.getInstance().setmReplayStatus(false);
                    break;
                case 4:
                case 5:
                    if(!isPlay){
                        isPlay = true;
                    }
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case 6:
                    DvrController.getInstance().play();
                    break;
                case 7:
                    updatePlayOrPauseStatus(isPlay);
                    break;
                case FREE_REPLAY_SHOT_CHANGE_PIC:
                    updatePhotoStatus();
                    break;
                default:
                    break;
            }
        }
    };


    public void initView() {
        Log.d(TAG, "initView: " + DvrStatusInfo.getInstance().getType());
        llReplayBack = (ConstraintLayout) findViewById(R.id.ll_replay_back);
        ivReplayRewind = (ImageView) findViewById(R.id.iv_replay_rewind);
        ivReplayPlayOrPause = (ImageView) findViewById(R.id.iv_replay_play_or_pause);
        ivReplayForward = (ImageView) findViewById(R.id.iv_replay_forward);
        ivReplayMove = (ImageView) findViewById(R.id.iv_replay_move);
        ivReplayDelete = (ImageView) findViewById(R.id.iv_replay_delete);


    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: ");
        llReplayBack.setOnClickListener(this);
        ivReplayRewind.setOnClickListener(this);
        ivReplayPlayOrPause.setOnClickListener(this);
        ivReplayForward.setOnClickListener(this);
        ivReplayMove.setOnClickListener(this);
        ivReplayDelete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_replay_back:
                mDvrController.quit();
                DvrStatusInfo.getInstance().setmPlayStatus(false);
                DvrStatusInfo.getInstance().setmPlayBackStatus(false);

                break;
            case R.id.iv_replay_rewind:
                Log.d(TAG, "onClick: currentStatus ==" + DvrStatusInfo.getInstance().getType());
                //播放状态，快退 暂停状态 上一曲
                if (DvrStatusInfo.getInstance().getType() == PLAY_TYPE_VIDEO) {
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
            case R.id.iv_replay_play_or_pause:
                playOrPause(isPlay);
                break;
            case R.id.iv_replay_forward:
                Log.d(TAG, "onClick: currentStatus ==" + DvrStatusInfo.getInstance().getType());
                //播放状态 快进 暂停状态 下一曲
                if (DvrStatusInfo.getInstance().getType() == PLAY_TYPE_VIDEO) {
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
            case R.id.iv_replay_move:
                moveDocument();
                break;
            case R.id.iv_replay_delete:
                deleteDocument();
                break;
            default:
                break;
        }
    }

    public boolean isPlay = true;

    public void playOrPause(boolean nisPlay) {
        if (nisPlay) {
            DvrController.getInstance().pause();
            isPlay = false;
        } else {
            DvrController.getInstance().play();
            isPlay = true;
        }
    }

    /**
     * 更新视频按钮状态
     */
    public void updatePlayOrPauseStatus(boolean nisPlay) {
        ivReplayPlayOrPause.setVisibility(View.VISIBLE);
        ivReplayMove.setVisibility(View.VISIBLE);
        if (!nisPlay) {
            AAOP_HSkin
                    .with(ivReplayRewind)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_left_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivReplayPlayOrPause)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_play_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivReplayForward)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_right_bg)
                    .applySkin(false);
            //暂停状态，删除和移动按钮恢复正常
            AAOP_HSkin
                    .with(ivReplayDelete)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_delete_bg)
                    .applySkin(false);
            ivReplayDelete.setEnabled(true);
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.EMERGENCY)) {
                ivReplayMove.setImageResource(R.drawable.btn_moveout_emergency);
                AAOP_HSkin
                        .with(ivReplayMove)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_moveout_emergency)
                        .applySkin(false);
            } else {
                AAOP_HSkin
                        .with(ivReplayMove)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_moveto_emergency)
                        .applySkin(false);
            }
            ivReplayMove.setEnabled(true);
        } else {
            AAOP_HSkin
                    .with(ivReplayRewind)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_rewind_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivReplayPlayOrPause)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_pause_bg)
                    .applySkin(false);
            AAOP_HSkin
                    .with(ivReplayForward)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_forward_bg)
                    .applySkin(false);
            //播放状态，删除和移动按钮失效
            AAOP_HSkin
                    .with(ivReplayDelete)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_big_delete_dis)
                    .applySkin(false);
            ivReplayDelete.setEnabled(false);
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.EMERGENCY)) {
                AAOP_HSkin
                        .with(ivReplayMove)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_transfer_out_emergency_dis)
                        .applySkin(false);
            } else {
                AAOP_HSkin
                        .with(ivReplayMove)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.icon_transfer_emergency_dis)
                        .applySkin(false);
            }
            ivReplayMove.setEnabled(false);
        }
    }

    /**
     * 更新照片状态栏
     */
    private void updatePhotoStatus() {
        Log.d(TAG, "updatePhotoStatus: " + DvrStatusInfo.getInstance().getType());
        ivReplayPlayOrPause.setVisibility(View.GONE);
        ivReplayMove.setVisibility(View.GONE);
        AAOP_HSkin
                .with(ivReplayRewind)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_left_bg)
                .applySkin(false);
        AAOP_HSkin
                .with(ivReplayForward)
                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.btn_right_bg)
                .applySkin(false);

    }

    private void moveDocument() {
        if (DvrStatusInfo.getInstance().getMode().equals(Constant.EMERGENCY)) {
            CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                    .setText(R.string.text_edit_move_to_normal,
                           R.string.text_move_current_to_normal1,
                           R.string.text_move_current_to_normal1)
                    .setOkText(R.string.text_ok)
                    .setCancelText(R.string.text_cancel)
                    .setImageView(R.mipmap.icon_singlerow_transfer)
                    .initClickListener(new CustomDialog.OnClickListener() {
                        @Override
                        public void ok() {
                            DvrController.getInstance().moveCurrentToNormal();
                        }

                        @Override
                        public void cancel() {

                        }
                    })
                    .showDialog();
        } else if(DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
            CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                    .setText(R.string.text_edit_move_to_emergency,
                            R.string.text_move_current_to_emergency1,
                            R.string.text_move_current_to_emergency1)
                    .setOkText(R.string.text_ok)
                    .setCancelText(R.string.text_cancel)
                    .setImageView(R.mipmap.icon_singlerow_transfer)
                    .initClickListener(new CustomDialog.OnClickListener() {
                        @Override
                        public void ok() {
                            DvrController.getInstance().moveCurrentToEmergency();
                        }

                        @Override
                        public void cancel() {

                        }
                    })
                    .showDialog();
        }
    }

    private void deleteDocument() {
        int text = 0;
        int text1 = 0 ;
        if (DvrStatusInfo.getInstance().getType().equals(Constant.PLAY_TYPE_VIDEO)) {
            text = R.string.text_delete_current_video;
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                text1 =R.string.text_edit_del_normal;
            } else {
                text1 = R.string.text_edit_del_normal;
            }
        } else {
            text = R.string.text_delete_current_photo;
            text1 = R.string.text_replay_delete_picture1;

        }
        CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                .setText(text, text1,0)
                .setOkText(R.string.text_replay_delete)
                .setCancelText(R.string.text_cancel)
                .setImageView(R.mipmap.icon_singlerow_delete)
                .initClickListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void ok() {
                        DvrController.getInstance().deleteCurrentDoc();
                        DvrStatusInfo.getInstance().setmReplayStatus(true);
                    }

                    @Override
                    public void cancel() {

                    }
                })
                .showDialog();
    }

    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_DeleteCurDocument:
                Log.d(TAG, "notifyChange: DVR_DeleteCurDocument replay " +value);
                if(value == 1){

                    if (DvrStatusInfo.getInstance().getType().equals(PLAY_TYPE_VIDEO)) {
                        mReplayHandler.sendEmptyMessage(4);
                    } else {
                        mReplayHandler.sendEmptyMessage(FREE_REPLAY_SHOT_CHANGE_PIC);
                    }
                }else {
                    Log.d(TAG, "notifyChange: DVR_DeleteCurDocument==fail");
                }
                break;
            case DVR_MoveCurDocumentToNoneDeleteArea:
                Log.d(TAG, "notifyChange: DVR_MoveCurDocumentToNoneDeleteArea" + value);
                if(value == 1 ){
                    mReplayHandler.sendEmptyMessage(5);
                }else {
                    Log.d(TAG, "notifyChange: DVR_MoveCurDocumentToNoneDeleteArea == fail");
                }
                break;
            case DVR_St_ThumbnailOpera:
                    mReplayHandler.sendEmptyMessage(6);
                 break;
            case DVR_Play:
                if (value == 1) {
                    if (DvrStatusInfo.getInstance().getType().equals(PLAY_TYPE_VIDEO)) {
                        if (!isPlay){
                            isPlay = true;
                        }
                        mReplayHandler.sendEmptyMessage(1);
                    } else {
                        mReplayHandler.sendEmptyMessage(2);
                    }
                } else {
                    Log.d(TAG, "notifyChange: replay==failure");
                }

                break;
            case DVR_Pause:
            case DVR_NextVideoOrPhoto:
            case DVR_LastVideoOrPhoto:
                if (value == 1) {
                    if (DvrStatusInfo.getInstance().getType().equals(PLAY_TYPE_VIDEO)) {
                        mReplayHandler.sendEmptyMessage(1);
                    } else {
                        mReplayHandler.sendEmptyMessage(2);
                    }
                } else {
                    Log.d(TAG, "notifyChange: replay==failure");
                }

                break;
            case DVR_CB_KEY_ST_PLAYVIDEO:
                if (DvrStatusInfo.getInstance().getType().equals(PLAY_TYPE_VIDEO)){
                    if (value == 0 ){
                        isPlay = false;
                    }else{
                        isPlay = true;
                    }
                    mReplayHandler.sendEmptyMessage(7);
                }
                break;
            case DVR_Quit:
                if (value == 1){
                    mReplayHandler.sendEmptyMessage(3);
                }
                break;
            default:
                break;

        }

    }

    /**
     * 注册监听DRIVER_DISPLAY_DRIVE_MODE_VALUE档位信息
     */
    private void initCarPropertyListener() {
        Log.d(TAG, " initCarPropertyListener() begin");
        try {
            boolean registerRet = mCarPropertyManager.registerListener(carPropertyEventListener, VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, 0);
            if (!registerRet) {
                Log.d(TAG, " initCarPropertyListener() register Failed");
            }
        } catch (CarNotConnectedException e) {
            Log.e(TAG, " onServiceConnected() CarNotConnectedException e = " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, " onServiceConnected() Exception e = " + e.toString());
        }
        Log.d(TAG, " initCarPropertyListener() end");
    }

    private void unRegisterCarPropertyListener() {
        Log.d(TAG, " unRegisterCarPropertyListener() begin");
        try {
            mCarPropertyManager.unregisterListener(carPropertyEventListener);
        } catch (Exception e) {
            Log.e(TAG, " unRegisterCarPropertyListener() Exception e = " + e.toString());
        }
        Log.d(TAG, " unRegisterCarPropertyListener() end");
    }

    /**
     * P档位信息回调
     */
    private final CarPropertyManager.CarPropertyEventListener carPropertyEventListener = new CarPropertyManager.CarPropertyEventListener() {

        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            if (null == carPropertyValue) {
                Log.d(TAG, " onChangeEvent() carPropertyValue is null");
                return;
            }

            int value = (int) carPropertyValue.getValue();
            Log.d(TAG, " onChangeEvent() begin carPropertyValue :" + value);
            //callback notify

            //P档信息
            if (value == 0) {

            } else {

            }

            Log.d(TAG, " onChangeEvent() end");
        }

        @Override
        public void onErrorEvent(int var1, int var2) {
            Log.e(TAG, " onErrorEvent() var1 = " + var1 + " var2 = " + var2);
        }
    };


}
