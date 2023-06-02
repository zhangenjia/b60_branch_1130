package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.DVR_Quit;
import static com.adayo.app.dvr.constant.Constant.PAGE_NOTICE;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAPWILL;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAYBACK;
import static com.adayo.app.dvr.constant.Constant.PAGE_REPLAY;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;

public class NoticeControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + NoticeControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile NoticeControl mModel = null;
    private ConstraintLayout llReplayNoticeBack;
    private DvrController mDvrController;

    private NoticeControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mDvrController = DvrController.getInstance();
    }
    public static NoticeControl getNoticeControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (RecordControl.class) {
                if (mModel == null) {
                    mModel = new NoticeControl(mainActivity);
                }
            }
        }
        return mModel;
    }
    public void initView() {
        Log.d(TAG, "initView: start");
        llReplayNoticeBack = (ConstraintLayout) findViewById(R.id.ll_replay_notice_back);
        Log.d(TAG, "initView: end");
    }
    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        llReplayNoticeBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_replay_notice_back:
                mDvrController.quit();
                DvrStatusInfo.getInstance().setmPlayStatus(false);
                break;
            default:
                break;
        }
    }
    /**
     * 显示 安全警告
     */
    public void openNotice() {
        mNoticeHandler.sendEmptyMessage(1);

    }
    private static final int CLOSE_VALUE_ONE = 1;
    private static final int CLOSE_VALUE_TWO = 2;
    public  void closeNotice(int closeType){
        if (closeType == CLOSE_VALUE_ONE){
            mNoticeHandler.sendEmptyMessage(2);
        }else if (closeType ==CLOSE_VALUE_TWO ){
            mNoticeHandler.sendEmptyMessage(3);
        }

    }
    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_Quit:
                if (value == 1) {
                    mNoticeHandler.sendEmptyMessage(4);
                }
                break;
            default:
                break;
        }
    }
    private Handler mNoticeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    DvrStatusInfo.getInstance().setmSpeedError(true);
                    mMainActivity.changePage(PAGE_NOTICE);
                    break;
                case 2:
                    DvrStatusInfo.getInstance().setmSpeedError(false);
                    mMainActivity.changePage(PAGE_PLAPWILL);
                    break;
                case 3:
                    DvrStatusInfo.getInstance().setmSpeedError(false);
                    mMainActivity.changePage(PAGE_REPLAY);
                    break;
                case 4:
                    DvrStatusInfo.getInstance().setmSpeedError(false);
                    DvrStatusInfo.getInstance().setmReplayStatus(false);
                    DvrStatusInfo.getInstance().setmFreeshotplayStatus(false);
                    DvrStatusInfo.getInstance().setmPlayBackStatus(false);
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    break;
                default:
                    break;
            }
        }
    };
}
