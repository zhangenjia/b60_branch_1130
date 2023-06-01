package com.adayo.app.camera.trackline.function;

import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.Point.A;
import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.Point.B;
import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.Point.C;
import static com.adayo.app.camera.trackline.loadconfig.LoadConfig.Point.D;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.camera.trackline.interfaces.ICalibrationPoint;
import com.adayo.app.camera.trackline.loadconfig.LoadConfig;
import com.adayo.app.camera.trackline.view.PointView;
import com.adayo.crtrack.IntergePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CalibrationPointImpl implements ICalibrationPoint {
    private static final String TAG = "CalibrationPointImpl";
    private SharedPreferencesUtils mSharedPreferencesUtils;
    private Context mContext;
    private ArrayList<PointView> mPointViews;
    private LoadConfig.Point[] mPoints = {A, B, C, D};
    private volatile static CalibrationPointImpl mCalibrationPointImpl;
    private PointView mMoveView = null;
    private LoadConfig.Point mCurrentPiont = A;
    private IntergePoint[] mScreenPoints;


    private CalibrationPointImpl(Context context, ViewGroup viewGroup) {
        mContext = context;
        mSharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
        initPointViews(viewGroup);
    }


    public static CalibrationPointImpl getInstance(Context context, ViewGroup viewGroup) {
        if (mCalibrationPointImpl == null) {
            synchronized (CalibrationPointImpl.class) {
                if (mCalibrationPointImpl == null) {
                    mCalibrationPointImpl = new CalibrationPointImpl(context, viewGroup);
                }
            }
        }
        return mCalibrationPointImpl;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPointViews(ViewGroup viewGroup) {
        mPointViews = new ArrayList<>();
        for (LoadConfig.Point p : mPoints) {
            PointView pointView = new PointView(mContext);
            pointView.setPoint(p);
            pointView.setVisibility(View.INVISIBLE);
            if (mSharedPreferencesUtils.isCalibration()) {
                mPointViews.add(mSharedPreferencesUtils.getPoint(pointView));
            } else {
                mPointViews.add(pointView);
            }
            viewGroup.addView(pointView);
        }
    }


    @Override
    public boolean getCalibrationStatus() {
        return mSharedPreferencesUtils.isCalibration();
    }


    @Override
    public void saveCalibrationPoint() {
        for (PointView p : mPointViews) {
            mSharedPreferencesUtils.savePoint(p);
        }
    }


    @Override
    public IntergePoint[] getScreenPoints() {
        if (mScreenPoints == null) {
            mScreenPoints = new IntergePoint[mPointViews.size()];
        }
        for (int i = 0; i < mPointViews.size(); i++) {
            if (mScreenPoints[i] == null) {
                mScreenPoints[i] = new IntergePoint();
            }
            mScreenPoints[i].x = (int) mPointViews.get(i).getXPoint();
            Log.d("AdayoCamera", TAG + " - getScreenPoints: " + "getXpoint" + mScreenPoints[i].x);
            mScreenPoints[i].y = (int) mPointViews.get(i).getYPoint();
            Log.d("AdayoCamera", TAG + " - getScreenPoints: " + "getYpoint" + mScreenPoints[i].y);
        }
        return mScreenPoints;
    }

    @Override
    public void moveLeft() {
        for (PointView p : mPointViews) {
            p.moveLeft();
        }
    }

    @Override
    public void moveRight() {
        for (PointView p : mPointViews) {
            p.moveRight();
        }
    }

    @Override
    public void moveUp() {
        for (PointView p : mPointViews) {
            p.moveUp();
        }
    }

    @Override
    public void moveDown() {
        for (PointView p : mPointViews) {
            p.moveDown();
        }
    }

    @Override
    public LoadConfig.Point calibration() {
        mCurrentPiont = A;
        notifyPoint();
        showPointView(true);
        return mCurrentPiont;
    }

    @Override
    public LoadConfig.Point switchPoint() {
        if (mCurrentPiont.code() < 3) {
            mCurrentPiont = LoadConfig.Point.intToPoint(mCurrentPiont.code() + 1);
        } else {
            mCurrentPiont = A;
        }
        notifyPoint();
        return mCurrentPiont;
    }

    @Override
    public void finishCalibration() {
        saveCalibrationPoint();
        mSharedPreferencesUtils.calibrationFinish();
        showPointView(false);
    }

    //清除SP内容
    public void clearSP() {
        mSharedPreferencesUtils.clearSP();
    }

    @Override
    public void cancelCalibration() {
        showPointView(false);
    }

    private void showPointView(boolean show) {
        for (PointView pointView : mPointViews) {
            pointView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void notifyPoint() {
        for (PointView pointView : mPointViews) {
            if (pointView.getPoint() == mCurrentPiont) {
                pointView.setHaveFocus(true);
            } else {
                pointView.setHaveFocus(false);
            }
        }
    }

    private PointView pointDistance(float x, float y) {
        final float currentDiagonal = (float) Math.sqrt(x * x + y * y);
        Collections.sort(mPointViews, new Comparator<PointView>() {
            @Override
            public int compare(PointView pointView1, PointView pointView2) {
                int ret = (int) ((pointView1.getLengthOfDiagonal() - currentDiagonal) - ((pointView2.getLengthOfDiagonal() - currentDiagonal)));
                if (ret > 0) {
                    return 1;
                } else if (ret < 0) {
                    return -1;
                }
                return 0;
            }
        });
        for (int i = 0; i < mPointViews.size(); i++) {
            Log.d("AdayoCamera", TAG + " - pointDistance: " + mPointViews.get(i).getPoint() + mPointViews.get(i).getLengthOfDiagonal());
        }
        return mPointViews.get(0);
    }
}
