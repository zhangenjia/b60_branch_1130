package com.adayo.app.systemui.windows.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.business.VolumeControllerImpl;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class VolumeDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {
    private volatile static VolumeDialog volumePopup = null;

    private ImageView volIcon;
    private SeekBar volSeekBar;
    private TextView volText;

    private int type = SystemUIContent.SETTING_STREAM_TYPE_MEDIA;

    private VolumeDialog(Context context) {
        super(context, R.style.TransparentStyle);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setType(2020);
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.y = 21;
        layoutParams.width = 830;
        layoutParams.height = 180;
        window.setAttributes(layoutParams);
        setContentView(R.layout.volum_dialog_layout);
        AAOP_HSkin.getWindowViewManager().addWindowView(this.findViewById(android.R.id.content));
        setCanceledOnTouchOutside(false);
        volIcon = findViewById(R.id.subtract);
        volSeekBar = findViewById(R.id.seek_bar);
        volSeekBar.setOnSeekBarChangeListener(this);
        volText = findViewById(R.id.text);
    }

    public static VolumeDialog getInstance() {
        if (volumePopup == null) {
            synchronized (VolumeDialog.class) {
                if (volumePopup == null) {
                    volumePopup = new VolumeDialog(SystemUIApplication.getSystemUIContext());
                }
            }
        }
        return volumePopup;
    }

    private final int DISMISS_VOLUME_POPUP = 10001;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISMISS_VOLUME_POPUP:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void resetHandle() {
        mHandler.removeMessages(DISMISS_VOLUME_POPUP);
        mHandler.sendEmptyMessageDelayed(DISMISS_VOLUME_POPUP, SystemUIContent.VOLUME_POPUP_DELAYED);
    }

    public synchronized void showVolumePanel(int type, int volume) {
        if (volIcon == null || volSeekBar == null || volText == null) {
            return;
        }
        if (type == SystemUIContent.SETTING_STREAM_TYPE_MEDIA ||
                type == SystemUIContent.SETTING_STREAM_TYPE_PHONE ||
                type == SystemUIContent.SETTING_STREAM_TYPE_TTS ||
                type == SystemUIContent.SETTING_STREAM_TYPE_RING ||
                type == SystemUIContent.SETTING_STREAM_TYPE_BT_MEDIA ||
                type == SystemUIContent.SETTING_STREAM_TYPE_NAVI) {
            switch (type) {
                case SystemUIContent
                        .SETTING_STREAM_TYPE_MEDIA:
                    volIcon.setImageResource(R.drawable.icon_media_volume);
                    volSeekBar.setMin(0);
                    break;
                case SystemUIContent
                        .SETTING_STREAM_TYPE_PHONE:
                    volIcon.setImageResource(R.drawable.icon_phone_volume);
                    volSeekBar.setMin(5);
                    break;
                case SystemUIContent
                        .SETTING_STREAM_TYPE_TTS:
                    volIcon.setImageResource(R.drawable.icon_voice_volume);
                    volSeekBar.setMin(5);
                    break;
                case SystemUIContent
                        .SETTING_STREAM_TYPE_NAVI:
                    volIcon.setImageResource(R.drawable.icon_navigation_volume);
                    volSeekBar.setMin(0);
                    break;
                case SystemUIContent
                        .SETTING_STREAM_TYPE_RING:
                    volIcon.setImageResource(R.drawable.icon_incoming_call_ringtone_volume);
                    volSeekBar.setMin(0);
                    break;
                case SystemUIContent
                        .SETTING_STREAM_TYPE_BT_MEDIA:
                    volIcon.setImageResource(R.drawable.icon_bt_music_volume);
                    volSeekBar.setMin(0);
                    break;
            }
            if (volume >= volSeekBar.getMin() && volume <= volSeekBar.getMax()) {
                volSeekBar.setProgress(volume);
                volText.setText(volume + "");
            }
            this.type = type;
            if (!isShowing()) {
                super.show();
            }
            resetHandle();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void dismiss() {
        mHandler.removeMessages(DISMISS_VOLUME_POPUP);
        super.dismiss();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        resetHandle();
        if (fromUser) {
            if (null != volText) {
                volText.setText(progress + "");
            }
            VolumeControllerImpl.getInstance().setVolume(type, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        resetHandle();
        if (null != volText) {
            volText.setText(seekBar.getProgress() + "");
        }
        VolumeControllerImpl.getInstance().setVolume(type, seekBar.getProgress());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getRawY() < 509 || event.getRawX() < 484 ||
                event.getRawY() > 686 || event.getRawX() > 1410) {
            LogUtil.debugD(SystemUIContent.TAG, "dismiss");
            dismiss();
        }
        return true;
    }
}
