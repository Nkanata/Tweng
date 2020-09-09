package com.example.tweng.Player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {
    MediaPlayer player;
    String LOG_TAG = "Playback Service";
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;



    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(getApplicationContext(), LOG_TAG);

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE|
                                PlaybackStateCompat.ACTION_PLAY_FROM_URI|
                                PlaybackStateCompat.ACTION_PREPARE_FROM_URI|
                                PlaybackStateCompat.ACTION_SEEK_TO
                );
        mediaSession.setPlaybackState(stateBuilder.build());
        //mediaSession.setCallback(new MySessionCallback());

        setSessionToken(mediaSession.getSessionToken());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
