# Caton
Caton是一个android卡顿监测模块。当UI线程卡顿（得不到执行、无反应）达到预定阈值时，将把卡顿期间线程堆栈打印出来，以便开发人员分析和优化App的性能。
Caton由于本身有个收集线程堆栈的后台线程工作，所以会带来一定的性能消耗，这个大概在百分之2%左右。
# Usage
默认情况下，你在Application的onCreate方法中这样写就可以。
<pre><code>
Caton.initialize(this);//default
</pre></code>
</br >
你也可以使用Builder来自定义
<pre><code>
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
                        //这里你可以把卡顿信息上传到自己服务器
                    }
                });
  Caton.initialize(builder);
</pre></code>
1. 监测模式</br>
</br>
监测模式有两种:</br>
</br><li>Caton.MonitorMode.FRAME</li></br>
这种模式是通过监测绘制帧间隔时间来判断是否卡顿。也就是给Choreographer设置FrameCallback的方式。这种方式只能在API 16上才能使用，否则默认使用LOOPER模式。</br>
</br><li>Caton.MonitorMode.LOOPER</li></br>
这种模式是通过监测主线程消息处理时间来判断。也就是给主线程Looper设置Printer，来计算消息处理开始前和处理后的时间间隔判断。
</br>
2.结构原理图</br>
![](https://github.com/pruas/Caton/raw/master/caton_design.png)
</br>
3.测试。</br>
我们人为在MainActivity中制造卡顿:</br>
<pre><code>
 public void pause(View view){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
</pre></code>
logcat将打印出如下图：</br>
![](https://github.com/pruas/Caton/raw/master/caton_log.png)
</br>
4.好了，现在你自己去测试吧！
