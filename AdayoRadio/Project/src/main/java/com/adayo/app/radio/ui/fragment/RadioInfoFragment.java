package com.adayo.app.radio.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adayo.app.radio.R;
import com.adayo.app.radio.ui.adapter.NewRadioCollectionListAdapter;
import com.adayo.app.radio.ui.adapter.NewRadioSearchListAdapter;
import com.adayo.app.radio.ui.bean.RadioBean;
import com.adayo.app.radio.ui.controller.RadioDataMng;
import com.adayo.app.radio.utils.CollectionItemAnimator;
import com.adayo.app.radio.utils.SearchItemAnimator;
import com.adayo.app.radio.utils.MyItemTouchHandler;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.tuner.radio.RadioCollectionFreq;
import com.adayo.proxy.tuner.radio.RadioManager;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.adayo.app.radio.constant.Constants.BAND_AM;
import static com.adayo.app.radio.constant.Constants.BAND_AM_TEXT;
import static com.adayo.app.radio.constant.Constants.BAND_FM;
import static com.adayo.app.radio.constant.Constants.BAND_FM_TEXT;
import static com.adayo.app.radio.constant.Constants.ISUNMUTE;
import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_SEARCHSTATUS_1;
import static com.adayo.app.radio.constant.Constants.SEARCHTYPE_MINIMUM;
import static com.adayo.app.radio.utils.Utils.ATTR_JSON_IMG_VIEW;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_RADIO_AM;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_RADIO_FM;

/**
 * @author ADAYO-06
 */
