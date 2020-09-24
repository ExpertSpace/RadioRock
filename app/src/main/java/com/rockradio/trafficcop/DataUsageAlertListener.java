package com.rockradio.trafficcop;

public interface DataUsageAlertListener {
    void alertThreshold(Threshold threshold, DataUsage dataUsage);
}
