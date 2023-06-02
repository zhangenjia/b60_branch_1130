package com.adayo.app.launcher.weather;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.jar.weather.client.IWeatherListener;
import com.adayo.jar.weather.client.WeatherBean;
import com.adayo.jar.weather.client.WeatherManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.system.systemservice.SystemServiceManager;

import java.util.HashMap;
import java.util.Map;

import static android.animation.ValueAnimator.INFINITE;

public class WeatherCardView extends RelativeLayout implements TextureView.SurfaceTextureListener, IViewBase,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = WeatherCardView.class.getSimpleName();
    private Context mContext;
    private View view;
    private TextView location;
    private TextView temp;
    private TextView tv_aqi;
    private TextView maxtemp;
    private TextView mintemp;
    private ImageView refresh;
    private ObjectAnimator rotate;
    private boolean notifyState = false;
    private RelativeLayout noNet;
    private RelativeLayout inPositioning;
    private RelativeLayout normal;
    private RelativeLayout parent_layout;
    private BlurTransitionView blur;
    private MediaPlayer mPlayer;
    private String uri = "file:///mnt/sdcard/adayo/weather/";
    private String path;
    private TextureView textureView;
    private SurfaceTexture mTexture;
    private Surface surface;
    private TextView tv_weather;
    private TextView tv_weathercardname;
    private ImageView iv_weathercard_frame;
    private TextView airquality;
    private String aqi;
    private Integer weather;
    private static final int NOTIFYWEATHERCHANGE = 1001;
    private static final int NOTIFYCURWEATHER = 1002;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bean = (WeatherBean) msg.obj;
            int weatherStatus = bean.getmWeatherStatus();

            switch (msg.what) {
                case NOTIFYWEATHERCHANGE:
                    Log.d(TAG, " weatherStatus = " + weatherStatus + " wind = " + bean.getmWind() + " wind level = " + bean.getmWindLevel() +
                            " name = " + bean.getmName() + " temp = " + bean.getmTemp() + " temp_day = " + bean.getmTemp_Day() + " temp_night = " + bean.getmTemp_Night() +
                            " aqi = " + bean.getmAqi());
                    if (weatherStatus == -1) {//无网
                        upDateLoadingAnim(0);
                        noNet.setVisibility(VISIBLE);
                        inPositioning.setVisibility(INVISIBLE);
                        normal.setVisibility(INVISIBLE);
                        textureView.setAlpha(0);
                        if (mPlayer != null) {
                            mPlayer.stop();
                        }
                    } else if (weatherStatus == -2) {//定位中
                        inPositioning.setVisibility(VISIBLE);
                        noNet.setVisibility(INVISIBLE);
                        normal.setVisibility(INVISIBLE);
                        upDateLoadingAnim(1);
                    } else {//正常
                        upDateLoadingAnim(0);
                        textureView.setAlpha(1);
                        normal.setVisibility(VISIBLE);
                        inPositioning.setVisibility(INVISIBLE);
                        noNet.setVisibility(INVISIBLE);
                        WeatherInfo weatherInfo = weatherNameMap.get(weatherStatus);
                        if (weatherInfo != null) {
                            @SuppressLint("HandlerLeak") String videoName = weatherInfo.getVideoId();
                            path = uri + videoName + ".mp4";
                            playVideo();
                            weather = weatherInfo.getWeatherId();
                            if (weather != null) {
                                tv_weather.setText((mContext.getResources().getString(weather)));
                            }
                            iv_weather.setBackgroundResource(weatherInfo.getResId());

                        }
                        if (bean.getmWind() != null) {
                            windDirection = bean.getmWind();
                            if (windDirectionMap.get(windDirection) != null) {
                                windDirectionText.setText(mContext.getResources().getString(windDirectionMap.get(windDirection)));
                            }

                        } else {
                        }

                        if (mContext.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            windScale.setText(bean.getmWindLevel() + getResources().getString(R.string.windclass));
                        } else if (mContext.getResources().getConfiguration().locale.getCountry().equals("")) {
                            windScale.setText(getResources().getString(R.string.windclass) + bean.getmWindLevel());
                        }


                        location.setText(bean.getmName());//定位
                        temp.setText(bean.getmTemp());//温度
                        aqi = bean.getmAqi();
                        if (aqi != null) {
                            if (aqiMap.get(aqi) != null) {
                                tv_aqi.setText(mContext.getResources().getString(aqiMap.get(aqi)));
                            }
                        }
                        maxtemp.setText(bean.getmTemp_Day());//最大气温
                        mintemp.setText(bean.getmTemp_Night());//最小气温

                        if (!bean.getmAqi().equals("null") && !bean.getmAqi().equals(null)) {
                            tv_aqi.setText(mContext.getResources().getString(aqiMap.get(bean.getmAqi())));
                            switch (bean.getmAqi()) {
                                case "优":
                                    iv_aqi_point.setBackgroundResource(R.drawable.icon_excellent);
                                    break;
                                case "良":
                                    iv_aqi_point.setBackgroundResource(R.drawable.icon_good);
                                    break;
                                case "轻度污染":
                                case "中度污染":
                                case "重度污染":
                                case "严重污染":
                                    iv_aqi_point.setBackgroundResource(R.drawable.icon_bad);
                                    break;
                            }

                        }
                    }


                    break;
                case NOTIFYCURWEATHER:
                    Log.d(TAG + "_APP", "notifyWeatherChange: bean = " + bean.getmWeatherStatus() + " wind = " + bean.getmWind() +
                            " name = " + bean.getmName() +
                            " temp = " + bean.getmTemp() + " MaxTemp = " +
                            bean.getmTemp_Day() + " MinxTemp = " + bean.getmTemp_Night() + " aqi = " + bean.getmAqi());
                    location.setText(bean.getmName() + "");
                    temp.setText(bean.getmTemp());
                    if (!bean.getmAqi().equals("null") && !bean.getmAqi().equals(null)) {
                        tv_aqi.setText(mContext.getResources().getString(aqiMap.get(bean.getmAqi())));
                        switch (bean.getmAqi()) {
                            case "优":
                                iv_aqi_point.setBackgroundResource(R.drawable.icon_excellent);
                                break;
                            case "良":
                                iv_aqi_point.setBackgroundResource(R.drawable.icon_good);
                                break;
                            case "轻度污染":
                            case "中度污染":
                            case "重度污染":
                            case "严重污染":
                                iv_aqi_point.setBackgroundResource(R.drawable.icon_bad);
                                break;
                        }

                    }
                    if (bean.getmTemp_Day() != null) {
//                        float temp_Day = Float.parseFloat(bean.getmTemp_Day());
//                        BigDecimal bd = new BigDecimal(temp_Day);
//                        temp_Day = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
//                        maxtemp.setText(temp_Day + "");
                        maxtemp.setText(bean.getmTemp_Day());//最大气温
//                        mintemp.setText(bean.getmTemp_Night());//最小气温
                    }
                    if (bean.getmTemp_Night() != null) {
//                        float temp_Night = Float.parseFloat(bean.getmTemp_Night());
//                        BigDecimal bd = new BigDecimal(temp_Night);
//                        temp_Night = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
//                        mintemp.setText(temp_Night + "");
//                        maxtemp.setText(bean.getmTemp_Day());//最大气温
                        mintemp.setText(bean.getmTemp_Night());//最小气温
                    }

                    Log.d(TAG, "notifyCurWeather: ");
                    break;

            }

        }
    };
    /**
     * 天气值对应MP4文件映射
     * map中存的是项目用到的19种
     * 墨迹天气会上报44种
     */
    public static final Map<Integer, WeatherInfo> weatherNameMap = new HashMap<Integer, WeatherInfo>() {
        {
            put(1, new WeatherInfo(R.string.sunny, "sunnyday", R.drawable.icon_weather_sunny));//晴 //
            put(2, new WeatherInfo(R.string.sunny, "sunnyday", R.drawable.icon_weather_sunny));//大部分晴朗 //
            put(3, new WeatherInfo(R.string.cloudy, "cloudy", R.drawable.icon_weather_cloudy));//多云 //
            put(4, new WeatherInfo(R.string.cloudy, "cloudy", R.drawable.icon_weather_cloudy));//少云 //
            put(5, new WeatherInfo(R.string.overcast, "overcast", R.drawable.icon_weather_overcast));//阴//
            put(6, new WeatherInfo(R.string.shower, "shower", R.drawable.icon_weather_showers));//阵雨 //
            put(7, new WeatherInfo(R.string.shower, "shower", R.drawable.icon_weather_showers));//局部阵雨 //
            put(8, new WeatherInfo(R.string.shower, "shower", R.drawable.icon_weather_showers));//小阵雨 //
            put(9, new WeatherInfo(R.string.shower, "shower", R.drawable.icon_weather_showers));//强阵雨 //
            put(10, new WeatherInfo(R.string.koyuki, "lightsnow", R.drawable.icon_weather_lightsnow));//阵雪//
            put(11, new WeatherInfo(R.string.koyuki, "lightsnow", R.drawable.icon_weather_lightsnow));//小阵雪//
            put(12, new WeatherInfo(R.string.fog, "fog", R.drawable.icon_weather_fog));//雾 //
            put(13, new WeatherInfo(R.string.fog, "fog", R.drawable.icon_weather_fog));//冻雾 //
            put(14, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//沙尘暴 //
            put(15, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//浮尘 //
            put(16, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//尘卷风 //
            put(17, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//扬沙 //
            put(18, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//强沙尘暴 //
            put(19, new WeatherInfo(R.string.sanddust, "sanddust", R.drawable.icon_weather_sanddust));//霾 //
            put(20, new WeatherInfo(R.string.thundershower, "thundershower", R.drawable.icon_weather_thundershowers));//雷阵雨 //
            put(21, new WeatherInfo(R.string.thundershower, "thundershower", R.drawable.icon_weather_thundershowers));//雷电 //
            put(22, new WeatherInfo(R.string.thundershower, "thundershower", R.drawable.icon_weather_thundershowers));//雷暴 //
            put(23, new WeatherInfo(R.string.thundershower, "thundershower", R.drawable.icon_weather_thundershowers));//雷阵雨伴有冰雹 //

            put(24, new WeatherInfo(R.string.sleet, "sleet", R.drawable.icon_weather_rainsnow));//冰雹 //
            put(25, new WeatherInfo(R.string.sleet, "sleet", R.drawable.icon_weather_rainsnow));//冰针 //
            put(26, new WeatherInfo(R.string.sleet, "sleet", R.drawable.icon_weather_rainsnow));//冰粒 //
            put(27, new WeatherInfo(R.string.sleet, "sleet", R.drawable.icon_weather_rainsnow));//雨夹雪 //

            put(28, new WeatherInfo(R.string.lightrain, "lightrain", R.drawable.icon_weather_lightrain));//小雨//
            put(29, new WeatherInfo(R.string.moderaterain, "moderaterain", R.drawable.icon_weather_midrain));//中雨//
            put(30, new WeatherInfo(R.string.heavyrain, "heavyrain", R.drawable.icon_weather_heavyrain));//大雨//
            put(31, new WeatherInfo(R.string.rainstorm, "rainstorm", R.drawable.icon_weather_rainstorm));//暴雨//
            put(32, new WeatherInfo(R.string.rainstorm, "rainstorm", R.drawable.icon_weather_rainstorm));//大暴雨//
            put(33, new WeatherInfo(R.string.rainstorm, "rainstorm", R.drawable.icon_weather_rainstorm));//特大暴雨//
            put(34, new WeatherInfo(R.string.koyuki, "lightsnow", R.drawable.icon_weather_lightsnow));//小雪//
            put(35, new WeatherInfo(R.string.zhongxue, "moderatesnow", R.drawable.icon_weather_midsnow));//中雪//
            put(36, new WeatherInfo(R.string.heavysnow, "heavysnow", R.drawable.icon_weather_heavysnow));//大雪//
            put(37, new WeatherInfo(R.string.blizzard, "blizzard", R.drawable.icon_weather_blizzard));//暴雪//
            put(38, new WeatherInfo(R.string.freezingrain, "freezingrain", R.drawable.icon_weather_freezingrain));//冻雨//
            put(39, new WeatherInfo(R.string.koyuki, "lightsnow", R.drawable.icon_weather_lightsnow));//雪//
            put(40, new WeatherInfo(R.string.rain, "lightrain", R.drawable.icon_weather_sunny));//雨
            put(41, new WeatherInfo(R.string.moderaterain, "moderaterain", R.drawable.icon_weather_midrain));//小到中雨//
            put(42, new WeatherInfo(R.string.heavyrain, "heavyrain", R.drawable.icon_weather_heavyrain));//中到大雨//
            put(43, new WeatherInfo(R.string.rainstorm, "rainstorm", R.drawable.icon_weather_rainstorm));//大到暴雨//
            put(44, new WeatherInfo(R.string.zhongxue, "moderatesnow", R.drawable.icon_weather_midsnow));//小到中雪//
        }
    };

    public static final Map<String, Integer> aqiMap = new HashMap<String, Integer>() {
        {
            put("优", R.string.excellent);
            put("良", R.string.good);
            put("轻度污染", R.string.lightpollution);
            put("中度污染", R.string.moderatelypollution);
            put("重度污染", R.string.heavypollution);
            put("严重污染", R.string.seriouspollution);
        }
    };

    public static final Map<String, Integer> windDirectionMap = new HashMap<String, Integer>() {
        {
            put("东风", R.string.eastwind);
            put("南风", R.string.southwind);
            put("西风", R.string.westwind);
            put("北风", R.string.northwind);
            put("东南风", R.string.southeastwind);
            put("西南风", R.string.southwestwind);
            put("西北风", R.string.northwestwind);
            put("东北风", R.string.northeastwind);

        }
    };


    private TextView tv_checknetwork;
    private TextView tv_notnetworked;
    private TextView positioning;
    private ImageView iv_aqi_point;
    private TextView windDirectionText;
    private TextView windScale;
    private String windDirection;

    private ImageView loadingImg;
    private AnimationDrawable drawable;
    private ImageView iv_weather;
    private WeatherBean bean;


    public WeatherCardView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        initView();
        initData();
        initPlayer();

    }

    private void initData() {

        if (!SystemServiceManager.getInstance().conectsystemService()) {
            return;
        }
//        final String sn = SystemServiceManager.getInstance().getSystemConfigInfo((byte) 0x06);
//        WeatherManager.getInstance().init(mContext, sn, "HM6C17A", "2");
        WeatherManager.getInstance().registerListener(new IWeatherListener() {
            @Override
            public void notifyWeatherChange(WeatherBean bean) {//
                Log.d(TAG, "notifyWeatherChange: ");
                Message message = Message.obtain();
                message.what = NOTIFYWEATHERCHANGE;
                message.obj = bean;
                handler.sendMessage(message);

            }

            @Override
            public void notifyCurWeather(WeatherBean bean) {//点击刷新按钮后上报
                Log.d(TAG, "notifyCurWeather: ");
                notifyState = true;
                Message message = Message.obtain();
                message.what = NOTIFYCURWEATHER;
                message.obj = bean;
                handler.sendMessage(message);
            }
        });
    }


    @Override
    public void launcherLoadComplete() {
        Log.d(TAG, "launcherLoadComplete: weather");
        blur.post(new Runnable() {
            @Override
            public void run() {
                blur.setBitmap(5, 0.5f);
                blur.show(300);
            }
        });

    }

    @Override
    public void launcherAnimationUpdate(int i) {
        if (i == 1) {
            blur.show(300);
        } else {
            if (blur.getIsShow()) {
                blur.hide(0);
            }
        }
    }


    private void initView() {

        view = LayoutInflater.from(mContext).inflate(R.layout.layout_weather, null);
        parent_layout = (RelativeLayout) view.findViewById(R.id.parent_layout);
        noNet = (RelativeLayout) view.findViewById(R.id.noNet);//无网络
        inPositioning = (RelativeLayout) view.findViewById(R.id.inPositioningLayout);//定位中
        normal = (RelativeLayout) view.findViewById(R.id.normal);//正常
        location = (TextView) view.findViewById(R.id.locationText);
        temp = (TextView) view.findViewById(R.id.tempText);
        tv_aqi = (TextView) view.findViewById(R.id.aqi);
        maxtemp = (TextView) view.findViewById(R.id.tempMaxText);
        mintemp = (TextView) view.findViewById(R.id.mintempText);
        refresh = (ImageView) view.findViewById(R.id.refresh);
        blur = (BlurTransitionView) view.findViewById(R.id.blur);
        textureView = (TextureView) view.findViewById(R.id.textureView);
        tv_weather = (TextView) view.findViewById(R.id.weatherText);
        tv_weathercardname = (TextView) view.findViewById(R.id.tv_weathercardname);
        iv_weathercard_frame = (ImageView) view.findViewById(R.id.iv_weathercard_frame);
        airquality = (TextView) view.findViewById(R.id.aqiText);//空气质量 文言
        tv_checknetwork = (TextView) view.findViewById(R.id.checknetwork);
        tv_notnetworked = (TextView) view.findViewById(R.id.notnetworked);
        positioning = (TextView) view.findViewById(R.id.positioningText);
        iv_aqi_point = (ImageView) view.findViewById(R.id.aqiPointImg);
        windDirectionText = (TextView) view.findViewById(R.id.windDirectionText);
        windScale = (TextView) view.findViewById(R.id.windLevelText);

        loadingImg = (ImageView) view.findViewById(R.id.loadingImg);
        iv_weather = (ImageView) view.findViewById(R.id.weatherImg);


        textureView.setSurfaceTextureListener(this);

        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(view);
        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(this.parent_layout, true);
        AAOP_HSkin.with(iv_weathercard_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                .applySkin(false);
        rotate = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360f);
        rotate.setDuration(888);//时间
        rotate.setRepeatCount(INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAnimation();
                WeatherManager.getInstance().requestRefreshData();
            }
        });


    }


    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        Log.d(TAG, "initPlayer: ");
    }

    /**
     * 初始化控件
     */

    /**
     * 更新天气应用状态，无网络，定位中，normal状态
     */

    /**
     * normal状态更新天气信息
     */

    /**
     * 更新天气视频
     */
    private void playVideo() {//todo 不在定位中  已连接网络  且已经上屏
        mTexture = textureView.getSurfaceTexture();
        if (mTexture != null) {
            surface = new Surface(mTexture);
            mPlayer.setSurface(surface);
            if (mPlayer != null) {
                try {
                    //使用手机本地视频
                    if (path != null) {
                        mPlayer.reset();
                        mPlayer.setDataSource(getContext(), Uri.parse(path));
                        mPlayer.prepare();
                        mPlayer.setLooping(true);
                        mPlayer.start();
                        Log.d(TAG, "playVideo: start");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "playVideo: ");
            }
        } else {
            Log.d(TAG, "mTexture ==null: ");
        }

    }

    private void refreshAnimation() {
        rotate.start();//开始
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (notifyState) {
                    rotate.cancel();
                }
                notifyState = false;
            }
        });
    }

    @Override
    public View initCardView(String id, String type, String type1) {
        WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
            @Override
            public void setWallPaper(Bitmap bitmap) {
                blur.setBitmap(5, 0.5f);
                blur.show(0);
            }

            @Override
            public void resumeDefault() {
                blur.setBitmap(5, 0.5f);
                blur.show(0);
            }

            @Override
            public void deleteWallPaper() {
                blur.setBitmap(5, 0.5f);
                blur.show(0);
            }


        });

        return view;
    }

    @Override
    public void unInitCardView(String id, String type) {
        Log.d(TAG, "unInitCardView: weather");
        upDateLoadingAnim(0);

        //todo 下屏
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void playAnimation(String id, int delay) {

    }

    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: ");
        if (airquality != null) {
            airquality.setText(mContext.getResources().getString(R.string.airquality));
        }

        if (tv_weathercardname != null) {
            tv_weathercardname.setText(mContext.getResources().getString(R.string.weather));
        }

        if (aqi != null) {
            tv_aqi.setText(mContext.getResources().getString(aqiMap.get(aqi)));
        }
        if (tv_weather != null) {
            if (weather != null) {
                tv_weather.setText((mContext.getResources().getString(weather)));
            }
        }
        if (tv_checknetwork != null) {
            tv_checknetwork.setText((mContext.getResources().getString(R.string.checknetwork)));

        }
        if (tv_notnetworked != null) {
            tv_notnetworked.setText((mContext.getResources().getString(R.string.notnetworked)));
        }
        if (positioning != null) {
            positioning.setText((mContext.getResources().getString(R.string.positioning)));
        }

        if (windDirectionText != null) {
            if (windDirectionMap.get(windDirection) != null) {

                windDirectionText.setText(mContext.getResources().getString(windDirectionMap.get(windDirection)));
            }
        }
        Log.d(TAG, "onConfigurationChanged: "+windScale +"  "+bean+"  "+bean.getmWindLevel()+"  "+mContext.getResources().getConfiguration().locale.getCountry()+"  aa");
        if (windScale != null ) {
            if ( bean != null){
                if (bean.getmWindLevel()!=null){
                    if (mContext.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                        windScale.setText(bean.getmWindLevel() + getResources().getString(R.string.windclass));
                    } else if (mContext.getResources().getConfiguration().locale.getCountry().equals("")) {
                        windScale.setText(getResources().getString(R.string.windclass) + bean.getmWindLevel());
                    }
                }
            }
        }


    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable: ");
        playVideo();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " + mp + "   " + what + "   " + extra);
        return false;
    }

    private void upDateLoadingAnim(int status) {
        if (status == 1) {
            drawable = (AnimationDrawable) loadingImg.getDrawable();
            drawable.start();
        } else if (status == 0) {
            if (drawable != null) {
                drawable.stop();

            }
        }

    }
}
