package com.example.tweng.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tweng.Music;
import com.example.tweng.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    List<Music> musicList;
    Context context;
    String category;

    public PostAdapter(List<Music> musicList, Context context, String category) {
        this.musicList = musicList;
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posts, parent, false);
        return new PostAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Music music = musicList.get(position);
      //  music.setCategory(category);

        //if (music.getCategory().equals(category)) {
            holder.title.setText(music.getTitle());
            holder.artist.setText(music.getArtist());

            if (music.getMusic_art_url() != null) {
                Glide.with(context).load(music.getMusic_art_url()).into(holder.song_art);
            }
      //  }

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView song_art;
        TextView artist, title;
        ImageButton download, play;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            song_art = itemView.findViewById(R.id.song_art);
            artist = itemView.findViewById(R.id.artist);
            title = itemView.findViewById(R.id.title);
            download = itemView.findViewById(R.id.download);
            play = itemView.findViewById(R.id.play_button);
        }
    }
}
