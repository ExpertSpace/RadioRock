package com.rockradio;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

import static com.google.android.exoplayer.ExoPlayer.STATE_READY;
import static com.rockradio.MainActivity.controlButton;
import static com.rockradio.MainActivity.playingAnimation;
import static com.rockradio.MainActivity.toast;
import static com.rockradio.MainActivity.vibrate;
import static com.rockradio.NetworkState.isOnline;

public class Player {
    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;

    // функция для старта плеера
    public static void start(String URL, final Context context)
    {
        if(exoPlayer != null)
        {
            exoPlayer.stop();
        }

        // Объявление URI со ссылкой на аудиопоток и извлечение его потокового формата
        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(context, URI, null);

        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);

        // инициализация плеера
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        exoPlayer.prepare(audioRenderer);

        // начать проигрывание после окончания буферизации
        exoPlayer.setPlayWhenReady(true);

        // при нажатии на кнопку play
        toast = Toast.makeText(context.getApplicationContext(), "Идет буферизация...",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 130);
        toast.show();

        // регистрация состояния плеера
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(!isOnline(this))
                {
                    toast = Toast.makeText(context.getApplicationContext(), "Нет интернета",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 130);
                    toast.show();

                    Player.stop();
                    controlButton.setImageResource(R.drawable.play);
                    playingAnimation.setVisibility(View.GONE);

                    vibrate(context);
                }
                else
                {
                    if(playbackState == STATE_READY)
                    {
                        playingAnimation.setVisibility(View.VISIBLE);
                        MainActivity.loadingAnimation.setVisibility(View.GONE);
                        controlButton.setVisibility(View.VISIBLE);
                        controlButton.setImageResource(R.drawable.pause);
                    }
                }
            }

            // когда все готово для воспроизведения информируем
            @Override
            public void onPlayWhenReadyCommitted() {
                toast = Toast.makeText(context.getApplicationContext(), "Все готово, Вы можете слушать...",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 130);
                toast.show();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
    }

    public static void stop()
    {
        if(exoPlayer != null) {
            exoPlayer.stop();
        }
    }

    // функция, которой можно остановить плеер
    public static void setVolume(float volume)
    {
        if(exoPlayer != null) {
            exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
        }
    }
}
