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
import com.example.tweng.Profile.GuestProfile;
import com.example.tweng.R;

import java.util.List;

public class Influence_rAdapter extends RecyclerView.Adapter<Influence_rAdapter.MyViewHolder> {
    private List<GuestProfile> guestProfiles;
    private Context context;

    public Influence_rAdapter(List<GuestProfile> guestProfiles, Context context) {
        this.guestProfiles = guestProfiles;
        this.context = context;
    }

    @NonNull
    @Override
    public Influence_rAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.influencer, parent, false);
        return new Influence_rAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Influence_rAdapter.MyViewHolder holder, int position) {
        final GuestProfile profile = guestProfiles.get(position);

        holder.username.setText(profile.getUsername());

        if (profile.getProfile_pic_url() != null) {
            Glide.with(context).load(Uri.parse(profile.getProfile_pic_url())).into(holder.dp);
        }
    }

    @Override
    public int getItemCount() {
        return guestProfiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView username;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            username = itemView.findViewById(R.id.username);
        }
    }
}
