package com.adayo.app.setting.utils.timer;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class DevTimer {

    private final String mTag;
    private final long   mDelay;
    private final long   mPeriod;
    private final int    mTriggerLimit;

    private DevTimer(final Builder builder) {
        mTag          = builder.tag;
        mDelay        = builder.delay;
        mPeriod       = builder.period;
        mTriggerLimit = builder.limit;
    }


    public static final class Builder {

        private String tag;
        private long   delay;
        private long   period;
        private int    limit = -1;

        public Builder(long period) {
            this.period = period;
        }

        public Builder(
                long delay,
                long period
        ) {
            this.delay  = delay;
            this.period = period;
        }

        public Builder(
                long delay,
                long period,
                int limit
        ) {
            this.delay  = delay;
            this.period = period;
            this.limit  = limit;
        }

        public Builder(
                long delay,
                long period,
                int limit,
                String tag
        ) {
            this.delay  = delay;
            this.period = period;
            this.limit  = limit;
            this.tag    = tag;
        }

        public Builder(Builder builder) {
            if (builder != null) {
                tag    = builder.tag;
                delay  = builder.delay;
                period = builder.period;
                limit  = builder.limit;
            }
        }

        public String getTag() {
            return tag;
        }

        public Builder setTag(final String tag) {
            this.tag = tag;
            return this;
        }

        public long getDelay() {
            return delay;
        }

        public Builder setDelay(final long delay) {
            this.delay = delay;
            return this;
        }

        public long getPeriod() {
            return period;
        }

        public Builder setPeriod(final long period) {
            this.period = period;
            return this;
        }

        public int getLimit() {
            return limit;
        }

        public Builder setLimit(final int limit) {
            this.limit = limit;
            return this;
        }

        public DevTimer build() {
            return new DevTimer(this);
        }
    }


    public interface Callback {


        void callback(
                DevTimer timer,
                int number,
                boolean end,
                boolean infinite
        );
    }

    private final int           mUUID          = UUID.randomUUID().hashCode();
    private final AtomicInteger mTriggerNumber = new AtomicInteger();
    private       boolean       mRunning;
    private       boolean       mMarkSweep;
    private       Handler       mHandler;
    private       Callback      mCallback;
    private       Timer         mTimer;
    private       TimerTask     mTimerTask;


    public String getTag() {
        return mTag;
    }


    public int getUUID() {
        return mUUID;
    }


    public long getDelay() {
        return mDelay;
    }


    public long getPeriod() {
        return mPeriod;
    }


    public boolean isRunning() {
        return mRunning;
    }


    public boolean isMarkSweep() {
        return mMarkSweep;
    }


    public int getTriggerNumber() {
        return mTriggerNumber.get();
    }


    public int getTriggerLimit() {
        return mTriggerLimit;
    }


    public boolean isTriggerEnd() {
        return (mTriggerLimit >= 0 && mTriggerNumber.get() >= mTriggerLimit);
    }


    public boolean isInfinite() {
        return (mTriggerLimit <= -1);
    }


    public DevTimer setHandler(final Handler handler) {
        mHandler = handler;
        return this;
    }


    public DevTimer setCallback(final Callback callback) {
        mCallback = callback;
        return this;
    }


    public DevTimer start() {
        mMarkSweep = false;
        TimerManager.addContainsChecker(this);
        return startTimer();
    }


    public DevTimer stop() {
        mMarkSweep = true;
        return cancelTimer();
    }


    private DevTimer startTimer() {
        cancelTimer();
        mRunning = true;
        mTriggerNumber.set(0);
        mTimer = new Timer(); mTimerTask = new java.util.TimerTask() {
            @Override
            public void run() {
                mRunning = true;
                int _number = mTriggerNumber.incrementAndGet();
                boolean _end = isTriggerEnd();
                boolean _infinite = isInfinite();
                if (_end) {
                    stop();
                }

                if (mCallback != null) {
                    if (mHandler != null) {
                        mHandler.post(() -> mCallback.callback(
                                DevTimer.this, _number, _end, _infinite));
                    } else {
                        mCallback.callback(DevTimer.this, _number, _end, _infinite);
                    }
                }
            }
        };
        try {
            mTimer.schedule(mTimerTask, mDelay, mPeriod);
        } catch (Exception e) {
            mRunning = false;
            stop(); }
        return this;
    }


    private DevTimer cancelTimer() {
        mRunning = false;
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
        } catch (Exception ignored) {
        }
        return this;
    }
}