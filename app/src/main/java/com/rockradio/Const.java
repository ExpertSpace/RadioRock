package com.rockradio;

public class Const {
    public static String RADIO_PATH =
            "http://lin3.ash.fast-serv.com:6026/stream_96";

    public static String TRACK_INFO_URL =
            "https://rr.streamanalyst.com/RR.update.php?d=1601396696614.html";

    public static int PHOTO_LOAD_REFRESH_TIME = 20000;

    public final static int VIBRATE_TIME = 5;

    public interface ACTION {
        String MAIN_ACTION = "com.tockabillyradio.action.main";
        String PLAY_ACTION = "com.tockabillyradio.action.play";
        String STARTFOREGROUND_ACTION = "com.tockabillyradio.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.tockabillyradio.action.stopforeground";
    }

    public static int FOREGROUND_SERVICE = 101;
}
