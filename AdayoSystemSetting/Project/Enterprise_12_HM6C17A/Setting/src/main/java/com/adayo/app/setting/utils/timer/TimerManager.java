package com.adayo.app.setting.utils.timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;




public final class TimerManager {

    private TimerManager() {
    }

    private static final String TAG = TimerManager.class.getSimpleName();

    protected static final List<DevTimer> mTimerLists = Collections.synchronizedList(new ArrayList<>());


    protected static void addContainsChecker(final DevTimer timer) {
        synchronized (mTimerLists) {
            if (!mTimerLists.contains(timer)) {
                mTimerLists.add(timer);
            }
        }
    }


    public static int getSize() {
        return mTimerLists.size();
    }


    public static void recycle() {
        synchronized (mTimerLists) {
            try {
                Iterator<DevTimer> iterator = mTimerLists.iterator();
                while (iterator.hasNext()) {
                    DevTimer timer = iterator.next();
                    if (timer == null || timer.isMarkSweep()) {
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "recycle");
            }
        }
    }


    public static DevTimer getTimer(final String tag) {
        if (tag != null) {
            synchronized (mTimerLists) {
                try {
                    for (DevTimer timer : mTimerLists) {
                        if (timer != null && tag.equals(timer.getTag())) {
                            return timer;
                        }
                    }
                } catch (Exception e) {
                    LogPrintUtils.eTag(TAG, e, "getTimer");
                }
            }
        }
        return null;
    }


    public static DevTimer getTimer(final int uuid) {
        synchronized (mTimerLists) {
            try {
                for (DevTimer timer : mTimerLists) {
                    if (timer != null && uuid == timer.getUUID()) {
                        return timer;
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "getTimer");
            }
        }
        return null;
    }


    public static List<DevTimer> getTimers(final String tag) {
        List<DevTimer> lists = new ArrayList<>();
        if (tag != null) {
            synchronized (mTimerLists) {
                try {
                    for (DevTimer timer : mTimerLists) {
                        if (timer != null && tag.equals(timer.getTag())) {
                            lists.add(timer);
                        }
                    }
                } catch (Exception e) {
                    LogPrintUtils.eTag(TAG, e, "getTimers");
                }
            }
        }
        return lists;
    }


    public static List<DevTimer> getTimers(final int uuid) {
        List<DevTimer> lists = new ArrayList<>();
        synchronized (mTimerLists) {
            try {
                for (DevTimer timer : mTimerLists) {
                    if (timer != null && uuid == timer.getUUID()) {
                        lists.add(timer);
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "getTimers");
            }
        }
        return lists;
    }


    public static void closeAll() {
        synchronized (mTimerLists) {
            try {
                Iterator<DevTimer> iterator = mTimerLists.iterator();
                while (iterator.hasNext()) {
                    DevTimer timer = iterator.next();
                    if (timer != null) {
                        timer.stop();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "closeAll");
            }
        }
    }


    public static void closeAllNotRunning() {
        synchronized (mTimerLists) {
            try {
                Iterator<DevTimer> iterator = mTimerLists.iterator();
                while (iterator.hasNext()) {
                    DevTimer timer = iterator.next();
                    if (timer != null && !timer.isRunning()) {
                        timer.stop();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "closeAllNotRunning");
            }
        }
    }


    public static void closeAllInfinite() {
        synchronized (mTimerLists) {
            try {
                Iterator<DevTimer> iterator = mTimerLists.iterator();
                while (iterator.hasNext()) {
                    DevTimer timer = iterator.next();
                    if (timer != null && timer.isInfinite()) {
                        timer.stop();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "closeAllInfinite");
            }
        }
    }


    public static void closeAllTag(final String tag) {
        if (tag != null) {
            synchronized (mTimerLists) {
                try {
                    Iterator<DevTimer> iterator = mTimerLists.iterator();
                    while (iterator.hasNext()) {
                        DevTimer timer = iterator.next();
                        if (timer != null && tag.equals(timer.getTag())) {
                            timer.stop();
                            iterator.remove();
                        }
                    }
                } catch (Exception e) {
                    LogPrintUtils.eTag(TAG, e, "closeAllTag");
                }
            }
        }
    }


    public static void closeAllUUID(final int uuid) {
        synchronized (mTimerLists) {
            try {
                Iterator<DevTimer> iterator = mTimerLists.iterator();
                while (iterator.hasNext()) {
                    DevTimer timer = iterator.next();
                    if (timer != null && uuid == timer.getUUID()) {
                        timer.stop();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "closeAllUUID");
            }
        }
    }


    public static void startTimer(final DevTimer timer) {
        if (timer != null) {
            timer.start();
        }
    }


    public static void stopTimer(final DevTimer timer) {
        if (timer != null) {
            timer.stop();
        }
    }
}