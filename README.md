[![](https://jitpack.io/v/pruas/Caton.svg)](https://jitpack.io/#pruas/Caton)
# Caton
Caton是一个android卡顿监测模块。当UI线程卡顿（得不到执行、无反应）达到预定阈值时，将把卡顿期间线程堆栈打印出来，以便开发人员分析和优化App的性能。
Caton由于本身有个收集线程堆栈的后台线程工作，所以会带来一定的性能消耗，这个大概在百分之2%-3%左右。设置收集堆栈时间越小，消耗越大。触发卡顿时间范围是500ms\~4000ms,默认为3000ms;收集时间间隔范围为500ms\~2000ms，默认为1000ms。注意：自定义时，不要把收集时间间隔设置大于触发卡顿时间间隔。
# Usage
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.pruas:Caton:v1.0.1'
}
```
默认情况下，你在Application的onCreate方法中这样写就可以。
```java
  Caton.initialize(this);//default
```

你也可以使用Builder来自定义

```java
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
```

1、监测模式

监测模式有两种:

<li>Caton.MonitorMode.FRAME</li>
这种模式是通过监测绘制帧间隔时间来判断是否卡顿。也就是给Choreographer设置FrameCallback的方式。这种方式只能在API 16上才能使用，否则默认使用LOOPER模式。

<li>Caton.MonitorMode.LOOPER</li>
这种模式是通过监测主线程消息处理时间来判断。也就是给主线程Looper设置Printer，来计算消息处理开始前和处理后的时间间隔判断。

2、结构原理图

![](https://github.com/pruas/Caton/blob/master/caton_design.png)

3、测试。

我们人为在MainActivity中制造卡顿:
```java
 public void pause(View view){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```
logcat将打印出如下图:

![](https://github.com/pruas/Caton/blob/master/caton_log.png)


4、好了，是时候去测试一把了！

