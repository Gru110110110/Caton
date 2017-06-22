package com.seek.caton;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

import static com.seek.caton.Config.log;

/**
 * Created by seek on 2017/6/20.
 */

public class BlockHandler {

    private static final String TAG = "BlockHandler";
    private Context mContext;
    private Collector mCollector;
    private Caton.Callback mCallback;

    public BlockHandler(Context context, Collector collector, Caton.Callback callback) {
        mContext = context;
        mCollector = collector;
        mCallback = callback;
    }

    public void notifyBlockOccurs(boolean needCheckAnr, long... blockArgs) {
        String[] stackTraces = mCollector.getStackTraceInfo();
        printStackTrace(stackTraces);
        String anr = "";
        if (needCheckAnr) {
            anr = checkAnr();
        }
        if (mCallback != null) {
            mCallback.onBlockOccurs(stackTraces, anr, blockArgs);
        }
    }

    private String checkAnr() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.ProcessErrorStateInfo> errorStateInfos = activityManager.getProcessesInErrorState();
        if (errorStateInfos != null) {
            for (ActivityManager.ProcessErrorStateInfo info : errorStateInfos) {
                if (info.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {
                    StringBuilder anrInfo = new StringBuilder();
                    anrInfo.append(info.processName)
                            .append("\n")
                            .append(info.shortMsg)
                            .append("\n")
                            .append(info.longMsg);
                    log(TAG, anrInfo.toString());
                    return anrInfo.toString();
                }
            }
        }
        return "";
    }

    private void printStackTrace(String[] stackTraces) {
        for (String item : stackTraces) {
            log(TAG, item);
        }
    }
}
