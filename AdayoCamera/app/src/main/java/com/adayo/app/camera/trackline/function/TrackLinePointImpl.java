package com.adayo.app.camera.trackline.function;

import android.util.Log;

import com.adayo.app.camera.trackline.interfaces.ILineView;
import com.adayo.app.camera.trackline.interfaces.ITrackLinePoint;
import com.adayo.app.camera.trackline.loadconfig.bean.CalibrateTrack;
import com.adayo.app.camera.utils.Utils;
import com.adayo.crtrack.FloatPoint;
import com.adayo.crtrack.IntergePoint;
import com.adayo.crtrack.ProxyCover;


public class TrackLinePointImpl implements ITrackLinePoint {
    private static final String TAG = TrackLinePointImpl.class.getSimpleName();
    private static TrackLinePointImpl mInstance;
    //    private ProxyCover mCrtrackProxy = ProxyCover.getInstance();
    private CalibrateTrack mCalibrateTrack;

    private FloatPoint[][] mTyrePoints;
    private FloatPoint[][] mTrackPoints;
    private FloatPoint[][] mStaticTrackPoints;
    private FloatPoint[][] mWarningLines;
    private FloatPoint[][] mStaticWarningLines;
    private FloatPoint[] mCtrlPoints;
    private FloatPoint[] mStaticCtrlPoints;

    private TrackLinePointImpl() {
//        mCrTrack = crTrack;
    }

    public static TrackLinePointImpl getInstance() {
        if (mInstance == null) {
            synchronized (TrackLinePointImpl.class) {
                if (mInstance == null) {
                    mInstance = new TrackLinePointImpl();
                }
            }
        }
        return mInstance;
    }

    public void setCrTrack(CalibrateTrack calibrateTrack) {
        mCalibrateTrack = calibrateTrack;
    }

    @Override
    public FloatPoint[][] getTyrePoints(int angle) {
        int tyreLineCount = mCalibrateTrack.getTrack_line().getXPos().length;
        if (tyreLineCount > 2) {
            if (mTyrePoints == null) {
                mTyrePoints = new FloatPoint[tyreLineCount * 2 - 2][];
            }
            Log.d("AdayoCamera", TAG + " - getTyrePoints: tyreLineCount = " + tyreLineCount + " | mTyrePoints.length =" + mTyrePoints.length);
            for (int i = 1; i < tyreLineCount; i++) {
                mTyrePoints[i - 1] = ProxyCover.getInstance().getTrackLine(-angle,
                        -mCalibrateTrack.getTrack_line().getXPos()[i],
                        mCalibrateTrack.getTrack_line().getYPoss(),
                        mCalibrateTrack.getTrack_line().getDense());
            }

            for (int i = 1; i < tyreLineCount; i++) {
                mTyrePoints[mTyrePoints.length / 2 + i - 1] = ProxyCover.getInstance().getTrackLine(-angle,
                        mCalibrateTrack.getTrack_line().getXPos()[i],
                        mCalibrateTrack.getTrack_line().getYPoss(),
                        mCalibrateTrack.getTrack_line().getDense());
            }

            Log.d("AdayoCamera", TAG + " - getTyrePoints: mTrackPoints: get success | mTrackPoints: length " + mTyrePoints.length);

        } else {
            Log.e("AdayoCamera", TAG + " - getTyrePoints: tyreLineCount <= 2");
        }
        return mTyrePoints;
    }

