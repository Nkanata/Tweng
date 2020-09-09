package com.example.tweng.chats;

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

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {
    private Context context;
    private List<Chats> chatsList;
    private OnChatListener onChatListener;

    public ChatsAdapter(Context context, List<Chats> chatsList, OnChatListener onChatListener) {
        this.context = context;
        this.chatsList = chatsList;
        this.onChatListener = onChatListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat, parent, false);
        return new MyViewHolder(view, onChatListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Chats chats = chatsList.get(position);
        holder.username.setText(chats.getUsername());
        holder.time.setText(chats.getTime());
        holder.preview_text.setText(chats.getPreview_text());

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView username, preview_text, time;
        public ImageView sender_profile_pic;
        OnChatListener onChatListener;
        public MyViewHolder(@NonNull View itemView, OnChatListener onChatListener) {
            super(itemView);
            username = itemView.findViewById(R.id.chat_username);
            time = itemView.findViewById(R.id.text_time);
            preview_text = itemView.findViewById(R.id.preview_text);
            this.onChatListener = onChatListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatListener.onChatClickListener(getAdapterPosition());
        }
    }

    public interface OnChatListener{
        void onChatClickListener(int position);
    }
}
