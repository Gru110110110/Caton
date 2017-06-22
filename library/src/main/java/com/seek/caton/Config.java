package com.seek.caton;

import android.util.Log;

/**
 * Created by seek on 2017/6/20.
 */

public class Config {
    public static long THRESHOLD_TIME = 0;
    public static boolean LOG_ENABLED = true;

    public static void log(String tag, String msg) {
        if (LOG_ENABLED) {
            Log.e(tag, msg);
        }
    }
}
