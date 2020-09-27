package com.rockradio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import io.github.nikhilbhutani.analyzer.DataAnalyzer;

public class MainActivity extends AppCompatActivity {

    static ImageView background;
    static ImageButton control_button;

    static TextView title_tv;

    static CircularSeekBar volumeChanger;

    static AVLoadingIndicatorView playing_animation;
    static AVLoadingIndicatorView loading_animation;

    static boolean controlIsActivated = false;

    static DataAnalyzer dataAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
        setCustomFont();
        startListenVolume();
        startRefreshing();
        startCounting();
    }

    void initialise() {
        background = (ImageView) findViewById(R.id.bckg);
        title_tv = (TextView) findViewById(R.id.title_tv);
        volumeChanger = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        playing_animation = (AVLoadingIndicatorView) findViewById(R.id.playing_anim);
        playing_animation.setVisibility(View.GONE);
        loading_animation = (AVLoadingIndicatorView) findViewById(R.id.load_animation);
        control_button = (ImageButton) findViewById(R.id.control_button);
        control_button.setOnClickListener(controlButtonListener);
    }

    void setCustomFont() {
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "BadSignal.otf");
        title_tv.setTypeface(tf);
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
                                //new GetTrackInfo().execute();
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
