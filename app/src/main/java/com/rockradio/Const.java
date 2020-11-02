package com.rockradio;

public class Const {
    public static String RADIO_PATH =
            "http://lin3.ash.fast-serv.com:6026/stream_96";

    public static String TRACK_INFO_URL =
            "https://rr.streamanalyst.com/RR.update.php?d=1601396696614.html";

    public static int INFO_LOAD_REFRESH_TIME = 300000;

    public final static int VIBRATE_TIME = 5;

    public interface ACTION {
        String MAIN_ACTION = "MAIN_ACTION";
        String PLAY_ACTION = "PLAY_ACTION";
        String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
        String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";
    }

    public static int FOREGROUND_SERVICE = 101;
}
