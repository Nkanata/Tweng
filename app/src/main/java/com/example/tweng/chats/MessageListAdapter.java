package com.example.tweng.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Messages> messageList;
    private FirebaseAuth mAuth;


    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat datetime = new SimpleDateFormat("HH:mm:ss");

    public MessageListAdapter(Context context, List<Messages> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
           view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

        public int getItemCount () {
            return messageList.size();
        }

        @Override
        public int getItemViewType ( int position){
            Messages message = messageList.get(position);
            mAuth = FirebaseAuth.getInstance();

            if (message.getSenderId().equals(mAuth.getUid())) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        private class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);

                messageText = itemView.findViewById(R.id.text_message_body);
                timeText = itemView.findViewById(R.id.text_message_time);
            }

            void bind(Messages message) {
                messageText.setText(message.getMessage());

                // Format the stored timestamp into a readable String using method.
                if (message.getTimestamp() != null) {
                    timeText.setText(datetime.format(message.getTimestamp().toDate()));
                }
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText, nameText;
            ImageView profileImage;

            ReceivedMessageHolder(View itemView) {
                super(itemView);

                messageText = itemView.findViewById(R.id.text_message_body);
                timeText = itemView.findViewById(R.id.text_message_time);

            }

            void bind(Messages message) {
                messageText.setText(message.getMessage());

                // Format the stored timestamp into a readable String using method.
                if (message.getTimestamp() != null) {
                    timeText.setText(datetime.format(message.getTimestamp().toDate()));
                }

                //nameText.setText(message.getSender().getNickname());

                // Insert the profile image from the URL into the ImageView.
                //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
            }
        }
}
