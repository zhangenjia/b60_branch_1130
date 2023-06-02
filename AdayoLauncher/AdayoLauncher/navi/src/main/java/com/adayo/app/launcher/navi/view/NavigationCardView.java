package com.adayo.app.launcher.navi.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.navi.R;
import com.adayo.app.launcher.navi.bean.NaviBean;
import com.adayo.app.launcher.navi.control.NavigationControl;
import com.adayo.app.launcher.navi.listener.INavigationInfoListener;
import com.adayo.app.launcher.navi.util.ConstantsUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;

public class NavigationCardView extends RelativeLayout implements INavigationInfoListener, View.OnClickListener, IViewBase {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PLAY_ANIMATION) {
                if (ConstantUtil.CURRENT_THEME !=-1) {
                if (CURRENT_THEME==2){
                    Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                    card_name_animator.setTarget(tv_smallcardname);
                    card_name_animator.start();
                }
                if (msg.what == PLAY_ANIMATION) {
                    lottie_view.setAnimation("navi" + ConstantUtil.CURRENT_THEME + ".json");
                    lottie_view.playAnimation();
                    lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            AAOP_HSkin.with(lottie_view)
                                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.navi45_lottie)
                                    .applySkin(false);
                        }
                    });
                }
                }
            }
        }
    };
    private static final String TAG = NavigationCardView.class.getSimpleName();
    private Context mContext;
    private View bigcard_view;
    private ImageView iv_turnarrow;
    private TextView tv_distancenextroad;
    private TextView tv_dTD;
    private TextView tv_eTA;
    private TextView tv_nextRoadname_3;
    private TextView tv_terminateNavigation;
    private TextView tv_bigcardname;
    private TextView tv_currentlocation;
    private ImageView iv_search;
    private ImageView iv_home;
    private ImageView iv_company;
    private int mNaviGuidStatus = 1;//默认未导航
    private static final int INNAVIGATIONSTATE = 0;
    private static final int UNINNAVIGATIONSTATE = 1;
    private static NavigationCardView mNavigationCardView;
    private TextView tv_distancenextroadunit;
    private NavigationControl naviControl;
    private RelativeLayout rl_inprogress;
    private RelativeLayout rl_uninprogress;
    private RelativeLayout parent_layout;
    private View smallcard_view;
    private boolean isRunning = false;
    private TextView tv_one;
    private ImageView iv_location;
    //    private TextView tv_location;
    private String province;
    private String city;
    private String district;
    private String street;
    private String code;
    private TextView tv_location;
    private static final int PLAY_ANIMATION = 1;
    private TextView tv_smallcardname;
    private RelativeLayout smallcar_parent_layout;
    private BlurTransitionView navi_blur;
    private LottieAnimationView lottie_view;
    private ImageView iv_navi_big_frame;
    private ImageView iv_navi_one;
    private TextView tv_enter;
    private TextView tv_naviname;
    private CityInfo currentCityInfo;

    public NavigationCardView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public NavigationCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initView() {
        bigcard_view = LayoutInflater.from(mContext).inflate(R.layout.layout_navi_bigcard, null);//导航中View
        smallcard_view = LayoutInflater.from(mContext).inflate(R.layout.layout_navi_smallcard, null);
        iv_location = (ImageView) smallcard_view.findViewById(R.id.iv_navi_one);
        tv_smallcardname = (TextView) smallcard_view.findViewById(R.id.tv_smallcardname);
        parent_layout = (RelativeLayout) bigcard_view.findViewById(R.id.parent_layout);
        iv_turnarrow = (ImageView) bigcard_view.findViewById(R.id.iv_turnarrow);//转向箭头//todo
        tv_distancenextroad = (TextView) bigcard_view.findViewById(R.id.tv_distancenextroad);//转向距离
        tv_distancenextroadunit = (TextView) bigcard_view.findViewById(R.id.tv_distancenextroadunit);   //转向距离单位
        tv_nextRoadname_3 = (TextView) bigcard_view.findViewById(R.id.tv_nextRoadname_3);//下一路口名
        tv_dTD = (TextView) bigcard_view.findViewById(R.id.tv_dTD);//剩余距离
        tv_eTA = (TextView) bigcard_view.findViewById(R.id.tv_eTA);//剩余时间
        tv_terminateNavigation = (TextView) bigcard_view.findViewById(R.id.tv_terminateNavigation);//结束导航
        tv_terminateNavigation.getPaint().setFakeBoldText(true);
        tv_bigcardname = (TextView) bigcard_view.findViewById(R.id.tv_bigcardname);//文字内容为"导航"

        tv_currentlocation = (TextView) bigcard_view.findViewById(R.id.tv_currentlocation);//当前位置
        tv_currentlocation.getPaint().setFakeBoldText(true);
        iv_search = (ImageView) bigcard_view.findViewById(R.id.iv_search); //查询
        iv_home = (ImageView) bigcard_view.findViewById(R.id.iv_home);   //家
        iv_company = (ImageView) bigcard_view.findViewById(R.id.iv_company);//公司
        rl_inprogress = (RelativeLayout) bigcard_view.findViewById(R.id.rl_inprogress);
        rl_uninprogress = (RelativeLayout) bigcard_view.findViewById(R.id.rl_uninprogress);
        navi_blur = (BlurTransitionView) bigcard_view.findViewById(R.id.navi_blur);
        iv_navi_big_frame = (ImageView) bigcard_view.findViewById(R.id.iv_navi_big_frame);
        tv_enter = (TextView) bigcard_view.findViewById(R.id.tv_enter);
        tv_naviname = (TextView) bigcard_view.findViewById(R.id.tv_naviname);

        tv_location = (TextView) smallcard_view.findViewById(R.id.tv_location);
        lottie_view = (LottieAnimationView) smallcard_view.findViewById(R.id.lottie_view);
        smallcar_parent_layout = (RelativeLayout) smallcard_view.findViewById(R.id.smallcar_parent_layout);

        ImageView iv_navi_frame = (ImageView) smallcard_view.findViewById(R.id.iv_navi_frame);
        iv_navi_one = (ImageView) smallcard_view.findViewById(R.id.iv_navi_one);
        ImageView iv_navi_two = (ImageView) smallcard_view.findViewById(R.id.iv_navi_two);

//        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
//        windowViewManager.addWindowView(smallcard_view);
//        ISkinManager skinManager = AAOP_HSkin.getInstance();
//        skinManager.applySkin(smallcar_parent_layout, true);
//        AAOP_HSkin.getWindowViewManager().addWindowView(bigcard_view);
        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(bigcard_view);

        windowViewManager.addWindowView(smallcard_view);

        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(parent_layout, true);
        skinManager.applySkin(smallcar_parent_layout, true);

        AAOP_HSkin.with(iv_navi_big_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                .applySkin(false);
        AAOP_HSkin.with(iv_navi_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                .applySkin(false);
        AAOP_HSkin.with(iv_navi_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_navi_top)
                .applySkin(false);
        lottie_view.setImageAssetsFolder("images");
        iv_turnarrow.setOnClickListener(this);
        tv_distancenextroad.setOnClickListener(this);
        tv_nextRoadname_3.setOnClickListener(this);
        tv_dTD.setOnClickListener(this);
        tv_eTA.setOnClickListener(this);
        tv_terminateNavigation.setOnClickListener(this);
        tv_bigcardname.setOnClickListener(this);
        tv_currentlocation.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_company.setOnClickListener(this);
        naviControl = NavigationControl.getInstance(mContext);
        naviControl.registerNavigationInfoListener(this);
        rl_inprogress.setVisibility(GONE);
        rl_uninprogress.setVisibility(VISIBLE);
        naviControl.autoNaviInit();

    }


    @Override
    public boolean onReportGuideInfo(NaviBean navibean) {
        String city = navibean.getCityInfo();
        Log.d(TAG, "onReportGuideInfo: tv_currentlocation====> "+city);
        if (city!=null){
            Gson gson = new GsonBuilder().create();
            if (gson != null){
                currentCityInfo = gson.fromJson(city, CityInfo.class);
                if (currentCityInfo !=null){
                    if (tv_currentlocation!=null){
                        tv_currentlocation.setText(currentCityInfo.getCity()+ currentCityInfo.getDistrict());
                    }
                    Log.d(TAG, "onReportGuideInfo: tv_currentlocation= "+ currentCityInfo.getCity()+ currentCityInfo.getDistrict());

                }
            }
        }
        if (rl_inprogress.getVisibility() == VISIBLE) {//导航中
            //  iv_turnarrow //todo
            int resourceId = ConstantsUtil.TURN_ICO_ARR.get(navibean.getNavArrow());
            Log.d(TAG, "onReportGuideInfo: resourceId" + resourceId);
            if (resourceId != 0 && resourceId != -1) {
                if (iv_turnarrow!=null){
                    iv_turnarrow.setImageResource(resourceId);//转向图标
                }

            }

            if (tv_distancenextroad!=null){
                tv_distancenextroad.setText(navibean.getDistanceNextRoad());//转向距离
            }

            if (tv_distancenextroadunit!=null){
                tv_distancenextroadunit.setText(navibean.getDistanceNextRoadUnit());
            }
            if (tv_nextRoadname_3!=null){
                tv_nextRoadname_3.setText(navibean.getNextRoadName());
            }

            if (tv_dTD!=null){
                tv_dTD.setText(navibean.getdTD());//剩余里程
            }
            if (tv_eTA!=null){
                tv_eTA.setText(navibean.geteTA());//剩余时间
            }


            String cityInfo = navibean.getCityInfo();//小卡
            String info = parsingJson(cityInfo);
            if (tv_location!=null){
                tv_location.setVisibility(VISIBLE);
            }

            if (info != null) {
                if (tv_location!=null){
                    tv_location.setText(info);
                }

            }
        } else if (rl_uninprogress.getVisibility() == VISIBLE) {//未导航
            String cityInfo = navibean.getCityInfo();
            Log.d(TAG, "onReportGuideInfo: " + "UnIn navigation  " + cityInfo);
        }
        if (currentCityInfo==null){
            return false;
        }
        if (currentCityInfo.getCity().equals("")){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void oReportNaviGuideStatus(int status) {
        Log.d(TAG, "oReportNaviGuideStatus: " + status);
        if (status == INNAVIGATIONSTATE) {//导航中
            rl_inprogress.setVisibility(VISIBLE);
            rl_uninprogress.setVisibility(GONE);
        } else if (status == UNINNAVIGATIONSTATE) {//未导航
            rl_inprogress.setVisibility(GONE);
            rl_uninprogress.setVisibility(VISIBLE);
            tv_location.setText("");
            tv_location.setVisibility(INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {//Android library中生成的R.java中的资源ID不是常数不能使用switch…case
        int id = v.getId();
        if (id == R.id.tv_terminateNavigation) {
            //退出导航状态
            naviControl.requestExitGuide();
        } else if (id == R.id.iv_search) {
            naviControl.intoSearchPage();
        } else if (id == R.id.iv_home) {
            naviControl.NavigateToHomeOrCompany(0);
        } else if (id == R.id.iv_company) {
            naviControl.NavigateToHomeOrCompany(1);
        }
    }


    @Override
    public View initCardView(String id, String type, String type1) {
        if (type.equals(TYPE_BIGCARD)) {
            WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
                @Override
                public void setWallPaper(Bitmap bitmap) {
                    navi_blur.setBitmap(5, 0.5f);
                    navi_blur.show(0);
                }

                @Override
                public void resumeDefault() {
                    navi_blur.setBitmap(5, 0.5f);
                    navi_blur.show(0);
                }

                @Override
                public void deleteWallPaper() {
                    navi_blur.setBitmap(5, 0.5f);
                    navi_blur.show(0);
                }

//                @Override
//                public void bgChange(Bitmap bitmap, boolean isDefult) {
//                    blurView.setBitmap(5, 0.5f);
//                    blurView.show(0);
//                }
            });
            return bigcard_view;//返回大卡
        } else if (type.equals(TYPE_SMALLCARD)) {
            if (type1.equals(DRAG_TO_INITCARD)) {
                AAOP_HSkin.with(lottie_view)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.navi45_lottie)
                        .applySkin(false);
            }
            if (type1.equals(DEFAULT_INITCARD)&&CURRENT_THEME==2){
                tv_smallcardname.setAlpha(0);
            }
            return smallcard_view;
            //返回小卡
        }

        return null;
    }

    @Override
    public void unInitCardView(String id, String type) {
        Log.d(TAG, "unInitCardView: navi");
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void playAnimation(String id, int delay) {
        Log.d(TAG, "playAnimation navi : " + delay);
        Message msg = Message.obtain();
        msg.what = PLAY_ANIMATION;
        handler.sendMessageDelayed(msg, delay);

    }

    @Override
    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: ");
        if (tv_bigcardname != null) {
            tv_bigcardname.setText(mContext.getResources().getString(R.string.navigation));
        }
        if (tv_naviname!=null){
            tv_naviname.setText(mContext.getResources().getString(R.string.navigation));
        }
        if (tv_smallcardname != null) {
            tv_smallcardname.setText(mContext.getResources().getString(R.string.navigation));
        }
        if (tv_terminateNavigation != null) {
            tv_terminateNavigation.setText(mContext.getResources().getString(R.string.endnavi));
        }
        if (tv_enter!=null){
            tv_enter.setText(mContext.getResources().getString(R.string.enter));
        }
        naviControl.onConfigChange();
    }

    @Override
    public void launcherLoadComplete() {
        Log.d(TAG, "launcherLoadComplete: navi");
        navi_blur.post(new Runnable() {
            @Override
            public void run() {
                navi_blur.setBitmap(5, 0.5f);
                navi_blur.show(300);
            }
        });
    }

    @Override
    public void launcherAnimationUpdate(int i) {
        if (i == 1) {
            navi_blur.show(300);
        } else {
            if (navi_blur.getIsShow()) {
                navi_blur.hide(0);
            }
        }
    }

    private String parsingJson(String cityInfo) {
        try {
            if (!TextUtils.isEmpty(cityInfo)) {
                JSONObject jsonObject = new JSONObject(cityInfo);
                //当前位置的省份 注意判空处理
                province = jsonObject.optString("province");
                //当前位置的城市 注意判空处理
                city = jsonObject.optString("city");
                //当前位置的区县 注意判空处理
                district = jsonObject.optString("district");
                //当前位置的街道 腾讯有值，高德无值 注意判空处理
                street = jsonObject.optString("street");
                //地图区域码 腾讯无值，高德有值 有可能为空 注意判空处理
                code = jsonObject.optString("code");
                Log.i(TAG, "onReportCityChanged: city = " + city + " district = " + district);
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException !!!: " + e);
            e.printStackTrace();
        }
        return district;
    }
    static class CityInfo{
        private String city;
        private String code;
        private String district;
        private String province;
        private String street;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }
}