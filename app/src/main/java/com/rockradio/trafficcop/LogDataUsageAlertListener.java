package com.rockradio.trafficcop;

import android.util.Log;

public class LogDataUsageAlertListener implements DataUsageAlertListener {
    private static final String TAG = "DataUsageWarning";

    private String tag;

    public LogDataUsageAlertListener() {
        this.tag = TAG;
    }

    public LogDataUsageAlertListener(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag cannot be null");
        }
        this.tag = tag;
    }

    @Override
    public void alertThreshold(Threshold threshold, DataUsage dataUsage) {
        Log.e(tag, dataUsage.getWarningMessage());
    }
}
