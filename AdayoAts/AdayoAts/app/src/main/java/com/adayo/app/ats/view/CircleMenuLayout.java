package com.adayo.app.ats.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.ats.R;
import com.adayo.app.ats.factory.ResourcesFactory;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import static com.adayo.app.ats.util.Constants.ATS_VERSION;
import static com.adayo.app.ats.util.Constants.PICTUREMOD;
import static com.adayo.app.ats.util.Constants.TEXTMOD;

public class CircleMenuLayout extends RelativeLayout {

    private static final String TAG = ATS_VERSION + CircleMenuLayout.class.getSimpleName();
    /**
     * 直径
     */
    private int mRadius;
    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 3f;
    /**
     * 菜单的中心child的默认尺寸
     */
    private final float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;
    /**
     * /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 0;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;
    /**
     * 菜单项的图标数组
     */
    private int[] mItemNormalImages;
    private int[] mItemDisImages;
    private int[] mItemSelImages;
    /**
     * 菜单项的文字数组
     */
    private int[] mItemGlobalTexts;
    /**
     * 菜单的个数
     */
    private int mMenuItemCount;
    /**
     * intint
     * 引用布局id
     */
    private int mMenuItemLayoutId = R.layout.circle_menu_item;
    /**
     * 目标模式
     */
    private int mTargetMode = 0;
    private View mItemView;
    private List<ViewHolder> mItemViewlist;
    public LayoutInflater mInflater;
    private ViewHolder holder;
    private Timer timer;
    private boolean mClockwise = true;
    private List<String> list;
    private boolean flag = true;//在当前模式移动到目标模式的时候之后才能点击下一次OK
    private int mCurrentAtsMode = 0;//驾驶模式
    private int mCurrentDisplayMod = 0;//文字/图片模式
    private int targetAtsMode;
    private int currentAtsMode;
    private final ResourcesFactory factory;
    private Context context;

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
        factory = ResourcesFactory.getInstance(context);
        this.context = context;
    }

    /**
     * 设置布局的宽高，并测量menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的宽度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            // 主要设置为背景图的高度
            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
            Log.d("", "onMeasure: " + "resWidth = " + resWidth + "resHeight = " + resHeight);
        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }
        //存储宽高
        setMeasuredDimension(resWidth, resHeight);
        // 获得直径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;
        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec;
            if (child.getId() == R.id.rl_center_content) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION), childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = 25;
    }

    /**
     * MenuItem的点击事件接口
     *
     * @author zhy
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    /**
     * 设置MenuItem的点击事件接口
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置menu item的位置
     * cos 弧度得到的是对应角度的余弦
     * 角度的余弦
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;
        // Laying out the child views
        final int childCount = getChildCount();
        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // 根据menu item的个数，计算角度
        float angleDelay = 360 / (getChildCount());
        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getId() == R.id.rl_center_content) {
                continue;
            }
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算，中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 + mPadding;
            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);
            // tmp sina 即menu item的纵坐标
            top = (layoutRadius / 2) + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth - 10);
            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
            if (i == childCount - 1) {//todo onlayout执行两次bug
                mStartAngle = factory.getAngleValue();
            }
        }
        // 找到中心的view，如果存在设置onclick事件
        View cView = findViewById(R.id.rl_center_content);
        if (cView != null) {
            // 设置center item位置
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl - 2, cl, cr, cr);
        }
    }


    /**
     * 设置菜单条目的图标和文本
     * 参数 1    非当前模式(与目标模式是同一模式)    文字大小 big   color white
     * 2        非当前模式(与目标模式是同一模式)    文字大小 big   color white
     * 3        当前模式(与目标模式不是同一模式)    文字大小 nomal   color green
     * 4       当前模式(与目标模式是同一模式)    文字大小 big   color green
     * 5
     * 6
     *
     * @param
     */

    public void setMenuItemIconsAndTexts(int[] mItemNormalImages, int[] mItemDisImages, int[] mItemSelImages, int[] mItemGlobalTexts) {

        this.mItemNormalImages = mItemNormalImages;//目标 高亮
        this.mItemDisImages = mItemDisImages;//默认 暗
        this.mItemSelImages = mItemSelImages;//当前 绿
        this.mItemGlobalTexts = mItemGlobalTexts;//文字
        // 参数检查
        if (this.mItemNormalImages == null && mItemGlobalTexts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }
        // 初始化mMenuCount
        mMenuItemCount = this.mItemNormalImages == null ? mItemGlobalTexts.length : this.mItemNormalImages.length;
        if (this.mItemNormalImages != null && mItemGlobalTexts != null) {
            mMenuItemCount = Math.min(this.mItemNormalImages.length, mItemGlobalTexts.length);
        }
        addMenuItems();
    }


    /**
     * 添加菜单项
     */
    private void addMenuItems() {

//      mInflater = LayoutInflater.from(getContext());
        mInflater = AAOP_HSkin.getLayoutInflater(getContext());
        /**
         * 根据用户设置的参数，初始化view
         */
        mItemViewlist = new ArrayList<>();
        Log.d(TAG, "setMenuItemIconsAndTexts: __" + mMenuItemCount);
        if (mCurrentDisplayMod == PICTUREMOD) { // 图片
            for (int i = 0; i < mMenuItemCount; i++) {
                Log.d(TAG, "setMenuItemIconsAndTexts: ==" + i);
                holder = new ViewHolder();
                mItemView = mInflater.inflate(mMenuItemLayoutId, this, false);
                holder.iv = (ImageView) mItemView.findViewById(R.id.id_circle_menu_item_image);
                holder.tv = (TextView) mItemView.findViewById(R.id.id_circle_menu_item_text);

                if (holder.iv != null) {
                    holder.iv.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(80, 80);
                    layoutParams.gravity = Gravity.CENTER;
                    holder.iv.setLayoutParams(layoutParams);
                    if (i < 9) {
                        AAOP_HSkin
                                .with(holder.iv)
                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemDisImages[i])
                                .applySkin(false);
//                        holder.iv.setImageResource(mItemDisImages[i]);
                        if (i == 0) {
                        } else {
                        }
                    }
                }
                if (holder.tv != null) {
                    String language = Locale.getDefault().getLanguage();
                    Log.d(TAG, "addMenuItems111111:   " + language);
                    if ("CN".equals(language)) {
                        Log.d(TAG, "addMenuItems111111: CN");
                        holder.tv.setTextSize(25);
                    } else {
                        holder.tv.setTextSize(25);
                    }
                    AAOP_HSkin
                            .with(holder.tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.gray)
                            .applySkin(true);
                    holder.tv.setText(mItemGlobalTexts[i]);
                    holder.tv.setVisibility(View.INVISIBLE);

                }
                // 添加view到容器中
                mItemViewlist.add(holder);
                addView(mItemView);
            }
        } else if (mCurrentDisplayMod == TEXTMOD) { // 文字
            for (int i = 0; i < mMenuItemCount; i++) {
                holder = new ViewHolder();
                mItemView = mInflater.inflate(mMenuItemLayoutId, this, false);
                holder.iv = (ImageView) mItemView.findViewById(R.id.id_circle_menu_item_image);
                holder.tv = (TextView) mItemView.findViewById(R.id.id_circle_menu_item_text);

                if (holder.iv != null) {
                    holder.iv.setVisibility(View.INVISIBLE);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(80, 80);
                    layoutParams.gravity = Gravity.CENTER;
                    holder.iv.setLayoutParams(layoutParams);
                    if (i < 9) {
                        AAOP_HSkin
                                .with(holder.iv)
                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemNormalImages[i])
                                .applySkin(true);
//                        holder.iv.setImageResource(mItemDisImages[i]);
                        if (i == 0) {
                        } else {
                        }
                    }
                }

                if (holder.tv != null) {
                    String language = Locale.getDefault().getLanguage();
                    Log.d(TAG, "addMenuItems111111:   " + language);
                    if ("CN".equals(language)) {
                        Log.d(TAG, "addMenuItems111111: CN");
                        holder.tv.setTextSize(25);
                    } else {
                        holder.tv.setTextSize(25);
                    }
                    AAOP_HSkin
                            .with(holder.iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.gray)
                            .applySkin(false);
                    holder.tv.setVisibility(View.VISIBLE);
                    holder.tv.setText(mItemGlobalTexts[i]);
                }
                // 添加view到容器中
                mItemViewlist.add(holder);
                addView(mItemView);
            }
        }
        int childCount = getChildCount();
        Log.d(TAG, "setMenuItemIconsAndTexts: ==" + childCount);
    }

    /**
     * 更新模式
     */
    public void updateCurrentMode(int mode) {

        for (int i = 0; i < mItemViewlist.size(); i++) {
            ViewHolder viewHolder = mItemViewlist.get(i);
            Log.d(TAG, "updateCurrentMode: " + viewHolder.tv + viewHolder.iv);
            if (mode == PICTUREMOD) {
                viewHolder.iv.setVisibility(VISIBLE);
                viewHolder.tv.setVisibility(INVISIBLE);
            } else if (mode == TEXTMOD) {
                viewHolder.iv.setVisibility(INVISIBLE);
                viewHolder.tv.setVisibility(VISIBLE);
            }
        }
    }


    private void notifyItemView() {


    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public void setOk() {


    }

    public class ViewHolder {
        private ImageView iv;
        private TextView tv;
    }


    /**
     * 设置当前模式,文字/图片
     */
    public void setCurrentMode(String currentMode) {
        this.mCurrentDisplayMod = Integer.valueOf(currentMode);
    }

    /**
     * ATS目标模式
     *
     * @param atsTargetMode
     */
    public void setTargetAtsMode(int atsTargetMode) {
        mStartAngle = factory.getAngleValue();
        Log.d(TAG, "layout_____setTargetAtsMode: " + atsTargetMode);
        this.targetAtsMode = atsTargetMode;
        if (mCurrentDisplayMod == PICTUREMOD) {
            for (int i = 0; i < mItemViewlist.size(); i++) {
                if (atsTargetMode == i && currentAtsMode == i) {//当前 绿色
                    Log.d(TAG, "setTargetAtsMode picturemod: mItemSelImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemSelImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemSelImages[i]);
                } else if (atsTargetMode == i) {//目标白色
                    Log.d(TAG, "setTargetAtsMode picturemod: mItemNormalImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemNormalImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemNormalImages[i]);
                } else if (i != currentAtsMode) {//普通 灰色
                    Log.d(TAG, "setTargetAtsMode picturemod: mItemDisImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemDisImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemDisImages[i]);
                }
            }
        } else if (mCurrentDisplayMod == TEXTMOD) {
            for (int i = 0; i < mItemViewlist.size(); i++) {
                if (atsTargetMode == i && currentAtsMode == i) {
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.textColor)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(getContext().getResources().getColor(R.color.textColor));
                } else if (atsTargetMode == i) {
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.colorWhite)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(Color.WHITE);
                } else if (i != currentAtsMode) {
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.gray)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(Color.GRAY);
                }
            }
        }

    }

    /**
     * ATS当前模式
     *
     * @param atsCurrentMode
     */
    public void setCurrentAtsMode(int atsCurrentMode) {
        mStartAngle = factory.getAngleValue();
        this.currentAtsMode = atsCurrentMode;
        if (mCurrentDisplayMod == PICTUREMOD) {
            for (int i = 0; i < mItemViewlist.size(); i++) {
                if (atsCurrentMode == i) {
                    Log.d(TAG, "setCurrentAtsMode picturemod: mItemSelImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemSelImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemSelImages[i]);
                } else if (targetAtsMode == i && atsCurrentMode != i) {
                    Log.d(TAG, "setCurrentAtsMode picturemod: mItemNormalImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemNormalImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemNormalImages[i]);
                } else if (i != targetAtsMode) {
                    Log.d(TAG, "setCurrentAtsMode picturemod: mItemDisImages");
                    AAOP_HSkin.with(mItemViewlist.get(i).iv)
                            .addViewAttrs(AAOP_HSkin.ATTR_SRC, mItemDisImages[i])
                            .applySkin(false);
//                    mItemViewlist.get(i).iv.setImageResource(mItemDisImages[i]);
                }
            }
        } else if (mCurrentDisplayMod == TEXTMOD) {
            for (int i = 0; i < mItemViewlist.size(); i++) {
                if (atsCurrentMode == i) { //目标 高亮
                    Log.d(TAG, "setCurrentAtsMode: textColor");
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.textColor)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(0xff00f0cc);
                } else if (targetAtsMode == i && atsCurrentMode != i) {
                    Log.d(TAG, "setCurrentAtsMode: colorWhite");
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.colorWhite)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(Color.WHITE);
                } else if (i != targetAtsMode) { //普通 灰色
                    Log.d(TAG, "setCurrentAtsMode: GRAY");
                    AAOP_HSkin.with(mItemViewlist.get(i).tv)
                            .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.gray)
                            .applySkin(false);
//                    mItemViewlist.get(i).tv.setTextColor(Color.GRAY);
                }
            }
            Log.d(TAG, "setCurrentAtsMode: " + getChildCount());
        }
    }

}