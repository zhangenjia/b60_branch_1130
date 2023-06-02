package com.adayo.app.launcher.wecarflow;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.wecarflow.util.LogUtil;
import com.adayo.app.launcher.wecarflow.bean.MusicInfo;
import com.adayo.app.launcher.wecarflow.util.RoundImageViewUtil;
import com.adayo.app.launcher.wecarflow.util.initSDKUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;

public class WeCarFlowCardView extends RelativeLayout implements IViewBase, View.OnClickListener {

    private static final String TAG = WeCarFlowCardView.class.getSimpleName();
    private Context mContext;
    private MusicInfo mMusicInfo;
    private View view;
    private View view_big;
    private ImageView logoImgBig;
    private SeekBar processSeekBar;
    private Button preButton;
    private Button playButtonBig;
    private Button nextButton;
    private View noNetworkLayBig;
    private TextView wifi_name;
    private TextView wifi_info;
    private View noPlayLayBig;
    private TextView noContentText;
    private View normalLayBig;
    private TextView music_name;
    private TextView music_lyric;
    private View view_small;

    private View normalLaySmall;
    private TextView small_music_name;
    private Button playButtonSmall;
    public final int STOP_STATE = 0;
    public final int PLAY_STATE = 1;
    public final int PAUSE_STATE = 2;
    private final int PROGRESS_TOTAL_CHANGE = 999;
    private final int PROGRESS_CURRENT_CHANGE = 888;
    private final int MEDIA_INFO_CHANGE = 777;
    private final int NETWORK_CONNECTED_CHANGED = 100;
    private final int CONNECTED_STATUE_CHANGED = 101;
    private final int IMAGE_PIC_UPDATE = 1000;
    private final int PLAY_ANIMATION = 102;
    private boolean networkEnable = false;
    private boolean state_connect = false;
    private int play_state;
    private long current;
    private long total;
    private String url;
    private String name;
    private Bitmap bitmap;
    private boolean isCreateBigView = true;
    private boolean isCreateSmallView = false;
    private boolean isFirstClick = true;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "msg.what = " + msg.what);

            switch (msg.what) {
                case PLAY_STATE:
                    if (isCreateBigView) {
                        if (networkEnable) {
                            noPlayLayBig.setVisibility(View.GONE);
                            normalLayBig.setVisibility(View.VISIBLE);
                            isFirstClick = false;
                        }
                        playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_music_pause_n));
                    }
                    if (isCreateSmallView) {
//                        playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_radio_suspend_1));
                        AAOP_HSkin
                                .with(playButtonSmall)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_radio_suspend_1)
                                .applySkin(false);
                    }
                    break;
                case STOP_STATE:
                case PAUSE_STATE:
                    if (isCreateBigView) {
                        playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_icon_play_n));
                    }
                    if (isCreateSmallView) {
//                        playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_music_play_1));
                        AAOP_HSkin
                                .with(playButtonSmall)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_music_play_1)
                                .applySkin(false);
                    }
                    break;
                case PROGRESS_CURRENT_CHANGE:
                    if (isCreateBigView) {
                        if (networkEnable) processSeekBar.setProgress((int) current);
                        LogUtil.d(TAG, "music_process = current 1    " + current);
                    }
                    break;
                case PROGRESS_TOTAL_CHANGE:
                    if (isCreateBigView)
                        processSeekBar.setMax((int) total);
                    LogUtil.d(TAG, "music_process = total 1    " + total);
                    break;
                case MEDIA_INFO_CHANGE:
                    LogUtil.d(TAG, "name = " + name);
                    if (isCreateBigView) {
                        music_name.setText(name);
                        music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        music_name.setSingleLine(true);
                        music_name.setSelected(true);
                        music_name.setFocusable(true);
                        music_name.setFocusableInTouchMode(true);
                    }
                    if (isCreateSmallView) {
                        small_music_name.setText(name);
                        small_music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        small_music_name.setSingleLine(true);
                        small_music_name.setSelected(true);
                        small_music_name.setFocusable(true);
                        small_music_name.setFocusableInTouchMode(true);
                    }
                    break;
                case NETWORK_CONNECTED_CHANGED:
                case CONNECTED_STATUE_CHANGED:
                    setView();
                    break;
                case IMAGE_PIC_UPDATE:
                    blurBitMap(WeCarFlowCardView.this.bitmap);
                    logoImgBig.setImageBitmap(WeCarFlowCardView.this.bitmap);
                    break;
                case PLAY_ANIMATION:
                    if (CURRENT_THEME == 2) {
                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                        card_name_animator.setTarget(title_smallcard);
                        card_name_animator.start();
                    }
                    break;
                default:
                    LogUtil.e(TAG, "msg is Invalid!!");
                    break;
            }
        }
    };

    private RoundImageViewUtil roundImage;
    private ImageView bgImgBig;
    private RelativeLayout rootLayoutBigCard;
    private RelativeLayout rootLayoutSmallCard;
    private TextView title_smallcard;
    private TextView title_bigcard;
    private ImageView iv_wecarflow_one;
    private ImageView iv_wecarflow_two;
    private BlurTransitionView blurView;
    private boolean isLogin;


    public WeCarFlowCardView(Context context) {
        super(context);
        mContext = context;
        mMusicInfo = MusicInfo.getInstance(mContext);
        mMusicInfo.setMusicInfoChangeListener(new MusicInfo.MusicInfoChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                LogUtil.d(TAG, "onPlayStateChanged():playState = " + playState);
                play_state = playState;
                Message message = new Message();
                message.what = playState;
                handler.sendMessage(message);
            }

            @Override
            public void onMediaInfoChanged(MediaInfo mediaInfo) {
                LogUtil.d(TAG, "onMediaInfoChanged():");

                url = mediaInfo.getMediaImage();
                name = mediaInfo.getMediaName();

                if (url != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                bitmap = getBitmap(url);
                                Message message = new Message();
                                message.what = IMAGE_PIC_UPDATE;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

                Message message = new Message();
                message.what = MEDIA_INFO_CHANGE;
                handler.sendMessage(message);
            }

            @Override
            public void onConnectStateChanged(boolean connectState) {
                LogUtil.d(TAG, "onConnectStateChanged():");
                state_connect = connectState;
                Message message = new Message();
                message.what = CONNECTED_STATUE_CHANGED;
                handler.sendMessage(message);
            }

            @Override
            public void onNetWorkStateChanged(boolean netWorkState) {
                LogUtil.d(TAG, "onNetWorkStateChanged():");
                networkEnable = netWorkState;
                Log.d("bug1013178", "state_network = : " + networkEnable);
                Message message = new Message();
                message.what = NETWORK_CONNECTED_CHANGED;
                handler.sendMessage(message);
            }

            @Override
            public void onProcessTotalChanged(long progress) {
                LogUtil.d(TAG, "onProcessTotalChanged():");

                total = progress;
                Message message = new Message();
                message.what = PROGRESS_TOTAL_CHANGE;
                handler.sendMessage(message);
            }

            @Override
            public void onProcessCurrentChanged(long progress) {
                LogUtil.d(TAG, "onProcessCurrentChanged():");

                current = progress;
                Message message = new Message();
                message.what = PROGRESS_CURRENT_CHANGE;
                handler.sendMessage(message);
            }
        });

        initSDKUtil.getInstance(mContext).initSDK();
        initView();
        initStatus();
    }

    private void initView() {
        LogUtil.d(TAG, "initView():");
        view_big = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.wecarflow_bigcard_layout, null);
        logoImgBig = view_big.findViewById(R.id.logoImgBig);
        processSeekBar = view_big.findViewById(R.id.processSeekBar);
        roundImage = (RoundImageViewUtil) view_big.findViewById(R.id.roundImage);
        bgImgBig = (ImageView) view_big.findViewById(R.id.bgImgBig);
        rootLayoutBigCard = (RelativeLayout) view_big.findViewById(R.id.parent_layout);
        blurView = (BlurTransitionView) view_big.findViewById(R.id.blurView);

        processSeekBar.setClickable(false);
        processSeekBar.setEnabled(false);
        processSeekBar.setFocusable(false);
        preButton = view_big.findViewById(R.id.preButton);
        preButton.setOnClickListener(this);
        playButtonBig = view_big.findViewById(R.id.playButtonBig);
        playButtonBig.setOnClickListener(this);
        nextButton = view_big.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        noNetworkLayBig = view_big.findViewById(R.id.noNetworkLayBig);
        wifi_name = view_big.findViewById(R.id.wifi_name);
        wifi_info = view_big.findViewById(R.id.wifi_info);
        noPlayLayBig = view_big.findViewById(R.id.noPlayLayBig);
        noContentText = view_big.findViewById(R.id.noContentText);
        normalLayBig = view_big.findViewById(R.id.normalLayBig);
        music_name = view_big.findViewById(R.id.music_name);
        music_lyric = view_big.findViewById(R.id.music_lyric);
        title_bigcard = (TextView) view_big.findViewById(R.id.title_bigcard);

        music_lyric.setVisibility(View.GONE);

        view_small = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.wecarflow_smallcard_layout, null);
        rootLayoutSmallCard = (RelativeLayout) view_small.findViewById(R.id.parent_layout);

        normalLaySmall = view_small.findViewById(R.id.normalLaySmall);
        small_music_name = view_small.findViewById(R.id.small_music_name);
        playButtonSmall = view_small.findViewById(R.id.playButtonSmall);
        title_smallcard = (TextView) view_small.findViewById(R.id.title_smallcard);
        iv_wecarflow_one = (ImageView) view_small.findViewById(R.id.iv_wecarflow_one);
        iv_wecarflow_two = (ImageView) view_small.findViewById(R.id.iv_wecarflow_two);
        playButtonSmall.setOnClickListener(this);

        AAOP_HSkin.getWindowViewManager().addWindowView(view_big);

        AAOP_HSkin.getWindowViewManager().addWindowView(view_small);
        ISkinManager skinManager = AAOP_HSkin.getInstance();

        skinManager.applySkin(rootLayoutBigCard, true);

        skinManager.applySkin(rootLayoutSmallCard, true);
        AAOP_HSkin
                .getInstance()
                .applySkin(rootLayoutBigCard, true);
        AAOP_HSkin
                .getInstance()
                .applySkin(rootLayoutSmallCard, true);
        AAOP_HSkin.with(view_small)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                .applySkin(false);
        AAOP_HSkin.with(iv_wecarflow_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_wecarflow)
                .applySkin(false);
        AAOP_HSkin.with(iv_wecarflow_two)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_wecarflow_b)
                .applySkin(false);
        AAOP_HSkin
                .with(playButtonSmall)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_music_play_1)
                .applySkin(false);

        AAOP_HSkin
                .with(processSeekBar)
                .addViewAttrs(AAOP_HSkin.ATTR_PROGRESS_DRAWABLE, R.drawable.music_progress)
                .applySkin(false);

        playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_icon_play_n));


    }


    private void initStatus() {
        LogUtil.d(TAG, "initStatus():");
        networkEnable = mMusicInfo.getNetworkState();
        state_connect = mMusicInfo.getContentState();
        play_state = mMusicInfo.getPlayState();
        current = mMusicInfo.getCurrentProcess();
        total = mMusicInfo.getTotalProcess();
        if (mMusicInfo.getMediaInfo() != null) {
            url = mMusicInfo.getMediaInfo().getMediaImage();
            name = mMusicInfo.getMediaInfo().getMediaName();
        } else {
            name = (String) music_name.getText();
        }
        setView();
    }

    private void setView() {
        LogUtil.d(TAG, "setView():  bigCardStatus " + isCreateBigView + " state_network " + networkEnable + " state_connect " + state_connect + " state_play " + play_state);
        if (isCreateBigView) {
            if (networkEnable) {
                noNetworkLayBig.setVisibility(View.GONE);
                if (state_connect) {
                    switch (play_state) {
                        case PLAY_STATE:
                            noPlayLayBig.setVisibility(View.GONE);
                            normalLayBig.setVisibility(View.VISIBLE);
                            processSeekBar.setProgress((int) current);
                            music_name.setText(name);
                            music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            music_name.setSingleLine(true);
                            music_name.setSelected(true);
                            music_name.setFocusable(true);
                            music_name.setFocusableInTouchMode(true);
                            playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_music_pause_n));
                            isFirstClick = false;
                            if (url != null) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            bitmap = getBitmap(url);
                                            Message message = new Message();
                                            message.what = IMAGE_PIC_UPDATE;
                                            handler.sendMessage(message);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                logoImgBig.setImageBitmap(bitmap);
                            }
                            break;
                        case PAUSE_STATE:
                            noPlayLayBig.setVisibility(View.GONE);
                            normalLayBig.setVisibility(View.VISIBLE);
                            processSeekBar.setProgress((int) current);
                            music_name.setText(name);
                            music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            music_name.setSingleLine(true);
                            music_name.setSelected(true);
                            music_name.setFocusable(true);
                            music_name.setFocusableInTouchMode(true);
                            if (url != null) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            bitmap = getBitmap(url);
                                            Message message = new Message();
                                            message.what = IMAGE_PIC_UPDATE;
                                            handler.sendMessage(message);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                logoImgBig.setImageBitmap(bitmap);
                            }
                            break;
                        case STOP_STATE:
                            noPlayLayBig.setVisibility(View.VISIBLE);
                            normalLayBig.setVisibility(View.GONE);
                            logoImgBig.setBackground(getResources().getDrawable(R.drawable.frame_aiquting));
                            current = 0;
                            total = 100;
                            processSeekBar.setProgress((int) current);
                            playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_icon_play_n));
                            break;
                        default:
                            LogUtil.d(TAG, "state_play is Invalid!!!!");
                            break;
                    }
                } else {
                    noPlayLayBig.setVisibility(View.VISIBLE);
                    noNetworkLayBig.setVisibility(GONE);
                    normalLayBig.setVisibility(View.GONE);
                    logoImgBig.setBackground(getResources().getDrawable(R.drawable.frame_aiquting));
                    current = 0;
                    total = 100;
                    processSeekBar.setProgress((int) current);
                    playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_icon_play_n));
                }
            } else {

                noNetworkLayBig.setVisibility(View.VISIBLE);
                noPlayLayBig.setVisibility(View.GONE);
                normalLayBig.setVisibility(View.GONE);
                logoImgBig.setBackground(getResources().getDrawable(R.drawable.frame_aiquting));
                current = 0;
                total = 100;
                processSeekBar.setProgress((int) current);
            }
        }


        if (isCreateSmallView) {
            if (networkEnable && state_connect) {
                switch (play_state) {
                    case PLAY_STATE:
                        small_music_name.setText(name);
                        small_music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        small_music_name.setSingleLine(true);
                        small_music_name.setSelected(true);
                        small_music_name.setFocusable(true);
                        small_music_name.setFocusableInTouchMode(true);
//                        playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_radio_suspend_1));
                        AAOP_HSkin
                                .with(playButtonSmall)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_radio_suspend_1)
                                .applySkin(false);
                        break;
                    case PAUSE_STATE:
                    case STOP_STATE:
                        small_music_name.setText(name);
                        small_music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        small_music_name.setSingleLine(true);
                        small_music_name.setSelected(true);
                        small_music_name.setFocusable(true);
                        small_music_name.setFocusableInTouchMode(true);
//                        playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_music_play_1));
                        AAOP_HSkin
                                .with(playButtonSmall)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_music_play_1)
                                .applySkin(false);
                        break;
                    default:
                        LogUtil.d(TAG, "state_play is Invalid!!!!");
                        break;
                }
            } else { //无网络/未连接
                small_music_name.setText("");
                small_music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                small_music_name.setSingleLine(true);
                small_music_name.setSelected(true);
                small_music_name.setFocusable(true);
                small_music_name.setFocusableInTouchMode(true);
//                playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_music_play_1));
                AAOP_HSkin
                        .with(playButtonSmall)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_music_play_1)
                        .applySkin(false);
            }

        }

    }

    @Override
    public void onClick(final View view) {

        Log.d(TAG, "onClick: networkEnable" + networkEnable);
        FlowPlayControl.getInstance().queryLoginStatus(new QueryCallback<UserInfo>() {
            @Override
            public void onError(int i) {
                Log.d(TAG, "queryLoginStatus onError: ---------------->");
            }
            @Override
            public void onSuccess(UserInfo userInfo) {
                boolean isLogin = userInfo.isLogin();
                Log.d(TAG, "onSuccess: isLogin "+isLogin);
                if (!isLogin) {
                    return;
                }
                Log.d(TAG, "queryLoginStatus onSuccess: ---------------->" + userInfo.isLogin());
                if (view.getId() == R.id.preButton) {
                    Log.d(TAG, "onClick: doPre");

                    FlowPlayControl.getInstance().doPre();
                } else if (view.getId() == R.id.nextButton) {
                    Log.d(TAG, "onClick: doNext");
                    FlowPlayControl.getInstance().doNext();
                } else if ((view.getId() == R.id.playButtonBig)
                        || (view.getId() == R.id.playButtonSmall)) {
                    Log.d(TAG, "onClick: play/pause play_state" + play_state);
                    switch (play_state) {
                        case PLAY_STATE:
                            if (isCreateBigView) {
                                playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_icon_play_n));
                            }
                            if (isCreateSmallView) {
//                            playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_music_play_1));
                                AAOP_HSkin
                                        .with(playButtonSmall)
                                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_music_play_1)
                                        .applySkin(false);
                            }
                            FlowPlayControl.getInstance().doPause();
                            break;

                        case STOP_STATE:
                        case PAUSE_STATE:
                            if (isCreateBigView) {
                                playButtonBig.setBackground(getResources().getDrawable(R.drawable.large_card_music_pause_n));
                            }
                            if (isCreateSmallView) {
                                AAOP_HSkin
                                        .with(playButtonSmall)
                                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_small_radio_suspend_1)
                                        .applySkin(false);
//                            playButtonSmall.setBackground(getResources().getDrawable(R.drawable.icon_small_radio_suspend_1));
                            }
                            FlowPlayControl.getInstance().doPlay();
                            break;
                        default:
                            LogUtil.e(TAG, "state_play is Invalid!!!");
                            break;
                    }
                } else {
                    LogUtil.d(TAG, "Touch is Invalid!");
                }
            }

        });

        Log.d(TAG, "queryLoginStatus : aaaa ---------------->");

    }


    @Override
    public View initCardView(String id, String type, String type1) {
        LogUtil.d(TAG, "initCardView():id = " + id + " type = " + type);
        if (type.equals(TYPE_BIGCARD)) {
            view = view_big;
            isCreateBigView = true;
            WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
                @Override
                public void setWallPaper(Bitmap bitmap) {
                    blurView.setBitmap(5, 0.5f);
                    blurView.show(0);
                }

                @Override
                public void resumeDefault() {
                    blurView.setBitmap(5, 0.5f);
                    blurView.show(0);
                }

                @Override
                public void deleteWallPaper() {
                    blurView.setBitmap(5, 0.5f);
                    blurView.show(0);
                }

//                @Override
//                public void bgChange(Bitmap bitmap, boolean isDefult) {
//                    blurView.setBitmap(5, 0.5f);
//                    blurView.show(0);
//                }
            });

        } else {
            if (type1.equals(DEFAULT_INITCARD) && CURRENT_THEME == 2) {
                title_smallcard.setAlpha(0);
            }
            view = view_small;
            isCreateSmallView = true;
        }

        if (type1 == DRAG_TO_INITCARD) {
            setView();
        }

        return view;
    }

    @Override
    public void unInitCardView(String id, String type) {
        Log.d(TAG, "unInitCardView: weacar");
        if (type.equals(TYPE_BIGCARD)) {
            isCreateBigView = false;
        } else {
            isCreateSmallView = false;
        }

        LogUtil.d(TAG, "bigCardStatus = " + isCreateBigView + " smallCardStatus = " + isCreateSmallView);
        if (isCreateBigView && isCreateSmallView) {
            initSDKUtil.getInstance(mContext).destroyListener();
        }
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void playAnimation(String id, int delay) {
        Message msg = Message.obtain();
        msg.what = PLAY_ANIMATION;
        handler.sendMessageDelayed(msg, delay);
    }

    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: ");
        if (title_bigcard != null) {
            title_bigcard.setText(mContext.getResources().getString(R.string.card_name));
        }
        if (title_smallcard != null) {
            title_smallcard.setText(mContext.getResources().getString(R.string.card_name));
        }
        if (noContentText != null) {
            noContentText.setText(mContext.getResources().getString(R.string.music_null));
        }
        if (wifi_name != null) {
            wifi_name.setText(mContext.getResources().getString(R.string.wifi_name));
        }
        if (wifi_info != null) {
            wifi_info.setText(mContext.getResources().getString(R.string.wifi_info));
        }
        if (small_music_name != null) {
            if (small_music_name.getText() != null) {
                CharSequence text = small_music_name.getText();
                if ("Unknown".equals(text) || "未知".equals(text)) {
                    small_music_name.setText(mContext.getResources().getString(R.string.music_name));
                }


            }

        }

    }

    @Override
    public void launcherLoadComplete() {
        Log.d(TAG, "launcherLoadComplete: wecarflow");
        blurView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg);
                blurView.setBitmap(5, 0.5f);
                blurView.show(300);
            }
        });

    }

    @Override
    public void launcherAnimationUpdate(int i) {
        Log.d(TAG, "launcherAnimationUpdate: ");
        if (i == 1) {
            blurView.show(300);
        } else {
            if (blurView.getIsShow()) {
                blurView.hide(0);
            }
        }
    }

    private Bitmap getBitmap(String path) throws IOException {

        LogUtil.d(TAG, "getBitmap():");
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }

    private void blurBitMap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 564;
        int newHeight = 860;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.getWidth();
        bitmap.getHeight();
        Glide.with(this)
                .load(bitmap)
                .centerCrop()
                .dontAnimate()
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(18, 10)))
                .into(new ViewTarget<ImageView, Drawable>(roundImage) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                        Drawable current = resource.getCurrent();
                        current.setColorFilter(0xff585858, PorterDuff.Mode.MULTIPLY);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(564, 860);
                        layoutParams.setMargins(0, 0, 0, 15);
                        roundImage.setLayoutParams(layoutParams);
                        roundImage.setImageDrawable(current);
                    }
                });
    }

}
