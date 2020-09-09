package com.example.tweng.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private Context _context;
    private List<Comment> comments;

    public CommentsAdapter(Context _context, List<Comment> comments) {
        this._context = _context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        // Todo profile pic
        if (comment.getTimestamp() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = comment.getTimestamp().toDate();

            holder.date_time.setText(dateFormat.format(date));
        }
        holder.comment_box.setText(comment.getComment());
        holder.username.setText(comment.getUsername());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView comment_box, username, date_time;
        ImageView profile_pic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            comment_box = itemView.findViewById(R.id.comment_box);
            username = itemView.findViewById(R.id.username);
            date_time = itemView.findViewById(R.id.date_time);


        }
    }
}
