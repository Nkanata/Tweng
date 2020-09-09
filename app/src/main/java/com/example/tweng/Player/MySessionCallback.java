package com.example.tweng.Player;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tweng.Music;
import com.example.tweng.Notifications.CreateNotification;
import com.example.tweng.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MySessionCallback extends MediaSessionCompat.Callback  {
    private static final String CHANNEL_ID = "channel1" ;
    private static final String ACTION_PREVIOUS = "action_prev";
    private static final String ACTION_PLAY = "action_play";
    private static final String ACTION_NEXT = "action_next";
    //final MediaPlayer mediaPlayer;
    Context context;
    MediaSessionCompat mediaSession;
    int drw_play = R.drawable.ic_play_arrow;
    int drw_pause = R.drawable.ic_pause;
    int duration = 0;
    int length = 0;
    Music music;
    List<Music> musicList;
    int position;

    final SimpleExoPlayer exoPlayer;
    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder notification ;
    Intent intentPlay;

    PendingIntent pendingIntentPlay;

    public MySessionCallback(Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaSession = mediaSession;
       // this.mediaPlayer = new MediaPlayer();
        this.exoPlayer = new SimpleExoPlayer.Builder(context).build();
        this.notificationManagerCompat = NotificationManagerCompat.from(context);

        this.intentPlay = new Intent(context, NotificationActionReceiver.class)
                .setAction(CreateNotification.ACTION_PLAY);

    }

    public void setMusic(Music music) {
        this.music = music;
    }



    public long getDuration() {
        //duration = mediaPlayer.getDuration();
        long duration = exoPlayer.getDuration();

        return duration;
    }

    public long getLength() {
        //length = mediaPlayer.getCurrentPosition();
        long position = exoPlayer.getCurrentPosition();

        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        return super.onMediaButtonEvent(mediaButtonEvent);
    }


    @Override
    public void onPlay() {
        super.onPlay();
       /* if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }**/
        exoPlayer.setPlayWhenReady(true);
        eventListener.onPlayerStateChanged(true, exoPlayer.getPlaybackState());
        //CreateNotification.createNotification(context, this.music, drw_pause, mediaSession, getLength(), getDuration());


    }


    ExoPlayer.EventListener eventListener = new Player.EventListener() {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_ENDED:
                            Log.i("EventListenerState", "Playback ended!");
                            exoPlayer.setPlayWhenReady(false);

                            break;
                        case Player.STATE_READY:
                            Log.i("EventListenerState", "Playback State Ready!");
                            if (playWhenReady){
                                createNotification(drw_pause, "Pause");
                                //pausePlayNotification(drw_pause, "Pause");
                            }else {
                                createNotification(drw_play, "Play");
                                //pausePlayNotification(drw_play, "Play");
                            }


                            //hideProgressBar();
                            break;
                        case Player.STATE_BUFFERING:
                            Log.i("EventListenerState", "Playback buffering");
                            //showProgressBar();

                            break;
                        case Player.STATE_IDLE:

                            //notification.addAction(drw_play, "Play", pendingIntentPlay);
                            //notificationManagerCompat.notify(1, notification.build());
                            break;

                    }

        }
    };


    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        super.onPlayFromUri(uri, extras);
        Music music = musicList.get(position);

       /* mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }**/

        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        createNotification(drw_pause, "Pause");
        //pausePlayNotification(drw_pause, "Pause");



        long duration = exoPlayer.getDuration();

        long position = exoPlayer.getCurrentPosition();


        //CreateNotification.createNotification(context, music, drw_pause, mediaSession, getLength(), getDuration());
        Log.d("player duration", String.valueOf(duration));
        Log.d("player position", String.valueOf(position));


    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, "TWENG");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @Override
    public void onPause() {
        /*if (mediaPlayer.isPlaying()) {
            super.onPause();
            mediaPlayer.pause();

            Log.d("session callback", "pause");
        }**/
        exoPlayer.setPlayWhenReady(false);
        //createNotification(drw_play, "Play");
        eventListener.onPlayerStateChanged(false, exoPlayer.getPlaybackState());
        //CreateNotification.createNotification(context, music, drw_play, mediaSession, getLength(), getDuration());

    }

    public void createNotification(int play_pause_icon, String play_pause){
        pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay
                , PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cover);

        this.notification = new NotificationCompat.Builder(context, CHANNEL_ID);

        notification.setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(music.getTitle()+ " by " + music.getArtist() )
                //.setContentText("4:11")
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                //        .setMediaSession(mediaSessionCompat.getSessionToken())
                //        .setShowActionsInCompactView(1))
                .setShowWhen(false)
                .setColorized(true)
                //.setProgress(0, position, false)

                .addAction(play_pause_icon, play_pause, pendingIntentPlay)
                //.addAction(drw_previous, "Previous", pendingIntentPrevious)

                //.addAction(drw_next, "Next", pendingIntentNext)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManagerCompat.notify(1, notification.build());
        long p_position ;
        long timer = 0;

        


            for (p_position = exoPlayer.getCurrentPosition(); p_position <= exoPlayer.getDuration(); p_position += 1) {
                setProgress();
                //timer +=1;
            }




    }





    public void setProgress(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(exoPlayer.getCurrentPosition());
        notification.setContentText(dateFormat.format(date));
        notification.setProgress((int) exoPlayer.getDuration(), (int)((exoPlayer.getCurrentPosition()*100)/exoPlayer.getDuration()), false);
        notificationManagerCompat.notify(1, notification.build());

    }
    public void pausePlayNotification(int play_pause_icon, String play_pause){
        notification.addAction(play_pause_icon, play_pause, pendingIntentPlay);
    }

    @Override
    public void onSkipToNext() {
        super.onSkipToNext();
    }

    @Override
    public void onSkipToPrevious() {
        super.onSkipToPrevious();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSeekTo(long pos) {
        super.onSeekTo(pos);
    }

    @Override
    public void onCustomAction(String action, Bundle extras) {
        super.onCustomAction(action, extras);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        super.onAddQueueItem(description);
    }

    @Override
    public void onRemoveQueueItem(MediaDescriptionCompat description) {
        super.onRemoveQueueItem(description);
    }

}
