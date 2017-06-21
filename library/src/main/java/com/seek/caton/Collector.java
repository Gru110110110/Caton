package com.seek.caton;

/**
 * Created by seek on 2017/6/19.
 */

public interface Collector {

    String[] getStackTraceInfo();

    void add(String stackTrace);
}
