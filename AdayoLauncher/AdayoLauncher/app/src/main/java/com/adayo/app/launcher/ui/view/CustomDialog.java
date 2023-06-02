package com.adayo.app.launcher.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.model.adapter.LeftAdapter;
import com.adayo.app.launcher.model.adapter.ListRightAdapter;

import com.adayo.app.launcher.presenter.factory.CardMappingFactory;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.presenter.function.SysPropFunction;
import com.adayo.app.launcher.presenter.listener.BigCardDragImpl;
import com.adayo.app.launcher.presenter.listener.SmallCardDragImpl;
import com.adayo.app.launcher.presenter.manager.BaseCallback;
import com.adayo.app.launcher.presenter.manager.CardManager;
import com.adayo.app.launcher.presenter.manager.MyLayoutManager;
import com.adayo.app.launcher.presenter.manager.WindowsControllerImpl;
import com.adayo.app.launcher.presenter.manager.WindowsManager;
import com.adayo.app.launcher.util.SpacesItemDecoration;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import static com.adayo.app.launcher.util.MyConstantsUtil.AppTAG;
import static com.adayo.app.launcher.util.MyConstantsUtil.HIGH_CONFIG_VEHICLE;
import static com.adayo.app.launcher.util.MyConstantsUtil.LOW_CONFIG_VEHICLE;
import static com.adayo.app.launcher.util.MyConstantsUtil.SHARE_DATA_SOURCE_MANAGER_ID;

public class CustomDialog extends Dialog implements BigCardDragImpl.BottomBigCardCallBack, SmallCardDragImpl.BottomSmallCardCallBack, View.OnClickListener, IShareDataListener {

    private static final String TAG = AppTAG + CustomDialog.class.getSimpleName();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case mTabLayoutLeft:
                    rv_LeftList.setVisibility(View.VISIBLE);
                    mShowListAnimation(rv_LeftList);
                    break;
                case mTabLayoutRight:
                    rv_RightList.setVisibility(View.VISIBLE);
                    mShowListAnimation(rv_RightList);
                    break;
                case 1054://xiaoka
                    SmallCardDragImpl.getInstance().ChangeIdToTopToInitView(mDragCardId, mDragPosition);//加载卡片
                    sl_right.setDragItemState(true);
                    rightmanager.setScrollEnabled(false);
                    SmallCardDragImpl.getInstance().hideFrameAndMask();
                    if (isSmallcardEnable){
                        createSmallCardDragView(mBitmap);
                    }
                    if (mItemView != null) {
//                        mItemView.setVisibility(View.INVISIBLE);//隐藏该ItemView
                        mRightListAdapter.setVisibility(false);
//                        mRightListAdapter.notifyDataSetChanged();
                    }
                    break;
                case 1055://daka
                    BigCardDragImpl.getInstance().InitView(mDragCardId, mDragPosition);
                    sl_left.setDragItemState(true);
                    leftmanager.setScrollEnabled(false);
                    SmallCardDragImpl.getInstance().hideFrameAndMask();
                    if (isBigcardEnable){
                        createBigCardDragView(mBitmap);
                    }
                    if (mItemView != null) {
//                      mItemView.setVisibility(View.INVISIBLE);//隐藏该ItemView
                        mLeftListAdapter.setVisibility(false);
                    }
                    break;
                case 3:
                    if (mDragImageView != null) {
                        mDragImageView.setVisibility(View.INVISIBLE);
                        mWindowManager.removeView(mDragImageView);
                        mDragImageView = null;
                        SmallCardDragImpl.getInstance().showFrameAndMask();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private final String offLineConfiguration;
    private Context mContext;
    private CustomRecyclerView rv_LeftList;
    private CustomRecyclerView rv_RightList;
    private LeftAdapter mLeftListAdapter;
    private ListRightAdapter mRightListAdapter;
    private String mDefaultBottomBigCardInfoList = "";
    private String mDefaultBottomSmallCardInfoList = "";
    private TextView tv_complete;
    private static final int mTabLayoutLeft = 0;
    private static final int mTabLayoutRight = 1;
    private boolean isScroll = false;
    private boolean isMove;
    private Runnable smallcard_runnable;
    private Runnable bigcard_runnable;
    private View touchView;
    private MyLayoutManager leftmanager;
    private Bitmap mBitmap;
    private final WindowManager mWindowManager;
    private View mItemView;
    private ImageView mDragImageView;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private int statusBarHeight;
    private onLeftListTouchUpListener mOnLeftListTouchUpListener;
    private onRightListTouchUpListener mOnRightListTouchUpListener;
    private MyLayoutManager rightmanager;
    private Map<Integer, Integer> integerIntegerMap;
    private String mDragCardId;//拖拽卡片的id
    private int mDragPosition = -1;//拖拽卡片的位置
    private int mItemViewRawX;//手指按下时当前itemView左端距离屏幕左侧距离，长按底部卡片，卡片中心位移到手指起始点X轴
    private int mItemViewRawY;//手指按下时当前itemView上端距离屏幕上侧距离，长按底部卡片，卡片中心位移到手指起始点Y轴
    private int dragX;//长按后阴影以手指为中心时，阴影左端相对屏幕左侧距离
    private int dragY;//长按后阴影以手指为中心时，阴影上端相对屏幕上侧距离
    private float mTopViewX;
    private float mTopViewY;
    private final SysPropFunction mSysPropFunction;
    private TextView largecardoptional;
    private TextView smallcardoptional;
    private ImageView line_sel_big;
    private ImageView line_sel_small;
    private RelativeLayout parent_layout;
    private int rv_right_ScrollState;
    private int rv_left_ScrollState;
    private CustomOverScrollLayout sl_right;
    private CustomOverScrollLayout sl_left;
    private CardManager cardManager;
    private static CustomDialog mCustomDialog;
    private OnDismissMissListener onDismissMissListener;
    private boolean isVisibility;
    private boolean isBigcardEnable;
    private boolean isSmallcardEnable;
//    private SourceInfo sourceInfo = new SourceInfo();
    private String lastuid;
    private boolean isChangeUid;
    //    public static CustomDialog getInstance(Context context) {
//        if (null == mCustomDialog) {
//            synchronized (ConfigFunction.class) {
//                if (null == mCustomDialog) {
//                    mCustomDialog = new CustomDialog(context);
//                }
//            }
//        }
//        return mCustomDialog;
//    }

    public CustomDialog(Context context) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        offLineConfiguration = ConfigFunction.getInstance(context).getOffLineConfiguration();
        mSysPropFunction = SysPropFunction.getInstance();
        initData();//获得卡片映射
        initView();
        WindowsControllerImpl.getInstance().addCallback(new BaseCallback() {
            @Override
            public void onDataChange(Object data) {
                int visibility = (int) data;
                if (visibility!=View.VISIBLE){
                    CustomDialog.this.dismiss();
                }
            }
        });
        ShareDataManager  shareDataManager = ShareDataManager.getShareDataManager();
        shareDataManager.registerShareDataListener(SHARE_DATA_SOURCE_MANAGER_ID, this);
    }

