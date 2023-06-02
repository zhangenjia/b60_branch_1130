package com.adayo.app.launcher.radio;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.radio.constant.SystemPropertiesPresenter;
import com.adayo.app.launcher.radio.constant.Utils;
import com.adayo.app.launcher.radio.dataMng.RadioDataMng;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.tuner.radio.RadioManager;
import com.airbnb.lottie.LottieAnimationView;

import java.util.EmptyStackException;
import java.util.Timer;
import java.util.TimerTask;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import static com.adayo.app.launcher.radio.constant.Constants.BAND_AM;
import static com.adayo.app.launcher.radio.constant.Constants.BAND_FM;
import static com.adayo.app.launcher.radio.constant.Constants.RADIO_CALLBACK_UPDATEVIEW;
import static com.adayo.app.launcher.radio.constant.Constants.PLAY_ANIMATION;
import static com.adayo.app.launcher.radio.constant.Constants.RADIO_UPDATEVIEW;
import static com.adayo.app.launcher.radio.constant.Constants.SYSTEM_PROPERTY_REQUEST_AUDIO_FOCUS_KEY;
import static com.adayo.app.launcher.radio.constant.Constants.isUnMute;
import static com.adayo.app.launcher.radio.constant.Utils.ATTR_JSON_IMG_VIEW;

public class RadioCarView extends RelativeLayout implements View.OnClickListener, IViewBase {

