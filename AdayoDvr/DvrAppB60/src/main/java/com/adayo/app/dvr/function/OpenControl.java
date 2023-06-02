package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;

public class OpenControl {

    private static final String TAG = APP_TAG + OpenControl.class.getSimpleName();
    private MainActivity mMainActivity;
    private static volatile OpenControl mModel = null;

    private LinearLayout ll_record_ordinary_open_on_video;
    private LinearLayout ll_record_emergency_open_on_video;
    private LinearLayout ll_record_open_on_shot;
    private LinearLayout ll_record_open_on_mic;
    private LinearLayout ll_record_open_on_playback;
    private LinearLayout ll_record_open_on_setting;
    private LinearLayout ll_record_open_on_close;

    private ImageView iv_ordinary_open_on_video;
    private TextView tv_ordinary_open_on_video;

    private OpenControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static OpenControl getRecordControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (RecordControl.class) {
                if (mModel == null) {
                    mModel = new OpenControl(mainActivity);
                }
            }
        }
        return mModel;
    }
    public void initView() {
        Log.d(TAG, "initView: ");
        ll_record_ordinary_open_on_video = (LinearLayout) findViewById(R.id.ll_record_ordinary_open_on_video);
        ll_record_emergency_open_on_video = (LinearLayout) findViewById(R.id.ll_record_emergency_open_on_video);
        ll_record_open_on_shot = (LinearLayout) findViewById(R.id.ll_record_open_on_shot);
        ll_record_open_on_mic = (LinearLayout) findViewById(R.id.ll_record_open_on_mic);
        ll_record_open_on_playback = (LinearLayout) findViewById(R.id.ll_record_open_on_playback);
        ll_record_open_on_setting = (LinearLayout) findViewById(R.id.ll_record_open_on_setting);
        ll_record_open_on_close = (LinearLayout) findViewById(R.id.ll_record_open_on_close);

        iv_ordinary_open_on_video = (ImageView) findViewById(R.id.iv_ordinary_open_on_video);
        tv_ordinary_open_on_video = (TextView) findViewById(R.id.tv_ordinary_open_on_video);
    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }


}
