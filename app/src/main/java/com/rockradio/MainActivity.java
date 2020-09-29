package com.rockradio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

import io.github.nikhilbhutani.analyzer.DataAnalyzer;

import static com.rockradio.GetTrackInfo.infoTrack;

public class MainActivity extends AppCompatActivity {

    static String artist, track;

    @SuppressLint("StaticFieldLeak")
    static ImageView background;

    @SuppressLint("StaticFieldLeak")
    static ImageButton control_button;

    @SuppressLint("StaticFieldLeak")
    static TextView title_font;

    @SuppressLint("StaticFieldLeak")
    static TextView info;

    @SuppressLint("StaticFieldLeak")
    static TextView infoSong;

    static CircularSeekBar volumeChanger;

    static AVLoadingIndicatorView playing_animation;
    static AVLoadingIndicatorView loading_animation;

    static boolean controlIsActivated = false;

    @SuppressLint("StaticFieldLeak")
    static DataAnalyzer dataAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initialise();
        setCustomFont();
        startListenVolume();
        new GetTrackInfo().execute();
        startRefreshing();
        startCounting();
    }

    @SuppressLint("CutPasteId")
    void initialise() {
        background = (ImageView) findViewById(R.id.bckg);
        title_font = (TextView) findViewById(R.id.title_tv);
        info = (TextView) findViewById(R.id.info);
        infoSong = (TextView) findViewById(R.id.info_song);
        volumeChanger = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        playing_animation = (AVLoadingIndicatorView) findViewById(R.id.playing_anim);
        playing_animation.setVisibility(View.GONE);
        loading_animation = (AVLoadingIndicatorView) findViewById(R.id.load_animation);
        control_button = (ImageButton) findViewById(R.id.control_button);
        control_button.setOnClickListener(controlButtonListener);
    }

    void setCustomFont() {
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "BadSignal.otf");
        title_font.setTypeface(tf);
    }

    void startListenVolume() {
        volumeChanger.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Player.setVolume((100 - circularSeekBar.getProgress()) / 100f);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

        });
    }

    public static void setSongData() {
        info.setText(infoTrack);
    }

    public void startPlayerService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    public void vibrate() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(Const.VIBRATE_TIME);
    }

    public void startRefreshing()
    {
        dataAnalyzer = new DataAnalyzer(this);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(Const.PHOTO_LOAD_REFRESH_TIME);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new GetTrackInfo().execute();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public void startCounting()
    {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dataAnalyzer!=null) {
                                    Log.e("DATA USAGE", "------");
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    View.OnClickListener controlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!controlIsActivated) {
                startPlayerService();
                control_button.setImageResource(R.drawable.pause);
                playing_animation.setVisibility(View.GONE);
                loading_animation.setVisibility(View.VISIBLE);
                control_button.setVisibility(View.GONE);
                controlIsActivated = true;
            } else {
                Player.stop();
                control_button.setImageResource(R.drawable.play);
                playing_animation.setVisibility(View.GONE);
                loading_animation.setVisibility(View.VISIBLE);
                control_button.setVisibility(View.VISIBLE);
                controlIsActivated = false;
            }
            vibrate();
        }
    };

    @Override
    public void onBackPressed() {
        Player.stop();
        super.onBackPressed();
    }
}
