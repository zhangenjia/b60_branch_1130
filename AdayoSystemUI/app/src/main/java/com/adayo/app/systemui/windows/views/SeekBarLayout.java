package com.adayo.app.systemui.windows.views;

import static com.adayo.app.systemui.configs.SystemUIContent.SETTING_STREAM_TYPE_BT_MEDIA;
import static com.adayo.app.systemui.configs.SystemUIContent.SETTING_STREAM_TYPE_MEDIA;
import static com.adayo.app.systemui.configs.SystemUIContent.SETTING_STREAM_TYPE_PHONE;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

import android.annotation.Nullable;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.bean.SystemUISourceInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.bean.BrightnessInfo;
import com.adayo.app.systemui.bean.VolumeInfo;
import com.adayo.app.systemui.managers.business.BrightnessControllerImpl;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.managers.business.VolumeControllerImpl;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.adayosource.TecentSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SeekBarLayout extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private int type;
    private boolean responseClick;
    private boolean hasSubtract;
    private boolean hasPlus;
    private boolean hasText;
    private boolean isVertical;
    private ImageView plusView;
    private ImageView subtractView;
    private SeekBar seekBar;
    private TextView textView;

    private VolumeControllerImpl volumeController;
    private BrightnessControllerImpl brightnessController;
    private SourceControllerImpl sourceController;
    private int currentAudioSource = SETTING_STREAM_TYPE_MEDIA;

    public SeekBarLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SeekBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SeekBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.customLayout);
        type = array.getInt(R.styleable.customLayout_type, -1);
        responseClick = array.getBoolean(R.styleable.customLayout_responseClick, false);
        hasPlus = array.getBoolean(R.styleable.customLayout_hasPlus, false);
        hasSubtract = array.getBoolean(R.styleable.customLayout_hasSubtract, false);
        hasText = array.getBoolean(R.styleable.customLayout_hasText, false);
        isVertical = array.getBoolean(R.styleable.customLayout_isVertical, false);
        array.recycle();

        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(isVertical ? R.layout.vertical_seek_bar_layout :
                R.layout.custom_seek_bar_layout, this, true);
        plusView = mRootView.findViewById(R.id.plus);
        subtractView = mRootView.findViewById(R.id.subtract);
        seekBar = mRootView.findViewById(R.id.seek_bar);
        textView = mRootView.findViewById(R.id.text);
        plusView.setVisibility(hasPlus ? VISIBLE : INVISIBLE);
        subtractView.setVisibility(hasSubtract ? VISIBLE : INVISIBLE);
        textView.setVisibility(hasText ? VISIBLE : INVISIBLE);

        if (responseClick) {
            if (null != plusView) {
                plusView.setOnClickListener(v -> requestPlus());
            }
            if (null != subtractView) {
                subtractView.setOnClickListener(v -> requestSubtract());
            }
        }
        if (null != seekBar) {
            seekBar.setOnSeekBarChangeListener(this);
        }

        switch (type) {
            case SystemUIContent.VOLUME:
                sourceController = SourceControllerImpl.getInstance();
                volumeController = VolumeControllerImpl.getInstance();
                sourceController.addCallback((BaseCallback<SystemUISourceInfo>) sourceInfo -> {
                    currentAudioSource = getCurrentMediaSource();
                    LogUtil.debugD(TAG, "currentAudioSource = " + currentAudioSource);
                    setPlusViewRes(volumeController.getVolume(currentAudioSource));
                });
                currentAudioSource = getCurrentMediaSource();
                LogUtil.debugD(TAG, "currentAudioSource1 = " + currentAudioSource);
                setPlusViewRes(volumeController.getVolume(currentAudioSource));
                subtractView.setImageResource(R.drawable.icon_volume_low);
                int minValue = (currentAudioSource == SETTING_STREAM_TYPE_PHONE ? 5 : 0);
                seekBar.setMin(minValue);
                seekBar.setMax(39);
                volumeController.addCallback((BaseCallback<VolumeInfo>) volumeInfo -> {
                    if (null != volumeInfo) {
                        setPlusViewRes(volumeInfo.getAudio_stream_arr()[currentAudioSource]);
                    }
                });

                break;
            case SystemUIContent.BRIGHTNESS:
                if (null != plusView) {
                    plusView.setImageResource(R.drawable.icon_top_light);
                }
                if (null != subtractView) {
                    subtractView.setImageResource(R.drawable.icon_light);
                }
                seekBar.setMin(1);
                seekBar.setMax(10);
                brightnessController = BrightnessControllerImpl.getInstance();
                brightnessController.addCallback((BaseCallback<BrightnessInfo>) brightnessInfo -> {
                    LogUtil.debugD(SystemUIContent.TAG, "type = " + type + " ; getBrightness = " + brightnessInfo.getBrightness());
                    if (null != brightnessInfo) {
                        if (null != seekBar) {
                            seekBar.setProgress(brightnessInfo.getBrightness());
                        }
                        if (hasText && null != textView) {
                            textView.setText(brightnessInfo.getBrightness() + "");
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    private void setPlusViewRes(int progress) {
        LogUtil.debugD(SystemUIContent.TAG, "type = " + type + " ; progress = " + progress  + " ; currentAudioSource = " + currentAudioSource );
        if (null != seekBar) {
            int minValue = (currentAudioSource == SETTING_STREAM_TYPE_PHONE ? 5 : 0);
            seekBar.setMin(minValue);
            seekBar.setMax(39);
            seekBar.setProgress(progress);
        }
        if (hasText && null != textView) {
            textView.setText(progress + "");
        }
        if (null != plusView) {
            if (0 == progress) {
                plusView.setImageResource(R.drawable.icon_mute);
            } else if (10 >= progress) {
                plusView.setImageResource(R.drawable.icon_volume_1);
            } else if (25 >= progress) {
                plusView.setImageResource(R.drawable.icon_volume_2);
            } else if (40 >= progress) {
                plusView.setImageResource(R.drawable.icon_volume_3);
            }
        }
    }

    private void requestPlus() {
        if (null != seekBar) {
            requestSeekChange(seekBar.getProgress() + 1);
        }
    }

    private void requestSubtract() {
        requestSeekChange(seekBar.getProgress() - 1);
    }

    private void requestSeekChange(int progress) {
        LogUtil.debugD(SystemUIContent.TAG, "type = " + type + " ; progress = " + progress  + " ; currentAudioSource = " + currentAudioSource );
        switch (type) {
            case SystemUIContent.VOLUME:
                if (null != volumeController) {
                    volumeController.setVolume(currentAudioSource, progress);
                }
                break;
            case SystemUIContent.BRIGHTNESS:
                if (null != brightnessController) {
                    brightnessController.setBrightness(progress);
                }
                break;
            default:
                break;
        }
    }

    private int getCurrentMediaSource() {
        Stack<String> mStack = SourceControllerImpl.getInstance().getAudioSources();
        if (null == mStack) {
            return SETTING_STREAM_TYPE_MEDIA;
        }
        List<String> sourceList = new ArrayList<>(mStack);
        for (int i = sourceList.size() - 1 ; i >=0; i--) {
            switch (sourceList.get(i)) {
                case AdayoSource.ADAYO_SOURCE_BT_PHONE:
                    return SETTING_STREAM_TYPE_PHONE;
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return SETTING_STREAM_TYPE_BT_MEDIA;
                case AdayoSource.ADAYO_SOURCE_NAVI:
                case TecentSource.TECENT_WECHAT:
                case AdayoSource.ADAYO_SOURCE_VR:
                    break;
                default:
                    return SETTING_STREAM_TYPE_MEDIA;
            }
        }
        return SETTING_STREAM_TYPE_MEDIA;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            requestSeekChange(progress);
            if (hasText && null != textView) {
                textView.setText(progress + "");
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        requestSeekChange(seekBar.getProgress());
        if (hasText && null != textView) {
            textView.setText(seekBar.getProgress() + "");
        }
    }
}
