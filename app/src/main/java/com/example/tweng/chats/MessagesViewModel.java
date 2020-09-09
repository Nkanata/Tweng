package com.example.tweng.chats;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessagesViewModel extends ViewModel {
    List<Messages> messagesList = new ArrayList<>();
    String TAG = "Messages ViewModel";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference chatChanelCollectionRef = db.collection("ChatChannels");
    MutableLiveData<List<Messages>> messages;


    public LiveData<List<Messages>> getMessages(String channelId) {
        if (messages == null) {
            messages = new MutableLiveData<>();
            listenToMessages(channelId);
        }

        return messages;
    }


    void listenToMessages(String channelId) {
        Log.d(TAG, channelId + "Chattttinhghh");
        CollectionReference messagesCollRef = chatChanelCollectionRef.document(channelId).collection("Messages");
       // messagesCollRef.orderBy("timestamp", Query.Direction.ASCENDING);
        messagesCollRef
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG + " chat messages listener", "Listen failed.", e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (doc.getType()) {
                        case ADDED:
                                Messages message = doc.getDocument().toObject(Messages.class);
                                messagesList.add(message);
                                messages.setValue(messagesList);
                            break;
                    }
                }
            }
        });

    }
}
