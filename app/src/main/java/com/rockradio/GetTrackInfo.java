package com.rockradio;

import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class GetTrackInfo extends AsyncTask<Void, Void, Void> {
    public static String firstInfo;
    public static String infoTrack;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Document doc = null;
        try {
            doc = Jsoup.connect(Const.TRACK_INFO_URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstInfo = doc.select("body").text();
        infoTrack =  firstInfo.replaceAll("[0-9]+", "");
        infoTrack =  infoTrack.replaceAll("[()]+", "");
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        MainActivity.setSongData();
    }
}