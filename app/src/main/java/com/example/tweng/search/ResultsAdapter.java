package com.example.tweng.search;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tweng.Music;
import com.example.tweng.R;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {
    private List<Music> resultList;
    private Context context;

    public ResultsAdapter(List<Music> resultList, Context context) {
        this.resultList = resultList;
        this.context = context;
    }


    @NonNull
    @Override
    public ResultsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_result, parent, false);
        return new ResultsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.MyViewHolder holder, int position) {
        final Music music = resultList.get(position);

        holder.artist.setText(music.getArtist());
        holder.title.setText(music.getTitle());

        if (music.getMusic_art_url() != null) {
            Glide.with(context).load(Uri.parse(music.getMusic_art_url())).into(holder.song_art);
        }

    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView song_art;
        TextView title, artist;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            song_art = itemView.findViewById(R.id.song_art);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
        }
    }
}
