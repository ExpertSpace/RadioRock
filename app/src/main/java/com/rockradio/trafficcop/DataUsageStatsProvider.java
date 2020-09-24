package com.rockradio.trafficcop;

public interface DataUsageStatsProvider {
    public long getNanoTime();
    public long getBytesTransmitted();
    public long getBytesReceived();
}
