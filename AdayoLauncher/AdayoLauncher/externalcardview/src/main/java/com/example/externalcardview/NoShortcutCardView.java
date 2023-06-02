package com.example.externalcardview;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_APS;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AVM;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_CARBIT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WECHAT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_YUEYEQUAN;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.airbnb.lottie.LottieAnimationView;
import com.example.BcmImpl;

public class NoShortcutCardView extends RelativeLayout implements IViewBase {
    /**
     * 无快捷操作的卡片类
     * A彩色卡片 不区分主题   不设置lottie  v2小图  v3大图 在initview时都要显示
     * <p>
     * B非彩色卡片儿 需要lottie
     * 第一套组主题
     * initView v2显示  V3隐藏
     * 1 入场   V3用lottieView (动画播放时设置的图)
     * 2 拖拽上屏 V3用lottieView (lottieView手动添加动画最后一张)
     * <p>
     * 第二套主题
     * initView v2隐藏  V3隐藏
     * 1 入场   V2V3用lottieView (动画播放时设置的图)
     * 2 拖拽上屏 V2V3用lottieView (lottieView手动添加动画最后一张)
     */
    private static final String TAG = "NoShortcutCardView";
    private final Context mContext;
    private final int img1;
    private final int img2;
    private final int img3;
    private final String jsonName;
    private  String id;
    private TextView textView;
    private int stringName;
    private LottieAnimationView lottieView;
    private View view;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String id = (String) msg.obj;
            if (CURRENT_THEME == 2) {
                Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                card_name_animator.setTarget(textView);
                card_name_animator.start();
            }

