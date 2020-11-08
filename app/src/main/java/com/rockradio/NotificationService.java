package com.rockradio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import static com.rockradio.TrackInfo.infoTrack;

public class NotificationService extends Service {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    Notification notification;
    boolean isPause = true;

    private void showNotification(int pos) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Const.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Const.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);


        // состояние после первого нажатия на кнопку play
        if (pos == 0)
        {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.pause_ntf);
        }

        // pos = 1 и pos = 2 состояние после нажатия на кнопку play/pause
        if(pos == 1) {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.pause_ntf);
            if(MainActivity.controlButton != null)
            {
                MainActivity.controlButton.setImageResource(R.drawable.play);
                MainActivity.playingAnimation.setVisibility(View.GONE);
                MainActivity.loadingAnimation.setVisibility(View.VISIBLE);
                MainActivity.controlButton.setVisibility(View.GONE);
                MainActivity.controlIsActivated = true;
            }
        }

        if(pos == 2)
        {
            views.setImageViewResource(R.id.status_bar_play, R.drawable.play_ntf);
            if(MainActivity.controlButton != null)
            {
                MainActivity.controlButton.setImageResource(R.drawable.play);
                MainActivity.playingAnimation.setVisibility(View.GONE);
                MainActivity.loadingAnimation.setVisibility(View.GONE);
                MainActivity.controlButton.setVisibility(View.VISIBLE);
                MainActivity.controlIsActivated = false;
            }
        }

        notification = new Notification.Builder(this).setContentText(infoTrack).build();
        notification.contentView = views;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.drawable.radio;

        notification.contentIntent = pendingIntent;

        // для работы в фоновом режиме
        startForeground(Const.FOREGROUND_SERVICE, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        if (intent.getAction().equals(Const.ACTION.STARTFOREGROUND_ACTION)) {
            isPause = false;
            showNotification(0);
            Player.start(Const.RADIO_PATH, this);
        }
        else if (intent.getAction().equals(Const.ACTION.PLAY_ACTION)) {
            if(!isPause) {
                showNotification(2);
                Player.stop();
                isPause = true;
            }
            else
            {
                showNotification(1);
                isPause = false;
                Player.start(Const.RADIO_PATH, this);
            }
        }
        else if (intent.getAction().equals(Const.ACTION.STOPFOREGROUND_ACTION)) {
            if(MainActivity.controlButton != null)
            {
                MainActivity.controlButton.setImageResource(R.drawable.play);
                MainActivity.playingAnimation.setVisibility(View.GONE);
                MainActivity.loadingAnimation.setVisibility(View.GONE);
                MainActivity.controlButton.setVisibility(View.VISIBLE);
                MainActivity.controlIsActivated = false;
            }
            Player.stop();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }
}