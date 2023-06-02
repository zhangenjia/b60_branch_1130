package com.adayo.app.launcher.btphone.ui.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.app.launcher.btphone.R;
import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;

public class BTPhoneSmallCardView extends RelativeLayout implements View.OnClickListener, IViewBase {
    private static final String TAG = BTPhoneSmallCardView.class.getSimpleName();
    private static final int SHARE_BLUETOOTH = 27;
    private Context mContext;
    private View mCardView;
    private TextView mDeviceName;
    private ShareDataManager mShareDataMgr;
    private LottieAnimationView lottie_view;
    private static final int PLAY_ANIMATION = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (ConstantUtil.CURRENT_THEME != -1) {
                if (CURRENT_THEME == 2) {
                    Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                    card_name_animator.setTarget(tv_smallcardname);
                    card_name_animator.start();
                }
                if (msg.what == PLAY_ANIMATION) {
                    lottie_view.setAnimation("tel" + ConstantUtil.CURRENT_THEME + ".json");
                    lottie_view.playAnimation();
                    lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            AAOP_HSkin.with(lottie_view)
                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.tel45_lottie)
                                    .applySkin(false);
                        }
                    });
                }
            }
        }
    };
    private TextView tv_smallcardname;
    private ImageView iv_bt_one;

    public BTPhoneSmallCardView(Context context) {
        super(context);
        mContext = context;
        initView();
        mShareDataMgr = ShareDataManager.getShareDataManager();
        mShareDataMgr.registerShareDataListener(SHARE_BLUETOOTH, mCallBack);
        String s = mShareDataMgr.getShareData(SHARE_BLUETOOTH);
        btConnectState(s);
    }

    public BTPhoneSmallCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initView() {
        mCardView = LayoutInflater.from(mContext).inflate(R.layout.small_card_layout, null);
        mDeviceName = mCardView.findViewById(R.id.device_name_tv);
        tv_smallcardname = (TextView) mCardView.findViewById(R.id.tv_smallcardname);
        lottie_view = (LottieAnimationView) mCardView.findViewById(R.id.lottie_view);
        RelativeLayout parent_layout = (RelativeLayout) mCardView.findViewById(R.id.parent_layout);
        ImageView iv_bt_frame = (ImageView) mCardView.findViewById(R.id.iv_bt_frame);
        iv_bt_one = (ImageView) mCardView.findViewById(R.id.iv_bt_one);
        ImageView iv_bt_two = (ImageView) mCardView.findViewById(R.id.iv_bt_two);
        lottie_view.setImageAssetsFolder("images");

        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(mCardView);

        ISkinManager skinManager = AAOP_HSkin.getInstance();
        AAOP_HSkin.with(iv_bt_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_tel_top)
                .applySkin(false);//设置小背景，第二套主题因部分卡片把小背景切到lottie资源中，所以用透明图代替
        AAOP_HSkin.with(iv_bt_two)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_tel_b)
                .applySkin(false);
        AAOP_HSkin.with(iv_bt_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                .applySkin(false);
        skinManager.applySkin(parent_layout, true);




    }

    IShareDataListener mCallBack = new IShareDataListener() {
        @Override
        public void notifyShareData(int i, String s) {
            btConnectState(s);
        }
    };

    @Override
    public void onClick(View v) {//Android library中生成的R.java中的资源ID不是常数不能使用switch…case

    }


    @Override
    public View initCardView(String id, String type, String type1) {
        if (type.equals(TYPE_BIGCARD)) {

        } else if (type.equals(TYPE_SMALLCARD)) {
            //返回小卡
            if (type1.equals(DRAG_TO_INITCARD)) {
                AAOP_HSkin.with(lottie_view)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.tel45_lottie)
                        .applySkin(false);
            }
            if (type1.equals(DEFAULT_INITCARD)&&CURRENT_THEME==2){
                tv_smallcardname.setAlpha(0);
            }
            return mCardView;
        }

        return null;
    }


    @Override
    public void unInitCardView(String id, String type) {

    }

    @Override
    public void releaseResource() {

    }

    /**
     * 播小卡动画
     *
     * @param id
     */
    @Override
    public void playAnimation(String id, int delay) {
        Log.d(TAG, "playAnimation bt phone : " + delay);
        Message message = Message.obtain();
        message.what = PLAY_ANIMATION;
        handler.sendMessageDelayed(message, delay);
    }

    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: small");
        String s = mShareDataMgr.getShareData(SHARE_BLUETOOTH);
        btConnectState(s);
//        if (mDeviceName != null) {
//            mDeviceName.setText(mContext.getResources().getString(R.string.connect_bt_device));
//        }
        if (tv_smallcardname != null) {
            tv_smallcardname.setText(mContext.getResources().getString(R.string.phone));
        }
    }

    @Override
    public void launcherLoadComplete() {
        mDeviceName.setVisibility(VISIBLE);
    }

    @Override
    public void launcherAnimationUpdate(int i) {

    }

    public void btConnectState(String s) {
        if (s == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject(s);
            if (object == null) {
            }
            boolean connect = object.optBoolean("is_hfp_connected", false);
            if (connect) {
                String deviceName = object.optString("phoneName");
                mDeviceName.setText(deviceName);
            } else {
                mDeviceName.setText(getResources().getText(R.string.connect_bt_device));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}