    @Override
    public FloatPoint[][] getTrackPoints(int angle) {
        if (mTrackPoints == null) {
            mTrackPoints = new FloatPoint[2][];
        }

        mTrackPoints[0] = ProxyCover.getInstance().getTrackLine(-angle,
                -mCalibrateTrack.getTrack_line().getXPos()[0],
                mCalibrateTrack.getTrack_line().getYPoss(),
                mCalibrateTrack.getTrack_line().getDense());
        for (int i = 0; i < mCalibrateTrack.getTrack_line().getYPoss().length; i++) {
            Log.d("AdayoCamera", TAG + " - getTrackPoints: mCalibrateTrack.getTrack_line().getYPoss(): " + mCalibrateTrack.getTrack_line().getYPoss()[i]);
        }


        Log.d("AdayoCamera", TAG + " - getTrackPoints: mTrackPoints: get success ");
        Log.d("AdayoCamera", TAG + " - getTrackPoints: mTrackPoints: length " + mTrackPoints.length);
//        LogUtil.loggerLine("mTrackPoints", mTrackPoints[0]);

        mTrackPoints[1] = ProxyCover.getInstance().getTrackLine(-angle,
                mCalibrateTrack.getTrack_line().getXPos()[0],
                mCalibrateTrack.getTrack_line().getYPoss(),
                mCalibrateTrack.getTrack_line().getDense());
        Log.d("AdayoCamera", TAG + " - getTrackPoints: mTrackPoints: get success ");
        Log.d("AdayoCamera", TAG + " - getTrackPoints: mTrackPoints: length " + mTrackPoints.length);
//        LogUtil.loggerLine("mTrackPoints", mTrackPoints[1]);
        return mTrackPoints;
    }

    @Override
    public FloatPoint[][] getStaticTrackPoints() {
        if (mStaticTrackPoints == null) {
            mStaticTrackPoints = new FloatPoint[2][];
        }
        mStaticTrackPoints[0] = ProxyCover.getInstance().getTrackLine(0,
                -mCalibrateTrack.getTrack_line().getXPos()[0],
                mCalibrateTrack.getTrack_line().getYPoss(),
                mCalibrateTrack.getTrack_line().getDense());
        Log.d("AdayoCamera", TAG + " - getStaticTrackPoints: mStaticTrackPoints: get success ");
        Log.d("AdayoCamera", TAG + " - getStaticTrackPoints: mStaticTrackPoints: length " + mStaticTrackPoints.length);
//        LogUtil.loggerLine("mTrackPoints", mStaticTrackPoints[0]);

        mStaticTrackPoints[1] = ProxyCover.getInstance().getTrackLine(0,
                mCalibrateTrack.getTrack_line().getXPos()[0],
                mCalibrateTrack.getTrack_line().getYPoss(),
                mCalibrateTrack.getTrack_line().getDense());
        Log.d("AdayoCamera", TAG + " - getStaticTrackPoints: mStaticTrackPoints: get success ");
        Log.d("AdayoCamera", TAG + " - getStaticTrackPoints: mStaticTrackPoints: length " + mStaticTrackPoints.length);
//        LogUtil.loggerLine("mStaticTrackPoints", mStaticTrackPoints[1]);
        return mStaticTrackPoints;
    }

    @Override
    public FloatPoint[][] getWarningLines(int angle) {

        if (mWarningLines == null) {
            mWarningLines = new FloatPoint[mCalibrateTrack.getWarning_line().getYPos().length][];
        }
        for (int i = 0; i < mWarningLines.length; i++) {
            mWarningLines[i] = ProxyCover.getInstance().getWarningLine(-angle,
                    mCalibrateTrack.getWarning_line().getYPos()[i],
                    mCalibrateTrack.getWarning_line().getXPoss(),
                    mCalibrateTrack.getWarning_line().getDense());
        }
        return mWarningLines;
    }

    @Override
    public FloatPoint[][] getStaticWarningLines() {
        if (mStaticWarningLines == null) {
            mStaticWarningLines = new FloatPoint[mCalibrateTrack.getWarning_line().getYPos().length][];
        }
        for (int i = 0; i < mStaticWarningLines.length; i++) {
            mStaticWarningLines[i] = ProxyCover.getInstance().getWarningLine(0,
                    mCalibrateTrack.getWarning_line().getYPos()[i],
                    mCalibrateTrack.getWarning_line().getXPoss(),
                    mCalibrateTrack.getWarning_line().getDense());
        }
        return mStaticWarningLines;
    }


