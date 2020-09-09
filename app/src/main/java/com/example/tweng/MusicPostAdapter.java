package com.example.tweng;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tweng.Player.MusicPlayService;

import java.io.IOException;
import java.util.List;

public class MusicPostAdapter extends RecyclerView.Adapter<MusicPostAdapter.MyViewHolder> {
    //public static Object OnPostListener;
    private Context context;
    private List<Music> musicList;
    private OnPostListener onPostListener;
    //private boolean isPlaying = false;
    private int currentPlaying;
    private int prevPlaying = -1;
    private int setPause = -1;
    private int setPlay = -1;


    public MusicPostAdapter(Context context, List<Music> musicList, OnPostListener onPostListener) {
        this.context = context;
        this.musicList = musicList;
        this.onPostListener = onPostListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_post, parent, false);
        return new MyViewHolder(view, onPostListener);
    }

    public void setPrevPlaying(int prevPosition) {
        prevPlaying = prevPosition;
    }

    public void setSetPause(int setPause) {
        this.setPause = setPause;
    }

    public void setSetPlay(int setPlay) {
        this.setPlay = setPlay;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Music music = musicList.get(position);
        holder.song_name.setText(music.getTitle());
        holder.album.setText(music.getAlbum());
        holder.time.setText(music.getTime());
        holder.artist_name.setText(music.getArtist());
        holder.genre.setText(music.getGenre());

        if (music.getMusic_art_url() != null) {
            Glide.with(context).load(Uri.parse(music.getMusic_art_url())).into(holder.song_art);
        }


        if (prevPlaying == position) {
            holder.play_text.setText(R.string.play);
            holder.play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
            holder.isPlaying = false;
            return;
        }
        if (setPause == position){
            holder.play_text.setText(R.string.play);
            holder.play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
            holder.isPlaying = false;
            setPause = -1;
            return;
        }
        if (setPlay == position){
            holder.play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
            holder.play_text.setText(R.string.pause);
            holder.isPlaying = true;
            setPlay = -1;
        }



    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements MusicPlayService.Playable, View.OnClickListener {
        public TextView artist_name, song_name, time, album, genre, play_text;
        ImageView artist_profile_pic, song_art;
        LinearLayout download, detail_layout, comment, play;
        OnPostListener onPostListener;
        ImageButton play_button;
        public boolean isPlaying = false;

        public MyViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);
            artist_name = itemView.findViewById(R.id.post_artist);
            song_name = itemView.findViewById(R.id.song_name);
            time = itemView.findViewById(R.id.time);
            album = itemView.findViewById(R.id.song_album);
            genre = itemView.findViewById(R.id.genre);
            artist_profile_pic = itemView.findViewById(R.id.artist_profile_pic);
            song_art = itemView.findViewById(R.id.song_art);
            download = itemView.findViewById(R.id.download);
            play = itemView.findViewById(R.id.play);
            play_button = itemView.findViewById(R.id.play_button);
            play_text = itemView.findViewById(R.id.play_text);
            comment = itemView.findViewById(R.id.comment);
            this.onPostListener = onPostListener;
            detail_layout = itemView.findViewById(R.id.detail_layout);
            detail_layout.setOnClickListener(this);
            download.setOnClickListener(this);
            play.setOnClickListener(this);
            comment.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.detail_layout:
                    Log.d("iddddddddddd", String.valueOf(v.getId()));
                    onPostListener.onPostClickListener(getAdapterPosition());
                    break;
                case R.id.play:
                    if(isNetworkAvailable()) {
                        try {
                            if (!isPlaying) {
                                onPostListener.onPlayClickListener(getAdapterPosition());
                                play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
                                play_text.setText(R.string.pause);
                                isPlaying = true;
                            } else {
                                onPostListener.onPlayClickListener(getAdapterPosition());
                                play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
                                play_text.setText(R.string.play);
                                isPlaying = false;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(context, "No Network Available", Toast.LENGTH_SHORT).show();
                    }
                    // Log.d("playyy", "playyyyyyy");
                    break;
                case R.id.comment:
                    onPostListener.onCommentClickListener(getAdapterPosition());
                    Log.d("comeeent", "commeeent");
                    break;
                case R.id.download:
                    onPostListener.onDownloadClickListener(getAdapterPosition());
                    Log.d("download", "downloadddddddd");
                    break;

            }
        }

        @Override
        public void onPlayPause() {
            if (!isPlaying) {
                play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
                play_text.setText(R.string.pause);
                isPlaying = true;
            } else {
                play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
                play_text.setText(R.string.play);
                isPlaying = false;
            }

        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public interface OnPostListener {
        void onPostClickListener(int position);

        void onPlayClickListener(int position) throws IOException;

        void onDownloadClickListener(int position);

        void onCommentClickListener(int position);
    }
}
