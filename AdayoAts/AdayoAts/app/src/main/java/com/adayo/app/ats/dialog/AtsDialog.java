package com.adayo.app.ats.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.app.ats.factory.ResourcesFactory;
import com.adayo.app.ats.view.CircleMenuLayout;
import com.adayo.app.ats.R;
import com.adayo.app.ats.util.SystemPropertyUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static com.adayo.app.ats.util.Constants.CURRENTMOD;
import static com.adayo.app.ats.util.Constants.PICTUREMOD;
import static com.adayo.app.ats.util.Constants.ATS_VERSION;
import static com.adayo.app.ats.util.Constants.TEXTMOD;
import static com.adayo.app.ats.util.Constants.mItemCenterTexts_High;
import static com.adayo.app.ats.util.Constants.mItemGlobalTexts_High;
import static com.adayo.app.ats.util.Constants.mTipsText_High;
import static com.adayo.app.ats.util.Constants.mbgImages_High;

public class AtsDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = ATS_VERSION + AtsDialog.class.getSimpleName();

    /**
     * 首先定义4个数组，三个是图片模式，一个是文字模式
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == ACTION_HINDETEXT) {
                int i = (int) msg.obj;
                confirmModeSuccess3sAnim(i);
            }

        }
    };
    private final int ACTION_CONFIRM = 123;
    private final int ACTION_TARGET = 124;
    private final int ACTION_HINDETEXT = 125;
    private final int ACTION_FAILED = 126;

    private boolean isShow = false;
    private RelativeLayout btLeft;
    private RelativeLayout btRight;
    private TextView tvLeft;
    private TextView tvRight;
    private String mCurrentDisplayMod;
    private CircleMenuLayout clMenulayout;
    private RelativeLayout rvLight;
    private Context context;
    private float mAngleValue = 0;
    private float mConfirAngleValue = 0;
    private int size26px = 26;
    private int size32px = 32;
    private Float offset = -3f;
    private Timer dialogtimer;
    private TimerTask dialogtask;
    private ImageView ivCancel;
    private ObjectAnimator rotation;
    private ImageView rlBg;
    private int targetAtsMode;
    private int currentAtsMode;
    private TextView tvTips;
    private Timer contenttimer;
    private TimerTask contenttimertask;
    private ImageView ivLight;
    private ImageView ivTiaoXingDeng;
    private ImageView ivMenuBg;
    private TextView content1tv1;
    private TextView content1tv2;
    private TextView content2tv1;
    private TextView content2tv2;
    private RelativeLayout rlContent2;//当前是什么模式
    private RelativeLayout rlContent1;//准备进入什么模式

    private final ResourcesFactory resourcesFactory;
    private RelativeLayout parentLayout;
    private int[] normalImages;
    private int[] disImages;
    private int[] selImages;
    private int[] globalTexts;
    private int type;
    private ImageView ivConfigHigh;
    private ImageView ivConfigLow;

    private boolean isContent1Show;
    private boolean isContent2Show = true;
    private int lastTargetMode = -1;
    private boolean isPlaying;
    private RelativeLayout rvPoint;


    public AtsDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        resourcesFactory = ResourcesFactory.getInstance(context);
        initView();
    }


    @Override
    public void show() {
        super.show();
        isShow = true;
        startCircleMenuBgAnim();
        startCircleMenuAnim();
        startBgAnim();
        startLightImgAnim();
        startPointImgAnim();
    }

    @Override
    public void cancel() {
        super.cancel();
        isShow = false;
    }


    private void initView() {
        Log.d(TAG, "initView: ");
        setContentView(R.layout.ats_dialog_layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.TOP;
        params.type = 2062;
        params.y = 104;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//      params.windowAnimations = R.style.LauncherBottomDialog;
        params.dimAmount = 0f;
        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        rlBg = (ImageView) findViewById(R.id.rl_bg);
        btLeft = (RelativeLayout) findViewById(R.id.rl_left);
        btRight = (RelativeLayout) findViewById(R.id.rl_right);
        tvLeft = (TextView) findViewById(R.id.tv_left);

        tvRight = (TextView) findViewById(R.id.tv_right);

        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        rvLight = (RelativeLayout) findViewById(R.id.rv_light);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        ivLight = (ImageView) findViewById(R.id.iv_light);
        ivTiaoXingDeng = (ImageView) findViewById(R.id.iv_tiaoxingdeng);
        rvLight = (RelativeLayout) findViewById(R.id.rv_light);
        rvPoint = (RelativeLayout) findViewById(R.id.rv_point);
        ivMenuBg = (ImageView) findViewById(R.id.iv_menu_bg);

        rlContent1 = (RelativeLayout) findViewById(R.id.rl_content1);
        rlContent2 = (RelativeLayout) findViewById(R.id.rl_content2);
        content1tv1 = (TextView) findViewById(R.id.content1tv1);
        content1tv2 = (TextView) findViewById(R.id.content1tv2);
        content2tv1 = (TextView) findViewById(R.id.content2tv1);
        content2tv2 = (TextView) findViewById(R.id.content2tv2);


        ivConfigHigh = (ImageView) findViewById(R.id.iv_config_high);
        ivConfigLow = (ImageView) findViewById(R.id.iv_config_low);
        boolean isConfigWading = ResourcesFactory.getInstance(getContext()).isConfigWading();
        if (isConfigWading) {
            ivConfigHigh.setVisibility(View.VISIBLE);
            ivConfigLow.setVisibility(View.INVISIBLE);
        } else {
            ivConfigHigh.setVisibility(View.INVISIBLE);
            ivConfigLow.setVisibility(View.VISIBLE);
        }
        rlBg.setAlpha(0f);
        rvLight.setAlpha(0f);
        rlContent2.setAlpha(0f);
        btLeft.setSelected(true);
        tvLeft.setSelected(true);
        btRight.setSelected(false);
        tvRight.setSelected(false);
        btLeft.setOnClickListener(this);
        btRight.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        initSkinChange();
        getCurrentDisPlayMod();//获取显示模式
        initCircleMenuLayout();
    }

    /**
     * 设置皮肤
     */
    private void initSkinChange() {
        AAOP_HSkin
                .getWindowViewManager()
                .addWindowView(parentLayout);
        AAOP_HSkin
                .with(btLeft)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.button_image_bg_selector)
                .applySkin(false);
        AAOP_HSkin
                .with(btRight)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.button_text_bg_selector)
                .applySkin(false);

        AAOP_HSkin
                .with(ivLight)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.glow_chuanyue_sel)
                .applySkin(false);
        AAOP_HSkin
                .with(ivTiaoXingDeng)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_tiaoxingdeng)
                .applySkin(false);
        AAOP_HSkin
                .with(ivMenuBg)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_empty_dial)
                .applySkin(false);
        AAOP_HSkin
                .with(ivConfigHigh)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_line_1)
                .applySkin(false);
        AAOP_HSkin
                .with(ivConfigLow)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_line_2)
                .applySkin(false);


        AAOP_HSkin
                .with(tvLeft)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.color_bg_selector)
                .applySkin(false);

        AAOP_HSkin
                .with(tvRight)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.color_bg_selector)
                .applySkin(false);


    }

    /**
     * 读取上一次的显示模式 文字 / 图片
     */
    private void getCurrentDisPlayMod() { //todo tab变了布局变了吗
        mCurrentDisplayMod = SystemPropertyUtil.getSystemProperty(CURRENTMOD, String.valueOf(TEXTMOD));
        if (mCurrentDisplayMod.equals(String.valueOf(PICTUREMOD))) {
            setImageBgSelector(true);
        } else if (mCurrentDisplayMod.equals(String.valueOf(TEXTMOD))) {
            setImageBgSelector(false);
        }
        Log.d(TAG, "getCurrentMod: " + mCurrentDisplayMod);
    }

    /**
     * 初始化菜单项
     */
    private void initCircleMenuLayout() {
        clMenulayout = (CircleMenuLayout) findViewById(R.id.cl_menulayout);//菜单项控件
        clMenulayout.setAlpha(0f);
        clMenulayout.setCurrentMode(mCurrentDisplayMod);
        normalImages = resourcesFactory.getNormalImages();
        disImages = resourcesFactory.getDisImages();
        selImages = resourcesFactory.getSelImages();
        globalTexts = resourcesFactory.getGlobalTexts();
        clMenulayout.setMenuItemIconsAndTexts(normalImages, disImages, selImages, globalTexts);
    }

    /**
     * 设置文字/图片按钮的选中状态
     */
    private void setImageBgSelector(boolean b) {
        btLeft.setSelected(b);
        tvLeft.setSelected(b);
        btRight.setSelected(!b);
        tvRight.setSelected(!b);
        startOrUpdateDialogTimer();
    }

    /**
     * 开启或者更新计时器用于控制弹窗消失
     *
     * @param
     */
    public void startOrUpdateDialogTimer() {
        if (dialogtimer != null) {
            dialogtimer.cancel();
            dialogtimer = null;
            dialogtask.cancel();
            dialogtask = null;
        }
        if (dialogtimer == null) {
            dialogtimer = new Timer();
            dialogtask = new TimerTask() {
                @Override
                public void run() {
                    dialogtimer.cancel();
                    dialogtimer = null;
                    AtsDialog.this.cancel();//消去dialog
                }
            };
        }
        dialogtimer.schedule(dialogtask, 1000 * 10);
    }

    /**
     * 开启或者更新计时器用于中间文言更新或消失
     *
     * @param
     */
    public void startOrUpdateContentTimer(final int i) {

        if (contenttimer != null) {
            contenttimer.cancel();
            contenttimer = null;
            if (contenttimertask != null) {
                contenttimertask.cancel();
                contenttimertask = null;
            }
        }
        if (contenttimer == null) {
            contenttimer = new Timer();
            contenttimertask = new TimerTask() {
                @Override
                public void run() {
                    contenttimertask.cancel();
                    contenttimertask = null;
                    Message message = Message.obtain();
                    message.what = ACTION_HINDETEXT;
                    message.obj = i;
                    handler.sendMessage(message);
                }
            };
        }
        contenttimer.schedule(contenttimertask, 1000 * 3);
    }

    /**
     * 设置ATS当前模式
     *
     * @param mode
     */
    public void setTargetAtsMode(int mode) {
        if (targetAtsMode == mode || mode == -1) {
            return;
        }
        Log.d(TAG, "test===> target : " + mItemCenterTexts_High[mode]);
        if (mode < 0) {
            return;
        }
        if (rotation != null) {
            rotation.cancel();
        }
        this.targetAtsMode = mode;
        clMenulayout.setTargetAtsMode(mode);//给菜单项更新ATS目标模式
        int mValueUnit = resourcesFactory.getValueUnit();
        Map<Integer, Float> offSetMap = resourcesFactory.getRotationOffset();
        offset = offSetMap.get(mode);
        Log.d(TAG, "setTargetAtsMode: ------------- " + mode + " " + this.offset);

        if (!isShowing()) {//如果没显示  更新值
            rotation = ObjectAnimator.ofFloat(rvLight, "rotation", mValueUnit * mode + this.offset, mValueUnit * mode + this.offset);
            rotation.setDuration(0);
        } else {
            rotation = ObjectAnimator.ofFloat(rvLight, "rotation", mAngleValue, mValueUnit * mode + this.offset);
            rotation.setDuration(150);
        }
        rotation.start();
        mAngleValue = mValueUnit * mode + this.offset;

//        if (lastTargetMode != targetAtsMode) {
//            Log.d(TAG, "ats============>:Target  "+isContent1Show +"   "+isContent2Show);
//            updateContent1(ACTION_TARGET);
//            updateContent2(ACTION_TARGET);
//        }
        lastTargetMode = targetAtsMode;
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isPlaying = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isPlaying = false;
            }
        });
    }

    /**
     * 设置ATS目标模式
     *
     * @param mode
     */
    public void setCurrentAtsMode(int mode) {
        Log.d(TAG, "test===> confirm : " + mode + "     ");
        Message message = Message.obtain();//更新中间文字动画
        if (mode == -1) {
            updateContent1(ACTION_FAILED);
            updateContent2(ACTION_FAILED);
        } else {
            this.currentAtsMode = mode;
            rlBg.setBackgroundResource(mbgImages_High[mode]);//更新背景
            tvTips.setText(getContext().getResources().getString(mTipsText_High[mode]));//底部提示语改变
            clMenulayout.setCurrentAtsMode(mode);//更新ATS模式
            updateContent1(ACTION_CONFIRM);
            updateContent2(ACTION_CONFIRM);
            int mValueUnit = resourcesFactory.getValueUnit();
            if (!isShowing()) {//如果没显示  更新值
                ObjectAnimator rotation = ObjectAnimator.ofFloat(rvPoint, "rotation", mValueUnit * mode + this.offset, mValueUnit * mode + this.offset);
                rotation.setDuration(0);
                rotation.start();

            } else {

                ObjectAnimator rotation = ObjectAnimator.ofFloat(rvPoint, "rotation", mConfirAngleValue, mValueUnit * mode + this.offset);
                rotation.setDuration(150);
                rotation.start();
                mConfirAngleValue = mValueUnit * mode + this.offset;
            }
        }
    }

    private void updateContent1(int type) { // rl_content_1
        Log.d(TAG, "updateContent1: " + type);
        if (isContent1Show) {
            isContent1Show = false;
            ObjectAnimator cancelX = ObjectAnimator.ofFloat(rlContent1, "scaleX", 1f, 0.7f);
            ObjectAnimator cancelY = ObjectAnimator.ofFloat(rlContent1, "scaleY", 1f, 0.7f);
            ObjectAnimator cancelAlpha = ObjectAnimator.ofFloat(rlContent1, "alpha", 1, 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(cancelX).with(cancelY).with(cancelAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        } else {
            isContent1Show = true;
            content1tv2.setAlpha(1);
            content1tv2.setScaleX(1);
            content1tv2.setScaleY(1);

            if (type == ACTION_CONFIRM) {// todo 更新文字
                content1tv1.setTextSize(COMPLEX_UNIT_PX, size32px);
                content1tv2.setTextSize(COMPLEX_UNIT_PX, size26px);
                if (currentAtsMode != -1) {
                    content1tv1.setText(getContext().getResources().getString(mItemCenterTexts_High[currentAtsMode]));
                }
                content1tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[10]));//已开启
                startOrUpdateContentTimer(1);

            } else if (type == ACTION_TARGET) {
                content1tv1.setTextSize(COMPLEX_UNIT_PX, size26px);//COMPLEX_UNIT_PX 表示后面的值是px
                content1tv2.setTextSize(COMPLEX_UNIT_PX, size32px);
                content1tv1.setText(getContext().getResources().getString(mItemCenterTexts_High[9]));//正在准备进入
                if (targetAtsMode != -1) {
                    content1tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[targetAtsMode]));
                }
            } else if (type == ACTION_FAILED) {

                content1tv1.setTextSize(COMPLEX_UNIT_PX, size32px);
                content1tv2.setTextSize(COMPLEX_UNIT_PX, size26px);
                content1tv1.setText(getContext().getResources().getString(mItemCenterTexts_High[targetAtsMode]));//
                if (targetAtsMode != -1) {
                    content1tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[11]));
                }
            }
            ObjectAnimator displayX = ObjectAnimator.ofFloat(rlContent1, "scaleX", 0.7f, 1);
            ObjectAnimator displayY = ObjectAnimator.ofFloat(rlContent1, "scaleY", 0.7f, 1);
            ObjectAnimator displayAlpha = ObjectAnimator.ofFloat(rlContent1, "alpha", 0, 1);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(displayX).with(displayY).with(displayAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        }
        Log.d(TAG, "ats============>:  " + isContent1Show + "   " + isContent2Show);
    }


    private void updateContent2(int type) {   //rl_content_2
        if (isContent2Show) {
            isContent2Show = false;
            ObjectAnimator cancelX = ObjectAnimator.ofFloat(rlContent2, "scaleX", 1f, 0.7f);
            ObjectAnimator cancelY = ObjectAnimator.ofFloat(rlContent2, "scaleY", 1f, 0.7f);
            ObjectAnimator cancelAlpha = ObjectAnimator.ofFloat(rlContent2, "alpha", 1, 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(cancelX).with(cancelY).with(cancelAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        } else {
            isContent2Show = true;
            content2tv2.setAlpha(1);
            content2tv2.setScaleX(1);
            content2tv2.setScaleY(1);
            if (type == ACTION_CONFIRM) {//  todo 更新文字
                content2tv1.setTextSize(COMPLEX_UNIT_PX, size32px);
                content2tv2.setTextSize(COMPLEX_UNIT_PX, size26px);
                if (currentAtsMode != -1) {
                    content2tv1.setText(getContext().getResources().getString(mItemCenterTexts_High[currentAtsMode]));
                }
                content2tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[10]));//已开启
                startOrUpdateContentTimer(2);
            } else if (type == ACTION_TARGET) {
                content2tv1.setTextSize(COMPLEX_UNIT_PX, size26px);//COMPLEX_UNIT_PX 表示后面的值是px
                content2tv2.setTextSize(COMPLEX_UNIT_PX, size32px);
                content2tv1.setText(getContext().getResources().getString(mItemCenterTexts_High[9]));//正在准备进入
                if (targetAtsMode != -1) {
                    content2tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[targetAtsMode]));
                }
            } else if (type == ACTION_FAILED) {
                content2tv1.setTextSize(COMPLEX_UNIT_PX, size32px);
                content2tv2.setTextSize(COMPLEX_UNIT_PX, size26px);
                content2tv1.setText(getContext().getResources().getString(mItemGlobalTexts_High[targetAtsMode]));//正在准备进入
                if (targetAtsMode != -1) {
                    content2tv2.setText(getContext().getResources().getString(mItemCenterTexts_High[11]));
                }
            }
            ObjectAnimator displayX = ObjectAnimator.ofFloat(rlContent2, "scaleX", 0.7f, 1);
            ObjectAnimator displayY = ObjectAnimator.ofFloat(rlContent2, "scaleY", 0.7f, 1);
            ObjectAnimator displayAlpha = ObjectAnimator.ofFloat(rlContent2, "alpha", 0, 1);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(displayX).with(displayY).with(displayAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        }
        Log.d(TAG, "ats============>:  " + isContent1Show + "   " + isContent2Show);
    }

    private void confirmModeSuccess3sAnim(int i) {
        if (i == 1) {
            ObjectAnimator cancelX = ObjectAnimator.ofFloat(content1tv2, "scaleX", 1f, 0.7f);
            ObjectAnimator cancelY = ObjectAnimator.ofFloat(content1tv2, "scaleY", 1f, 0.7f);
            ObjectAnimator cancelAlpha = ObjectAnimator.ofFloat(content1tv2, "alpha", 1, 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(cancelX).with(cancelY).with(cancelAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        } else if (i == 2) {
            ObjectAnimator cancelX = ObjectAnimator.ofFloat(content2tv2, "scaleX", 1f, 0.7f);
            ObjectAnimator cancelY = ObjectAnimator.ofFloat(content2tv2, "scaleY", 1f, 0.7f);
            ObjectAnimator cancelAlpha = ObjectAnimator.ofFloat(content2tv2, "alpha", 1, 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(cancelX).with(cancelY).with(cancelAlpha);
            animatorSet.setDuration(500);
            animatorSet.start();
        }

    }

    /**
     * dialog出现动画
     */

    private void startCircleMenuBgAnim() {//轮盘(带有北京字样)背景

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivMenuBg, "scaleX", 0.5f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivMenuBg, "scaleY", 0.5f, 1);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivMenuBg, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).with(alpha);
        animatorSet.setDuration(850);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());
        alpha.setInterpolator(new AccelerateInterpolator());
//        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.start();
    }

    private void startCircleMenuAnim() {//轮盘菜单项(各驾驶模式)

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(clMenulayout, "scaleX", 0.3f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(clMenulayout, "scaleY", 0.3f, 1);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(clMenulayout, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).with(alpha);
        animatorSet.setDuration(500);
        animatorSet.setStartDelay(100);
        scaleX.setInterpolator(new DecelerateInterpolator());
        scaleY.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void startLightImgAnim() {//亮光透明度动画
        rvLight.setAlpha(0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(rvLight, "alpha", 0, 1);
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.setDuration(396);
        alpha.setStartDelay(500);
        alpha.start();
    }

    private void startPointImgAnim() {//亮点儿透明度动画
        rvPoint.setAlpha(0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(rvPoint, "alpha", 0, 1);
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.setDuration(396);
        alpha.setStartDelay(500);
        alpha.start();
    }

    private void startBgAnim() {//背景透明度缩放动画

        Log.d(TAG, "startBgAnim: -----");
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlBg, "scaleX", 1.1f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlBg, "scaleY", 1.1f, 1);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(rlBg, "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).with(alpha);
        animatorSet.setDuration(330);
        scaleX.setInterpolator(new AccelerateInterpolator());
        scaleY.setInterpolator(new AccelerateInterpolator());
        animatorSet.start();
    }

    public void startNoCycleTipsAnim(int requestDriveMode) { //提示用户不可循环切换动效
        Log.d(TAG, "startNoCycleTipsAnim: " + targetAtsMode + " " + currentAtsMode + " " + requestDriveMode + "  " + isPlaying);
        int tipsAnimOffset18 = 10;

        if (isPlaying) {
            return;
        }


        if (requestDriveMode == 1) { //顺时针旋转
            if (resourcesFactory.isConfigWading()) {
                if (currentAtsMode == 8 && targetAtsMode == 8) { //涉水
                    if (requestDriveMode == 1) {
                        int mValueUnit = resourcesFactory.getValueUnit();
                        rotation = ObjectAnimator.ofFloat(rvLight, "rotation",
                                mAngleValue,
                                mValueUnit * targetAtsMode + 1,
                                mValueUnit * targetAtsMode + 1,
                                mValueUnit * targetAtsMode + 1,
                                mValueUnit * targetAtsMode + 1,
                                mValueUnit * targetAtsMode + 1,
                                (mValueUnit * targetAtsMode + offset));//使用 mAngleValue 要保证 先上报请求模式
                        rotation.setDuration(333);
                        rotation.start();
                        isPlaying = true;
                    }
                }
            } else {
                if (currentAtsMode == 7 && targetAtsMode == 7) { //岩石
                    int mValueUnit = resourcesFactory.getValueUnit();
                    rotation = ObjectAnimator.ofFloat(rvLight, "rotation",
                            mAngleValue,
                            mValueUnit * targetAtsMode + tipsAnimOffset18,
                            mValueUnit * targetAtsMode + tipsAnimOffset18,
                            mValueUnit * targetAtsMode + tipsAnimOffset18,
                            mValueUnit * targetAtsMode + tipsAnimOffset18,
                            mValueUnit * targetAtsMode + tipsAnimOffset18,
                            mValueUnit * targetAtsMode);//使用 mAngleValue 要保证 先上报请求模式
                    rotation.setDuration(333);
                    rotation.start();
                    isPlaying = true;
                }
            }
        }

        if (requestDriveMode == 2) {
            if (resourcesFactory.isConfigWading()) {
                if (currentAtsMode == 0 && targetAtsMode == 0) { //运动
                    int mValueUnit = resourcesFactory.getValueUnit();
                    rotation = ObjectAnimator.ofFloat(rvLight, "rotation",
                            mAngleValue,
                            mValueUnit * targetAtsMode - 10,
                            mValueUnit * targetAtsMode - 10,
                            mValueUnit * targetAtsMode - 10,
                            mValueUnit * targetAtsMode - 10,
                            mValueUnit * targetAtsMode - 10,
                            mValueUnit * targetAtsMode);//使用 mAngleValue 要保证 先上报请求模式
                    rotation.setDuration(300);
                    rotation.start();
                    isPlaying = true;
                }
            } else {
                if (currentAtsMode == 0 && targetAtsMode == 0) { //运动
                    int mValueUnit = resourcesFactory.getValueUnit();

                    rotation = ObjectAnimator.ofFloat(rvLight, "rotation",
                            mAngleValue,
                            mValueUnit * targetAtsMode - tipsAnimOffset18,
                            mValueUnit * targetAtsMode - tipsAnimOffset18,
                            mValueUnit * targetAtsMode - tipsAnimOffset18,
                            mValueUnit * targetAtsMode - tipsAnimOffset18,
                            mValueUnit * targetAtsMode - tipsAnimOffset18,
                            mValueUnit * targetAtsMode);//使用 mAngleValue 要保证 先上报请求模式
                    rotation.setDuration(300);
                    rotation.start();
                    isPlaying = true;

                }
            }

        }

        if (rotation != null) {
            rotation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isPlaying = false;
                    Log.d(TAG, "fffffffffffaaa onAnimationCancel: ");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isPlaying = false;
                    Log.d(TAG, "fffffffffffaaa onAnimationEnd: ");
                }
            });
        }


    }

    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left://图片模式
                setImageBgSelector(true);//更新tab栏
                upDateMenuLayout(PICTUREMOD);//更新菜单项UI
                SystemPropertyUtil.setSystemProperty(CURRENTMOD, String.valueOf(PICTUREMOD));//存系统属性
                break;
            case R.id.rl_right://文字模式
                setImageBgSelector(false);//更新tab栏
                upDateMenuLayout(TEXTMOD);//更新菜单项UI
                SystemPropertyUtil.setSystemProperty(CURRENTMOD, String.valueOf(TEXTMOD));//存系统属性
                break;
            case R.id.iv_cancel://退出图标
                rlBg.setAlpha(0f);
                clMenulayout.setAlpha(0f);
                rvLight.setAlpha(0f);
                rlContent2.setAlpha(0f);
                AtsDialog.this.cancel();
                break;
            default:
                break;
        }
    }

    /**
     * upDateMenuLayout
     *
     * @param mod
     */
    private void upDateMenuLayout(int mod) {
        clMenulayout.updateCurrentMode(mod);
        clMenulayout.setCurrentMode(String.valueOf(mod));//给菜单项传参
        clMenulayout.setTargetAtsMode(targetAtsMode);//给菜单项更新ATS目标模式
        clMenulayout.setCurrentAtsMode(currentAtsMode);//更新ATS模式
    }

    /**
     * language
     */
    public void onConfigurationChanged() {
        if (tvLeft != null) {
            tvLeft.setText(getContext().getResources().getString(R.string.picturemode));

        }
        if (tvRight != null) {
            tvRight.setText(getContext().getResources().getString(R.string.textmode));
        }
    }


}
