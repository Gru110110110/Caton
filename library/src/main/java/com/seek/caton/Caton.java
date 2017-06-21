package com.seek.caton;

import android.content.Context;
import android.os.Build;
import android.view.Choreographer;

/**
 * Created by seek on 2017/6/19.
 */

public class Caton {
    private static Caton sCaton;
    private UILooperObserver mLooperObserver;
    final static long DEFAULT_THRESHOLD_TIME = 3000;
    final static long DEFAULT_COLLECT_INTERVAL = 1000;
    final static long MIN_THRESHOLD_TIME = 500;
    final static long MIN_COLLECT_INTERVAL = 500;
    static MonitorMode DEFAULT_MODE = MonitorMode.LOOPER;

    private Caton(Context context, long thresholdTime, long collectInterval, MonitorMode mode, boolean loggingEnabled, Callback callback) {
        long mThresholdTime = thresholdTime < MIN_THRESHOLD_TIME ? MIN_THRESHOLD_TIME : thresholdTime;
        long mCollectInterval = collectInterval < MIN_COLLECT_INTERVAL ? MIN_COLLECT_INTERVAL : collectInterval;
        Config.LOG_ENABLED = loggingEnabled;
        Config.THRESHOLD_TIME = mThresholdTime;
        Collector mTraceCollector = new StackTraceCollector(mCollectInterval);
        BlockHandler mBlockHandler = new BlockHandler(context, mTraceCollector, callback);
        if (mode == MonitorMode.LOOPER) {
            mLooperObserver = new UILooperObserver(mBlockHandler);
        } else if (mode == MonitorMode.FRAME) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                FPSFrameCallBack fpsFrameCallBack = new FPSFrameCallBack(context, mBlockHandler);
                Choreographer.getInstance().postFrameCallback(fpsFrameCallBack);
            } else {
                mLooperObserver = new UILooperObserver(mBlockHandler);
            }
        }
    }

    public static void initialize(Context context) {
        initialize(new Builder(context));
    }

    public static void initialize(Builder builder) {
        if (sCaton == null) {
            synchronized (Caton.class) {
                if (sCaton == null) {
                    sCaton = builder.build();
                }
            }
        }
    }


    public static class Builder {
        private long mThresholdTime = DEFAULT_THRESHOLD_TIME;
        private long mCollectInterval = DEFAULT_COLLECT_INTERVAL;
        private Context mContext;
        private MonitorMode mMonitorMode = DEFAULT_MODE;
        private boolean loggingEnabled = false;
        private Callback mCallback;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder thresholdTime(long thresholdTimeMillis) {
            mThresholdTime = thresholdTimeMillis;
            return this;
        }

        public Builder collectInterval(long collectIntervalMillis) {
            mCollectInterval = collectIntervalMillis;
            return this;
        }

        public Builder monitorMode(MonitorMode mode) {
            this.mMonitorMode = mode;
            return this;
        }

        public Builder loggingEnabled(boolean enable) {
            loggingEnabled = enable;
            return this;
        }

        public Builder callback(Callback callback) {
            mCallback = callback;
            return this;
        }

        Caton build() {
            return new Caton(mContext, mThresholdTime, mCollectInterval, mMonitorMode, loggingEnabled, mCallback);
        }
    }

    public enum MonitorMode {
        LOOPER(0), FRAME(1);
        int mode;

        MonitorMode(int mode) {
            this.mode = mode;
        }
    }

    public interface Callback {
        void onBlockOccurs(String[] stackTraces, String anr, long... blockArgs);
    }
}
