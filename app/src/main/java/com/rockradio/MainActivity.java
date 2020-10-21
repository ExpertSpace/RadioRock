package com.rockradio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import io.github.nikhilbhutani.analyzer.DataAnalyzer;

import static com.rockradio.GetTrackInfo.infoTrack;
import static com.rockradio.NetworkState.isOnline;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static ImageView background;

    @SuppressLint("StaticFieldLeak")
    static ImageButton controlButton;

    @SuppressLint("StaticFieldLeak")
    static TextView titleFont;

    @SuppressLint("StaticFieldLeak")
    static TextView infoSong;

    static Toast toast;

    static CircularSeekBar volumeChanger;

    // Анимация в нижней части экрана при загрузке потока
    static AVLoadingIndicatorView playingAnimation;

    // Анимация загрузки потока в центре экрана
    static AVLoadingIndicatorView loadingAnimation;

    //Проверка того, активирована ли кнопка для воспроизведения/паузы
    static boolean controlIsActivated = false;
    @SuppressLint("StaticFieldLeak")
    static DataAnalyzer dataAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initialise();
        if(!isOnline(this))
        {
            toast = Toast.makeText(this, "Нет интернета",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 130);
            toast.show();
        }
        else
        {
            setCustomFont();
            startListenVolume();
            new GetTrackInfo().execute();
            startRefreshing();
        }
    }

    // Инициализация всех view элементов
    @SuppressLint("CutPasteId")
    void initialise() {
        background = (ImageView) findViewById(R.id.bckg);
        titleFont = (TextView) findViewById(R.id.title_tv);
        infoSong = (TextView) findViewById(R.id.info_song);
        volumeChanger = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        playingAnimation = (AVLoadingIndicatorView) findViewById(R.id.playing_anim);
        playingAnimation.setVisibility(View.GONE);
        loadingAnimation = (AVLoadingIndicatorView) findViewById(R.id.load_animation);
        controlButton = (ImageButton) findViewById(R.id.control_button);
        controlButton.setOnClickListener(controlButtonListener);
    }

    // Функция кастомного шрифта
    void setCustomFont() {
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "BadSignal.otf");
        titleFont.setTypeface(tf);
    }

    // Функция для прослушивания и изменения громкости от панели поиска до плеера
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

    // Фунция для установки названия трека и имя исполнителя
    public static void setSongData() {
        infoSong.setText(infoTrack);
    }

    // Фунукция для фонового воспроизведения звука
    public void startPlayerService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    // Функция для вибрации, если сеть упала/восстановилась
    public static void vibrate(Context c) {
        Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(Const.VIBRATE_TIME);
        }
    }

    // Обновление данных о треке
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
                                if(isOnline())
                                {
                                    new GetTrackInfo().execute();
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
                controlButton.setImageResource(R.drawable.pause);
                playingAnimation.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);
                controlButton.setVisibility(View.GONE);
                controlIsActivated = true;

            } else {
                Player.stop();
                controlButton.setImageResource(R.drawable.play);
                playingAnimation.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.VISIBLE);
                controlButton.setVisibility(View.VISIBLE);
                controlIsActivated = false;
            }
        }
    };

    @Override
    public void onBackPressed() {
        Player.stop();
        super.onBackPressed();
    }
}
