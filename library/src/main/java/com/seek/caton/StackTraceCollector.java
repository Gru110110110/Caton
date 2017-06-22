package com.seek.caton;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.ArrayDeque;
import java.util.Map;

/**
 * Created by seek on 2017/6/19.
 */

public class StackTraceCollector implements Collector {
    private final static String TAG = "StackTraceCollector";
    private final static String THREAD_TAG = "------";
    private final static int COLLECT_MSG = 0x0037;
    private final static int COLLECT_SPACE_TIME = 5000;
    private final static int MIN_COLLECT_COUNT = 5;
    private long mCollectInterval;
    private volatile Looper mLooper;
    private volatile CollectorHandler mCollectorHandler;
    private ArrayDeque<String> mStackQueue;
    private int mLimitLength;

    public StackTraceCollector(long collectInterval) {
        mCollectInterval = collectInterval;
        HandlerThread thread = new HandlerThread(TAG);
        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mLooper = thread.getLooper();
        mCollectorHandler = new CollectorHandler(mLooper);
        int space = (int) (COLLECT_SPACE_TIME / mCollectInterval);
        mLimitLength = space <= MIN_COLLECT_COUNT ? MIN_COLLECT_COUNT : space;
        mStackQueue = new ArrayDeque<>(mLimitLength);
        trigger();
    }

    public void trigger() {
        Message message = mCollectorHandler.obtainMessage();
        message.obj = this;
        message.what = COLLECT_MSG;
        mCollectorHandler.sendMessageDelayed(message, mCollectInterval);
    }

    @Override
    public String[] getStackTraceInfo() {
        return mStackQueue.toArray(new String[0]);
    }

    @Override
    public void add(String stackTrace) {
        if (mStackQueue.size() >= mLimitLength) {
            mStackQueue.poll();
        }
        mStackQueue.offer(stackTrace);
    }


    private static class CollectorHandler extends Handler {

        public CollectorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COLLECT_MSG) {
                StackTraceCollector traceCollector = (StackTraceCollector) msg.obj;
                traceCollector.add(traceCollector.getAllStackInfo());
                traceCollector.trigger();
            }
        }
    }

    private String getAllStackInfo() {
        Thread main = Looper.getMainLooper().getThread();
        Map<Thread, StackTraceElement[]> allLiveThreadStackMap = main.getAllStackTraces();
        StringBuilder stackBuilder = new StringBuilder(128);
        for (Thread item : allLiveThreadStackMap.keySet()) {
            StackTraceElement[] stackTraceElements = item.getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                stackBuilder.append(THREAD_TAG).append(item.getName()).append("\n");
                for (StackTraceElement stackTraceElement : stackTraceElements) {
                    stackBuilder.append("\tat ").append(stackTraceElement.toString()).append("\n");
                }
            }
        }
        return stackBuilder.toString();
    }

}