    public void setCardManager(CardManager cardManager) {
        this.cardManager = cardManager;
    }

    /**
     * 获取卡片映射
     */
    private void initData() {

        String bigcardMappingList;
        bigcardMappingList = mSysPropFunction.getBottomBigCardIdFromSysProperties();
        Log.d(TAG, "bigcardMappingList: "+bigcardMappingList);
        if (bigcardMappingList == null || bigcardMappingList.equals("")) {
            mDefaultBottomBigCardInfoList = CardMappingFactory.getInstance().getBottomDefaultBigCardResource(offLineConfiguration);
            mSysPropFunction.setBottomBigCardIdToSysProperties(mDefaultBottomBigCardInfoList);//把默认list存到sp里
        } else {
            String topBigCardId = mSysPropFunction.getTopBigCardIdFromProperties();
            Log.d(TAG, "topBigCardId: "+topBigCardId);
            if (bigcardMappingList.contains(topBigCardId)) {//去重
                String allBigCardMapping = CardMappingFactory.getInstance().getAllBigCardMapping(offLineConfiguration);

                for (int i = 0; i < allBigCardMapping.length(); i++) {
                    char ch = allBigCardMapping.charAt(i);
                    String s = String.valueOf(ch);
                    if (!bigcardMappingList.contains(s)) {
                        String newBigCardList = bigcardMappingList.replace(topBigCardId, s);
                        Log.d(TAG, "initData: " + " topBigCardId = " + topBigCardId);
                        mDefaultBottomBigCardInfoList = newBigCardList;
                    }
                }
                Log.d(TAG, "initData: " + " topBigCardId = " + topBigCardId);
            } else {
                mDefaultBottomBigCardInfoList = bigcardMappingList;
            }
            Log.d(TAG, "bigcardMappingList: " + bigcardMappingList+" mDefaultBottomBigCardInfoList "+mDefaultBottomBigCardInfoList);

        }

        String smallcardlistfromsp;
        smallcardlistfromsp = mSysPropFunction.getSmallCardInFoFromSysProperties();
        Log.d(TAG, "initData: "+smallcardlistfromsp);
        if (smallcardlistfromsp == null || smallcardlistfromsp.equals("")) {
            mDefaultBottomSmallCardInfoList = CardMappingFactory.getInstance().getBottomDefaultSmallCardResource(offLineConfiguration);
            mSysPropFunction.setSmallCardInFoToSysProperties(mDefaultBottomSmallCardInfoList);//把默认list存到sp里
        } else {
            mDefaultBottomSmallCardInfoList = smallcardlistfromsp;
        }
    }