            if (jsonName != null) {
                lottieView.setAnimation(jsonName + ConstantUtil.CURRENT_THEME + ".json");//
                lottieView.playAnimation();
                lottieView.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (ID_APS.equals(id)){
                            if (BcmImpl.getInstance().getNewEngineStatus()!=0){
                                textView.setAlpha(1f);
                            }else {
                                textView.setAlpha(0.38f);
                            }

                        }

                        if (ID_AVM.equals(id)){
                            if (BcmImpl.getInstance().getPowerStatus()!=0){
                                textView.setAlpha(1f);
                            }else {
                                textView.setAlpha(0.38f);
                            }
                        }
                        super.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        AAOP_HSkin
                                .with(lottieView)
                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, img3)
                                .applySkin(false);
                    }
                });
            } else {//当没有json不管是第几套都要显示小图iv_2，没有json所以显示iv_3
            }
        }
    };
    private ImageView iv_1;
    private ImageView iv_2;
    private ImageView iv_3;


    public NoShortcutCardView(Context context, int Res, int stringName, int img1, final int img2, final int img3, String jsonName, final String id) {
        super(context);
        mContext = context;
        this.stringName = stringName;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.jsonName = jsonName;
        this.id = id;
        initView(Res, stringName, img1, img2, img3, id);

        BcmImpl.getInstance().setOnBcmDataChangeListener(new BcmImpl.OnBcmDataChangeListener() {
            @Override
            public void powerStatusChange(int value) {

                if (ID_AVM.equals(NoShortcutCardView.this.id)){
                    if (value!=0){
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img2)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img3)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(1f);
                    }else {
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_panoramic_s_dis)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_panoramic_l_dis)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(0.38f);
                    }
                }
            }

            @Override
            public void engineStatusChange(int value) {
                if (ID_APS.equals(NoShortcutCardView.this.id)) {
                    String trailerMode = BcmImpl.getInstance().getTrailerMode();
                    if (value != 0&& "false".equals(trailerMode)) {
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,img2)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img3)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(1);
                    }else {
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_parking_s_dis)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_parking_l_dis)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(0.38f);
                    }
                }
            }

            @Override
            public void onTrailerModeStatusChange() {

                if (ID_APS.equals(NoShortcutCardView.this.id)) {
                    int newEngineStatus = BcmImpl.getInstance().getNewEngineStatus();
                    String trailerMode = BcmImpl.getInstance().getTrailerMode();
                    if (newEngineStatus != 0 && "false".equals(trailerMode)) {
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img2)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img3)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(1);
                    } else {
                        AAOP_HSkin.with(iv_2)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_parking_s_dis)
                                .applySkin(false);
                        AAOP_HSkin.with(iv_3)
                                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_parking_l_dis)
                                //todo
                                .applySkin(false);
                        textView.setAlpha(0.38f);
                    }
                }

            }
        },2);

    }

    private void initView(int Res, int stringName, int img1, int img2, int img3, String id) {
        // 布局   名字  图片1(外框)  图片2  图片3
        view = AAOP_HSkin.getLayoutInflater(mContext).inflate(Res, null);
        //图片1(外框)
        iv_1 = (ImageView) view.findViewById(R.id.Img1);
        //图片2
        iv_2 = (ImageView) view.findViewById(R.id.Img2);
        //图片3
        iv_3 = (ImageView) view.findViewById(R.id.Img3);
        this.stringName = stringName;
        textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setText(mContext.getResources().getString(stringName));
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.parent_layout);
        lottieView = (LottieAnimationView) view.findViewById(R.id.lottie_view);


        //动画IMG
        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(view);
        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(layout, true);

        if (isAvmOrApa(id)){
            return;
        }

        AAOP_HSkin.with(iv_2)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img2)
                .applySkin(false);
        if (ID_YUEYEQUAN.equals(id) || ID_WECHAT.equals(id) || ID_WITHTENCENT.equals(id) || ID_CARBIT.equals(id)) {
            //无json 则不判断主题都要显示小图
            iv_2.setVisibility(VISIBLE);
            iv_3.setVisibility(VISIBLE);
            AAOP_HSkin
                    .with(iv_3)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, img3)
                    .applySkin(false);

        } else {
            iv_2.setVisibility(VISIBLE);
            iv_3.setVisibility(GONE);
        }
        Log.d(TAG, "initView: " + CURRENT_THEME);

    }

    @Override
    public View initCardView(String id, String type, String type1) {

        if (type.equals(TYPE_SMALLCARD)) {
            if (type1.equals(DRAG_TO_INITCARD)) {
                Log.d(TAG, "initCardView: " + id + " img2 " + img2);
                if (!ID_YUEYEQUAN.equals(id) || !ID_WECHAT.equals(id) || !ID_WITHTENCENT.equals(id) || !ID_CARBIT.equals(id)) {
                    AAOP_HSkin
                            .with(lottieView)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, img3)
                            .applySkin(false);
                }
            }

            if (CURRENT_THEME == 2 && type1.equals(DEFAULT_INITCARD)) {
                textView.setAlpha(0);
            }

        }

        return view;
    }

    @Override
    public void unInitCardView(String id, String type) {

    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void playAnimation(String id, int delay) {
        Message msg = Message.obtain();
        msg.obj = id;
        handler.sendMessageDelayed(msg, delay);
    }

    @Override
    public void onConfigurationChanged() {
        if (stringName > 0) {
            textView.setText(mContext.getResources().getString(stringName));
        }
    }

    @Override
    public void launcherLoadComplete() {

    }

    @Override
    public void launcherAnimationUpdate(int i) {

    }

    public void setTheme(int i) {

    }


    private boolean isAvmOrApa(String id){
        if (ID_APS.equals(id)){
            int newEngineStatus = BcmImpl.getInstance().getNewEngineStatus();
            iv_2.setVisibility(VISIBLE);
            iv_3.setVisibility(GONE);

            AAOP_HSkin.with(iv_2)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img2)
                    .applySkin(false);

            if (newEngineStatus!=0){
                textView.setAlpha(1f);
            }else { //todo
                textView.setAlpha(0.38f);
            AAOP_HSkin.with(iv_2)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_parking_s_dis)
                    .applySkin(false);
            AAOP_HSkin.with(iv_3)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_parking_l_dis)
                    //todo
                    .applySkin(false);
            }

            return true;
        }else if (ID_AVM.equals(id)){
            iv_2.setVisibility(VISIBLE);
            iv_3.setVisibility(GONE);

            AAOP_HSkin.with(iv_2)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, img2)
                    .applySkin(false);
            int powerStatus = BcmImpl.getInstance().getPowerStatus();
            if (powerStatus!=0){
                textView.setAlpha(1f);
            }else {
                AAOP_HSkin.with(iv_2)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.icon_smallcard_panoramic_s_dis)
                        .applySkin(false);
                AAOP_HSkin.with(iv_3)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_smallcard_panoramic_l_dis)
                        //todo
                        .applySkin(false);
                textView.setAlpha(0.38f);
            }
            return true;
        }else {
            return false;
        }

    }

}
