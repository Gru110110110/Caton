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
                .monitorMode(Caton.MonitorMode.FRAME)//默认监测模式为Caton.MonitorMode.LOOPER，这样指定Caton.MonitorMode.FRAME
                .loggingEnabled(true)// 是否打印log
                .collectInterval(1000) //监测采集堆栈时间间隔
                .thresholdTime(2000) // 触发卡顿时间阈值
                .callback(new Caton.Callback() { //设置触发卡顿时回调
                    @Override
                    public void onBlockOccurs(String[] stackTraces, String anr, long... blockArgs) {
                        // stackTraces : 收集到的堆栈，以便分析卡顿原因。 anr : 如果应用发生ANR，这个就我ANR相关信息，没发生ANR，则为空。
                        //采用Caton.MonitorMode.FRAME模式监测时，blockArgs的size为1，blockArgs[0] 即是发生掉帧的数。
                        //采用Caton.MonitorMode.LOOPER模式监测时，blockArgs的size为2，blockArgs[0] 为UI线程卡顿时间值，blockArgs[1]为在此期间UI线程能执行到的时间。
                        //这里你可以卡顿信息上传到自己服务器
                    }
                });
        Caton.initialize(builder);
    }
}