    public void initView() {

        setContentView(R.layout.layout_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.windowAnimations = R.style.LauncherBottomDialog;
        params.dimAmount = 0f;
        //获取状态栏高度
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");//获取status_bar_height资源的ID
        if (resourceId > 0) {    //根据资源ID获取响应的尺寸值
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        //findViewById
        parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        largecardoptional = (TextView) findViewById(R.id.largecardoptional);
        smallcardoptional = (TextView) findViewById(R.id.smallcardoptional);
        rv_LeftList = (CustomRecyclerView) findViewById(R.id.rv_bottom_bigcard_list);
        rv_RightList = (CustomRecyclerView) findViewById(R.id.rv_bottom_smallcard_list);//rv_bottom_smallcard_list
        line_sel_big = (ImageView) findViewById(R.id.line_sel_big);
        line_sel_small = (ImageView) findViewById(R.id.line_sel_small);
        sl_left = (CustomOverScrollLayout) findViewById(R.id.sl_left);
        sl_right = (CustomOverScrollLayout) findViewById(R.id.sl_right);
        //todo ================================================== skin start>
        AAOP_HSkin
                .getWindowViewManager()
                .addWindowView(parent_layout);
        //todo ================================================== skin end>

        //LinearLayoutManager
        leftmanager = new MyLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv_LeftList.setLayoutManager(leftmanager);
        rightmanager = new MyLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv_RightList.setLayoutManager(rightmanager);
        //Adapter
        mLeftListAdapter = new LeftAdapter(mContext);
        mLeftListAdapter.setData(mDefaultBottomBigCardInfoList);
        rv_LeftList.setAdapter(mLeftListAdapter);
        mRightListAdapter = new ListRightAdapter(mContext);
        mRightListAdapter.setData(mDefaultBottomSmallCardInfoList);
        rv_RightList.setAdapter(mRightListAdapter);
        //设置recyclerview 间距
        if (offLineConfiguration.equals(HIGH_CONFIG_VEHICLE)) {
            rv_LeftList.addItemDecoration(new SpacesItemDecoration(60, 60, mLeftListAdapter.getItemCount()));
            rv_RightList.addItemDecoration(new SpacesItemDecoration(16, 16, mRightListAdapter.getItemCount()));
        } else if (offLineConfiguration.equals(LOW_CONFIG_VEHICLE)) {
            rv_LeftList.addItemDecoration(new SpacesItemDecoration(60, 60, mLeftListAdapter.getItemCount()));
            rv_RightList.addItemDecoration(new SpacesItemDecoration(5, 5, mRightListAdapter.getItemCount()));
        }
//        //todo ================================================== skin start>
//        AAOP_HSkin
//                .with(rv_LeftList)
//                .addViewAttrs(new SkinAttr("clearRecyclerView"));
//        AAOP_HSkin
//                .with(rv_RightList)
//                .addViewAttrs(new SkinAttr("clearRecyclerView"));
//        //todo ================================================== skin end>
        //完成按钮点击
        tv_complete.setOnClickListener(this);
        largecardoptional.setOnClickListener(this);
        smallcardoptional.setOnClickListener(this);
        //adapter中itemView的TouchListener回调

        mLeftListAdapter.setOnTouchItemListener(new LeftAdapter.OnTouchItemListener() {
            @Override
            public boolean onTouchItem(int position, String id, RecyclerView.Adapter adapter, View view, MotionEvent event) {
                Log.d("123123", "onTouchItem: =====");
                mDragCardId = id;
                mDragPosition = position;
                mItemView = view;//RecyclerView的item
                int x = (int) event.getRawX();//X轴手指距离屏幕左侧距离
                int y = (int) event.getRawY() - statusBarHeight;//Y轴手指距离状态栏底部距离
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isBigcardEnable = true;
                        Log.d(TAG, "MotionEvent.ACTION_DOWN: ");
                        if (!isScroll) {
                            int[] location = new int[2];
                            mItemView.getLocationOnScreen(location);
                            mItemViewRawX = location[0];
                            mItemViewRawY = location[1] - statusBarHeight;
                            view.setDrawingCacheEnabled(true);//开启图形缓存，没有这步无法生成bitmap
                            mBitmap = Bitmap.createBitmap(view.getDrawingCache());
                            view.destroyDrawingCache();//***释放绘图缓存，避免出现重复的镜像***
                            dragX = (x - (view.getWidth()) + 254 / 4);//控件左侧距离屏幕左侧距离
                            dragY = (y - (view.getHeight()) + 387 / 4);//控件顶部距离状态栏底部距离
                            mHandler.postDelayed(bigcard_runnable, 300);
                            isScroll = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "MotionEvent.ACTION_MOVE: ");
                        if (!isMove && !leftmanager.isScrollEnabled()) {
                            mWindowLayoutParams.x = ((int) x - (view.getWidth()) + 254 / 4);//移动时
                            mWindowLayoutParams.y = ((int) y - (view.getHeight()) + 387 / 4);//移动时
                            if (mDragImageView == null) {
//                                Toast.makeText(mContext, "崩溃 3 ", Toast.LENGTH_SHORT).show();
                            }
                            if (mDragImageView != null) {
                                mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        isBigcardEnable = false;
                        Log.d(TAG, "MotionEvent.ACTION_UP: ");
                        sl_left.setDragItemState(false);
                        if (isScroll) {
                            isScroll = false;
                            if (touchView != null) {
                                if (!leftmanager.isScrollEnabled()) {
                                    //todo 消去阴影
                                }
                                touchView = null;
                            }
                        }
                        if (mWindowLayoutParams == null) {
//                            Toast.makeText(mContext, "********崩溃*********", Toast.LENGTH_SHORT).show();
                            if (mDragImageView == null) {
                                mHandler.removeCallbacks(bigcard_runnable);
//                                SmallCardDragImpl.getInstance().showFrameAndMask();
//                                mWindowManager.removeView(mDragImageView);
//                                mDragImageView = null;
                            }
                            return false;
                        }
                        mWindowLayoutParams.x = ((int) x - (view.getWidth() / 2));//移动时
                        mWindowLayoutParams.y = ((int) y - (view.getHeight() / 2));//移动时
                        mHandler.removeCallbacks(bigcard_runnable);
                        leftmanager.setScrollEnabled(true);
                        integerIntegerMap = mOnLeftListTouchUpListener.pointXY(x, y);//获取抬手被替换的控件的 左上角x y 坐标
                        mTopViewX = x;//被替换的控件的 x坐标
                        mTopViewY = y;//被替换的控件的 y坐标
                        BigCardDropDragViewAnimation();//开始位移动画，以被拖拽的windowmanager抬手时候的左上为起点，
                        // 以被替换控件的左上角x y为终点移动
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        isBigcardEnable = false;
                        isScroll = false;
                        sl_left.setDragItemState(false);
                        mHandler.removeCallbacks(bigcard_runnable);
                        if (mItemView != null) {
                            mItemView.setVisibility(View.VISIBLE);
                        }
                        if (mDragImageView != null) {
                            SmallCardDragImpl.getInstance().showFrameAndMask();
                            mWindowManager.removeView(mDragImageView);
                            mDragImageView = null;
                        }
                        return true;
                }
                return true;
            }
        });
        sl_left.addListener(new CustomOverScrollLayout.OverScrollLayoutListener() {//切换列表
            @Override
            public void jump() {
                setTabSelectInDialog(mTabLayoutRight);
            }
        });
        rv_LeftList.addOnScrollListener(new RecyclerView.OnScrollListener() {//监听小卡recyclerView滑动
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    if (rv_left_ScrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        int firstVisiblePosition = rv_LeftList.getChildLayoutPosition(rv_LeftList.getChildAt(0));
                        View childAt = rv_LeftList.getChildAt(0);
                        if (Math.abs(childAt.getLeft()) < childAt.getMeasuredWidth() / 2) {
                            rv_LeftList.smoothScrollToPosition(firstVisiblePosition);
                        } else {
                            rv_LeftList.smoothScrollToPosition(firstVisiblePosition + 1);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (Math.abs(dx) < 3) { //快停止了
                        rv_left_ScrollState = RecyclerView.SCROLL_STATE_SETTLING;//rv自己滚动
                        int firstVisiblePosition = rv_LeftList.getChildLayoutPosition(rv_LeftList.getChildAt(0));
                        View childAt = rv_LeftList.getChildAt(0);
                        if (Math.abs(childAt.getLeft()) < childAt.getMeasuredWidth() / 2) {
                            rv_LeftList.smoothScrollToPosition(firstVisiblePosition);
                        } else {
                            rv_LeftList.smoothScrollToPosition(firstVisiblePosition + 1);
                        }

                    }
                } else if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_DRAGGING");
                    rv_left_ScrollState = RecyclerView.SCROLL_STATE_DRAGGING;//上手了
                }
            }
        });
        rv_LeftList.addLeftListScrollListener(new CustomRecyclerView.OnLeftListScrollListener() {
            @Override
            public void notMoving() {
                Log.d("notMoving", "notMoving: ");
                isScroll = false;
                isMove = false;
            }
        });

        mRightListAdapter.setOnTouchItemListener(new ListRightAdapter.OnTouchItemListener() {
            @Override
            public boolean onTouchItem(int position, String id, RecyclerView.Adapter adapter, View itemView, MotionEvent event) {
                Log.d("123123", "onTouchItem: -----" + event.getAction());
                mDragCardId = id;
                mDragPosition = position;
                mItemView = itemView;//RecyclerView的item
                int x = (int) event.getRawX();//X轴手指距离屏幕左侧距离
                int y = (int) event.getRawY() - statusBarHeight;//Y轴手指距离状态栏底部距离
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSmallcardEnable = true;
                        Log.d(TAG, "MotionEvent.ACTION_DOWN: " + isScroll);
                        if (!isScroll) {
                            int[] location = new int[2];
                            mItemView.getLocationOnScreen(location);
                            mItemViewRawX = location[0];
                            mItemViewRawY = location[1] - statusBarHeight;
                            itemView.setDrawingCacheEnabled(true);//开启图形缓存，没有这步无法生成bitmap
                            mBitmap = Bitmap.createBitmap(itemView.getDrawingCache());
                            itemView.destroyDrawingCache();//***释放绘图缓存，避免出现重复的镜像***
                            dragX = (x - (itemView.getWidth() / 2));//控件左侧距离屏幕左侧距离
                            dragY = (y - (itemView.getHeight() / 2));//控件顶部距离状态栏底部距离
                            mHandler.postDelayed(smallcard_runnable, 300);
                            isScroll = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "MotionEvent.ACTION_MOVE: ");
                        if (!isMove && !rightmanager.isScrollEnabled()) {
                            mWindowLayoutParams.x = ((int) x - (itemView.getWidth() / 2));//移动时
                            mWindowLayoutParams.y = ((int) y - (itemView.getHeight() / 2));//移动时
                            if (mDragImageView == null) {
//                                Toast.makeText(mContext, "崩溃 3 ", Toast.LENGTH_SHORT).show();
                            }
                            if (mDragImageView != null) {
                                mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        isSmallcardEnable = false;
                        Log.d(TAG, "MotionEvent.ACTION_UP: ");
                        sl_right.setDragItemState(false);
                        if (isScroll) {
                            isScroll = false;
                            if (touchView != null) {
                                if (!rightmanager.isScrollEnabled()) {
                                    //todo 消去阴影
                                }
                                touchView = null;
                            }
                        }
                        mHandler.removeCallbacks(smallcard_runnable);
                        rightmanager.setScrollEnabled(true);
                        integerIntegerMap = mOnRightListTouchUpListener.pointXY(x, y);//获取抬手被替换的控件的 左上角x y 坐标
                        mTopViewX = x;//被替换的控件的 x坐标
                        mTopViewY = y;//被替换的控件的 y坐标
                        SmallCardDropDragViewAnimation();//开始位移动画，以被拖拽的windowmanager抬手时候的左上为起点，
                        // 以被替换控件的左上角x y为终点移动
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        isSmallcardEnable = false;
                        Log.d(TAG, "MotionEvent.ACTION_CANCEL: ");
                        sl_right.setDragItemState(false);
                        isScroll = false;
                        mHandler.removeCallbacks(smallcard_runnable);
                        if (mItemView != null) {
                            mItemView.setVisibility(View.VISIBLE);
                        }
                        if (mDragImageView != null) {
                            SmallCardDragImpl.getInstance().showFrameAndMask();
                            mWindowManager.removeView(mDragImageView);
                            mDragImageView = null;
                        }
                        return true;
                }
                return true;
            }
        });
        sl_right.addListener(new CustomOverScrollLayout.OverScrollLayoutListener() {//切换列表
            @Override
            public void jump() {
                setTabSelectInDialog(mTabLayoutLeft);
            }
        });
        rv_RightList.addOnScrollListener(new RecyclerView.OnScrollListener() {//监听小卡recyclerView滑动
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    if (rv_right_ScrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        int firstVisiblePosition = rv_RightList.getChildLayoutPosition(rv_RightList.getChildAt(0));
                        View childAt = rv_RightList.getChildAt(0);
                        if (Math.abs(childAt.getLeft()) < childAt.getMeasuredWidth() / 2) {
                            rv_RightList.smoothScrollToPosition(firstVisiblePosition);
                        } else {
                            rv_RightList.smoothScrollToPosition(firstVisiblePosition + 1);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (Math.abs(dx) < 3) { //快停止了
                        rv_right_ScrollState = RecyclerView.SCROLL_STATE_SETTLING;//rv自己滚动
                        int firstVisiblePosition = rv_RightList.getChildLayoutPosition(rv_RightList.getChildAt(0));
                        View childAt = rv_RightList.getChildAt(0);
                        if (Math.abs(childAt.getLeft()) < childAt.getMeasuredWidth() / 2) {
                            rv_RightList.smoothScrollToPosition(firstVisiblePosition);
                        } else {
                            rv_RightList.smoothScrollToPosition(firstVisiblePosition + 1);
                        }

                    }
                } else if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_DRAGGING");
                    rv_right_ScrollState = RecyclerView.SCROLL_STATE_DRAGGING;//上手了
                }
            }
        });
        rv_RightList.addRightListScrollListener(new CustomRecyclerView.OnRightListScrollListener() {
            @Override
            public void notMoving() {
                Log.d("notMoving", "notMoving: ");
                isScroll = false;
                isMove = false;
            }
        });
        //注册底部卡片刷新的回调监听
        BigCardDragImpl.getInstance().addCallBackToBottomBigCard(this);
        SmallCardDragImpl.getInstance().addCallBackToBottomSmallCard(this);
        mShowListAnimation(rv_LeftList);//播放动画
        smallcard_runnable = new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 1054;
                mHandler.sendMessage(message);
            }
        };
        bigcard_runnable = new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 1055;
                mHandler.sendMessage(message);
            }
        };
    }

    /**
     * 在Dialog未显示时设置TabLayout选中状态:
     * 长按大卡设置 Tab index 0
     * 长按小卡设置 Tab index 1
     * 进场动画在 show 方法中延迟500ms播放
     *
     * @param position
     */
    //todo ========================================= 优化
    public void setOnLongClickTabSelect(int position) {
        if (position == mTabLayoutLeft) {//设置选择小卡
            largecardoptional.setSelected(true);
            smallcardoptional.setSelected(false);
            largecardoptional.setTextSize(COMPLEX_UNIT_PX, 40);
            smallcardoptional.setTextSize(COMPLEX_UNIT_PX, 36);
            line_sel_big.setVisibility(View.VISIBLE);
            line_sel_small.setVisibility(View.INVISIBLE);
            rv_RightList.setVisibility(View.GONE);
            rv_LeftList.setVisibility(View.VISIBLE);
            rv_LeftList.setAdapter(mLeftListAdapter);
        } else if (position == mTabLayoutRight) {//设置选择大卡
            largecardoptional.setSelected(false);
            smallcardoptional.setSelected(true);
            largecardoptional.setTextSize(COMPLEX_UNIT_PX, 36);
            smallcardoptional.setTextSize(COMPLEX_UNIT_PX, 40);
            line_sel_big.setVisibility(View.INVISIBLE);
            line_sel_small.setVisibility(View.VISIBLE);
            rv_RightList.setVisibility(View.VISIBLE);
            rv_LeftList.setVisibility(View.GONE);
            rv_RightList.setAdapter(mRightListAdapter);
        }
    }


    /**
     * 弹出dialog
     * 判断当前选中状态是 大卡可选 还是小卡可选
     * 播放进场动画
     *
     * @param
     */
    @Override
    public void show() {
        super.show();
        Log.d(TAG, "show: =======>");
        isVisibility = true;
        if (largecardoptional.isSelected()) {
            sl_left.init(mTabLayoutLeft);
            line_sel_big.setVisibility(View.VISIBLE);
            line_sel_small.setVisibility(View.INVISIBLE);
            rv_LeftList.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessageDelayed(mTabLayoutLeft, 666);
        } else if (smallcardoptional.isSelected()) {
            sl_right.init(mTabLayoutRight);
            line_sel_big.setVisibility(View.INVISIBLE);
            line_sel_small.setVisibility(View.VISIBLE);
            rv_RightList.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessageDelayed(mTabLayoutRight, 666);
        }
    }

    /**
     * dialog消失
     */
    @Override
    public void cancel() {
        super.cancel();
//        AnimationControler.getInstance(mContext).hideFrame();
//        AnimationControler.getInstance(mContext).hideMask();
    }


    /**
     * 切换列表
     *
     * @param position
     */
    //todo ========================================= 优化
    public void setTabSelectInDialog(int position) {
        if (position == mTabLayoutLeft) {//设置选择大卡
            sl_left.init(mTabLayoutLeft);
            largecardoptional.setSelected(true);
            smallcardoptional.setSelected(false);
            largecardoptional.setTextSize(COMPLEX_UNIT_PX, 40);
            smallcardoptional.setTextSize(COMPLEX_UNIT_PX, 36);
            line_sel_big.setVisibility(View.VISIBLE);
            line_sel_small.setVisibility(View.INVISIBLE);
            rv_RightList.setVisibility(View.GONE);
            rv_LeftList.setVisibility(View.VISIBLE);
            rv_LeftList.setAdapter(mLeftListAdapter);
            mShowListAnimation(rv_LeftList);
        } else if (position == mTabLayoutRight) {//设置选择小卡
            sl_right.init(mTabLayoutRight);
            largecardoptional.setSelected(false);
            smallcardoptional.setSelected(true);
            largecardoptional.setTextSize(COMPLEX_UNIT_PX, 36);
            smallcardoptional.setTextSize(COMPLEX_UNIT_PX, 40);
            line_sel_big.setVisibility(View.INVISIBLE);
            line_sel_small.setVisibility(View.VISIBLE);
            rv_RightList.setVisibility(View.VISIBLE);
            rv_LeftList.setVisibility(View.GONE);
            rv_RightList.setAdapter(mRightListAdapter);
            mShowListAnimation(rv_RightList);

        }
    }

    /**
     * 换卡后底部大卡列表更新
     *
     * @param id
     * @param position
     */
    @Override
    public void bigCardIdChange(String id, int position) {
        Log.d(TAG, "bigCardIdChange: " + " id = " + id + " position = " + position);
        mDefaultBottomBigCardInfoList = mDefaultBottomBigCardInfoList.replace(String.valueOf(mDefaultBottomBigCardInfoList.charAt(position)), id);
        mSysPropFunction.setBottomBigCardIdToSysProperties(mDefaultBottomBigCardInfoList);
        mLeftListAdapter.setData(mDefaultBottomBigCardInfoList);
        mLeftListAdapter.setVisibility(true);
        mLeftListAdapter.notifyItemChanged(position);
        cardManager.unInitCard(id, TYPE_BIGCARD);//大卡下屏
    }

    /**
     * 换卡后底部大卡列表更新
     *
     * @param id
     * @param position
     */
    @Override
    public void smallCardIdChange(String id, int position) {
        Log.d(TAG, "smallCardIdChange: " + " id = " + id + " position = " + position);
        mDefaultBottomSmallCardInfoList = mDefaultBottomSmallCardInfoList.replace(String.valueOf(mDefaultBottomSmallCardInfoList.charAt(position)), id);
        mSysPropFunction.setSmallCardInFoToSysProperties(mDefaultBottomSmallCardInfoList);
        mRightListAdapter.setData(mDefaultBottomSmallCardInfoList);
        mRightListAdapter.setVisibility(true);
        mRightListAdapter.notifyItemChanged(position);
        cardManager.unInitCard(id, TYPE_SMALLCARD);//小卡下屏
    }

    /**
     * 点击完成按钮
     *
     * @param
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                Log.d(TAG, "onClick: tv_complete");
//                AnimationControler.getInstance(mContext).hideFrame();
//                AnimationControler.getInstance(mContext).hideMask();
                if (mItemView != null) {
                    mItemView.setVisibility(View.VISIBLE);
                }
                if (mDragImageView != null) {
                    mWindowManager.removeView(mDragImageView);
                    mDragImageView = null;
                }
                mHandler.removeCallbacks(smallcard_runnable);
                mHandler.removeCallbacks(bigcard_runnable);
                dismiss();
                break;
            case R.id.largecardoptional:
                setTabSelectInDialog(mTabLayoutLeft);
                break;
            case R.id.smallcardoptional:
                setTabSelectInDialog(mTabLayoutRight);
                break;
        }
    }

    @Override
    public void notifyShareData(int i, String s) {
        if (i==SHARE_DATA_SOURCE_MANAGER_ID){
            checkSourceShareData(s);
        }
    }
    private void checkSourceShareData(String shareData) {

        if (!TextUtils.isEmpty(shareData)) {
            try {
                JSONObject object = new JSONObject(shareData);
                String uid = object.getString("UID");

                if (uid!=null&&"ADAYO_SOURCE_SETTING".equals(uid)&&!uid.equals(lastuid)){
                    isChangeUid = true;
                    dismiss();
                }
                lastuid = uid;
                String audioId = object.getString("AudioID");

//                if (null == sourceInfo.getUiSource() || null == sourceInfo.getAudioSource() || !uid.equals(sourceInfo.getUiSource()) || !audioId.equals(sourceInfo.getAudioSource())) {
//                    sourceInfo.setUiSource(object.getString("UID"));
//                    sourceInfo.setAudioSource(object.getString("AudioID"));
//                }
            } catch (JSONException e) {

            }
        }
    }
    /**
     * pointXY函数是抬手事件给fragment的回调，
     * 返回值是当前落在顶部卡片的x，y坐标
     *
     * @param
     */
    public interface onLeftListTouchUpListener {
        Map<Integer, Integer> pointXY(float x, float y);
    }

    public void setOnLeftListTouchUpListener(onLeftListTouchUpListener mOnLeftListTouchUpListener) {
        this.mOnLeftListTouchUpListener = mOnLeftListTouchUpListener;
    }

    /**
     * pointXY函数是抬手事件给fragment的回调，
     * 返回值是当前落在顶部卡片的x，y坐标
     *
     * @param
     */
    public interface onRightListTouchUpListener {
        Map<Integer, Integer> pointXY(float x, float y);
    }

    public void setOnRightListTouchUpListener(onRightListTouchUpListener mOnRightListTouchUpListener) {
        this.mOnRightListTouchUpListener = mOnRightListTouchUpListener;
    }

    public interface OnDismissMissListener {
        void dismiss();
        void fastDismiss();
    }

    public void setOnDismissMissListener(OnDismissMissListener onDismissMissListener) {
        this.onDismissMissListener = onDismissMissListener;
    }

    /**
     * Animation
     * 场景 ： 卡片依次入场动画
     *
     * @param recyclerView
     */
    private void mShowListAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.animation_layout);
        recyclerView.setLayoutAnimation(controller);
