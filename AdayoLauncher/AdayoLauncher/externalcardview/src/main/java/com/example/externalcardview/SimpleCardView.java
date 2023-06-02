//package com.example.externalcardview;
//
//import android.animation.Animator;
//import android.animation.AnimatorInflater;
//import android.animation.AnimatorListenerAdapter;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.animation.PathInterpolator;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.adayo.app.launcher.communicationbase.ConstantUtil;
//import com.adayo.app.launcher.communicationbase.IViewBase;
//import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
//import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
//import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
//import com.airbnb.lottie.LottieAnimationView;
//import com.example.BcmImpl;
//
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AIQUTING;
//
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_APS;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AVM;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_CARBIT;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_DVR;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_HAVC;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_MUSIC;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_MYCAR;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_NAVI;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_OFFROADINFO;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_PICTURE;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_RADIO;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_SETTING;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_TEL;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_VIDEO;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WEATHER;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WECHAT;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WITHTENCENT;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_YUEYEQUAN;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
//import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
//
//public class SimpleCardView extends RelativeLayout implements IViewBase {
//    /**
//     * 视频 图片 dvr setting mycar 越野圈 腾讯随行 小程序 微信 aps avm
//     */
//    private static final String TAG = "lottietest" + SimpleCardView.class.getSimpleName();
//    private Context mContext;
//
//    private View apsView;
//    private View avmView;
//    private View dvrView;
//    private View mycarView;
//    private View pictureView;
//    private View settingView;
//    private View videoView;
//    private View wechatView;
//    private View withtencentView;
//    private View yueyequanView;
//    private View view;
//    private LottieAnimationView aps_lottie_view;
//    private LottieAnimationView avm_lottie_view;
//    private LottieAnimationView dvr_lottie_view;
//    private LottieAnimationView mycar_lottie_view;
//    private LottieAnimationView picture_lottie_view;
//    private LottieAnimationView setting_lottie_view;
//    private LottieAnimationView video_lottie_view;
//    private LottieAnimationView applets_lottie_view;
//
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String id = (String) msg.obj;
//            Log.d(TAG, "CURRENT_THEME: " + ConstantUtil.CURRENT_THEME);
//            switch (id) {
//                case ID_VIDEO:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_video);
//                        card_name_animator.start();
//                    }
//                    video_lottie_view.setAnimation("video" + ConstantUtil.CURRENT_THEME + ".json");
//                    boolean shown = video_lottie_view.isShown();
//                    Log.d(TAG, "run: ID_VIDEO   " + shown);
//                    video_lottie_view.playAnimation();
//                    video_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(video_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.video45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_TEL:
//                    break;
//                case ID_PICTURE:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_picture);
//                        card_name_animator.start();
//                    }
//                    picture_lottie_view.setAnimation("picture" + ConstantUtil.CURRENT_THEME + ".json");//
//                    picture_lottie_view.playAnimation();
//                    picture_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(picture_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.picture45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_SETTING:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_setting);
//                        card_name_animator.start();
//                    }
//                    setting_lottie_view.setAnimation("setting" + ConstantUtil.CURRENT_THEME + ".json");//
//                    setting_lottie_view.playAnimation();
//                    setting_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(setting_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.setting45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_MYCAR:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_mycar);
//                        card_name_animator.start();
//                    }
//                    mycar_lottie_view.setAnimation("mycar" + ConstantUtil.CURRENT_THEME + ".json");
//                    mycar_lottie_view.playAnimation();
//                    mycar_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(mycar_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.mycar45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_DVR:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_dvr);
//                        card_name_animator.start();
//                    }
//                    dvr_lottie_view.setAnimation("dvr" + ConstantUtil.CURRENT_THEME + ".json");//
//                    dvr_lottie_view.playAnimation();
//                    dvr_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(dvr_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.dvr45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//
//                case ID_AVM:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_avm);
//                        card_name_animator.start();
//                    }
//                    avm_lottie_view.setAnimation("avm" + ConstantUtil.CURRENT_THEME + ".json");//
//                    avm_lottie_view.playAnimation();
//                    avm_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(avm_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.avm45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_APS:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_aps);
//                        card_name_animator.start();
//                    }
//                    aps_lottie_view.setAnimation("aps" + ConstantUtil.CURRENT_THEME + ".json");//
//                    aps_lottie_view.playAnimation();
//                    aps_lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            super.onAnimationStart(animation);
//                            AAOP_HSkin
//                                    .with(aps_lottie_view)
//                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.aps45_lottie)
//                                    .applySkin(false);
//                        }
//                    });
//                    break;
//                case ID_WITHTENCENT:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_withtencent);
//                        card_name_animator.start();
//                    }
//                    break;
//
//                case ID_YUEYEQUAN:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_yueyequan);
//                        card_name_animator.start();
//                    }
//                    break;
//                case ID_WECHAT:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_wechat);
//                        card_name_animator.start();
//                    }
//                    break;
//                case ID_CARBIT:
//                    if (CURRENT_THEME == 2) {
//                        Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
//                        card_name_animator.setTarget(tv_catbit);
//                        card_name_animator.start();
//                    }
//                    break;
//            }
//
//        }
//    };
//
//
//    private TextView tv_aps;
//    private TextView tv_avm;
//    private TextView tv_dvr;
//    private TextView tv_mycar;
//    private TextView tv_offroadinfo;
//    private TextView tv_picture;
//    private TextView tv_setting;
//    private TextView tv_video;
//    private TextView tv_wechat;
//    private TextView tv_withtencent;
//    private TextView tv_yueyequan;
//    private RelativeLayout apslayout;
//    private RelativeLayout avmlayout;
//    private RelativeLayout dvrlayout;
//    private RelativeLayout mycarlayout;
//    private RelativeLayout picturelayout;
//    private RelativeLayout settinglayout;
//    private RelativeLayout videolayout;
//    private RelativeLayout wechatlayout;
//    private RelativeLayout withtencentlayout;
//    private RelativeLayout yueyequanlayout;
//    private ImageView iv_applets_one;
//    private ImageView iv_aps_one;
//    private ImageView iv_avm_one;
//    private ImageView iv_dvr_one;
//    private ImageView iv_mycar_one;
//    private ImageView iv_picture_one;
//    private ImageView iv_setting_one;
//    private ImageView iv_video_one;
//    private ImageView iv_wechat_one;
//    private ImageView iv_withtencent_one;
//    private ImageView iv_yueyequan_one;
//    private View carbitView;
//    private RelativeLayout carbitlayout;
//    private ImageView iv_carbit_one;
//    private TextView tv_catbit;
//    private ImageView iv_aps_frame;
//    private ImageView iv_aps_two;
//    private ImageView iv_avm_frame;
//    private ImageView iv_avm_two;
//
//
//    public SimpleCardView(Context context) {
//        super(context);
//        Log.d(TAG, "SimpleCardView: ");
//        mContext = context;
//        initView();
//        BcmImpl.getInstance().setOnBcmDataChangeListener(new BcmImpl.OnBcmDataChangeListener() {
//            @Override
//            public void powerStatusChange(int value) {
//                Log.d(TAG, "BcmDataListener powerStatusChange card: "+apsView);
//                if (avmView!=null){
//                    if (BcmImpl.getInstance().getPowerStatus()!=0){
//
//                        AAOP_HSkin.with(iv_avm_frame)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_avm_one)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_avm_top)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_avm_two)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_avm_b)//todo
//                                .applySkin(false);
//                    }else {
//                        AAOP_HSkin.with(iv_avm_frame)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.com_icon_small_frame_dis)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_avm_one)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_panoramic_s_dis)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_avm_two)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_panoramic_l_dis)//todo
//                                .applySkin(false);
//                    }
//                }
//            }
//
//            @Override
//            public void engineStatusChange(int value) {
//                Log.d(TAG, "BcmDataListener engineStatusChange card: ");
//                if (apsView!=null){
//                    if (BcmImpl.getInstance().getNewEngineStatus()!=0){
//
//                        AAOP_HSkin.with(iv_aps_frame)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_aps_one)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_aps_top)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_aps_two)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_panoramic_l_dis)//todo
//                                .applySkin(false);
//                    }else {
//                        AAOP_HSkin.with(iv_aps_frame)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.com_icon_small_frame_dis)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_aps_one)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_parking_s_dis)
//                                .applySkin(false);
//                        AAOP_HSkin.with(iv_aps_two)
//                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_parking_l_dis)//todo
//                                .applySkin(false);
//                    }
//                }
//            }
//        });
//    }
//
//    private void initView() {
//        // 加载小卡布局
//
//        apsView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_aps, null);
//        avmView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_avm, null);
//        dvrView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_dvr, null);
//        mycarView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_mycar, null);
//        pictureView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_picture, null);
//        settingView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_setting, null);
//        videoView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_video, null);
//        wechatView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_wechat, null);
//        withtencentView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_withtencent, null);
//        yueyequanView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_yueyequan, null);
//        carbitView = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.layout_smallcard_carbit, null);
//        //卡片名
//
//        tv_aps = apsView.findViewById(R.id.tv_name);
//        tv_avm = avmView.findViewById(R.id.tv_name);
//        tv_dvr = dvrView.findViewById(R.id.tv_name);
//        tv_mycar = mycarView.findViewById(R.id.tv_name);
//        tv_picture = pictureView.findViewById(R.id.tv_name);
//        tv_setting = settingView.findViewById(R.id.tv_name);
//        tv_video = videoView.findViewById(R.id.tv_name);
//        tv_wechat = wechatView.findViewById(R.id.tv_name);
//        tv_withtencent = withtencentView.findViewById(R.id.tv_name);
//        tv_yueyequan = yueyequanView.findViewById(R.id.tv_name);
//        tv_catbit = carbitView.findViewById(R.id.tv_name);
//        //父布局
//        apslayout = apsView.findViewById(R.id.parent_layout);
//        avmlayout = avmView.findViewById(R.id.parent_layout);
//        dvrlayout = dvrView.findViewById(R.id.parent_layout);
//        mycarlayout = mycarView.findViewById(R.id.parent_layout);
//        picturelayout = pictureView.findViewById(R.id.parent_layout);
//        settinglayout = settingView.findViewById(R.id.parent_layout);
//        videolayout = videoView.findViewById(R.id.parent_layout);
//        wechatlayout = wechatView.findViewById(R.id.parent_layout);
//        withtencentlayout = withtencentView.findViewById(R.id.parent_layout);
//        yueyequanlayout = yueyequanView.findViewById(R.id.parent_layout);
//        carbitlayout = carbitView.findViewById(R.id.parent_layout);
//        //lottie
//        aps_lottie_view = apsView.findViewById(R.id.lottie_view);
//        avm_lottie_view = avmView.findViewById(R.id.lottie_view);
//        dvr_lottie_view = dvrView.findViewById(R.id.lottie_view);
//        mycar_lottie_view = mycarView.findViewById(R.id.lottie_view);
//        picture_lottie_view = pictureView.findViewById(R.id.lottie_view);
//        setting_lottie_view = settingView.findViewById(R.id.lottie_view);
//        video_lottie_view = videoView.findViewById(R.id.lottie_view);
//
//        addWindowView();
//        initSkinChange();
//    }
//
//
//    private void addWindowView() {
//        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
//        windowViewManager.addWindowView(apsView);
//        windowViewManager.addWindowView(avmView);
//        windowViewManager.addWindowView(dvrView);
//        windowViewManager.addWindowView(pictureView);
//        windowViewManager.addWindowView(settingView);
//        windowViewManager.addWindowView(videoView);
//        windowViewManager.addWindowView(wechatView);
//        windowViewManager.addWindowView(withtencentView);
//        windowViewManager.addWindowView(yueyequanView);
//        windowViewManager.addWindowView(carbitView);
//        ISkinManager skinManager = AAOP_HSkin.getInstance();
//        skinManager.applySkin(apslayout, true);
//        skinManager.applySkin(avmlayout, true);
//        skinManager.applySkin(dvrlayout, true);
//        skinManager.applySkin(mycarlayout, true);
//        skinManager.applySkin(picturelayout, true);
//        skinManager.applySkin(settinglayout, true);
//        skinManager.applySkin(videolayout, false);
//        skinManager.applySkin(wechatlayout, true);
//        skinManager.applySkin(withtencentlayout, true);
//        skinManager.applySkin(yueyequanlayout, true);
//        skinManager.applySkin(carbitlayout, true);
//    }
//
//    private void initSkinChange() {
//
//
//        iv_aps_frame = (ImageView) apsView.findViewById(R.id.iv_aps_frame);
//        iv_aps_one = (ImageView) apsView.findViewById(R.id.iv_aps_one);
//        iv_aps_two = (ImageView) apsView.findViewById(R.id.iv_aps_two);
//
//        iv_avm_frame = (ImageView) avmView.findViewById(R.id.iv_avm_frame);
//        iv_avm_one = (ImageView) avmView.findViewById(R.id.iv_avm_one);
//        iv_avm_two = (ImageView) avmView.findViewById(R.id.iv_avm_two);
//
//        ImageView iv_dvr_frame = (ImageView) dvrView.findViewById(R.id.iv_dvr_frame);
//        iv_dvr_one = (ImageView) dvrView.findViewById(R.id.iv_dvr_one);
//        ImageView iv_dvr_two = (ImageView) dvrView.findViewById(R.id.iv_dvr_two);
//
//        ImageView iv_mycar_frame = (ImageView) mycarView.findViewById(R.id.iv_mycar_frame);
//        iv_mycar_one = (ImageView) mycarView.findViewById(R.id.iv_mycar_one);
//        ImageView iv_mycar_two = (ImageView) mycarView.findViewById(R.id.iv_mycar_two);
//
//
//        ImageView iv_picture_frame = (ImageView) pictureView.findViewById(R.id.iv_picture_frame);
//        iv_picture_one = (ImageView) pictureView.findViewById(R.id.iv_picture_one);
//        ImageView iv_picture_two = (ImageView) pictureView.findViewById(R.id.iv_picture_two);
//
//        ImageView iv_setting_frame = (ImageView) settingView.findViewById(R.id.iv_setting_frame);
//        iv_setting_one = (ImageView) settingView.findViewById(R.id.iv_setting_one);
//        ImageView iv_setting_two = (ImageView) settingView.findViewById(R.id.iv_setting_two);
//
//        ImageView iv_video_frame = (ImageView) videoView.findViewById(R.id.iv_video_frame);
//        iv_video_one = (ImageView) videoView.findViewById(R.id.iv_video_one);
//        ImageView iv_video_two = (ImageView) videoView.findViewById(R.id.iv_video_two);
//
//        ImageView iv_wechat_frame = (ImageView) wechatView.findViewById(R.id.iv_wechat_frame);
//        iv_wechat_one = (ImageView) wechatView.findViewById(R.id.iv_wechat_one);
//        ImageView iv_wechat_two = (ImageView) wechatView.findViewById(R.id.iv_wechat_two);
//
//        ImageView iv_withtencent_frame = (ImageView) withtencentView.findViewById(R.id.iv_withtencent_frame);
//        iv_withtencent_one = (ImageView) withtencentView.findViewById(R.id.iv_withtencent_one);
//        ImageView iv_withtencent_two = (ImageView) withtencentView.findViewById(R.id.iv_withtencent_two);
//
//        ImageView iv_yueyequan_frame = (ImageView) yueyequanView.findViewById(R.id.iv_yueyequan_frame);
//        iv_yueyequan_one = (ImageView) yueyequanView.findViewById(R.id.iv_yueyequan_one);
//        ImageView iv_yueyequan_two = (ImageView) yueyequanView.findViewById(R.id.iv_yueyequan_two);
//
//        ImageView iv_carbit_frame = (ImageView) carbitView.findViewById(R.id.iv_carbit_frame);
//        iv_carbit_one = (ImageView) carbitView.findViewById(R.id.iv_carbit_one);
//        ImageView iv_carbit_two = (ImageView) carbitView.findViewById(R.id.iv_carbit_two);
//
//
//        if (BcmImpl.getInstance().getNewEngineStatus()!=0){
//
//            AAOP_HSkin.with(iv_aps_frame)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_aps_one)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_aps_top)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_aps_two)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_aps_b)//todo
//                    .applySkin(false);
//        }else {
//            AAOP_HSkin.with(iv_aps_frame)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.com_icon_small_frame_dis)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_aps_one)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_parking_s_dis)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_aps_two)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_parking_l_dis)//todo
//                    .applySkin(false);
//        }
//
//        if (BcmImpl.getInstance().getPowerStatus()!=0){
//            AAOP_HSkin.with(iv_avm_frame)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_avm_one)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_avm_top)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_avm_two)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_avm_b)//todo
//                    .applySkin(false);
//
//        }else {
//            AAOP_HSkin.with(iv_avm_frame)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.com_icon_small_frame_dis)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_avm_one)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_panoramic_s_dis)
//                    .applySkin(false);
//            AAOP_HSkin.with(iv_avm_two)
//                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_panoramic_l_dis)//todo
//                    .applySkin(false);
//        }
//
//        AAOP_HSkin.with(iv_dvr_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_dvr_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_dvr)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_dvr_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_dvr_b)
//                .applySkin(false);
//
//        AAOP_HSkin.with(iv_mycar_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_mycar_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_mycar_top)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_mycar_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_mycar_b)
//                .applySkin(false);
//
//
//        AAOP_HSkin.with(iv_picture_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_picture_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_picture_top)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_picture_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_picture_b)
//                .applySkin(false);
//
//
//        AAOP_HSkin.with(iv_setting_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_setting_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_setting_top)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_setting_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_setting_b)
//                .applySkin(false);
//
//
//        AAOP_HSkin.with(iv_video_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_video_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_video_top)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_video_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_video_b)
//                .applySkin(false);
//
//
//        AAOP_HSkin.with(iv_wechat_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_wechat_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_wechat)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_wechat_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_wechat_b)
//                .applySkin(false);
//
//        AAOP_HSkin.with(iv_withtencent_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_withtencent_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_withtencent)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_withtencent_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_withtencent_b)
//                .applySkin(false);
//
//        AAOP_HSkin.with(iv_yueyequan_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_yueyequan_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_yueyequan)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_yueyequan_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_yueyequan_b)
//                .applySkin(false);
//
//
//        AAOP_HSkin.with(iv_carbit_frame)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_carbit_one)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_carbit)
//                .applySkin(false);
//        AAOP_HSkin.with(iv_carbit_two)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_carbit_b)
//                .applySkin(false);
//
//
//
//
//    }
//
//    /**
//     * 上屏
//     *
//     * @param id
//     * @param type
//     * @return
//     */
//    @Override
//    public View initCardView(String id, String type, String type1) {
//        Log.d(TAG, "initCardView: start");
//        switch (id) {
//            case ID_VIDEO:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(video_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.video45_lottie)
//                                .applySkin(false);
//                    }
//
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_video.setAlpha(0);
//                    }
//                    view = videoView;
//                }
//                break;
//            case ID_PICTURE:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(picture_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.picture45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_picture.setAlpha(0);
//                    }
//                    view = pictureView;
//                }
//                break;
//            case ID_SETTING:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(setting_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.setting45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_setting.setAlpha(0);
//                    }
//                    view = settingView;
//                }
//                break;
//            case ID_MYCAR:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(mycar_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.mycar45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_mycar.setAlpha(0);
//                    }
//                    view = mycarView;
//                }
//                break;
//            case ID_DVR:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(dvr_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.dvr45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_dvr.setAlpha(0);
//                    }
//                    view = dvrView;
//                }
//                break;
//            case ID_AVM:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(avm_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.avm45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_avm.setAlpha(0);
//                    }
//                    view = avmView;
//                }
//                break;
//
//            case ID_APS:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                        AAOP_HSkin
//                                .with(aps_lottie_view)
//                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.aps45_lottie)
//                                .applySkin(false);
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_aps.setAlpha(0);
//                    }
//                    view = apsView;
//                }
//                break;
//
//
//            case ID_YUEYEQUAN:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_yueyequan.setAlpha(0);
//                    }
//                    view = yueyequanView;
//                }
//                break;
//            case ID_WITHTENCENT:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_withtencent.setAlpha(0);
//                    }
//                    view = withtencentView;
//                }
//                break;
//
//            case ID_WECHAT:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_wechat.setAlpha(0);
//                    }
//                    view = wechatView;
//                }
//                break;
//            case ID_CARBIT:
//                if (type.equals(TYPE_SMALLCARD)) {
//                    if (type1.equals(DRAG_TO_INITCARD)) {
//
//                    }
//                    if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
//                        tv_catbit.setAlpha(0);
//                    }
//                    view = carbitView;
//                }
//                break;
//            case ID_HAVC:
//            case ID_RADIO:
//            case ID_MUSIC:
//            case ID_AIQUTING:
//            case ID_WEATHER:
//            case ID_TEL:
//            case ID_NAVI:
//            case ID_OFFROADINFO:
//                break;
//        }
//        Log.d(TAG, "initCardView: end" + view);
//        return view;
//
//    }
//
//    /**
//     * 下屏
//     *
//     * @param id
//     * @param type
//     */
//    @Override
//    public void unInitCardView(String id, String type) {
//    }
//
//    @Override
//    public void releaseResource() {
//    }
//
//    /**
//     * 播小卡单独动画
//     *
//     * @param id
//     */
//    @Override
//    public void playAnimation(String id, int delay) {
//        Log.d(TAG, "playAnimation: " + id);
//        Message msg = Message.obtain();
//        msg.obj = id;
//        handler.sendMessageDelayed(msg, delay);
//    }
//
//    @Override
//    public void onConfigurationChanged() {
//
//        tv_aps.setText(mContext.getResources().getString(R.string.apa));
//        tv_avm.setText(mContext.getResources().getString(R.string.avm));
//        tv_dvr.setText(mContext.getResources().getString(R.string.dvr));
//        tv_mycar.setText(mContext.getResources().getString(R.string.mycar));
//        tv_picture.setText(mContext.getResources().getString(R.string.picture));
//        tv_setting.setText(mContext.getResources().getString(R.string.settings));
//        String string = mContext.getResources().getString(R.string.video);
//        tv_video.setText(mContext.getResources().getString(R.string.video));
//        tv_wechat.setText(mContext.getResources().getString(R.string.wechat));
//        tv_withtencent.setText(mContext.getResources().getString(R.string.tencentway));
//        tv_yueyequan.setText(mContext.getResources().getString(R.string.yueyequan));
//        tv_catbit.setText(mContext.getResources().getString(R.string.easyconnect));
//        Log.d(TAG, "onConfigurationChanged: " + string);
//    }
//
//    @Override
//    public void launcherLoadComplete() {
//
//    }
//
//    @Override
//    public void launcherAnimationUpdate(int i) {
//
//    }
//
//
//}
//
