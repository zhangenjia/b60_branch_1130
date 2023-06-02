package com.adayo.app.launcher.ui.fragment;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.PathInterpolator;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.adayo.app.launcher.R;
import com.adayo.app.launcher.databinding.LfragmentLayoutBinding;
import com.adayo.app.launcher.presenter.factory.CardMappingFactory;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.presenter.function.OpenAppFunction;
import com.adayo.app.launcher.presenter.function.SysPropFunction;
import com.adayo.app.launcher.presenter.listener.BigCardDragImpl;
import com.adayo.app.launcher.presenter.listener.ConfigurationChangeImpl;
import com.adayo.app.launcher.presenter.listener.IBigCardDragListener;
import com.adayo.app.launcher.presenter.listener.ISmallCardDragListener;
import com.adayo.app.launcher.presenter.listener.SmallCardDragImpl;
import com.adayo.app.launcher.presenter.manager.CardManager;
import com.adayo.app.launcher.presenter.manager.WindowsManager;
import com.adayo.app.launcher.ui.view.CustomDialog;
import com.adayo.app.launcher.util.LogUtil;
import com.adayo.app.launcher.util.SystemPropertiesUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import warning.LauncherApplication;
import warning.listener.ConfirmClickImpl;
import warning.listener.IConfirmClickListener;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import static com.adayo.app.launcher.presenter.function.ConfigFunction.cardHeight;
import static com.adayo.app.launcher.presenter.function.ConfigFunction.cardWith;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_SKIP;
import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_STATE;