public class RadioInfoFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = RadioInfoFragment.class.getName();
    private volatile static RadioInfoFragment mInstance = null;
    private View mView;
    private SeekBar seekBarFM;
    private SeekBar seekBarAM;
    private Button mBtnRadioFM;
    private Button mBtnRadioAM;
    private ImageView mPrevFreq;
    private ImageView mNextFreq;
    private ImageView mCollectionBtn;
    private ImageView mPlayBtn;
    private ImageView mEqBtn;
    private ImageView searchBtn;
    private ImageView turnUp;
    private ImageView turnDown;
    private TextView mFreqText;
    private String currentFreq;
    private RecyclerView mSearchLv;
    private RecyclerView rvlinearlist;
    public NewRadioSearchListAdapter mRadioSearchListAdapter;
    private NewRadioCollectionListAdapter mRadioCollectionListAdapter;
    public List<RadioBean> mRadioSearchInfoList;
    private List<RadioBean> mRadioSearchFlag;
    public List<RadioBean> mRadioCollectionList;
    private List<RadioBean> mRadioCollection;
    private List<RadioBean> differentList;
    private RelativeLayout noCollectionRl;
    public LottieAnimationView boduanFM;
    public LottieAnimationView boduanAM;
    private ImageView deleteIv;
    private LinearLayout deleteLl;
    private Button clearBtn, cancelBtn;
    private TextView unitTv;
    private int progressMax = 0;
    private int progressMas = 0;
    private int fmProgressCurrentFreq;
    private int amProgressCurrentFreq;
    private String fmProgress;
    private String amProgress;
    public int mAMFreq;
    public double mFMFreq;
    public String currenBand = "FM";
    private Animation mRefBtnAnimation;
    private boolean mIsCollect = false;

    public void setmIsSearch(boolean mIsSearch) {
        Log.d(RadioAPPLog.TAG,TAG+  " setmIsSearch: mIsSearch = "+mIsSearch);
        this.mIsSearch = mIsSearch;
    }

    private boolean mIsSearch = false;

    AnimationDrawable refreshDrawable;

    @SuppressLint("ValidFragment")
    private RadioInfoFragment(){

    }

    public static RadioInfoFragment getmInstance(){
        if (mInstance == null){
            synchronized (RadioInfoFragment.class){
                if (mInstance == null){
                    mInstance = new RadioInfoFragment();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(RadioAPPLog.TAG,TAG+  " onCreateView: start");
        mView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.activity_radio, container, false);
        bindData();
        initView();
        initListener();
        updateView();
        Log.d(RadioAPPLog.TAG,TAG+  " onCreateView: end");
        return mView;
    }

    private void bindData() {
        mRadioSearchInfoList = new ArrayList<>();
        mRadioSearchFlag = new ArrayList<>();
        mRadioCollectionList = new ArrayList<>();
        mRadioCollection = new ArrayList<>();
        differentList = new ArrayList<>();
    }

    private void initView(){
        Log.d(RadioAPPLog.TAG,TAG+  " initView: start");
        onSystemUIVisibility(true);
        findViewById();
        AAOP_HSkin.with(boduanFM).addViewAttrs(new DynamicAttr(ATTR_JSON_IMG_VIEW)).applySkin(false);
        AAOP_HSkin.with(boduanAM).addViewAttrs(new DynamicAttr(ATTR_JSON_IMG_VIEW)).applySkin(false);

        boduanFM.setMinAndMaxFrame(0, 205);
        boduanAM.setMinAndMaxFrame(0, 119);
        AAOP_HSkin
                .with(clearBtn)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.button_top_small)
                .applySkin(false);
        mRadioSearchListAdapter = new NewRadioSearchListAdapter(getActivity().getApplicationContext(), mRadioSearchInfoList);
        mRadioCollectionListAdapter = new NewRadioCollectionListAdapter(getActivity().getApplicationContext(), mRadioCollectionList);

        SearchItemAnimator itemAnimator = new SearchItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        itemAnimator.setAddDuration(300);
        itemAnimator.setMoveDuration(300);
        itemAnimator.setRemoveDuration(200);
        mSearchLv.setItemAnimator(itemAnimator);

        //解决java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
        //继承封装LinearLayoutManager类，重写onLayoutChildren()方法，try-catch捕获该异常。
        LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
        mSearchLv.setLayoutManager(manager);
        mSearchLv.setAdapter(mRadioSearchListAdapter);
        mRefBtnAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_cw);

        mSearchLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    mRadioSearchListAdapter.showBlur();
                }else {
                    mRadioSearchListAdapter.hideBlur();
                }
            }
        });

        //线性布局拖拽
        CollectionItemAnimator collItemAnimator = new CollectionItemAnimator();
        collItemAnimator.setSupportsChangeAnimations(false);
        collItemAnimator.setAddDuration(300);
        collItemAnimator.setMoveDuration(300);
        collItemAnimator.setRemoveDuration(200);
        rvlinearlist.setItemAnimator(collItemAnimator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvlinearlist.setLayoutManager(linearLayoutManager);
        rvlinearlist.setAdapter(mRadioCollectionListAdapter);

        new ItemTouchHelper(new MyItemTouchHandler(mRadioCollectionListAdapter)).attachToRecyclerView(rvlinearlist);

        rvlinearlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    mRadioCollectionListAdapter.showBlur();
                }else {
                    mRadioCollectionListAdapter.hideBlur();
                }
            }
        });
        currenBand = (RadioDataMng.getmInstance().mBand == BAND_FM ? BAND_FM_TEXT:BAND_AM_TEXT);

        Log.d(RadioAPPLog.TAG,TAG+  " initView: end");
    }

    private void findViewById(){
        seekBarFM = mView.findViewById(R.id.seekbar_fm);
        seekBarAM = mView.findViewById(R.id.seekbar_am);
        mBtnRadioFM = mView.findViewById(R.id.tv_radio_fm);
        mBtnRadioAM = mView.findViewById(R.id.tv_radio_am);
        mCollectionBtn = mView.findViewById(R.id.play_collection_btn);
        mFreqText = mView.findViewById(R.id.play_text_hz_number);
        mPrevFreq = mView.findViewById(R.id.pre_btn_image);
        mNextFreq = mView.findViewById(R.id.next_btn_image);
        mPlayBtn = mView.findViewById(R.id.play_btn_image);
        mEqBtn = mView.findViewById(R.id.eq_btn_image);
        searchBtn = mView.findViewById(R.id.keyboard_btn_image);
        turnUp = mView.findViewById(R.id.turn_up);
        turnDown = mView.findViewById(R.id.turn_down);
        mSearchLv = mView.findViewById(R.id.radio_listview);
        rvlinearlist = mView.findViewById(R.id.radio_listview_collection);

        noCollectionRl = mView.findViewById(R.id.rl_no_collection);
        deleteIv = mView.findViewById(R.id.iv_delete);
        deleteLl = mView.findViewById(R.id.ll_delete);

        clearBtn = mView.findViewById(R.id.btn_clear);
        cancelBtn = mView.findViewById(R.id.btn_cancel);
        unitTv = mView.findViewById(R.id.tv_radio_unit);

        boduanFM = mView.findViewById(R.id.lav_fm);
        boduanAM = mView.findViewById(R.id.lav_am);
    }


    public void updateView(){
        Log.d(RadioAPPLog.TAG,TAG+  " updateView: isBackStage = "+RadioDataMng.getmInstance().isBackStage);
        //获取当前Band
        switch (RadioDataMng.getmInstance().mBand){
            case BAND_AM:
                selectedAMUI();
                break;
            case BAND_FM:
                selectedFMUI();
                break;
            default:
                break;
        }
        Log.d(RadioAPPLog.TAG,TAG+  " updateView: isPlay = "+RadioDataMng.getmInstance().isPlay);
        if (RadioDataMng.getmInstance().isPlay == ISUNMUTE){
            AAOP_HSkin
                    .with(mPlayBtn)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_pause)
                    .applySkin(false);
        }else{
            AAOP_HSkin
                    .with(mPlayBtn)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_play)
                    .applySkin(false);
        }
        clickDuringSearch(!mIsSearch);
        if (!mIsSearch){
            searchBtn.setImageResource(R.drawable.icon_refresh_n);
            if (refreshDrawable != null) {
                refreshDrawable.stop();
            }
            updateSelectListStatus(mFreqText.getText().toString());
            Log.d(RadioAPPLog.TAG, TAG+" updateView: currentFreq = "+currentFreq+ " mFreqText ="+mFreqText);
            if (!(mFreqText.getText().toString().equals(currentFreq))){
                updateCollectListStatus(mFreqText.getText().toString());
                currentFreq = mFreqText.getText().toString();
            }
        }else {
            if (RadioDataMng.getmInstance().isSearch == RADIO_CALLBACK_SEARCHSTATUS_1){
                if(refreshDrawable == null){
                    searchBtn.setImageResource(R.drawable.refresh_anim);
                    refreshDrawable = (AnimationDrawable) searchBtn.getDrawable();
                    refreshDrawable.start();
                }else {
                    if (!refreshDrawable.isRunning()){
                        searchBtn.setImageResource(R.drawable.refresh_anim);
                        refreshDrawable = (AnimationDrawable) searchBtn.getDrawable();
                        refreshDrawable.start();
                    }
                }
            }
        }
    }

    public void updateListView(){
        changeSearchList();
        changeCollectionList();
    }

    public void updateSearchList(){
        changeSearchList();
    }

    public void onSystemUIVisibility(boolean visibility) {
        Log.d(RadioAPPLog.TAG,TAG+  " onSystemUIVisibility: ");
        View decorView = getActivity().getWindow().getDecorView();
        /**
         * visibility：true 非全屏, false 全屏
         * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 导航栏不占位
         * FLAG_TRANSLUCENT_STATUS 状态栏透明
         * SYSTEM_UI_FLAG_FULLSCREEN 隐藏导航栏
         * SYSTEM_UI_FLAG_HIDE_NAVIGATION 隐藏状态栏
         */
        if (visibility) {
            Log.i(RadioAPPLog.TAG,TAG+  " Set SystemUI Visible");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            Log.i(RadioAPPLog.TAG,TAG+  " Set SystemUI GONE");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    /**
     * 选择FM
     */
    private void selectedFMUI() {
        Log.d(RadioAPPLog.TAG,TAG+  " selectedFMUI: start");
        AAOP_HSkin
                .with(mBtnRadioFM)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR,R.color.color_black)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioFM)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.img_select_left)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioAM)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.color.transparent)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioAM)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR,R.color.color_freq_white)
                .applySkin(false);

        boduanFM.setVisibility(VISIBLE);
        boduanAM.setVisibility(GONE);
        seekBarAM.setVisibility(GONE);
        seekBarFM.setVisibility(VISIBLE);

        unitTv.setText(R.string.tv_list_mhz);
        if (!mIsSearch){
            searchBtn.clearAnimation();
        }
        updateFMInfo();
        Log.d(RadioAPPLog.TAG,TAG+  " selectedFMUI: end");
    }

    /**
     * 选择AM
     */
    private void selectedAMUI() {
        Log.d(RadioAPPLog.TAG,TAG+  " selectedAMUI: start");
        AAOP_HSkin
                .with(mBtnRadioAM)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR,R.color.color_black)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioAM)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.img_select_right)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioFM)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR,R.color.color_freq_white)
                .applySkin(false);
        AAOP_HSkin
                .with(mBtnRadioFM)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.color.transparent)
                .applySkin(false);

        boduanAM.setVisibility(VISIBLE);
        boduanFM.setVisibility(GONE);
        seekBarAM.setVisibility(VISIBLE);
        seekBarFM.setVisibility(GONE);

        unitTv.setText(R.string.tv_list_khz);
        if (!mIsSearch){
            searchBtn.clearAnimation();
        }
        updateAMInfo();
        Log.d(RadioAPPLog.TAG,TAG+  " selectedAMUI: end");
    }

    private void updateAMInfo(){
        Log.d(RadioAPPLog.TAG,TAG+  " updateAMInfo: start");
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
        try {
            RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                    (int)(mAMFreq), BAND_AM);

            if (RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0){
                AAOP_HSkin
                        .with(mCollectionBtn)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_collection_big)
                        .applySkin(false);
                mIsCollect =true;
            }else {
                AAOP_HSkin
                        .with(mCollectionBtn)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_nocollection_big)
                        .applySkin(false);
                mIsCollect =false;
            }

        }catch (RemoteException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " updateAMInfo NullPointerException ", e);
        }
        Log.d(RadioAPPLog.TAG,TAG+  " updateAMInfo: end");
    }

    private void updateFMInfo(){
        Log.d(RadioAPPLog.TAG,TAG+  " updateFMInfo: start");
        mFMFreq = RadioDataMng.getmInstance().lastFMFreq;
        Log.d(RadioAPPLog.TAG,TAG+  " updateFMInfo: mFMFreq ="+mFMFreq);
        boduanFM.setMinAndMaxFrame(0,205);
        progressMax = 205;
        progressMas = 0;
        Log.d(RadioAPPLog.TAG,TAG+  " updateFMInfo: ((int)(mFMFreq*100) - 8750) ="+((int)(mFMFreq*100) - 8750));
        fmProgressCurrentFreq = ((int)(mFMFreq*100) - 8750) / 10 ;
        Log.d(RadioAPPLog.TAG,TAG+  " updateFMInfo: fmProgressCurrentFreq ="+fmProgressCurrentFreq);
        seekBarFM.setMax(progressMax);
        seekBarFM.setMin(progressMas);
        seekBarFM.setProgress(fmProgressCurrentFreq);
        boduanFM.setFrame(fmProgressCurrentFreq);

        mFreqText.setText(String.valueOf(mFMFreq));
        try {
            RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                    (int)(mFMFreq *100), BAND_FM);

            if (RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0){
                AAOP_HSkin
                        .with(mCollectionBtn)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_collection_big)
                        .applySkin(false);
                mIsCollect =true;
            }else {
                AAOP_HSkin
                        .with(mCollectionBtn)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.radio_btn_nocollection_big)
                        .applySkin(false);
                mIsCollect =false;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " updateFMInfo NullPointerException ", e);
        }

        Log.d(RadioAPPLog.TAG,TAG+  " updateFMInfo: end");
    }

    @Override
    public void onClick(View v) {
        Log.i(RadioAPPLog.TAG,TAG+  " onClick: " + v.getId());
        //判断如果是搜索状态则点击按键时停止搜索
        switch (v.getId()) {
            case R.id.tv_radio_fm:
                if (RadioDataMng.getmInstance().mBand != BAND_FM) {
                    switchBand(BAND_FM);
                    searchBtn.clearAnimation();
                    searchBtn.setImageResource(R.drawable.icon_refresh_n);
                }
                break;
            case R.id.tv_radio_am:
                if (RadioDataMng.getmInstance().mBand != BAND_AM) {
                    switchBand(BAND_AM);
                    searchBtn.clearAnimation();
                    searchBtn.setImageResource(R.drawable.icon_refresh_n);
                }
                break;
            case R.id.pre_btn_image:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " pre_btn_image NullPointerException ", e);
                    }
                }else {
                    try {
                        RadioManager.getRadioManager().seekUp();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " pre_btn_image NullPointerException ", e);
                    }
                }

                break;
            case R.id.eq_btn_image:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " eq_btn_image stopSearch NullPointerException ", e);
                    }
                }else {
                    try {
                        Intent intent = new Intent();
                        intent.setPackage("com.adayo.app.setting");
                        intent.setAction("adayo.setting.intent.action.EQ");
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " eq_btn_image startActivity NullPointerException ", e);
                    }
                }

                break;
            case R.id.next_btn_image:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " next_btn_image NullPointerException ", e);
                    }
                }else {
                    try {
                        RadioManager.getRadioManager().seekDown();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " next_btn_image NullPointerException ", e);
                    }
                }
                break;
            case R.id.play_btn_image:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " play_btn_image NullPointerException ", e);
                    }
                }else {
                    try {
                        if (RadioDataMng.getmInstance().isPlay == ISUNMUTE) {
                            RadioManager.getRadioManager().setMcuMuteState(true);
                        } else {
                            RadioManager.getRadioManager().requestPlay();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
                break;
            case R.id.play_collection_btn:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " play_collection_btn NullPointerException ", e);
                    }
                }else {
                    if (!mIsCollect) {
                        if (BAND_FM_TEXT.equals(currenBand)) {
                            mIsCollect = (RadioDataMng.getmInstance().setInsertCollectionFreq(BAND_FM, (int) (mFMFreq * 100)) == 0 ? true : false);
                        } else {
                            mIsCollect = (RadioDataMng.getmInstance().setInsertCollectionFreq(BAND_AM, mAMFreq) == 0 ? true : false);
                        }
                    } else {
                        if (BAND_FM_TEXT.equals(currenBand)) {
                            mIsCollect = (RadioDataMng.getmInstance().setDeleteCollectionFreq(BAND_FM, (int) (mFMFreq * 100)) == 0 ? true : false);
                        } else {
                            mIsCollect = (RadioDataMng.getmInstance().setDeleteCollectionFreq(BAND_AM, mAMFreq) == 0 ? true : false);
                        }
                        RadioDataMng.getmInstance().isDeleteItem = true;
                        deleteCollectionItem(mFreqText.getText().toString());
                    }
                }
                break;
            case R.id.keyboard_btn_image:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " keyboard_btn_image NullPointerException ", e);
                    }
                }else {
                    mIsSearch = true;
                    clearSearchListAndStartAnima();
                }
                break;
            case R.id.turn_up:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " turn_up NullPointerException ", e);
                    }
                }else {
                    try {
                        RadioManager.getRadioManager().tuneUp();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " turn_up NullPointerException ", e);
                    }
                }
                break;
            case R.id.turn_down:
                if(mIsSearch){
                    try {
                        RadioManager.getRadioManager().stopSearch();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " turn_down NullPointerException ", e);
                    }
                }else {
                    try {
                        RadioManager.getRadioManager().tuneDown();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " turn_down NullPointerException ", e);
                    }
                }
                break;
            case R.id.iv_delete:
                if (mRadioCollectionList.size() > 0) {
                    deleteIv.setVisibility(GONE);
                    deleteLl.setVisibility(VISIBLE);
                }

                break;
            case R.id.btn_clear:
                try {
                    int i = RadioManager.getRadioManager().deleteALLCollectionList();
                    Log.i(RadioAPPLog.TAG,TAG+  " onClick: R.id.btn_clear i = " + i);
                    if (i == 0){
                        mIsCollect = false;
                        clearCollectionList();

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    Log.e(RadioAPPLog.TAG, TAG+ " btn_clear NullPointerException ", e);
                }

//                if (mRadioCollectionList.size() == 0) {
                deleteIv.setVisibility(VISIBLE);
                deleteLl.setVisibility(GONE);
//                }
                break;
            case R.id.btn_cancel:
                if (mRadioCollectionList.size() > 0) {
                    deleteIv.setVisibility(VISIBLE);
                    deleteLl.setVisibility(GONE);
                }
                break;
            default:
                break;
        }
    }

    private String point = ".";
    private void initListener() {
        mBtnRadioFM.setOnClickListener(this);
        mBtnRadioAM.setOnClickListener(this);
        mCollectionBtn.setOnClickListener(this);
        mPrevFreq.setOnClickListener(this);
        mNextFreq.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mEqBtn.setOnClickListener(this);
        turnUp.setOnClickListener(this);
        turnDown.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        deleteIv.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        seekBarFM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条发生改变时会触发
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(RadioAPPLog.TAG,TAG+  " onProgressChanged:band====== " + RadioDataMng.getmInstance().mBand+ " FM");
                Log.i(RadioAPPLog.TAG,TAG+  " onProgressChanged:progress====== " + progress);
                boduanFM.setFrame(progress);
                Log.i(RadioAPPLog.TAG,TAG+  " onProgressChanged: boduanFM.getFrame " + boduanFM.getFrame());
                if (RadioDataMng.getmInstance().mBand == BAND_FM) {
                    seekBar.setMax(205);
                    seekBar.setMin(0);
                    double fmNum = 87.5;
//                    if (progress > 1){
//                        fmNum = (progress) / 10.0 + 87.5;
//                    }else {
                        fmNum = progress / 10.0 + 87.5;
//                    }
                    Log.i(RadioAPPLog.TAG,TAG+  " fmNum=== " + fmNum);
                    String fmStr = String.valueOf(progress == 0 ? 87.5 : fmNum);
                    mFreqText.setText(fmStr);
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
                playSearchChannel(((int)(Double.parseDouble(fmProgress) * 100)));
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
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playSearchChannel(Integer.parseInt(amProgress));
            }
        });


        /*
         * 取消单个收藏
         */
        mRadioCollectionListAdapter.setOnItemRemoveListener(new NewRadioCollectionListAdapter.OnItemRemoveListener() {
            @Override
            public void onRemove(int position) {
                Log.d(RadioAPPLog.TAG, TAG +" onRemove: " + position);
                try {
                    RadioCollectionFreq deletetCollectionFreq = new RadioCollectionFreq();
                    if (mRadioCollectionList.get(position).getBand().equals(BAND_FM_TEXT)){
                        deletetCollectionFreq.setFreq((int) (Double.parseDouble(mRadioCollectionList.get(position).getFreq()) * 100));
                        deletetCollectionFreq.setBand(BAND_FM);
                    }else {
                        deletetCollectionFreq.setFreq(Integer.parseInt(mRadioCollectionList.get(position).getFreq()));
                        deletetCollectionFreq.setBand(BAND_AM);
                    }
                    deletetCollectionFreq.setId(1L);
                    int ret = RadioManager.getRadioManager().deleteCollectionRadioInfo(deletetCollectionFreq);
                    Log.d(RadioAPPLog.TAG, TAG +" onRemove: ret = " + ret);
                    if (ret == 0){
                        RadioDataMng.getmInstance().isDeleteItem = true;
                        remove(position);
                    }
                }catch (Exception e){
                    Log.e(RadioAPPLog.TAG, TAG + " onCollect: e :" +e.toString());
                }

            }
        });

        mRadioCollectionListAdapter.setOnItemClickListener(new NewRadioCollectionListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                /**
                 * 点击单个收藏
                 */
                Log.d(RadioAPPLog.TAG,TAG+ " onClick: " + position);
                try{
                    int ret;
                    if(mIsSearch) {
                        RadioManager.getRadioManager().stopSearch();
                    }
                    if (mRadioCollectionList.get(position).getFreq().contains(point)) {

                        ret = RadioManager.getRadioManager().setBandAndFreq(BAND_FM, (int) (Double.parseDouble(mRadioCollectionList.get(position).getFreq())* 100));
                        Log.d(RadioAPPLog.TAG,TAG+ " onClick: BAND_FM ret" + ret);
                    } else {
                        ret = RadioManager.getRadioManager().setBandAndFreq(BAND_AM, Integer.parseInt(mRadioCollectionList.get(position).getFreq()));
                    }
                    Log.d(RadioAPPLog.TAG,TAG+ " onClick: ret =" + ret);
                    if (ret == 0){
                        updateCollectListStatus(mRadioCollectionList.get(position).getFreq());
                    }
                }catch (RemoteException e){
                    Log.e(RadioAPPLog.TAG, TAG+ " mRadioCollectionListAdapter.setOnItemClickListener RemoteException ", e);
                } catch (NullPointerException e){
                    Log.e(RadioAPPLog.TAG, TAG+ " mRadioCollectionListAdapter.setOnItemClickListener NullPointerException ", e);
                } catch (Exception e){
                    Log.e(RadioAPPLog.TAG, TAG+ " mRadioCollectionListAdapter.setOnItemClickListener Exception ", e);
                }
            }
        });


        /**
         * 电台列表
         * 点击单个item
         */
        mRadioSearchListAdapter.setOnItemClickListener(new NewRadioSearchListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d(TAG, "onClick: " + "start play");
                //容错处理防止数组越界
                if ((position + 1) > mRadioSearchInfoList.size()){
                    return;
                }
                if (RadioDataMng.getmInstance().mBand == BAND_AM){
                    int amFreq = Integer.parseInt(mRadioSearchInfoList.get(position).getFreq());
                    playSearchChannel(amFreq);
                }else {
                    int fmFreq = (int) (Double.parseDouble(mRadioSearchInfoList.get(position).getFreq()) * 100);
                    playSearchChannel(fmFreq);
                }
            }
        });

        mRadioSearchListAdapter.setOnItemCollectClickListener(new NewRadioSearchListAdapter.OnItemCollectClickListener() {
            @Override
            public void onCollect(int position) {
                Log.d(RadioAPPLog.TAG,TAG +" onClick: " + position);

                if (!mRadioSearchInfoList.get(position).getIsCollect()) {
                    try {
                        if (mRadioSearchInfoList.get(position).getBand().equals(BAND_FM_TEXT)){
                            Log.d(RadioAPPLog.TAG,TAG +" onCollect: FM freq:"+Double.parseDouble(mRadioSearchInfoList.get(position).getFreq()));
                            RadioDataMng.getmInstance().setInsertCollectionFreq(BAND_FM,(int) (Double.parseDouble(mRadioSearchInfoList.get(position).getFreq()) * 100));
                        }else {
                            RadioDataMng.getmInstance().setInsertCollectionFreq(BAND_AM,Integer.parseInt(mRadioSearchInfoList.get(position).getFreq()));
                        }
                        mRadioSearchInfoList.get(position).setmIsCollect(true);
                    }catch (Exception e){
                        Log.e(RadioAPPLog.TAG,TAG +" onCollect: e :" +e.toString());
                    }
                }else {
                    int deleteItem;
                    if (mRadioSearchInfoList.get(position).getBand().equals(BAND_FM_TEXT)){
                        deleteItem =  RadioDataMng.getmInstance().setDeleteCollectionFreq(BAND_FM,(int) (Double.parseDouble(mRadioSearchInfoList.get(position).getFreq()) * 100));
                    }else {
                        deleteItem =  RadioDataMng.getmInstance().setDeleteCollectionFreq(BAND_AM,Integer.parseInt(mRadioSearchInfoList.get(position).getFreq()));
                    }
                    mRadioSearchInfoList.get(position).setmIsCollect(false);
                    if (deleteItem == 0){
                        RadioDataMng.getmInstance().isDeleteItem =true;
                        deleteCollectionItem(mRadioSearchInfoList.get(position).getFreq());
                    }
                }

                mRadioSearchListAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 播放当前频段
     */
    private void playSearchChannel(int channel) {
        Log.d(RadioAPPLog.TAG,TAG+  " playSearchChannel channel = "+channel);
        if (RadioDataMng.getmInstance().mBand == BAND_AM){
            try {
                RadioManager.getRadioManager().setBandAndFreq(BAND_AM,channel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                Log.e(RadioAPPLog.TAG, TAG+ " playSearchChannel NullPointerException ", e);
            }
        }else {
            try {
                RadioManager.getRadioManager().setBandAndFreq(BAND_FM,channel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                Log.e(RadioAPPLog.TAG, TAG+ " playSearchChannel NullPointerException ", e);
            }
        }
        //mPlayBtn.setBackgroundResource(R.drawable.radio_btn_play);

    }

    /**
     * 切换频段
     * @param type band值
     */
    private void switchBand(int type) {
        Log.i(RadioAPPLog.TAG,TAG+  " switchBand type:"+type);
        try {
            switch (type){
                case BAND_AM:
                    try {
                        mSearchLv.setVisibility(View.INVISIBLE);
                        if (mIsSearch){
                            RadioManager.getRadioManager().stopSearch();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " switchBand AM NullPointerException ", e);
                    }
                    //先获取Band 频率
                    int currAmFreq = RadioManager.getRadioManager().getLastAMFreq();
                    Log.i(RadioAPPLog.TAG,TAG+  " switchBand currAmFreq:"+currAmFreq);
                    RadioManager.getRadioManager().setBandAndFreq(BAND_AM, currAmFreq);
                    break;
                case BAND_FM:
                    try {
                        mSearchLv.setVisibility(View.INVISIBLE);
                        if (mIsSearch){
                            RadioManager.getRadioManager().stopSearch();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.e(RadioAPPLog.TAG, TAG+ " switchBand FM NullPointerException ", e);
                    }

                    //先获取Band 频率
                    int currFmFreq = RadioManager.getRadioManager().getLastFMFreq();
                    Log.i(RadioAPPLog.TAG,TAG+  " switchBand currFmFreq:"+currFmFreq);
                    RadioManager.getRadioManager().setBandAndFreq(BAND_FM, currFmFreq);
                    break;
                default:
                    break;
            }
            Log.i(RadioAPPLog.TAG,TAG+  " switchBand change");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }//若服务未启动, 则捕获异常
    }//切换波段

    /**
     * 初始化更新预设列表
     */
    public void changeSearchList(){
        Log.i(RadioAPPLog.TAG,TAG+  " changeSearchList start");
        if (RadioDataMng.getmInstance().mBand == BAND_FM && currenBand.equals(BAND_FM_TEXT)){
            changeFmSearchList();
        }else if ((RadioDataMng.getmInstance().mBand == BAND_AM) && currenBand.equals(BAND_AM_TEXT)){
            changeAmSearchList();
        }
        RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeSearchList end");
    }

    private void changeFmSearchList(){
        mSearchLv.setVisibility(VISIBLE);
        if (RadioDataMng.getmInstance().mRadioFMSearchList != null && RadioDataMng.getmInstance().mRadioFMSearchList.size() >0){
            Log.i(RadioAPPLog.TAG,TAG+  " changeFmSearchList mRadioSearchList != null");
            for (int i =0;i < RadioDataMng.getmInstance().mRadioFMSearchList.size();i++){
                try {
                    RadioBean mRadio = new RadioBean();
                    mRadio.setBand(RadioDataMng.getmInstance().mRadioFMSearchList.get(i).getBand() == 0 ? "FM":"AM");
                    mRadio.setFreq(String.valueOf((double)RadioDataMng.getmInstance().mRadioFMSearchList.get(i).getFreq()/100));
                    mRadio.setIndex(i);
                    RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                            RadioDataMng.getmInstance().mRadioFMSearchList.get(i).getFreq(),
                            RadioDataMng.getmInstance().mRadioFMSearchList.get(i).getBand());
                    mRadio.setmIsCollect(RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0);
                    if (!mIsSearch){
                        mRadio.setIsPlay(mRadio.getFreq().equals(mFreqText.getText()));
                    }
                    upPresetList(mRadio);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    Log.e(RadioAPPLog.TAG, TAG+ " FM changeFmSearchList != null changeSearchList: NullPointerException ", e);
                }
            }
        }else {
            if (!mIsSearch){
                RadioBean mRadio = new RadioBean();
                mRadio.setBand("FM");
                mRadio.setFreq("87.5");
                mRadio.setIndex(mRadioSearchInfoList.size() + 1);
                mRadio.setIsPlay(RadioDataMng.getmInstance().lastFMFreq == 87.5 ? true:false);
                RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                        (int)(87.5 * 100), BAND_FM);
                try {
                    mRadio.setmIsCollect(RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    RadioAPPLog.e(RadioAPPLog.TAG, TAG+ " FM changeFmSearchList: NullPointerException e = "+ e);
                }
                upPresetList(mRadio);
            }
        }
    }

    private void changeAmSearchList(){
        mSearchLv.setVisibility(VISIBLE);
        if (RadioDataMng.getmInstance().mRadioAMSearchList != null && RadioDataMng.getmInstance().mRadioAMSearchList.size() >0){
            RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeAmSearchList mRadioSearchList != null");
            for (int i =0;i < RadioDataMng.getmInstance().mRadioAMSearchList.size();i++){
                try {
                    RadioBean mRadio = new RadioBean();
                    mRadio.setBand(RadioDataMng.getmInstance().mRadioAMSearchList.get(i).getBand() == 0 ? "FM":"AM");
                    mRadio.setFreq(String.valueOf(RadioDataMng.getmInstance().mRadioAMSearchList.get(i).getFreq()));
                    mRadio.setIndex(i);
                    RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                            RadioDataMng.getmInstance().mRadioAMSearchList.get(i).getFreq(),
                            RadioDataMng.getmInstance().mRadioAMSearchList.get(i).getBand());
                    mRadio.setmIsCollect(RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0);
                    if (!mIsSearch){
                        mRadio.setIsPlay(mRadio.getFreq().equals(mFreqText.getText()));
                    }
                    mRadio.setIsPlay(mRadio.getFreq().equals(mFreqText.getText()));
                    upPresetList(mRadio);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    RadioAPPLog.e(RadioAPPLog.TAG, TAG+ " AM changeAmSearchList != null changeSearchList: NullPointerException e:"+ e);
                }
            }
        }else {
            if (!mIsSearch){
                RadioBean mRadio = new RadioBean();
                mRadio.setBand("AM");
                mRadio.setFreq("531");
                mRadio.setIndex(mRadioSearchInfoList.size() + 1);
                mRadio.setIsPlay(RadioDataMng.getmInstance().lastAMFreq == 531 ? true:false);
                RadioCollectionFreq queryRadioNameInfo = new RadioCollectionFreq(1L,
                        (int)(531), BAND_AM);
                try {
                    mRadio.setmIsCollect(RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    Log.e(RadioAPPLog.TAG, TAG+ " AM changeAmSearchList == null changeSearchList: NullPointerException ", e);
                }
                mRadio.setIsPlay(mRadio.getFreq().equals(mFreqText.getText()));
                upPresetList(mRadio);
            }
        }
    }

    /**
     * 初始化更新收藏列表
     */
    @SuppressLint("NotifyDataSetChanged")
    public void changeCollectionList(){
        RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeCollectionList start ");
        if(RadioDataMng.getmInstance().mRadioCollectionList != null && RadioDataMng.getmInstance().mRadioCollectionList.size() >0){
            noCollectionRl.setVisibility(GONE);
            deleteIv.setEnabled(true);
            rvlinearlist.setVisibility(VISIBLE);
            RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeCollectionList mRadioCollectionList.size()="+RadioDataMng.getmInstance().mRadioCollectionList.size()+" mRadioCollectionList.size = "+mRadioCollectionList.size());
            if (RadioDataMng.getmInstance().mRadioCollectionList.size() > mRadioCollectionList.size()){
                for (int i =0;i < RadioDataMng.getmInstance().mRadioCollectionList.size();i++) {
                    RadioBean collectionRadioBean = new RadioBean();
                    collectionRadioBean.setBand(RadioDataMng.getmInstance().mRadioCollectionList.get(i).getBand() == 0 ? "FM":"AM");
                    if (RadioDataMng.getmInstance().mRadioCollectionList.get(i).getBand() == 0){
                        collectionRadioBean.setFreq(String.valueOf((double) RadioDataMng.getmInstance().mRadioCollectionList.get(i).getFreq()/100));
                    }else {
                        collectionRadioBean.setFreq(String.valueOf(RadioDataMng.getmInstance().mRadioCollectionList.get(i).getFreq()));
                    }
                    RadioAPPLog.d(RadioAPPLog.TAG,TAG+  " changeCollectionList:  mFreqText== "+mFreqText.getText());
                    RadioAPPLog.d(RadioAPPLog.TAG,TAG+  " changeCollectionList:  collectionRadioBean.getFreq() == "+collectionRadioBean.getFreq());
                    upCollectionList(collectionRadioBean);
                }
            }else if (RadioDataMng.getmInstance().mRadioCollectionList.size() < mRadioCollectionList.size()){
                mRadioCollection.clear();
                for (int i =0;i < RadioDataMng.getmInstance().mRadioCollectionList.size();i++) {
                    RadioBean collectionRadioBean = new RadioBean();
                    collectionRadioBean.setBand(RadioDataMng.getmInstance().mRadioCollectionList.get(i).getBand() == 0 ? "FM":"AM");
                    if (RadioDataMng.getmInstance().mRadioCollectionList.get(i).getBand() == 0){
                        collectionRadioBean.setFreq(String.valueOf((double) RadioDataMng.getmInstance().mRadioCollectionList.get(i).getFreq()/100));
                    }else {
                        collectionRadioBean.setFreq(String.valueOf(RadioDataMng.getmInstance().mRadioCollectionList.get(i).getFreq()));
                    }
                    RadioAPPLog.d(RadioAPPLog.TAG,TAG+  " changeCollectionList: del collectionRadioBean.getFreq() == "+collectionRadioBean.getFreq());
                    defCollectionList(collectionRadioBean);
                }
                listCompare(mRadioCollectionList, mRadioCollection);
                RadioAPPLog.d(RadioAPPLog.TAG,TAG+  " changeCollectionList: del differentList.size() = "+differentList.size());
                for (int i =0;i < differentList.size();i++){
                    deleteCollectionItem(differentList.get(i).getFreq());
                }
            }
            RadioAPPLog.d(RadioAPPLog.TAG,TAG+  " changeCollectionList mRadioCollectionList :"+mRadioCollectionList.size());
        }else {
            RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeCollectionList mRadioCollectionList == null");
            //start 1041677 对应
            mRadioCollectionList.clear();
            //end 1041677
            //start 973643 对应
            mRadioCollectionListAdapter.notifyDataSetChanged();
            //end
            noCollectionRl.setVisibility(VISIBLE);
            deleteLl.setVisibility(GONE);
            deleteIv.setVisibility(VISIBLE);
            deleteIv.setEnabled(false);
            rvlinearlist.setVisibility(GONE);
        }
        RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " changeCollectionList end");
    }

    /**
     * 避免显示重复数据
     */

    private void upPresetList(RadioBean radioBean){
        for (RadioBean hasAddBean : mRadioSearchInfoList){
            if (hasAddBean.getFreq().equals(radioBean.getFreq())) {
                return;
            }
        }
        radioBean.setIndex(mRadioSearchInfoList.size());
        mRadioSearchInfoList.add(radioBean);
        mRadioSearchListAdapter.setPresetLists(mRadioSearchInfoList,currenBand);
        mRadioSearchListAdapter.notifyItemInserted(mRadioSearchInfoList.size()-1);

    }

    /**
     * 避免显示重复数据
     */

    private void upCollectionList(RadioBean radioBean){
        for (RadioBean hasAddBean : mRadioCollectionList){
            if (hasAddBean.getFreq().equals(radioBean.getFreq())) {
                return;
            }
        }
        RadioAPPLog.d(TAG," upCollectionList mRadioCollectionList :"+mRadioCollectionList.size());
        radioBean.setIndex(mRadioCollectionList.size() + 1);
        mRadioCollectionList.add(radioBean);
        mRadioCollectionListAdapter.setPresetLists(mRadioCollectionList,currenBand);
        for (int i = 0; i < mRadioCollectionList.size(); i++){
            if (mRadioCollectionList.get(i).getFreq().equals(mFreqText.getText().toString()) && !mIsSearch){
                mRadioCollectionList.get(i).setIsPlay(true);
            }else {
                mRadioCollectionList.get(i).setIsPlay(false);
            }
        }
        mRadioCollectionListAdapter.notifyItemInserted(mRadioCollectionList.size()-1);
    }

    /**
     * 避免显示重复数据
     */

    private void defCollectionList(RadioBean radioBean){
        for (RadioBean hasAddBean : mRadioCollection){
            if (hasAddBean.getFreq().equals(radioBean.getFreq())) {
                return;
            }
        }
        radioBean.setIndex(mRadioCollection.size() + 1);
        mRadioCollection.add(radioBean);
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" defCollectionList mRadioCollection :"+mRadioCollection);
    }

    private void clickDuringSearch(boolean isEnabled) {
        seekBarFM.setEnabled(isEnabled);
        seekBarAM.setEnabled(isEnabled);

    }//搜索时的点击状体

    /**
     * 更新收藏列表播放状态
     * @param freq
     */
    private void updateCollectListStatus(String freq){
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" updateCollectListStatus:ferq = "+freq);
        if (mRadioCollectionList.size() > 0){
            for (int i = 0; i < mRadioCollectionList.size(); i++){
                if (mRadioCollectionList.get(i).getFreq().equals(freq) && !mIsSearch){
                    mRadioCollectionList.get(i).setIsPlay(true);
                }else {
                    mRadioCollectionList.get(i).setIsPlay(false);
                }
            }
            mRadioCollectionListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除收藏列表条目
     */
    private void deleteCollectionItem(String freq){
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" deleteCollectionItem:ferq = "+freq);
        for (int i = 0; i < mRadioCollectionList.size(); i++){
            if (freq.equals(mRadioCollectionList.get(i).getFreq())){
                remove(i);
            }
        }
    }

    /**
     * 更新预设列表播放状态
     * @param ferq
     */
    private void updateSelectListStatus(String ferq){
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" updateSelectListStatus:ferq = "+ferq);
        if (mRadioSearchInfoList.size() > 0){
            for (int i = 0; i < mRadioSearchInfoList.size(); i++){
                try {
                    if (mRadioSearchInfoList.get(i).getFreq().equals(ferq) && !mIsSearch){
                        mRadioSearchInfoList.get(i).setIsPlay(true);
                    }else {
                        mRadioSearchInfoList.get(i).setIsPlay(false);
                    }
                    RadioCollectionFreq queryRadioNameInfo;
                    if (RadioDataMng.getmInstance().mBand == BAND_FM){
                        queryRadioNameInfo = new RadioCollectionFreq(1L,
                                (int) (Double.parseDouble(mRadioSearchInfoList.get(i).getFreq()) * 100),
                                mRadioSearchInfoList.get(i).getBand().equals(BAND_FM_TEXT)? BAND_FM: BAND_AM);
                    }else {
                        queryRadioNameInfo = new RadioCollectionFreq(1L,
                                Integer.parseInt(mRadioSearchInfoList.get(i).getFreq()),
                                mRadioSearchInfoList.get(i).getBand().equals(BAND_FM_TEXT)? BAND_FM: BAND_AM);
                    }
                    mRadioSearchInfoList.get(i).setmIsCollect(RadioManager.getRadioManager().queryCollectionRadioInfo(queryRadioNameInfo) == 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }catch (NumberFormatException e){
                    RadioAPPLog.e(RadioAPPLog.TAG, TAG +" updateSelectListStatus: NumberFormatException e" +e.toString());
                }
            }
            mRadioSearchListAdapter.notifyDataSetChanged();
        }
    }

    public void clearCollectionList() {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_clear);
        rvlinearlist.setLayoutAnimation(controller);
        rvlinearlist.startLayoutAnimation();
        rvlinearlist.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRadioCollectionList.clear();
                RadioDataMng.getmInstance().mRadioCollectionList.clear();
                mRadioCollectionListAdapter.notifyDataSetChanged();
                RadioAPPLog.d(RadioAPPLog.TAG, TAG +" clearCollectionList: notifyDataSetChanged ");
                noCollectionRl.setVisibility(VISIBLE);
                deleteIv.setEnabled(false);
                rvlinearlist.setVisibility(GONE);
                updateView();
                changeSearchList();
            }
        },700);

    }

    /**
     * Band切换清除收藏列表
     */
    public void clearSearchListInfo() {
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" clearSearchListInfo: start ");
        mRadioSearchInfoList.clear();
        mRadioSearchListAdapter.notifyDataSetChanged();
    }

    /**
     * 搜索开始清除列表动画
     */
    public void clearSearchListAndStartAnima() {
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" clearSearchListAndStartAnima: start ");
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_clear);
        mSearchLv.setLayoutAnimation(controller);
        mSearchLv.startLayoutAnimation();
        mSearchLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mRadioSearchInfoList.clear();
                    mRadioSearchListAdapter.notifyDataSetChanged();
                    RadioManager.getRadioManager().autoSearch(SEARCHTYPE_MINIMUM);
                    if (mIsSearch) {
                        searchBtn.setImageResource(R.drawable.refresh_anim);
                        refreshDrawable = (AnimationDrawable) searchBtn.getDrawable();
                        refreshDrawable.start();
                    }
                } catch (Exception e) {
                    RadioAPPLog.e(TAG, e.toString());
                };
            }
        },700);

    }

    public void remove(int position) {
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" remove: start ");
        mRadioCollectionList.remove(position);
        mRadioCollectionListAdapter.notifyItemRemoved(position);
        mRadioCollectionListAdapter.notifyItemRangeChanged(position, mRadioCollectionList.size());
        RadioDataMng.getmInstance().setOff = position -1;
        RadioAPPLog.d(RadioAPPLog.TAG, TAG +" remove: end  mRadioCollectionList.size() = "+mRadioCollectionList.size()+ " setOff = "+RadioDataMng.getmInstance().setOff);
    }

    /**
     * 比较两个list
     * 取出存在mRadioCollectionList中，但不存在mRadioCollection中的数据，差异数据放入differentList
     */
    private void listCompare(List<RadioBean> mRadioCollectionList, List<RadioBean> mRadioCollection) {
        for (int i =0;i < mRadioCollectionList.size();i++){
            RadioAPPLog.d(RadioAPPLog.TAG, TAG +" listCompare: i = "+i+" mRadioCollection.size() = "+mRadioCollection.size());
            if (i < mRadioCollection.size()){
                if (mRadioCollectionList.get(i).equals(mRadioCollection.get(i))){
                    continue;
                } else {
                    differentList.add(mRadioCollectionList.get(i));
                    RadioAPPLog.d(RadioAPPLog.TAG, TAG +" listCompare: i<mRadioCollection.size() differentList = "+differentList);
                }
            }else {
                differentList.add(mRadioCollectionList.get(i));
                RadioAPPLog.d(RadioAPPLog.TAG, TAG +" listCompare: differentList = "+differentList);
            }
        }
    }
}