//        recyclerView.scheduleLayoutAnimation();


    }


    /**
     * 创建拖动的 window manager
     *
     * @param bitmap Animation
     *               场景 ： 长按底部卡片，卡片中心向手指移动动画
     *               崩溃 2 时序 down 事件一会在handler里 1.windowmanager addview 2.播放向手指移动动画
     *               长按down执行第一步1后还没执行2前当快速抬手，如果没放到卡片上会 清空 mDragImageView ，
     *               这时再执行2 就会出现 mDragImageView 为 null，更新windowmanager会崩溃
     */
    private void createBigCardDragView(Bitmap bitmap) {

        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.alpha = 0.8f; //透明度
        mWindowLayoutParams.width = 381;
        mWindowLayoutParams.height = 580;
//      mWindowLayoutParams.type = 2082;
        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mDragImageView.setScaleType(ImageView.ScaleType.FIT_START);
        mDragImageView.setScaleX(2 / 3f);
        mDragImageView.setScaleY(2 / 3f);
        mWindowLayoutParams.x = dragX;
        mWindowLayoutParams.y = dragY;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);

        int duration = 111;
        ValueAnimator valueAnimatorX = ValueAnimator.ofInt(mItemViewRawX - 254 / 4, dragX);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.x = animatedValue;
                if (mDragImageView == null) {
//                    Toast.makeText(mContext, "崩溃 2", Toast.LENGTH_SHORT).show();
                    return;
                }
                mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
            }
        });
        valueAnimatorX.setDuration(duration);
        ValueAnimator valueAnimatorY = ValueAnimator.ofInt(mItemViewRawY - 387 / 4, dragY);
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.y = animatedValue;
            }
        });
        valueAnimatorY.setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.start();

    }


    /**
     * Animation
     * 场景 ：大卡抬手磁吸动画
     */
    private void BigCardDropDragViewAnimation() {
        int x = 0;
        int y = 0;
        if (integerIntegerMap == null) { //todo  手指在被替换卡片外抬手
            if (mItemView != null) {
                mItemView.setVisibility(View.VISIBLE);
            }
            if (mDragImageView != null) {
                SmallCardDragImpl.getInstance().showFrameAndMask();
                mWindowManager.removeView(mDragImageView);
                mDragImageView = null;
            }
            return;
        }
        for (Integer key : integerIntegerMap.keySet()) {
            x = key;
            y = integerIntegerMap.get(key);
        }

        final ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.5f);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (mDragImageView != null) {
                    mDragImageView.setScaleX(animatedValue);
                    mDragImageView.setScaleY(animatedValue);
                }

            }
        });

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.6f, 0.1f);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (mDragImageView != null) {
                    mDragImageView.setAlpha(animatedValue);
                }

            }
        });

        ValueAnimator translateXAnimator = ValueAnimator.ofInt(mWindowLayoutParams.x - 50, 150);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.x = animatedValue;
                if (mDragImageView != null && mWindowLayoutParams != null) {
                    mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
                }
            }
        });

        ValueAnimator translateYAnimator = ValueAnimator.ofInt(mWindowLayoutParams.y - 100, 150);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWindowLayoutParams.y = (int) animation.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(scaleAnimator, alphaAnimator, translateXAnimator, translateYAnimator);
        mDragImageView.setScaleType(ImageView.ScaleType.CENTER);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd: ");
                if (mDragImageView != null) {
                    mDragImageView.setVisibility(View.INVISIBLE);
                    mWindowManager.removeView(mDragImageView);
                    mDragImageView = null;
                }
                BigCardDragImpl.getInstance().AddView(mDragCardId, mDragPosition);
                SmallCardDragImpl.getInstance().showFrameAndMask();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel: ");
                if (mDragImageView != null) {
                    mDragImageView.setVisibility(View.INVISIBLE);
                    mWindowManager.removeView(mDragImageView);
                    mDragImageView = null;
                }
                BigCardDragImpl.getInstance().AddView(mDragCardId, mDragPosition);
                SmallCardDragImpl.getInstance().showFrameAndMask();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 创建拖动的 window manager
     *
     * @param bitmap Animation
     *               场景 ： 长按底部卡片，卡片中心向手指移动动画
     *               崩溃 2 时序 down 事件一会在handler里 1.windowmanager addview 2.播放向手指移动动画
     *               长按down执行第一步1后还没执行2前当快速抬手，如果没放到卡片上会 清空 mDragImageView ，
     *               这时再执行2 就会出现 mDragImageView 为 null，更新windowmanager会崩溃
     */
    private void createSmallCardDragView(Bitmap bitmap) {

        Log.d("youDragView", "createSmallCardDragView: ");
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.alpha = 0.8f; //透明度
//        mWindowLayoutParams.type = 2082;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowLayoutParams.x = mItemViewRawX;
        mWindowLayoutParams.y = mItemViewRawY;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
        int duration = 111;
        ValueAnimator valueAnimatorX = ValueAnimator.ofInt(mItemViewRawX, dragX);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.x = animatedValue;
                if (mDragImageView == null) {
//                    Toast.makeText(mContext, "崩溃 2", Toast.LENGTH_SHORT).show();
                    return;
                }
                mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
            }
        });
        valueAnimatorX.setDuration(duration);
        ValueAnimator valueAnimatorY = ValueAnimator.ofInt(mItemViewRawY, dragY);
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.y = animatedValue;
            }
        });
        valueAnimatorY.setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.start();

    }

    /**
     * Animation
     * 场景 ： 小卡抬手磁吸动画
     */
    private void SmallCardDropDragViewAnimation() {
        int x = 0;
        int y = 0;
        if (integerIntegerMap == null) { //todo  手指在被替换卡片外抬手
            if (mItemView != null) {
                mItemView.setVisibility(View.VISIBLE);
            }
            if (mDragImageView != null) {
                SmallCardDragImpl.getInstance().showFrameAndMask();
                mWindowManager.removeView(mDragImageView);
                mDragImageView = null;
            }
            return;
        }
        for (Integer key : integerIntegerMap.keySet()) {
            x = key;
            y = integerIntegerMap.get(key);
        }
        int mCurrentX = (int) this.mTopViewX;
        if (mDragImageView == null) {
            return;
        }
        final ValueAnimator valueAnimatorX = ValueAnimator.ofInt(mCurrentX - mDragImageView.getWidth() / 2, x);
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.x = animatedValue;
                if (mDragImageView == null) {
//                    Toast.makeText(mContext, "崩溃 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
            }
        });
        valueAnimatorX.setDuration(333);

        int mCurrentY = (int) this.mTopViewY;
        ValueAnimator valueAnimatorY = ValueAnimator.ofInt(mCurrentY - mDragImageView.getHeight() / 2, y);//todo 查一下为什么是48
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mWindowLayoutParams.y = animatedValue;
            }
        });
        valueAnimatorY.setDuration(333);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimatorX, valueAnimatorY);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SmallCardDragImpl.getInstance().ChangeIdToTopToAddView(mDragCardId, mDragPosition);
                Message msg = Message.obtain();
                msg.what = 3;
                mHandler.sendMessageDelayed(msg, 100);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void dismiss() {

        super.dismiss();
        isVisibility = false;

         if (onDismissMissListener!=null){
             if (isChangeUid){
                 onDismissMissListener.fastDismiss();
             }else {
                 onDismissMissListener.dismiss();
             }

        }
        isChangeUid = false;
        WindowsManager.getInstance().seBottomDialogVisibility(View.GONE);
    }
    public boolean getVisibility(){
        return isVisibility;
    }
}