    @Override
    public FloatPoint[] getCtrlPoints(int angle) {
        IntergePoint points0[] = new IntergePoint[mCalibrateTrack.getPoints().length];

        for (int i = 0; i < mCalibrateTrack.getPoints().length; i++) {
            IntergePoint point = new IntergePoint();
            point.x = -mCalibrateTrack.getPoints()[i][0];
            point.y = mCalibrateTrack.getPoints()[i][1];
            points0[i] = point;
        }

        if (mCtrlPoints == null) {
            mCtrlPoints = new FloatPoint[2];
        }

        mCtrlPoints = ProxyCover.getInstance().getPoints(mCalibrateTrack.getWarning_line().getAngle(), points0);

        Log.d("AdayoCamera", TAG + " - getCtrlPoints: mCtrlPoints1 = " + Utils.getString(mCtrlPoints));

        IntergePoint points1[] = new IntergePoint[mCalibrateTrack.getPoints().length];

        for (int i = 0; i < mCalibrateTrack.getPoints().length; i++) {
            IntergePoint point = new IntergePoint();
            point.x = -mCalibrateTrack.getPoints()[i][0];
            point.y = mCalibrateTrack.getPoints()[i][1];
            points1[i] = point;
        }
        mCtrlPoints = ProxyCover.getInstance().getPoints(mCalibrateTrack.getWarning_line().getAngle(), points1);
        Log.d("AdayoCamera", TAG + " - getCtrlPoints: mCtrlPoints2 = " + Utils.getString(mCtrlPoints));

        return mCtrlPoints;
    }

    @Override
    public FloatPoint[] getStaticCtrlPoints() {
        IntergePoint points0[] = new IntergePoint[mCalibrateTrack.getPoints().length];
        for (int i = 0; i < mCalibrateTrack.getPoints().length; i++) {
            IntergePoint point = new IntergePoint();
            point.x = -mCalibrateTrack.getPoints()[i][0];
            point.y = mCalibrateTrack.getPoints()[i][1];
            points0[i] = point;
        }

        if (mStaticCtrlPoints == null) {
            mStaticCtrlPoints = new FloatPoint[2];
        }
        mStaticCtrlPoints = ProxyCover.getInstance().getPoints(mCalibrateTrack.getWarning_line().getAngle(), points0);
        Log.d("AdayoCamera", TAG + " - getStaticCtrlPoints: mStaticCtrlPoints1 = " + Utils.getString(mStaticCtrlPoints));

        IntergePoint points1[] = new IntergePoint[mCalibrateTrack.getPoints().length];
        for (int i = 0; i < mCalibrateTrack.getPoints().length; i++) {
            IntergePoint point = new IntergePoint();
            point.x = -mCalibrateTrack.getPoints()[i][0];
            point.y = mCalibrateTrack.getPoints()[i][1];
            points1[i] = point;
        }
        mStaticCtrlPoints = ProxyCover.getInstance().getPoints(mCalibrateTrack.getWarning_line().getAngle(), points1);
        Log.d("AdayoCamera", TAG + " - getStaticCtrlPoints: mStaticCtrlPoints2 = " + Utils.getString(mStaticCtrlPoints));

        return mStaticCtrlPoints;
    }

    @Override
    public int getAngleRange(int value) {
        int angle = 0;
        Log.d("AdayoCamera", TAG + " - getAngleRange() called with: value = [" + value + "]");
        int maxAngle = (int) ProxyCover.getInstance().getAngleRange();
        angle = maxAngle * value / mCalibrateTrack.getMaxSteeringWheel();
        Log.d("AdayoCamera", TAG + " - getAngleRange() returned: " + angle);
        return angle;
    }

    @Override
    public void setILineView(ILineView iLineView) {

    }
}
