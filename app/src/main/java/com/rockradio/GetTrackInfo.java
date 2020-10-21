package com.rockradio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

import static com.rockradio.NetworkState.isOnline;
import static com.rockradio.NotificationService.context;

public class GetTrackInfo extends AsyncTask<Void, Void, Void> {
    public static String firstInfo;
    public static String infoTrack;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public static boolean isOnline()
    {
        ConnectivityManager cm;
        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        return isConnected;
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