package com.adayo.app.setting.view.popwindow.harman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.SoundFactory;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.bean.SoundBean;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.product.ASoundProduct;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HarmanSurfaceView extends SurfaceView implements SurfaceHolder.Callback, IHarmanViewInData {private final static String TAG = HarmanSurfaceView.class.getSimpleName();
    private Context context;
    private final SurfaceHolder holder;
    private Rect rect;private ASoundProduct product;private Paint iconPaint = new Paint();private Paint animPaint = new Paint();private IHarmanViewOutData mIHarmanViewOutData;private int max = 10;private int min = -10;
    private int left = 1157;
    private int top = 461;
    private int initleft = 1157;
    private int inittop = 461;
    private List<FastBitBean> mFastBitBeanList = new ArrayList<>();private SoundBean mBean;
    private Handler handler;private Runnable runnable;
    private int TOUCH_MAX = 50;private int mLastMotionX;private int mLastMotionY;
    private int mLastMotionMoveX;private int mLastMotionMoveY;
    private List<Bitmap> mButtonsUPList;private List<Bitmap> mMidList;private List<Bitmap> mIconList = new ArrayList<>();private AnimDrawBean mButtonUpAnimDrawBean;private AnimDrawBean mMidAnimDrawBean;private Bitmap iconBitmap;private int iconX;private int iconY;
    private AnimManager mAnimManager;
    private boolean isEffective;
    private boolean isTouchIcon;
    private AnimDrawBean mFastBitAnimDrawBean;
    private AnimDrawBean mIconAnimDrawBean;
    private boolean isOnCreateEnd=false;



    public HarmanSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LogUtil.debugD(TAG, "" + this);
        this.context = context;
        setZOrderOnTop(true);getHolder().setFormat(PixelFormat.TRANSLUCENT);holder = this.getHolder();holder.addCallback(this);
        initView();
        initPaints();
        SkinUtil.setHarmanSkinAttr("harmanDrawable", this, R.drawable.buttons, context);
}

    @Override
    public IHarmanViewInData init(IHarmanViewOutData iHarmanViewOutData) {
        LogUtil.debugD(TAG, "" + this);
        mIHarmanViewOutData = iHarmanViewOutData;
        return this;
    }

    private void initPaints() {
        LogUtil.debugD(TAG, "" + this);
        iconPaint.setAntiAlias(true);
        iconPaint.setDither(true);
        iconPaint.setFilterBitmap(true);
        animPaint.setAntiAlias(true);
        animPaint.setDither(true);
        animPaint.setFilterBitmap(true);
    }


    private void initView() {
        LogUtil.debugD(TAG, "");
        product = SoundFactory.getSoundProduct("B60V");
        SoundBean b0 = new SoundBean(1157, 461, 0, 0);SoundBean b1 = new SoundBean(885, 261, min, max);SoundBean b2 = new SoundBean(1429, 261, max, max);
        SoundBean b3 = new SoundBean(644, 694, min, min);
        SoundBean b4 = new SoundBean(1670, 694, min, max);
        product.init(b0, b1, b2, b3, b4);
        product.calculateAllSoundLocation();iconBitmap = getImage(context, R.drawable.buttons);
        rect = new Rect(left, top, iconBitmap.getWidth(), iconBitmap.getHeight());initLongClickHandler();
    }


    private void initLongClickHandler() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.debugD(TAG, "LONGCLICK");
                if (mButtonUpAnimDrawBean == null) {
                    mButtonsUPList = initButtonsUP();
                    mButtonUpAnimDrawBean = new AnimDrawBean(mButtonsUPList, iconX, iconY, animPaint);
                }
                mAnimManager.cancel();
                mButtonUpAnimDrawBean.setIconX(iconX);
                mButtonUpAnimDrawBean.setIconY(iconY);
                mAnimManager.addAnimDrawBean(mButtonUpAnimDrawBean);
                mAnimManager.start();
            }
        };
    }

    @Override
    public void initSoundMaxMin(int max, int min) {
        LogUtil.debugD(TAG, "" + this);
        this.max = max;
        this.min = min;
    }

    @Override
    public void initPostion(int left, int top) {
        LogUtil.debugD(TAG, "left =" + left + "top = " + top);
        SoundBean bean = product.getScreenLocationBySound(left, top); rect.left = (int) bean.getmSreen_X();
        rect.top = (int) bean.getmSreen_Y();
        iconX = rect.left;
        iconY = rect.top;
        mLastMotionX=iconX;
        mLastMotionY=iconY;
        mLastMotionMoveX=iconX;
        mLastMotionMoveY=iconY;
    }

    @Override
    public void updataPostion(int x, int y) {
        SoundBean bean = product.getScreenLocationBySound(x, y); rect.left = (int) bean.getmSreen_X();
        rect.top = (int) bean.getmSreen_Y();
        iconX = rect.left;
        iconY = rect.top;
        if (isOnCreateEnd) {
            updataViewAnim(rect.left, rect.top, true, false);
        }
    }

    @Override
    public void initStartPostion(int left, int top) {
        LogUtil.debugD(TAG, "left =" + left + "top = " + top);
        this.left = left;
        this.top = top;

    }

    @Override
    public void initFastBit(List<FastBitBean> fastBitBeans) {
        LogUtil.debugD(TAG, "fastBitBeans =" + fastBitBeans);
        mFastBitBeanList = fastBitBeans;

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.debugD(TAG, "" + this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.debugD(TAG, "" + this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.debugD(TAG, "" + this.hashCode());
        mIconList.add(iconBitmap);
        mMidList = initMidAnimList();
        mFastBitAnimDrawBean = new AnimDrawBean(mFastBitBeanList.get(0).getList(), iconX, iconY, animPaint);
        mFastBitAnimDrawBean.setLoop(true);
        mAnimManager = new AnimManager(holder);
        mMidAnimDrawBean = new AnimDrawBean(mMidList, rect.left, rect.top, animPaint);
        mMidAnimDrawBean.setLoop(true);
        mIconAnimDrawBean = new AnimDrawBean(mIconList, rect.left, rect.top, animPaint);
        mIconAnimDrawBean.setLoop(true);
        mAnimManager.addAnimDrawBean(mMidAnimDrawBean);
        mAnimManager.addAnimDrawBean(mIconAnimDrawBean);
        mAnimManager.start();
        updataViewAnim(rect.left, rect.top, true, false);
        isOnCreateEnd=true;
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.debugD(TAG, "" + this.hashCode());
        mAnimManager.cancel();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }


    private void changeRect(float x, float y) {
        rect.left = (int) x;
        rect.top = (int) y;
        iconX = rect.left;
        iconY = rect.top;

    }


    private boolean updataViewAnim(float x, float y, boolean b, boolean isSetValue) {
        LogUtil.debugD(TAG, "x =" + x + "y = " + y);
boolean isFastBit = false;
        boolean isValid = false;
        for (int i = 0; i < mFastBitBeanList.size(); i++) {
            if (x > mFastBitBeanList.get(i).getLeftTopX() && x < mFastBitBeanList.get(i).getRightTopX() && y > mFastBitBeanList.get(i).getRightTopY() && y < mFastBitBeanList.get(i).getRightBottomY()) {
                isFastBit = true;
                if (b) {
                    mAnimManager.cancel();
                    changeRect(mFastBitBeanList.get(i).getCenterX(), mFastBitBeanList.get(i).getCenterY());
                    mFastBitAnimDrawBean.setAnimPictureList(mFastBitBeanList.get(i).getList());
                    mFastBitAnimDrawBean.setIconX(iconX);
                    mFastBitAnimDrawBean.setIconY(iconY);
                    mIconAnimDrawBean.setIconX(iconX);
                    mIconAnimDrawBean.setIconY(iconY);
                    mAnimManager.addAnimDrawBean(mFastBitAnimDrawBean);
                    mAnimManager.addAnimDrawBean(mIconAnimDrawBean);
                    mAnimManager.start();
                    mIHarmanViewOutData.hideFastBit(i);

                } else {
                    changeRect(x, y);
                }
                mBean = product.selectLocation(mFastBitBeanList.get(i).getCenterX(), mFastBitBeanList.get(i).getCenterY()); if (isSetValue) {
                    mIHarmanViewOutData.setSoundFiled(mBean.getmSound_X(), mBean.getmSound_Y());
                }
            }
        }
        if (!isFastBit) {
            isValid = product.isValidLocation(x, y); if (isValid) {
                changeRect(x, y);
                mBean = product.selectLocation(x, y); if (isSetValue) {
                    mIHarmanViewOutData.setSoundFiled(mBean.getmSound_X(), mBean.getmSound_Y());
                }
                mIHarmanViewOutData.showFastBit();
                if (b) {
                    mAnimManager.cancel();
                    mIconAnimDrawBean.setIconX(iconX);
                    mIconAnimDrawBean.setIconY(iconY);
                    mMidAnimDrawBean.setIconX(iconX);
                    mMidAnimDrawBean.setIconY(iconY);
                    mAnimManager.addAnimDrawBean(mMidAnimDrawBean);
                    mAnimManager.addAnimDrawBean(mIconAnimDrawBean);
                    mAnimManager.start();
                }
            }
        }
        return isValid || isFastBit;
    }

    private boolean isChange(int x, int y) {
        for (int i = 0; i < mFastBitBeanList.size(); i++) {
            if (x > mFastBitBeanList.get(i).getLeftTopX() && x < mFastBitBeanList.get(i).getRightTopX() && y > mFastBitBeanList.get(i).getRightTopY() && y < mFastBitBeanList.get(i).getRightBottomY() || product.isValidLocation(x, y)) {
                return true;
            }
        }
        return false;
    }

    public static final Bitmap getImage(Context context, int imageId) {
        return ((BitmapDrawable) AAOP_HSkin.getInstance().getResourceManager().getDrawable(imageId)).getBitmap();
}


    @Override
    public void onTouchHarman(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isEffective = true;
                if (!isChange(x, y)) {
                    isEffective = false;
                    return;
                }
                LogUtil.debugD(TAG, "ACTION_DOWN");
                handler.removeCallbacks(runnable);
                if (motionEvent.getX() > rect.left - iconBitmap.getWidth() / 2 && motionEvent.getX() < rect.left + iconBitmap.getWidth() / 2 && motionEvent.getY() > rect.top - iconBitmap.getHeight() / 2 && motionEvent.getY() < rect.top + iconBitmap.getHeight() / 2) {LogUtil.debugD(TAG, "an niu shi qu");
handler.postDelayed(runnable, 200);
                    isTouchIcon = true;

                } else {
                    isTouchIcon = false;
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouchIcon || !isEffective) {
                    LogUtil.debugD(TAG, "DONT WANT MOVE");
                    return;
                }
if (Math.abs(mLastMotionX - motionEvent.getX()) > TOUCH_MAX
                        || Math.abs(mLastMotionY - motionEvent.getY()) > TOUCH_MAX) {
                    handler.removeCallbacks(runnable);

                    if (isChange(x, y)) {
                        mLastMotionMoveX = x;
                        mLastMotionMoveY = y;
                    } else {
                        LogUtil.debugD(TAG, "NOT MOVW");
                        handler.removeCallbacks(runnable);
                        updataViewAnim(mLastMotionMoveX, mLastMotionMoveY, true, true);
                        isEffective = false;
                        return;
                    }
                    mAnimManager.cancel();
                    updataViewAnim(x, y, false, true);
                    draw();
                }

                break;
            case MotionEvent.ACTION_UP:
                LogUtil.debugD(TAG, "ACTION_UP");
                if (!isEffective) {
                    LogUtil.debugD(TAG, "returen");
                    return;
                }
                handler.removeCallbacks(runnable);

                if (isChange(x, y)) {
                    LogUtil.debugD(TAG, "IS OK");
                    updataViewAnim(motionEvent.getX(), motionEvent.getY(), true, true);
                } else {
                    updataViewAnim(mLastMotionMoveX, mLastMotionMoveY, true, true);
                }

                isEffective = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
                break;
        }

    }


    @Override
    public void resetView() {
        LogUtil.debugD(TAG, "");
        updataViewAnim(initleft, inittop, true, true);
        LogUtil.d("GGG4");
        mIHarmanViewOutData.showFastBit();
    }

    public void setBitmap(Bitmap bitmap) {
        LogUtil.debugD(TAG, "");
        iconBitmap = bitmap;
    }

    protected void draw() {
        Canvas canvas = holder.lockCanvas();canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);Bitmap bitmap = getImage(context, R.drawable.buttons_up10);
        canvas.drawBitmap(bitmap, iconX - bitmap.getWidth() / 2, iconY - bitmap.getHeight() / 2, iconPaint);
        holder.unlockCanvasAndPost(canvas);}

    private List<Bitmap> initMidAnimList() {
        List<Bitmap> mMidAnimList = Arrays.asList(
                getImage(context, R.drawable.mid00),
                getImage(context, R.drawable.mid01),
                getImage(context, R.drawable.mid02),
                getImage(context, R.drawable.mid03),
                getImage(context, R.drawable.mid04),
                getImage(context, R.drawable.mid05),
                getImage(context, R.drawable.mid06),
                getImage(context, R.drawable.mid07),
                getImage(context, R.drawable.mid08),
                getImage(context, R.drawable.mid09),
                getImage(context, R.drawable.mid10),
                getImage(context, R.drawable.mid11),
                getImage(context, R.drawable.mid12),
                getImage(context, R.drawable.mid13),
                getImage(context, R.drawable.mid14),
                getImage(context, R.drawable.mid15),
                getImage(context, R.drawable.mid16),
                getImage(context, R.drawable.mid17),
                getImage(context, R.drawable.mid18),
                getImage(context, R.drawable.mid19),
                getImage(context, R.drawable.mid20),
                getImage(context, R.drawable.mid21),
                getImage(context, R.drawable.mid22),
                getImage(context, R.drawable.mid23),
                getImage(context, R.drawable.mid24),
                getImage(context, R.drawable.mid25),
                getImage(context, R.drawable.mid26),
                getImage(context, R.drawable.mid27),
                getImage(context, R.drawable.mid28),
                getImage(context, R.drawable.mid29),
                getImage(context, R.drawable.mid30),
                getImage(context, R.drawable.mid31),
                getImage(context, R.drawable.mid32),
                getImage(context, R.drawable.mid33),
                getImage(context, R.drawable.mid34),
                getImage(context, R.drawable.mid35),
                getImage(context, R.drawable.mid36),
                getImage(context, R.drawable.mid37),
                getImage(context, R.drawable.mid38),
                getImage(context, R.drawable.mid39),
                getImage(context, R.drawable.mid40),
                getImage(context, R.drawable.mid41),
                getImage(context, R.drawable.mid42),
                getImage(context, R.drawable.mid43),
                getImage(context, R.drawable.mid44),
                getImage(context, R.drawable.mid45),
                getImage(context, R.drawable.mid46),
                getImage(context, R.drawable.mid47),
                getImage(context, R.drawable.mid48),
                getImage(context, R.drawable.mid49),
                getImage(context, R.drawable.mid50),
                getImage(context, R.drawable.mid51),
                getImage(context, R.drawable.mid52),
                getImage(context, R.drawable.mid53),
                getImage(context, R.drawable.mid54),
                getImage(context, R.drawable.mid55),
                getImage(context, R.drawable.mid56),
                getImage(context, R.drawable.mid57),
                getImage(context, R.drawable.mid58),
                getImage(context, R.drawable.mid59),
                getImage(context, R.drawable.mid60),
                getImage(context, R.drawable.mid61),
                getImage(context, R.drawable.mid62),
                getImage(context, R.drawable.mid63),
                getImage(context, R.drawable.mid64),
                getImage(context, R.drawable.mid65),
                getImage(context, R.drawable.mid66),
                getImage(context, R.drawable.mid67),
                getImage(context, R.drawable.mid68),
                getImage(context, R.drawable.mid69),
                getImage(context, R.drawable.mid70),
                getImage(context, R.drawable.mid71),
                getImage(context, R.drawable.mid72),
                getImage(context, R.drawable.mid73),
                getImage(context, R.drawable.mid74),
                getImage(context, R.drawable.mid75),
                getImage(context, R.drawable.mid76),
                getImage(context, R.drawable.mid77),
                getImage(context, R.drawable.mid78),
                getImage(context, R.drawable.mid79),
                getImage(context, R.drawable.mid80),
                getImage(context, R.drawable.mid81),
                getImage(context, R.drawable.mid82),
                getImage(context, R.drawable.mid83),
                getImage(context, R.drawable.mid84),
                getImage(context, R.drawable.mid85),
                getImage(context, R.drawable.mid86),
                getImage(context, R.drawable.mid87),
                getImage(context, R.drawable.mid88),
                getImage(context, R.drawable.mid89)
        );
        return mMidAnimList;
    }




    private List<Bitmap> initButtonsUP() {
        List<Bitmap> ButtonsUPList = Arrays.asList(
                getImage(context, R.drawable.buttons_up00),
                getImage(context, R.drawable.buttons_up01),
                getImage(context, R.drawable.buttons_up02),
                getImage(context, R.drawable.buttons_up03),
                getImage(context, R.drawable.buttons_up04),
                getImage(context, R.drawable.buttons_up05),
                getImage(context, R.drawable.buttons_up06),
                getImage(context, R.drawable.buttons_up07),
                getImage(context, R.drawable.buttons_up08),
                getImage(context, R.drawable.buttons_up09),
                getImage(context, R.drawable.buttons_up10));
        return ButtonsUPList;

    }

}

