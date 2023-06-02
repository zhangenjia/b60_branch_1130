package com.adayo.app.setting.view.popwindow.harman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import com.adayo.app.base.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.adayo.app.setting.view.popwindow.harman.AnimManager.STATE.START;

public class AnimManager {
    private final static String TAG = AnimManager.class.getSimpleName();
    private int mrefreshFrameRate = 30;

    public static enum STATE {
        START, RUNNING, END;
    }

    private HashMap<AnimDrawBean, STATE> mAnimDrawBeans = new HashMap<>();
    private SurfaceHolder mholder;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    public boolean mflag;
    private List<AnimDrawBean> readyanimDrawBeanList = new ArrayList<>();
    private DrawThread mDrawThread;


    public AnimManager(SurfaceHolder holder) {
        mholder = holder;

    }

    public void addAnimDrawBean(AnimDrawBean animDrawBean) {
        readyanimDrawBeanList.add(animDrawBean);
    }


    public HashMap<AnimDrawBean, STATE> getAnimDrawBeans() {
        return mAnimDrawBeans;
    }

    public void start() {
        Datathread mDatathread = new Datathread();
        mExecutorService.execute(mDatathread);
    }

    public void setMrefreshFrameRate(int mrefreshFrameRate) {
        this.mrefreshFrameRate = mrefreshFrameRate;
    }

    public void cancel() {
        mflag = false;
        synchronized (mholder) {
            readyanimDrawBeanList.clear();
        }
    }


    public class DrawThread extends Thread implements Runnable {
        private SurfaceHolder holder;


        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
        }


        @Override
        public void run() {
            LogUtil.debugD(TAG, "RUN");
            try {
                while (mflag) {
                    synchronized (holder) {
                        int i = 0;
                        for (Map.Entry<AnimDrawBean, STATE> entry : mAnimDrawBeans.entrySet()) {
                            if (entry.getValue() == STATE.END) {
                                i = i + 1;
                            }
                        }
                        if (i == mAnimDrawBeans.size()) {
                            mflag = false;
                            return;
                        }
                        Canvas canvas = holder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        for (Map.Entry<AnimDrawBean, STATE> entry : mAnimDrawBeans.entrySet()) {
                            if (entry.getValue() == STATE.END) {
                                continue;
                            }

                            entry.setValue(STATE.RUNNING);
                            entry.getKey().drawBitmapAnim(canvas);
                        }

                        holder.unlockCanvasAndPost(canvas);
                        for (Map.Entry<AnimDrawBean, STATE> entry : mAnimDrawBeans.entrySet()) {
                            if (entry.getKey().getCount() >= entry.getKey().getAnimPictureNumber()) {
                                if (!entry.getKey().isLoop()) {
                                    entry.setValue(STATE.END);
                                }
                                entry.getKey().setStartCount(entry.getKey().getStartCount());

                            }
                        }

                        SystemClock.sleep(mrefreshFrameRate);
                    }
                }
                LogUtil.debugD(TAG, "CANCEL");
                for (Map.Entry<AnimDrawBean, STATE> entry : mAnimDrawBeans.entrySet()) {
                    entry.getKey().setStartCount(entry.getKey().getStartCount());
                    entry.setValue(STATE.END);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Datathread extends Thread implements Runnable {
        @Override
        public void run() {
            synchronized (mholder) {
                LogUtil.debugD(TAG, "Datathread");
                for (int i = 0; i < readyanimDrawBeanList.size(); i++) {
                    mAnimDrawBeans.put(readyanimDrawBeanList.get(i), START);
                }
                readyanimDrawBeanList.clear();
                mflag = true;
                mDrawThread = new DrawThread(mholder);
                mExecutorService.execute(mDrawThread);

            }
        }
    }
}
