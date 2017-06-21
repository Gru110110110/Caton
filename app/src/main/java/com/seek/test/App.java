package com.seek.test;

import android.app.Application;

import com.seek.caton.Caton;

/**
 * Created by seek on 2017/6/21.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Caton.initialize(this);//default
        // use builder build your custom way
        Caton.Builder builder = new Caton.Builder(this)
                .monitorMode(Caton.MonitorMode.FRAME)
                .loggingEnabled(true);
        Caton.initialize(builder);
    }
}
