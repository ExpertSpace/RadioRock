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

import static com.rockradio.MainActivity.toast;
import static com.rockradio.MainActivity.vibrate;
import static com.rockradio.NetworkState.isOnline;

public class Player {
    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;

    public static void start(String URL, final Context context)
    {
        if(exoPlayer != null)
        {
            exoPlayer.stop();
        }

        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(context,URI, null);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);

        toast = Toast.makeText(context.getApplicationContext(), "Идет буферизация...",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 130);
        toast.show();

        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(!isOnline(this))
                {
                    toast = Toast.makeText(context.getApplicationContext(), "Нет интернета",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 130);
                    toast.show();
                    vibrate(context);
                }
                else
                {
                    if(playbackState == 4)
                    {
                        MainActivity.playingAnimation.setVisibility(View.VISIBLE);
                        MainActivity.loadingAnimation.setVisibility(View.GONE);
                        MainActivity.controlButton.setVisibility(View.VISIBLE);
                        MainActivity.controlButton.setImageResource(R.drawable.pause);
                    }
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                toast = Toast.makeText(context.getApplicationContext(), "Идет буферизация...",
                        Toast.LENGTH_SHORT);
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

    public static void setVolume(float volume)
    {
        if(exoPlayer != null) {
            exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
        }
    }
}
