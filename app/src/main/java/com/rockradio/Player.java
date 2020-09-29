package com.rockradio;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

import static com.rockradio.MainActivity.info;

public class Player {

    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;

    public static void start(String URL, Context context)
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
        info.setText("Идет буферизация...");
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == 4)
                {
                    MainActivity.playing_animation.setVisibility(View.VISIBLE);
                    MainActivity.loading_animation.setVisibility(View.GONE);
                    MainActivity.control_button.setVisibility(View.VISIBLE);
                    MainActivity.control_button.setImageResource(R.drawable.pause);
                    info.setText("");
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                info.setText("Идет буферизация...");
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
