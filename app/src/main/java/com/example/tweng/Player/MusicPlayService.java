package com.example.tweng.Player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tweng.Music;
import com.example.tweng.Notifications.CreateNotification;
import com.example.tweng.R;

import java.util.List;

public class MusicPlayService extends Service {
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    String LOG_TAG = "Playback Service";
    NotificationManager notificationManager;
    boolean isPlaying = false;
    boolean isRegistered;
    int prev_Position = -1;
    int currentPlaying = -1;
    MySessionCallback sessionCallback;
    List<Music> musicList;
    Playable playable;
    BroadcastReceiver broadcastReceiver;

    long duration;
    long length = 0;

    int drw_play = R.drawable.ic_play_arrow;
    int drw_pause = R.drawable.ic_pause;

    public void setCurrentPlaying(int currentPlaying) {
        this.currentPlaying = currentPlaying;
    }

    public class LocalBinder extends Binder {
        public MusicPlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicPlayService.this;
        }
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public long getDuration() {
        duration = sessionCallback.getDuration();
        return duration;
    }

    public long getLength() {
        length = sessionCallback.getLength();
        return length;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(getApplicationContext(), LOG_TAG);

        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                //MediaSessionCompat.
        );

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_FROM_URI |
                                PlaybackStateCompat.ACTION_PREPARE_FROM_URI |
                                PlaybackStateCompat.ACTION_SEEK_TO|
                                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
                );
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(sessionCallback);
        mediaSession.setActive(true);
        sessionCallback = new MySessionCallback(getApplicationContext(), mediaSession);
        playable = new Playable() {
            @Override
            public void onPlayPause() {

            }
        };

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");
                assert action != null;
                switch (action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        //onTrackPrevious();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if (isPlaying) {
                            //sessionCallback.onPause();
                            pauseMusic();
                            Log.d("service is playing", String.valueOf(isPlaying));
                            playable.onPlayPause();
                            //  onTrackPause();
                        } else {
                            //sessionCallback.onPlay();
                            resumePlay();
                            //onTrackPlay();
                        }
                        break;
                    case CreateNotification.ACTION_NEXT:
                        //onTrackNext();
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        isRegistered = true;


        //setSessionToken(mediaSession.getSessionToken());
    }

    private final IBinder binder = new LocalBinder();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void playMusic(List<Music> musicList, final int adapter_position) {
        // Log.d("music list size", String.valueOf(musicList.size()));
        //Log.d("position", String.valueOf(adapter_position));
        createChannel();

        setMusicList(musicList);
        final Music music = musicList.get(adapter_position);
        sessionCallback.setMusic(music);
        sessionCallback.musicList = musicList;
        sessionCallback.setPosition(adapter_position);
        setCurrentPlaying(adapter_position);

        //prev_Position = -1;

        if (prev_Position != adapter_position) {
            prev_Position = adapter_position;
            if (isNetworkAvailable()) {
                // Media session would be great from here
                //if(sessionCallback.mediaPlayer.isPlaying()) {
                //  sessionCallback.onStop();
                //}
                Log.d("play from uri", "started");
                isPlaying = true;
                sessionCallback.onPlayFromUri(Uri.parse(music.getUrl()), Bundle.EMPTY);
                Toast.makeText(getApplicationContext(), "playing music", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();

            }
        } else {
            //pauseMusic();
        }


    }

    public void pauseMusic() {
        sessionCallback.onPause();
        isPlaying = false;
    }

    public void resumePlay() {
        sessionCallback.onPlay();
        isPlaying = true;
    }


    private void createNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel((CreateNotification.CHANNEL_ID), "Player Notification",
                    NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);

            Log.d("notification channel", "createdddd");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isRegistered) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public interface Playable{
        void onPlayPause();
    }
}