    private static final String TAG = RadioCarView.class.getSimpleName();
    private Context mContext;
    private boolean serviceConnectStata;
    private View mCarView;
    private View mSmallCarView;
    private ImageView mSmallCarPlay;
    private ImageView mSmallCarDefaultIcon;
    private SeekBar seekBarFM;
    private SeekBar seekBarAM;
    private ImageView mPrevFreq;
    private ImageView mNextFreq;
    private ImageView mPlayBtn;
    private ImageView turnUP;
    private ImageView turnDown;
    private TextView mFreqText;
    private LottieAnimationView boduanFM;
    private LottieAnimationView boduanAM;
    private int progressMax = 0;
    private int progressMas = 0;
    private int fmProgressCurrentFreq;
    private int amProgressCurrentFreq;
    private String fmProgress;
    private String amProgress;
    private int mAMFreq;
    private double mFMFreq;
    private TextView unitTV;
    private String currenBand = "FM";
    private TextView smallCarFreq;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(Utils.TAG, TAG + " handleMessage: start msg = " + msg);
            if (msg == null) {
                return;
            }
            Log.d(Utils.TAG, TAG + " handleMessage: msg.what = " + msg.what);
            switch (msg.what) {
                case RADIO_UPDATEVIEW:
                case RADIO_CALLBACK_UPDATEVIEW:
                    updateView();
                    break;
                case PLAY_ANIMATION:
                    if (CURRENT_THEME != -1) {
                        if (CURRENT_THEME == 2) {
                            Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                            card_name_animator.setTarget(tv_radio_small);
                            card_name_animator.start();
                        }
                        lottie_view.setAnimation("radio" + ConstantUtil.CURRENT_THEME + ".json");
                        lottie_view.playAnimation();
                        lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                AAOP_HSkin.with(lottie_view)
                                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.radio45)
                                        .applySkin(false);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
            Log.d(TAG, TAG + " handleMessage: end");
        }
    };
    private RelativeLayout parent_layout;
    private LottieAnimationView lottie_view;
    private TextView tv_radio_big;
    private TextView tv_radio_small;
    private ImageView iv_radio_frame;
    private BlurTransitionView radio_blur;
    private RelativeLayout parent_layout_small;
    private ImageView car_bg;
    private RadioManager radioManager = null;

    public RadioCarView(Context context) {
        super(context);
        Log.i(Utils.TAG, TAG + " RadioCarView:  start");
        mContext = context;
        RadioDataMng.getmInstance().init(mContext, mHandler);
        JsonAttrHandler jsonAttrHandler = new JsonAttrHandler();
        AAOP_HSkin.getInstance().registerSkinAttrHandler(ATTR_JSON_IMG_VIEW, jsonAttrHandler);
        initBigView();
        initSmallCarView();
        initListener();
        //updateView();
        //获取RadioService状态
        Log.i(Utils.TAG, TAG + " RadioCarView:  RadioManager.getRadioManager() = " + RadioManager.getRadioManager());
        try {
            radioManager = RadioManager.getRadioManager();
            if (radioManager == null) {
                final Timer timer1 = new Timer();
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(Utils.TAG, TAG + " RadioCarView: Timer radioManager = " + radioManager);
                        if (radioManager != null) {
                            Log.i(Utils.TAG, TAG + " RadioCarView: 1111111111= ");
                            conService();
                            Log.i(Utils.TAG, TAG + " RadioCarView: 222222");
                            timer1.cancel();
                        } else {
                            radioManager = RadioManager.getRadioManager();
                        }
                    }
                }, 200, 200);
            } else {
                conService();
            }
        } catch (EmptyStackException e) {
            Log.e(Utils.TAG, TAG + "RadioBigCarView: e)" + e.toString());
        }
        Log.i(Utils.TAG, TAG + " RadioCarView:  end");
    }

    public RadioCarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void conService() {
        Log.i(Utils.TAG, TAG + " conService: serviceConnectStata = " + serviceConnectStata);
        if (serviceConnectStata) {
            RadioDataMng.getmInstance().registerService();
            RadioDataMng.getmInstance().getRadioData();
        } else {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(Utils.TAG, TAG + " conService: Timer = " + serviceConnectStata);
                    if (serviceConnectStata) {
                        RadioDataMng.getmInstance().registerService();
                        RadioDataMng.getmInstance().getRadioData();
                        timer.cancel();
                    } else {
                        serviceConnectStata = RadioManager.getRadioManager().getServiceConnection();
                    }
                }
            }, 200, 200);
        }
    }

    private void initBigView() {
        mCarView = LayoutInflater.from(mContext).inflate(R.layout.big_car_layout, null);
        parent_layout = (RelativeLayout) mCarView.findViewById(R.id.parent_layout);
        car_bg = (ImageView) mCarView.findViewById(R.id.car_bg);
        seekBarFM = mCarView.findViewById(R.id.seekbar_fm_car);
        seekBarAM = mCarView.findViewById(R.id.seekbar_am_car);
        boduanFM = mCarView.findViewById(R.id.lav_fm_car);
        boduanAM = mCarView.findViewById(R.id.lav_am_car);
        AAOP_HSkin.with(boduanFM)
                .addViewAttrs(new DynamicAttr(ATTR_JSON_IMG_VIEW))
                .applySkin(false);

        AAOP_HSkin.with(boduanAM)
                .addViewAttrs(new DynamicAttr(ATTR_JSON_IMG_VIEW))
                .applySkin(false);
        boduanFM.setMinAndMaxFrame(0, 205);
        boduanAM.setMinAndMaxFrame(0, 119);

        mFreqText = mCarView.findViewById(R.id.play_text_hz_number_car);
        mPrevFreq = mCarView.findViewById(R.id.pre_btn_image_car);
        mNextFreq = mCarView.findViewById(R.id.next_btn_image_car);
        mPlayBtn = mCarView.findViewById(R.id.play_btn_image_car);
        turnUP = mCarView.findViewById(R.id.turn_up_car);
        turnDown = mCarView.findViewById(R.id.turn_down_car);
        unitTV = mCarView.findViewById(R.id.tv_radio_unit_car);
        radio_blur = (BlurTransitionView) mCarView.findViewById(R.id.radio_blur);
        tv_radio_big = (TextView) mCarView.findViewById(R.id.tv_radio_big);

        AAOP_HSkin.getWindowViewManager().addWindowView(mCarView);
//        AAOP_HSkin.with(parent_layout)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
//                .applySkin(false);
    }

    private void initSmallCarView() {
        mSmallCarView = LayoutInflater.from(mContext).inflate(R.layout.small_car_layout, null);
        mSmallCarDefaultIcon = mSmallCarView.findViewById(R.id.iv_radio_one);
        mSmallCarPlay = mSmallCarView.findViewById(R.id.play_btn_image_small_car);
        lottie_view = (LottieAnimationView) mSmallCarView.findViewById(R.id.lottie_view);
        tv_radio_small = (TextView) mSmallCarView.findViewById(R.id.tv_radio_small);
        parent_layout_small = (RelativeLayout) mSmallCarView.findViewById(R.id.parent_layout);
        ImageView iv_radio_frame = (ImageView) mSmallCarView.findViewById(R.id.iv_radio_frame);
        ImageView iv_radio_one = (ImageView) mSmallCarView.findViewById(R.id.iv_radio_one);
        smallCarFreq = (TextView) mSmallCarView.findViewById(R.id.tv_radio_small_freq);

        mSmallCarPlay.setOnClickListener(this);
        lottie_view.setImageAssetsFolder("images");
        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(mCarView);
        windowViewManager.addWindowView(mSmallCarView);
        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(this.parent_layout, true);
        skinManager.applySkin(parent_layout_small, true);
        AAOP_HSkin.with(car_bg)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                .applySkin(false);
        AAOP_HSkin.with(iv_radio_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                .applySkin(false);
        AAOP_HSkin.with(iv_radio_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_fm)
                .applySkin(false);

    }

    public void updateView() {
        //获取当前Band
        switch (RadioDataMng.getmInstance().mBand) {
            case BAND_AM:
                selectedAMUI();
                break;
            case BAND_FM:
                selectedFMUI();
                break;
            default:
                break;

        }
        Log.d(Utils.TAG, TAG + " updateView: isPlay = " + RadioDataMng.getmInstance().isPlay);

        if (RadioDataMng.getmInstance().isPlay == isUnMute) {
            mSmallCarDefaultIcon.setVisibility(GONE);
            mSmallCarPlay.setVisibility(VISIBLE);
            AAOP_HSkin
                    .with(mPlayBtn)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.radio_btn_pause)
                    .applySkin(false);
            //mPlayBtn.setBackgroundResource(R.drawable.radio_btn_pause);
            AAOP_HSkin
                    .with(mSmallCarPlay)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_small_radio_suspend_1)
                    .applySkin(false);
            //mSmallCarPlay.setBackgroundResource(R.drawable.icon_small_radio_suspend_1);
        } else {
            mSmallCarDefaultIcon.setVisibility(GONE);
            mSmallCarPlay.setVisibility(VISIBLE);
            //mPlayBtn.setBackgroundResource(R.drawable.radio_btn_play);
            AAOP_HSkin
                    .with(mPlayBtn)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.radio_btn_play)
                    .applySkin(false);
            AAOP_HSkin
                    .with(mSmallCarPlay)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_small_music_play_1)
                    .applySkin(false);
            //mSmallCarPlay.setBackgroundResource(R.drawable.icon_small_music_play_1);
        }
    }

    //选择FM
    private void selectedFMUI() {
        Log.d(Utils.TAG, TAG + " selectedFMUI: start");

        boduanFM.setVisibility(VISIBLE);
        boduanAM.setVisibility(GONE);
        seekBarAM.setVisibility(GONE);
        seekBarFM.setVisibility(VISIBLE);

        unitTV.setText(R.string.tv_mhz);
        currenBand = "FM";
        updateFMInfo();
        Log.d(Utils.TAG, TAG + " selectedFMUI: end");
    }

    //选择AM
    private void selectedAMUI() {
        Log.d(Utils.TAG, TAG + " selectedAMUI: start");
        boduanAM.setVisibility(VISIBLE);
        boduanFM.setVisibility(GONE);
        seekBarAM.setVisibility(VISIBLE);
        seekBarFM.setVisibility(GONE);

        unitTV.setText(R.string.tv_khz);
        currenBand = "AM";
        updateAMInfo();
        Log.d(Utils.TAG, TAG + " selectedAMUI: end");
    }

    private void updateAMInfo() {
        Log.d(Utils.TAG, TAG + " updateAMInfo: start");
        mAMFreq = RadioDataMng.getmInstance().lastAMFreq;
        boduanAM.setMinAndMaxFrame(0,119);
        progressMax = 119;
        progressMas = 0;
        amProgressCurrentFreq = (mAMFreq - 531) / 9;
        seekBarAM.setMax(progressMax);
        seekBarAM.setMin(progressMas);
        seekBarAM.setProgress(amProgressCurrentFreq);
        boduanAM.setFrame(amProgressCurrentFreq);
        mFreqText.setText(String.valueOf(mAMFreq));
        smallCarFreq.setText("AM "+mAMFreq+" kHz");
        Log.d(Utils.TAG, TAG + " updateAMInfo: end");
    }

    @SuppressLint("SetTextI18n")
    private void updateFMInfo() {
        Log.d(Utils.TAG, TAG + " updateFMInfo: start");
        mFMFreq = RadioDataMng.getmInstance().lastFMFreq;
        Log.d(Utils.TAG, TAG + " updateFMInfo: mFMFreq =" + mFMFreq);
        boduanFM.setMinAndMaxFrame(0, 205);
        progressMax = 205;
        progressMas = 0;
        seekBarFM.setMax(progressMax);
        seekBarFM.setMin(progressMas);
        Log.d(Utils.TAG,TAG+  " updateFMInfo: ((int)(mFMFreq*100) - 8750) ="+((int)(mFMFreq*100) - 8750));
        fmProgressCurrentFreq = ((int)(mFMFreq*100) - 8750) / 10;
        Log.d(Utils.TAG, TAG + " updateFMInfo: fmProgressCurrentFreq =" + fmProgressCurrentFreq);
        seekBarFM.setProgress(fmProgressCurrentFreq);
        boduanFM.setFrame(fmProgressCurrentFreq);

        mFreqText.setText(String.valueOf(mFMFreq));
        smallCarFreq.setText("FM "+mFMFreq+" MHz");

        Log.d(Utils.TAG, TAG + " updateFMInfo: end");
    }

    private void initListener() {
        mPrevFreq.setOnClickListener(this);
        mNextFreq.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);

        turnUP.setOnClickListener(this);
        turnDown.setOnClickListener(this);

        seekBarFM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条发生改变时会触发
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(Utils.TAG, TAG + " onProgressChanged:band====== " + RadioDataMng.getmInstance().mBand + " FM");
                Log.i(Utils.TAG, TAG + " onProgressChanged:progress====== " + progress);
                boduanFM.setFrame(progress);
                if (RadioDataMng.getmInstance().mBand == BAND_FM) {
                    seekBar.setMax(205);
                    seekBar.setMin(0);
                    double fmNum = 87.5;
//                    if (progress > 1) {
//                        fmNum = (progress + 1) / 10.0 + 87.5;
//                    } else {
                        fmNum = progress / 10.0 + 87.5;
//                    }
                    Log.i(TAG, TAG + " fmNum=== " + fmNum);
                    String fmStr = String.valueOf(progress == 0 ? 87.5 : fmNum);
                    mFreqText.setText(fmStr);
                    smallCarFreq.setText("FM "+fmStr+" MHz");
                    fmProgress = fmStr;
                }
            }

            //按住seekbar时会触发
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //放开seekbar时会触发
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //在这里面设置手松开的时候发送freq来改变声音的值
                playSearchChannel(fmProgress);
            }
        });

        seekBarAM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                boduanAM.setMinAndMaxFrame(0, 119);
                boduanAM.setFrame(i);
                if (RadioDataMng.getmInstance().mBand == BAND_AM) {
                    seekBar.setMax(119);
                    seekBar.setMin(0);
                    int a = i * 9 + 531;

                    mFreqText.setText(String.valueOf(a));
                    amProgress = String.valueOf(a);
                    smallCarFreq.setText("AM "+a+" kHz");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playSearchChannel(amProgress);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.i(Utils.TAG, TAG + " onClick: " + v.getId());
        int id = v.getId();
        if (id == R.id.pre_btn_image_car) {
            try {
                RadioManager.getRadioManager().seekUp();
//                RadioManager.getRadioManager().setMcuMuteState(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.next_btn_image_car) {
            try {
                RadioManager.getRadioManager().seekDown();
//                RadioManager.getRadioManager().setMcuMuteState(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.play_btn_image_car) {
            try {
                if(RadioDataMng.getmInstance().isSearch != 0){
                    RadioManager.getRadioManager().stopSearch();
                }
//                if ("0".equals(SystemPropertiesPresenter.getInstance().getProperty(SYSTEM_PROPERTY_REQUEST_AUDIO_FOCUS_KEY, "0"))) {
//                    RadioManager.getRadioManager().requestPlay();
//                }
                if (RadioDataMng.getmInstance().isPlay == isUnMute) {
                    RadioManager.getRadioManager().setMcuMuteState(true);
                } else {
                    RadioManager.getRadioManager().requestPlay();
//                    RadioManager.getRadioManager().setMcuMuteState(false);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else if (id == R.id.turn_up_car) {
            try {
                RadioManager.getRadioManager().tuneUp();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.turn_down_car) {
            try {
                RadioManager.getRadioManager().tuneDown();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.play_btn_image_small_car) {
            try {
                if(RadioDataMng.getmInstance().isSearch != 0){
                    RadioManager.getRadioManager().stopSearch();
                }
                if (RadioDataMng.getmInstance().isPlay == isUnMute) {
                    RadioManager.getRadioManager().setMcuMuteState(true);
                } else {
                    RadioManager.getRadioManager().requestPlay();
//                  RadioManager.getRadioManager().setMcuMuteState(false);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    @Override
    public View initCardView(String id, String type, String type1) {
        if (type.equals(TYPE_BIGCARD)) {
            WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
                @Override
                public void setWallPaper(Bitmap bitmap) {
                    radio_blur.setBitmap(5, 0.5f);
                    radio_blur.show(0);
                }

                @Override
                public void resumeDefault() {
                    radio_blur.setBitmap(5, 0.5f);
                    radio_blur.show(0);
                }

                @Override
                public void deleteWallPaper() {
                    radio_blur.setBitmap(5, 0.5f);
                    radio_blur.show(0);
                }


            });

            return mCarView;
        } else if (type.equals(TYPE_SMALLCARD)) {
            String ret = SystemPropertiesPresenter.getInstance().getProperty(SYSTEM_PROPERTY_REQUEST_AUDIO_FOCUS_KEY, "0");
            Log.d(Utils.TAG, TAG + "initCardView: ret = " + ret);
            if ("1".equals(ret)) {
                //判断Radio是否抢过焦点，没抢过显示雷达信息号标识，抢过显示播放或暂停
                mSmallCarDefaultIcon.setVisibility(GONE);
                mSmallCarPlay.setVisibility(VISIBLE);
            } else {
                mSmallCarDefaultIcon.setVisibility(VISIBLE);
                mSmallCarPlay.setVisibility(GONE);
            }
            if (type1.equals(DRAG_TO_INITCARD)) {
                AAOP_HSkin.with(lottie_view)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.radio45)
                        .applySkin(false);
            }
            return mSmallCarView;
        }
        return null;
    }

    @Override
    public void unInitCardView(String id, String type) {

    }

    @Override
    public void releaseResource() {
        RadioDataMng.getmInstance().unRegisterService();
    }

    @Override
    public void playAnimation(String id, int delay) {
        Log.d(Utils.TAG, TAG + "playAnimation  radio : " + delay);
        Message message = Message.obtain();
        message.what = PLAY_ANIMATION;
        mHandler.sendMessageDelayed(message, delay);
    }

    public void onConfigurationChanged() {
        Log.d(Utils.TAG, TAG + "onConfigurationChanged: ");
        if (tv_radio_big != null) {
            tv_radio_big.setText((mContext.getResources().getString(R.string.radio_text)));
        }
        if (tv_radio_small != null) {
            tv_radio_small.setText((mContext.getResources().getString(R.string.radio_text)));
        }
    }

    @Override
    public void launcherLoadComplete() {
        Log.d(Utils.TAG, TAG + "launcherLoadComplete: radio");
        radio_blur.post(new Runnable() {
            @Override
            public void run() {
                radio_blur.setBitmap(5, 0.5f);
                radio_blur.show(300);
            }
        });
    }

    @Override
    public void launcherAnimationUpdate(int i) {
        if (i == 1) {
            radio_blur.show(300);
        } else {
            if (radio_blur.getIsShow()) {
                radio_blur.hide(0);
            }
        }
    }

    //播放当前频段
    private void playSearchChannel(String channel) {
        Log.d(Utils.TAG, TAG + " playSearchChannel channel = " + channel);
        if (RadioDataMng.getmInstance().mBand == BAND_AM) {
            int amFreq = Integer.parseInt(channel);
            try {
                RadioManager.getRadioManager().setBandAndFreq(BAND_AM, amFreq);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            int fmFreq = (int) (Double.parseDouble(channel) * 100);
            try {
                RadioManager.getRadioManager().setBandAndFreq(BAND_FM, fmFreq);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}
