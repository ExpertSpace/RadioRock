package com.rockradio;

public class Const {
    public final static String RADIO_PATH =
            "https://air.radiorecord.ru:805/rock_320";

    public final static int VIBRATE_TIME = 5;

    public interface ACTION {
        String MAIN_ACTION = "com.tockabillyradio.action.main";
        String PLAY_ACTION = "com.tockabillyradio.action.play";
        String STARTFOREGROUND_ACTION = "com.tockabillyradio.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.tockabillyradio.action.stopforeground";
    }

    public static int FOREGROUND_SERVICE = 101;
}
