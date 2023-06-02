package com.adayo.app.btphone.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("AppCompatCustomView")
public class SlideBar extends RelativeLayout {

    private static final String TAG = SlideBar.class.getSimpleName();

    public interface OnTouchAssortListener {
        boolean onTouchAssortListener(String s);
    }

    public interface OnDrownListener {
        void onDrown();
    }

    public interface OnVisibilityChangedListener {
        void onVisibilityChanged(int state);
    }

    private static final String[] ASSORT_TEXT = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "#"};

    private Paint mPaint = new Paint();
    private int mSelectIndex = -1;
    private OnTouchAssortListener mListener = null;
    private Activity mAttachActivity;
    private CustomPopupWindow mPopupWindow = null;
    private View layoutView;
    private TextView text;
    private TextView textRight;
    private Map<String, Point> pointList = new HashMap<String, Point>();
    private int mLayoutViewY = 0xFFFF;
    private String mInitial = "";
    private OnDrownListener onDrownListener;
    private boolean isDrown = false;
    private OnVisibilityChangedListener onVisibilityChangedListener;
    private RelativeLayout mLeftRL;

    private int flag = -1;

    private static final int OFFSET = 60;
    private static final int FFFF = 0xFFFF;

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAttachActivity = (Activity) context;
        init(context);
    }

    private void init(Context context) {
        layoutView = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.alert_dialog_menu_layout, null);
        mLeftRL = (RelativeLayout) layoutView.findViewById(R.id.left_rl);
        text = (TextView) layoutView.findViewById(R.id.content);
        textRight = (TextView) layoutView.findViewById(R.id.content_right);
        pointList.clear();
        addView(layoutView);
        layoutView.setY(-12);
    }

    public void setOnTouchAssortListener(OnTouchAssortListener listener) {
        this.mListener = listener;
    }

    public void setOnDrownListener(OnDrownListener listener) {
        this.onDrownListener = listener;
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener listener) {
        this.onVisibilityChangedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
//        Log.i(TAG,"onDraw");
//        int nHeight = getHeight();
//        int hWidth = getWidth();
//        int nAssortCount = ASSORT_TEXT.length;
//        int nInterval = nHeight / nAssortCount;
//        for (int i = 0; i < nAssortCount; i++) {
//            mPaint.setAntiAlias(true);
//            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
//            mPaint.setColor(getResources().getColor(R.color.colorWhite));
//            mPaint.setAlpha(127);
//            mPaint.setTextSize(16);
//            float xPos = hWidth / 2 - mPaint.measureText(ASSORT_TEXT[i]) / 2;
//            float yPos = nInterval * i + nInterval;
//            canvas.drawText(ASSORT_TEXT[i], xPos, yPos, mPaint);
//            pointList.put(ASSORT_TEXT[i], new Point((int)xPos, (int)yPos));
//            mPaint.reset();
//        }
//        if(!isDrown && onDrownListener != null) {
//            onDrownListener.onDrown();
//        }
//        isDrown = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG,"event.getX() = "+event.getX()+" event.getAction() = "+event.getAction());
        //保证左侧更多电话号码按钮可以点击
        if(event.getX() < OFFSET){
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mLeftRL.setVisibility(View.INVISIBLE);
            }
            return false;
        }
        int nIndex = (int) (event.getY() / getHeight() * ASSORT_TEXT.length);
        if (nIndex >= 0 && nIndex < ASSORT_TEXT.length) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "ACTION_MOVE : ASSORT_TEXT[" + nIndex + "] = " + ASSORT_TEXT[nIndex]);
                    if (mSelectIndex != nIndex) {
                        mSelectIndex = nIndex;
                        if (mListener != null && mListener.onTouchAssortListener(ASSORT_TEXT[mSelectIndex])) {
                            mLeftRL.setVisibility(View.VISIBLE);
                            showCharacter(ASSORT_TEXT[mSelectIndex]);
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "ACTION_DOWN : ASSORT_TEXT[" + nIndex + "] = " + ASSORT_TEXT[nIndex]);
                    if (mSelectIndex != nIndex) {
                        mSelectIndex = nIndex;
                        if (mListener != null && mListener.onTouchAssortListener(ASSORT_TEXT[mSelectIndex])) {
                            mLeftRL.setVisibility(View.VISIBLE);
                            showCharacter(ASSORT_TEXT[mSelectIndex]);
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mLeftRL.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mLeftRL.setVisibility(View.INVISIBLE);
        }

        invalidate();
        return true;
    }

    public void disShowCharacter() {
        if (mPopupWindow != null) {
            layoutView.setVisibility(GONE);
            mPopupWindow = null;
        }
        mLayoutViewY = 0xFFFF;
        mInitial = "";
    }

    private void showCharacter(String string) {
        Log.i(TAG, "showCharacter pointList.size() = " + pointList.size());
        if (pointList.size() <= 0) {
            return;
        }
        text.setText(string);
        textRight.setText(string);
        //控制左侧显示距离字母的距离
        int y = pointList.get(string).y - getTop() + 15;
        Log.i(TAG, "showCharacter[" + string + "] : from " + mLayoutViewY + " to " + y);
        if (mLayoutViewY == FFFF) {
            Log.d(TAG, "showCharacter+y: " + y);
            layoutView.setY(y);
        } else if (mLayoutViewY != y) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutView, "translationY", mLayoutViewY, y);
            animator.setDuration(500);
            animator.start();
        }
        mLayoutViewY = y;
    }

    public void updateCharacter(String initial) {
        Log.i(TAG, "updateCharacter : " + "mInitial = " + mInitial + "; initial = " + initial);
        if (initial.isEmpty()) {
            disShowCharacter();
        } else if (!mInitial.equals(initial)) {
            int index = -1;
            for (int i = 0; i < ASSORT_TEXT.length; i++) {
                if (initial.equals(ASSORT_TEXT[i])) {
                    index = i;
                    break;
                }
            }
            if (index != -1 && pointList.size() > 0) {
                showCharacter(ASSORT_TEXT[index]);
                mInitial = initial;
            }
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Log.i(TAG, "visibility = " + visibility);
        if (onVisibilityChangedListener != null) {
            onVisibilityChangedListener.onVisibilityChanged(visibility);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d(TAG, "dispatchDraw: ");
        super.dispatchDraw(canvas);
        int nHeight = getHeight();
        int hWidth = getWidth();
        int nAssortCount = ASSORT_TEXT.length;
        int nInterval = nHeight / nAssortCount;
        for (int i = 0; i < nAssortCount; i++) {
            mPaint.setAntiAlias(true);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setColor(getResources().getColor(R.color.colorWhite));
            mPaint.setAlpha(127);
            mPaint.setTextSize(16);
            float xPos = hWidth / 2 - mPaint.measureText(ASSORT_TEXT[i]) / 2;
            float yPos = nInterval * i + nInterval;
            canvas.drawText(ASSORT_TEXT[i], xPos + 40, yPos, mPaint);
            pointList.put(ASSORT_TEXT[i], new Point((int) xPos, (int) yPos));
            mPaint.reset();
        }
        if (!isDrown && onDrownListener != null) {
            onDrownListener.onDrown();
        }
        isDrown = true;
    }

}
