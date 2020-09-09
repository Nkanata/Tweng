package com.example.tweng.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tweng.Music;
import com.example.tweng.Player.NotificationActionReceiver;
import com.example.tweng.R;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";


    public static final String ACTION_PREVIOUS = "action_prev";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";


    Context _context;

    public static void createNotification(Context context, Music music, int playbutton, MediaSessionCompat mediaSessionCompat, long length, long duration) {
        int pos = 0;
       



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            //MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Log.d("notification", music.getAlbum());


            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cover);

           // Glide.get(context).

            /*PendingIntent pendingIntentPrevious;
            int drw_previous;

            if (pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionReceiver.class)
                        .setAction(ACTION_PREVIOUS);

                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious
                        , PendingIntent.FLAG_UPDATE_CURRENT);

                drw_previous = R.drawable.ic_skip_previous;
            }



            PendingIntent pendingIntentNext;
            int drw_next;

            /*if (pos == 2) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionReceiver.class)
                        .setAction(ACTION_NEXT);

                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext
                        , PendingIntent.FLAG_UPDATE_CURRENT);

                drw_next = R.drawable.ic_skip_next;
            }**/
            //mediaSessionCompat.setActive(true);

            Intent intentPlay = new Intent(context, NotificationActionReceiver.class)
                    .setAction(ACTION_PLAY);

            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay
                    , PendingIntent.FLAG_UPDATE_CURRENT);
            mediaSessionCompat.getMediaSession();

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setContentTitle(music.getTitle())
                    .setContentText(music.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    //        .setMediaSession(mediaSessionCompat.getSessionToken())
                    //        .setShowActionsInCompactView(1))
                    .setShowWhen(false)
                    .setColorized(true)
                    .setProgress(0, 0, false)
                    //.addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    //.addAction(drw_next, "Next", pendingIntentNext)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            //notification.setProgress();

            notificationManagerCompat.notify(1, notification.build());

        }
    }




}
