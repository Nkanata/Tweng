package com.example.tweng.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.R;

import java.util.List;

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.MyViewHolder> {
    private Context context;
    private List<Trends> trendsList;

    public TrendsAdapter(Context context, List<Trends> trendList) {
        this.context = context;
        this.trendsList = trendList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trending, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Trends trends = trendsList.get(position);
        holder.trending_song_name.setText(trends.getSong_title());
        holder.trending_album.setText(trends.getAlbum());
        holder.trending_time.setText(trends.time);
        holder.t_artist.setText(trends.getArtist());
    }



    @Override
    public int getItemCount() {
        return trendsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView t_artist, trending_song_name, trending_time, trending_album;// genre;
        public ImageView artist_profile_pic, trending_art;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            t_artist = itemView.findViewById(R.id.t_artist);
            trending_song_name = itemView.findViewById(R.id.trending_song_name);
            trending_time = itemView.findViewById(R.id.trending_time);
            trending_album = itemView.findViewById(R.id.trending_album);
           // genre = itemView.findViewById(R.id.genre);
            trending_art = itemView.findViewById(R.id.trending_art);
        }
    }
}