public class DefaultFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener,
        IBigCardDragListener.BigCardCallBack, ISmallCardDragListener.SmallCardCallBack,
        ConfigurationChangeImpl.ConfigurationChangeCallBack, IConfirmClickListener.CallBack {
    private static final String SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY = "launchcountrecord";
    private static String TAG = " DefaultFragment";
    private String mDefaultSmallCardMapping = "";//4个小卡映射
    private CustomDialog mBottomDialog;
    private String mBigCardChangeId = "";//拖拽底部卡片时回调的被拖拽卡片的id
    private String mSmallCardChangeId = "";//拖拽底部卡片时回调的被拖拽卡片的id
    private int mBigCardChangePosition = -1;//拖拽换卡时底部拖动位置
    private int mSmallCardChangePosition = -1;//拖拽换卡时底部拖动位置
    List<LinearLayout> mLayoutList = new ArrayList<>();
    private static final int mTabLayoutLeftPosition = 0;
    private static final int mTabLayoutRightPosition = 1;
    private CardManager cardManager;
    private List<String> list;
    private String bigId;
    private ViewParent mBigCardViewParent;
    private View mBigCardView;
    private View smallcardview;

    private List<View> mSmallCardViewList = new ArrayList<>();
    private Map<Integer, Integer> map = new HashMap<>();
    private int mUpIndex;
    private SysPropFunction mSysPropFunction;
    private View mCurrentDragView;
    private LinearLayout mCurrentFrame;
    private ShareDataManager shareDataManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isLongClickable = true;
    private boolean isClickable = true;
    private LfragmentLayoutBinding binding;
    private final int duraion = 200;
    private final int addDuraion = 155;
    private View mFrameView;
    private Animator animatorShowFrame;
    private View mMaskView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context.getApplicationContext());
        LogUtil.d(TAG, "intent != null: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//获取大小卡view资源，
        // 不要放在onCreateView生成view时获取
        super.onCreate(savedInstanceState);
        mSysPropFunction = SysPropFunction.getInstance();
        cardManager = new CardManager(LauncherApplication.getContext());//最先初始化
        initBigCardView();
        initSmallCardView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = LfragmentLayoutBinding.inflate(inflater, container, false);
        binding.llBigcard.setOnClickListener(this);
        binding.llBigcard.setOnLongClickListener(this);
        mLayoutList.add(binding.llOne);
        mLayoutList.add(binding.llTwo);
        mLayoutList.add(binding.llThree);
        mLayoutList.add(binding.llFour);
        binding.llOne.setOnClickListener(this);
        binding.llTwo.setOnClickListener(this);
        binding.llThree.setOnClickListener(this);
        binding.llFour.setOnClickListener(this);
        binding.llOne.setOnLongClickListener(this);
        binding.llTwo.setOnLongClickListener(this);
        binding.llThree.setOnLongClickListener(this);
        binding.llFour.setOnLongClickListener(this);
        BigCardDragImpl.getInstance().addCallBackToBigCard(this);
        SmallCardDragImpl.getInstance().addCallBackToSmallCard(this);
        initFunctionView();
        boolean once = getLaunchCountProperties().equals("once");
        if (WARNING_STATE == WARNING_SKIP || once) {
            LoadAnimation();
        }

        setLaunchCountProperties();//记录launcher开启一次
        initDialog();
        ConfigurationChangeImpl.getInstance().addConfigurationChangeCallBack(this);
        ConfirmClickImpl.getInstance().addCallBack(this);
        return binding.getRoot();
    }



    @Override
    public boolean onLongClick(View view) {
        LogUtil.d(TAG," onLongClick ");

//        if (!isLongClickable) {
//            return false;
//        }
//        isClickable = false;
        showMask(binding.llBgMask);
        WindowsManager.getInstance().seBottomDialogVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.ll_bigcard:
                mBottomDialog.setOnLongClickTabSelect(mTabLayoutLeftPosition);
                showFrame(binding.frameBg);
                mCurrentFrame = binding.frameBg;
                mBottomDialog.show();
                break;
            case R.id.ll_one:
            case R.id.ll_two:
            case R.id.ll_three:
            case R.id.ll_four:
                mBottomDialog.setOnLongClickTabSelect(mTabLayoutRightPosition);
                showFrame(binding.llFrameAll);
                mCurrentFrame = binding.llFrameAll;
                mBottomDialog.show();
                break;
            default:
                break;
        }
        return true;

    }


    @Override
    public void onClick(View v) {
        LogUtil.d(TAG," onClick ");

        switch (v.getId()) {
            case R.id.ll_bigcard:
                mOpenApp(binding.frameBg, -1);
                break;
            case R.id.ll_one:
                mOpenApp(binding.llFrameOne, 0);
                break;
            case R.id.ll_two:
                mOpenApp(binding.llFrameTwo, 1);
                break;
            case R.id.ll_three:
                mOpenApp(binding.llFrameThree, 2);
                break;
            case R.id.ll_four:
                mOpenApp(binding.llFrameFour, 3);
                break;
            default:
                break;

        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isLongClickable = true;
//            }
//        }, 1000);
    }

    /**
     * 打开应用
     *
     * @param index
     */
    private void mOpenApp(View frameView, int index) {
        showFrame(frameView);
        hideFrame();
        String id;
        if (index == -1) { //点击大卡判断进入哪一个应用
            id = mSysPropFunction.getTopBigCardIdFromProperties();//从系统属性查当前大卡是哪一个然后进入
        } else {//点击小卡判断进入哪一个应用
            String bigCardIdFromProperties = mSysPropFunction.getSmallCardIdFromProperties();
            id = String.valueOf(bigCardIdFromProperties.charAt(index));//从系统属性查当前小卡是哪一个然后进入
        }
        OpenAppFunction.getInstance().mOpenApp(id, LauncherApplication.getContext());
    }

    /**
     * 初始化大卡相关
     */
    private void initBigCardView() {
        bigId = mSysPropFunction.getTopBigCardIdFromProperties();
        if (bigId == null || bigId.equals("")) { //首次
            Log.d(TAG, "initBigCardView: default");
            mBigCardView = cardManager.initCardView(LauncherApplication.getContext(), ID_OFFROADINFO, TYPE_BIGCARD, DEFAULT_INITCARD); // 默认越野信息
            mSysPropFunction.setTopBigCardIdToProperties(ID_OFFROADINFO);
            bigId = ID_OFFROADINFO;
        } else {
            Log.d(TAG, "initBigCardView: sys prop" + bigId);
            mBigCardView = cardManager.initCardView(LauncherApplication.getContext(), bigId, TYPE_BIGCARD, DEFAULT_INITCARD);
        }
    }

    /**
     * 初始化小卡相关
     */
    private void initSmallCardView() {
        String smallCardId;
        smallCardId = mSysPropFunction.getSmallCardIdFromProperties();
        if (smallCardId == null || smallCardId.equals("")) {
            mDefaultSmallCardMapping = CardMappingFactory.getInstance().getDefaultSmallCardResMapping(ConfigFunction.getInstance(LauncherApplication.getContext()).getOffLineConfiguration());
            Log.d(TAG, "initSmallCardView: default  " + mDefaultSmallCardMapping);
            mSysPropFunction.setSmallCardIdToProperties(mDefaultSmallCardMapping);
        } else {
            Log.d(TAG, "initSmallCardView: sys prop " + smallCardId);
            mDefaultSmallCardMapping = smallCardId;
        }
        List<String> strings = Arrays.asList(mDefaultSmallCardMapping.split(""));//
        list = new ArrayList(strings);
        list.remove(0);
        Log.d(TAG, "initSmallCardView:  Mapping " + mDefaultSmallCardMapping);

        for (int i = 0; i < list.size(); i++) {
            smallcardview = cardManager.initCardView(LauncherApplication.getContext(), (String) list.get(i), TYPE_SMALLCARD, DEFAULT_INITCARD);
            if (smallcardview != null) {
                mSmallCardViewList.add(smallcardview);
            }
        }
    }

    /**
     * add 大卡小卡View
     */
    private void initFunctionView() {
        mBigCardViewParent = mBigCardView.getParent();
        if (mBigCardViewParent == null) {
            Log.d(TAG, "addBigCardView normal");
            binding.llBigcard.addView(mBigCardView, 564, 860);
        } else {
            Log.d(TAG, "addBigCardView: exception " + "上一次大卡片没清空");
        }
        for (int i = 0; i < mSmallCardViewList.size(); i++) {//todo addview判断
            View view = mSmallCardViewList.get(i);
            if (view != null) {
                ViewParent parent = view.getParent();
                if (parent == null) {
                    Log.d(TAG, "addSmallCardView normal");
                    mLayoutList.get(i).addView(mSmallCardViewList.get(i), cardWith, cardHeight);
                    Log.d(TAG, "mLayoutList.get(i).getWidth(): " + mLayoutList.get(i).getMeasuredWidth() + " " + mLayoutList.get(i).getHeight());
                } else {
                    Log.d(TAG, "addSmallCardView: exception" + "上一次小卡片没清空");
                }
            }
        }
    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
//        if (true)return;
        mBottomDialog = new CustomDialog(getActivity());
//        mBottomDialog = CustomDialog.getInstance(getActivity());
        mBottomDialog.setCardManager(cardManager);
        mBottomDialog.setOnLeftListTouchUpListener(new CustomDialog.onLeftListTouchUpListener() {
            @Override
            public Map<Integer, Integer> pointXY(float x, float y) {
                if (x > binding.llBigcard.getLeft() && x < binding.llBigcard.getRight() && y > binding.llBigcard.getTop() + 104 && y < binding.llBigcard.getTop() + 440) {
                    if (map != null) {
                        map.clear();
                    }
                    mUpIndex = 0;
                    map.put(binding.llBigcard.getLeft(), binding.llBigcard.getTop());
                    return map;
                }
                return null;
            }
        });
        mBottomDialog.setOnDismissMissListener(new CustomDialog.OnDismissMissListener() {
            @Override
            public void dismiss() {
                hideFrameAndMask();
//                Toast.makeText(getContext(),"aaaaaaaaaaaa",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fastDismiss() {
                fastHideFrameAndMask();
            }
        });
        mBottomDialog.setOnRightListTouchUpListener(new CustomDialog.onRightListTouchUpListener() {
            @Override
            public Map<Integer, Integer> pointXY(float x, float y) {

                if (x > binding.llOne.getLeft() && x < binding.llOne.getRight() && y > binding.llOne.getTop() + 104 && y < binding.llOne.getBottom() + 104) {

                    if (map != null) {
                        map.clear();
                    }
                    mUpIndex = 0;
                    map.put(binding.llOne.getLeft(), binding.llOne.getTop());
                    return map;
                }
                if (x > binding.llTwo.getLeft() && x < binding.llTwo.getRight() && y > binding.llTwo.getTop() + 104 && y < binding.llTwo.getBottom() + 104) {
                    if (map != null) {
                        map.clear();
                    }
                    mUpIndex = 1;
                    map.put(binding.llTwo.getLeft(), binding.llTwo.getTop());
                    return map;
                }
                if (x > binding.llThree.getLeft() && x < binding.llThree.getRight() && y > binding.llThree.getTop() + 104 && y < binding.llThree.getBottom() + 104) {
                    if (map != null) {
                        map.clear();
                    }
                    mUpIndex = 2;
                    map.put(binding.llThree.getLeft(), binding.llThree.getTop());
                    return map;
                }
                if (x > binding.llFour.getLeft() && x < binding.llFour.getRight() && y > binding.llFour.getTop() + 104 && y < binding.llFour.getBottom() + 104) {
                    if (map != null) {
                        map.clear();
                    }
                    mUpIndex = 3;
                    map.put(binding.llFour.getLeft(), binding.llFour.getTop());
                    return map;
                }
                mUpIndex = -1;
                return null;
            }
        });
    }

    /**
     * 拖拽底部大卡后抬手的事件处理
     *
     * @param
     */
    public void dealBigDragCardEventInitCardView() {
        mCurrentDragView = cardManager.initCardView(LauncherApplication.getContext(), mBigCardChangeId, TYPE_BIGCARD, DRAG_TO_INITCARD);
        Log.d(TAG, "dealBigDragCardEvent: " + mBigCardChangeId);
    }

    public View dealBigDragCardEventAddCardView() {
        if (mCurrentDragView == null) {
            Log.d(TAG, "dealBigDragCardEvent: 大卡View为null");
            return mCurrentDragView;
        }
        if (mCurrentDragView.getParent() == null) {
            binding.llBigcard.addView(mCurrentDragView, 564, 860);
            binding.llBigcard.removeViewAt(0);
        }
        String bigCardInFoFromSp = mSysPropFunction.getTopBigCardIdFromProperties();//从sp取
//        mSysPropFunction.setBigCardIdToProperties(mBigCardChangeId);//存
        if (bigCardInFoFromSp != null && bigCardInFoFromSp != "") {
            Log.d(TAG, "dealBigDragCardEventAddCardView: ");
            BigCardDragImpl.getInstance().ChangeInfoToBottomBigCard(bigCardInFoFromSp, mBigCardChangePosition);
        } else {
            Log.d(TAG, "bigCardInFoFromSp != null: ");
        }
        Log.d(TAG, "dealBigDragCardEventAddCardView: " + mBigCardChangeId);

        mSysPropFunction.setTopBigCardIdToProperties(mBigCardChangeId);
        return mCurrentDragView;
    }

    /**
     * 拖拽底部小卡后抬手的事件处理
     *
     * @param
     */
    public void dealSmallDragCardEventInitCardView() {
        mCurrentDragView = cardManager.initCardView(LauncherApplication.getContext(), mSmallCardChangeId, TYPE_SMALLCARD, DRAG_TO_INITCARD);
        Log.d(TAG, "dealSmallDragCardEvent: " + mSmallCardChangeId);
    }

    public View dealSmallDragCardEventAddCardView(int index) {
        Log.d(TAG, "dealSmallDragCardEvent: " + mSmallCardChangeId);
        if (mCurrentDragView == null) {
            return mCurrentDragView;
        }
        mDefaultSmallCardMapping = mSysPropFunction.getSmallCardIdFromProperties();
        LinearLayout layout = mLayoutList.get(index);
        layout.removeAllViews();
        if (mCurrentDragView.getParent() == null) {
            layout.addView(mCurrentDragView, 300, 322);
        }
        String smallCardId = mSysPropFunction.getSmallCardIdFromProperties();//从sp
        if (smallCardId != null && !smallCardId.equals("")) {
            SmallCardDragImpl.getInstance().ChangeIdFromCardToBottomSmallCard(String.valueOf(mDefaultSmallCardMapping.charAt(index)), mSmallCardChangePosition);//把拖拽的卡片位置，和落点的id传给底部
        }
        Log.d(TAG, "dealSmallDragCardEvent: " + mDefaultSmallCardMapping);
        String s = String.valueOf(mDefaultSmallCardMapping.charAt(index));
        mSysPropFunction.setSmallCardIdToProperties(mDefaultSmallCardMapping.replace(s, mSmallCardChangeId));//更新顶部默认四个小卡映射
        Log.d("qwerasdf", "dealSmallDragCardEventAddCardView: ");
        return mCurrentDragView;
    }

    /**
     * 拖动底部大卡片抬手后收到的回调，获取数据。
     *
     * @param id       被拖拽卡片id
     * @param position 是被拖拽卡片在底部列表中位置
     */
    @Override
    public void bigCardIdChange(String id, int position) {
        mBigCardChangeId = id;
        mBigCardChangePosition = position;
        Log.d("DragAndChange", "bigCardIdChange " + id);
    }

    @Override
    public void bigCardIdChangeToInitView(String id, int position) {
        Log.d(TAG, "Init bigCardIdChangeTo  : " + position);
        mBigCardChangeId = id;
        mBigCardChangePosition = position;
        dealBigDragCardEventInitCardView();
    }

    @Override
    public View bigCardIdChangeToAddView(String id, int position) {
        Log.d(TAG, "Add bigCardIdChangeTo: " + position);
        View view = dealBigDragCardEventAddCardView();
        return view;
    }

    /**
     * 拖动底部小卡片抬手后收到的回调，获取数据。
     *
     * @param id       被拖拽卡片id
     * @param position 是被拖拽卡片在底部列表中位置
     */
    @Override
    public void smallCardIdChangeToInitView(String id, int position) {
        mSmallCardChangeId = id;
        mSmallCardChangePosition = position;
        Log.d("tagtag", "smallCardIdChangeToInitView " + id + "  " + position);
        dealSmallDragCardEventInitCardView();
    }

    @Override
    public View smallCardIdChangeToAddView(String id, int position) {
        Log.d("tagtag", "smallCardIdChangeToInitView " + id + "  " + position);
        if (mUpIndex == -1) {
            return null;
        }
        View view = dealSmallDragCardEventAddCardView(mUpIndex);
        return view;
    }

    @Override
    public void hideFrameAndMask() {
        Log.d(TAG, "hideFrameAndMask: ");
        hideFrame();
        hideMask();
    }
    public void fastHideFrameAndMask() {
        Log.d(TAG, "hideFrameAndMask: ");
        if (mFrameView!=null){
            mFrameView.setAlpha(0);
        }
        if (mMaskView!=null){
            mMaskView.setAlpha(0);
        }

    }
    @Override
    public void showFrameAndMask() {
        Log.d(TAG, "showFrameAndMask: ");
        if (mBottomDialog.isShowing()) {
            showFrame(mCurrentFrame);
            showMask(binding.llBgMask);
        }
    }

    @Override
    public void configurationChange() {
        initDialog();//todo   未初始化时长按防崩溃
    }

    public void ViewPagerChange(int i) {
        if (cardManager != null) {
            cardManager.launcherAnimationUpdate(i);
        } else {
            Log.d(TAG, "cardManager == null ");
        }

    }



    @Override
    public void callBack() {
        Log.d(TAG, "callBack:playLoadLauncherAnimation ");
        LoadAnimation();
    }

    private void setLaunchCountProperties() {
        SystemPropertiesUtil.getInstance().setProperty(SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY, "once");
    }

    private String getLaunchCountProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY, "");
        return string;
    }




    public void LoadAnimation() {
        Log.d(TAG, "LauncherStartAnimation: ");
        String offLineConfiguration = ConfigFunction.getInstance(LauncherApplication.getContext()).getOffLineConfiguration();
        if (binding.llBigcard != null && binding.llOne != null && binding.llTwo != null && binding.llThree != null && binding.llFour != null && LauncherApplication.getContext() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (offLineConfiguration.equals("HM6C17A")) {
                            Thread.sleep(500);
                        } else if (offLineConfiguration.equals("HM6C18A")) {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            Log.d(TAG, "LauncherStartAnimation: null");
        }
        if (offLineConfiguration.equals("HM6C17A")) {
            AnimatorSet animatorSet = new AnimatorSet();

            Animator animator_translation = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.bigcardanimation);
            Animator animator_alpha = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.bigcard_alpha_animation);
            animator_translation.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation.setTarget(binding.llBigcard);
            animator_alpha.setTarget(binding.llBigcard);

            Animator animator_translation_1 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_1 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_1.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_1.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_1.setTarget(binding.llOne);
            animator_alpha_1.setTarget(binding.llOne);
            animator_translation_1.setStartDelay(duraion);
            animator_alpha_1.setStartDelay(duraion);

            Animator animator_translation_2 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_2 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_2.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_2.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_2.setTarget(binding.llTwo);
            animator_alpha_2.setTarget(binding.llTwo);
            animator_translation_2.setStartDelay(duraion + addDuraion);
            animator_alpha_2.setStartDelay(duraion + addDuraion);

            Animator animator_translation_3 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_3 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_3.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_3.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_3.setTarget(binding.llThree);
            animator_alpha_3.setTarget(binding.llThree);
            animator_translation_3.setStartDelay(duraion + addDuraion * 2);
            animator_alpha_3.setStartDelay(duraion + addDuraion * 2);

            Animator animator_translation_4 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_4 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_4.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_4.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_4.setTarget(binding.llFour);
            animator_alpha_4.setTarget(binding.llFour);
            animator_translation_4.setStartDelay(duraion + addDuraion * 3);
            animator_alpha_4.setStartDelay(duraion + addDuraion * 3);

            animatorSet.play(animator_translation).with(animator_alpha)
                    .with(animator_translation_1).with(animator_alpha_1).with(animator_translation_2).with(animator_alpha_2)
                    .with(animator_translation_3).with(animator_alpha_3).with(animator_translation_4).with(animator_alpha_4);
            animatorSet.start();

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardManager.launcherBootComplete();
                }
            });
            for (int i = 0; i < list.size(); i++) {
                cardManager.palyLottileAnimation(list.get(i), duraion + i * addDuraion);
            }

        } else if (offLineConfiguration.equals("HM6C18A")) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator animator_translation = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.bigcardanimation);
            Animator animator_alpha = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.bigcard_alpha_animation);
            animator_translation.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation.setTarget(binding.llBigcard);
            animator_alpha.setTarget(binding.llBigcard);

            Animator animator_translation_1 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_1 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_1.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_1.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_1.setTarget(binding.llOne);
            animator_alpha_1.setTarget(binding.llOne);
            animator_translation_1.setStartDelay(duraion);
            animator_alpha_1.setStartDelay(duraion);

            Animator animator_translation_2 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_2 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_2.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_2.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_2.setTarget(binding.llTwo);
            animator_alpha_2.setTarget(binding.llTwo);
            animator_translation_2.setStartDelay(duraion + addDuraion);
            animator_alpha_2.setStartDelay(duraion + addDuraion);

            Animator animator_translation_3 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_3 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_3.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_3.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_3.setTarget(binding.llThree);
            animator_alpha_3.setTarget(binding.llThree);
            animator_translation_3.setStartDelay(duraion + addDuraion * 2);
            animator_alpha_3.setStartDelay(duraion + addDuraion * 2);

            Animator animator_translation_4 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcardanimation);
            Animator animator_alpha_4 = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.smallcard_alpha_animation);
            animator_translation_4.setInterpolator(new PathInterpolator(0.00f, 0.66f, 0.42f, 0.99f));
            animator_alpha_4.setInterpolator(new PathInterpolator(0.87f, 0.25f, 0.88f, 0.39f));
            animator_translation_4.setTarget(binding.llFour);
            animator_alpha_4.setTarget(binding.llFour);
            animator_translation_4.setStartDelay(duraion + addDuraion * 3);
            animator_alpha_4.setStartDelay(duraion + addDuraion * 3);

            animatorSet.play(animator_translation).with(animator_alpha)
                    .with(animator_translation_1).with(animator_alpha_1).with(animator_translation_2).with(animator_alpha_2)
                    .with(animator_translation_3).with(animator_alpha_3).with(animator_translation_4).with(animator_alpha_4);
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardManager.launcherBootComplete();
                }
            });
            for (int i = 0; i < list.size(); i++) {
                cardManager.palyLottileAnimation(list.get(i), duraion + i * addDuraion);
            }
        }
    }
    public void showFrame(View view) { //显示卡片编辑绿框
        Log.d(TAG, "showFrame: ");
        mFrameView = view;
        animatorShowFrame = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.cardframedispalyanimation);
        animatorShowFrame.setTarget(mFrameView);
        animatorShowFrame.start();

    }

    public void hideFrame() { //隐藏卡片编辑绿框
        Log.d(TAG, "hideFrame: ");
        if (animatorShowFrame != null) {
            animatorShowFrame.cancel();
        }

        if (mFrameView != null) {
            Animator animator = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.cardframehideanimation);
            animator.setTarget(mFrameView);
            animator.start();
        }
    }

    public void showMask(View view) {//显示暗色遮罩
        Log.d(TAG, "showMask: ");
        this.mMaskView = view;
        Animator animator = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.showlaunchermaskanimation);
        animator.setTarget(view);
        animator.start();
    }

    public void hideMask() {//隐藏暗色遮罩
        Log.d(TAG, "hideMask: ");
        if (mMaskView != null) {
            Animator animator = AnimatorInflater.loadAnimator(LauncherApplication.getContext(), R.animator.hidelaunchermaskanimation);
            animator.setTarget(mMaskView);
            animator.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d(TAG, "");
        super.onConfigurationChanged(newConfig);
        mBottomDialog = new CustomDialog(getActivity());
    }

}
