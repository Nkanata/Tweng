package com.example.tweng.chats;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.Constants.Constants;
import com.example.tweng.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageList extends AppCompatActivity {
    private RecyclerView messageRecycler;
    private MessageListAdapter messageAdapter;
    private List<Messages> messageList;
    private FirebaseAuth mAuth;
    String other_user_id;
    String username;
    public static String channelId;
    TextView noMessages;
    Map<String, Object> channel;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference currentUserDocRef = db.collection("Users").document(user.getUid());
    CollectionReference usersCollectionRef = db.collection("Users");
    CollectionReference chatChanelCollectionRef = db.collection("ChatChannels");
    String TAG = "MessageList Class";
    final Map<String, Object> engaged = new HashMap<>();
    final CollectionReference currentUserEngagedChatsRef = currentUserDocRef.collection("EngagedChats");

    MessagesViewModel messagesViewModel;

    FloatingActionButton send, image;
    TextInputEditText messageInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        setSupportActionBar((Toolbar) findViewById(R.id.messages_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        username = getIntent().getStringExtra(Constants.USERNAME);
        other_user_id = getIntent().getStringExtra(Constants.USER_ID);
        actionBar.setTitle(username);
        //
        currentUserDocRef.collection("EngagedChats").document(other_user_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            channel = documentSnapshot.getData();
                            //channel.put("channelId", documentSnapshot.getString("channel id"));
                            channelId = documentSnapshot.getString("channel id");
                            //Toast.makeText(getApplicationContext(), channelId, Toast.LENGTH_SHORT).show();
                        } else {

                            CollectionReference newChannel = chatChanelCollectionRef;
                            Map<String, Object> usersInvolved = new HashMap<>();
                            usersInvolved.put("user1", user.getUid());
                            usersInvolved.put("user2", other_user_id);
                            newChannel.add(usersInvolved)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            channelId = documentReference.getId();
                                            engaged.put("channel id", channelId);
                                            channel.put("channelId", channelId);
                                            currentUserEngagedChatsRef.document(other_user_id)
                                                    .set(engaged);

                                            usersCollectionRef.document(other_user_id)
                                                    .collection("EngagedChats")
                                                    .document(user.getUid())
                                                    .set(engaged)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("created", "successfully");
                                                        }
                                                    });
                                        }
                                    });

                        }
                    }
                });
        //
        noMessages = findViewById(R.id.noMessages);
        messageRecycler = findViewById(R.id.recyclerview_message_list);
        messageInput = findViewById(R.id.messageInput);
        send = findViewById(R.id.send);
        image = findViewById(R.id.images);
        //
        messageList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setItemAnimator(new DefaultItemAnimator());

        // Todo put the firestore queries from here


        //
        if (channelId != null) {
            messagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
            messagesViewModel.getMessages(channelId).observe(this, new Observer<List<Messages>>() {
                @Override
                public void onChanged(List<Messages> messages) {
                    if (messages.size() != 0) {
                        Log.d(TAG, channelId + "Chattttinhghh");

                        noMessages.setVisibility(View.GONE);

                        messageList = messages;
                        messageAdapter = new MessageListAdapter(getApplicationContext(), messages);
                        messageRecycler.setAdapter(messageAdapter);
                    }

                }
            });
        }

        //
        //getOrCreateChatChannel(other_user_id, getApplicationContext());
        //channelId = (String) channel.get("channel id");

        Toast.makeText(getApplicationContext(), channelId, Toast.LENGTH_SHORT).show();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = String.valueOf(messageInput.getText());
                Map<String, Object>messageSent = new HashMap<>();
                if (!message.equals("")){
                    messageSent.put("type", MessageType.TEXT);
                    messageSent.put("senderId", user.getUid());
                    messageSent.put("Message", message);
                    messageSent.put("timestamp", FieldValue.serverTimestamp());

                    chatChanelCollectionRef.document(channelId)
                            .collection("Messages")
                            .add(messageSent)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                  Log.d(TAG,"message Sent");
                                }
                            });
                }

            }
        });

        //fetch();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    private void fetch() {

    }

    public String getOrCreateChatChannel(final String otherUserId, final Context context) {
        final Map<String, Object> engaged = new HashMap<>();
        final CollectionReference currentUserEngagedChatsRef = currentUserDocRef.collection("EngagedChats");
        final String[] channelId = new String[1];

        //documentReference.add(engaged);
        currentUserDocRef.collection("EngagedChats").document(otherUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            channel = documentSnapshot.getData();
                            //channel.put("channelId", documentSnapshot.getString("channel id"));
                            channelId[0] = documentSnapshot.getString("channel id");
                            //Toast.makeText(context, channelId, Toast.LENGTH_SHORT).show();

                            return;
                        }

                        CollectionReference newChannel = chatChanelCollectionRef;
                        Map<String, Object> usersInvolved = new HashMap<>();
                        usersInvolved.put("user1", user.getUid());
                        usersInvolved.put("user2", otherUserId);
                        newChannel.add(usersInvolved)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        channelId[0] = documentReference.getId();
                                        engaged.put("channel id", channelId);
                                        channel.put("channelId", channelId);
                                        currentUserEngagedChatsRef.document(otherUserId)
                                                .set(engaged);

                                        usersCollectionRef.document(otherUserId)
                                                .collection("EngagedChats")
                                                .document(user.getUid())
                                                .set(engaged)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("created", "successfully");
                                                    }
                                                });
                                    }
                                });

                    }
                });
        // Toast.makeText(context, channelId[0], Toast.LENGTH_SHORT).show();


        return channelId[0];
    }

}
