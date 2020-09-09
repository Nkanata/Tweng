package com.example.tweng.FirebaseOps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tweng.chats.MessageType;
import com.example.tweng.chats.Messages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtils {
    private FirebaseAuth mAuth;
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Map<String, Object> channel = new HashMap<>();


    private static CollectionReference chatChanelCollectionRef = db.collection("ChatChannels");
    private static DocumentReference currentUserDocRef = db.collection("Users").document(user.getUid());
    ;
    private static CollectionReference usersCollectionRef = db.collection("Users");
    static String TAG = "FirebaseUtils";


    public FirebaseUtils() {
        chatChanelCollectionRef = db.collection("ChatChannels");
        currentUserDocRef = db.collection("Users").document(user.getUid());
        usersCollectionRef = db.collection("Users");
    }

    public static String getOrCreateChatChannel(final String otherUserId, final Context context) {
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
                            //Toast.makeText(context, channelId[0], Toast.LENGTH_SHORT).show();

                        } else {

                            CollectionReference newChannel = chatChanelCollectionRef;
                            Map<String, Object> usersInvolved = new HashMap<>();
                            usersInvolved.put("user1", user.getUid());
                            usersInvolved.put("user2", otherUserId);
                            newChannel.add(usersInvolved)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            channelId[0] = documentReference.getId();
                                            engaged.put("channel id", channelId[0]);
                                            channel.put("channelId", channelId[0]);
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
                    }
                });
       // Toast.makeText(context, channelId[0], Toast.LENGTH_SHORT).show();


        return channelId[0];
    }

    public static ListenerRegistration addChatMessagesListener(String channelId, Context context) {
        return chatChanelCollectionRef.document(channelId)
                .collection("messages")
                .orderBy("time")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG + " chat messages listener", "Listen failed.", e);
                            return;
                        }

                        List<Messages> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.get("type") == MessageType.TEXT) {
                                messages.add(doc.toObject(Messages.class));
                            } else {
                                //Todo image messages
                            }
                        }
                    }
                });
    }

    public static void createUploadPost(final String genreSelected, Map<String, Object> post_data, final Bitmap bitmap) {
        final String[] documentId = new String[1];
        db.collection("posts").document(genreSelected).collection("music_posts")
                .add(post_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentId[0] = documentReference.getId();
                        new UploadMusicArt(bitmap, documentReference.getId(), genreSelected);

                        Log.d("New post Document", "DocumentSnapshot written with ID: " + documentReference.getId());
                        db.collection("posts").document(genreSelected).collection("music_posts")
                                .document(documentReference.getId())
                                .update("id", documentReference.getId());

                    }
                });


    }

    public static void createUploadMusicData(Map<String, Object> music_data, final Bitmap bitmap, final String genre) {

        db.collection("Music")
                .add(music_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        new UploadMusicArt(bitmap, documentReference.getId(), genre);
                        Log.d("New music Document", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }


    public static void postComment(Map<String, Object> comment) {

        final String[] id = new String[1];
        CollectionReference commentsRef = db.collection("Comments");
        commentsRef.add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("New Comment added", "DocumentSnapshot written with ID: " + documentReference.getId());
                        id[0] = documentReference.getId();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    public static ListenerRegistration commentsListener(String genre, String post_id) {
        CollectionReference commentsRef = db.collection("posts").document(genre).collection("music posts")
                .document(post_id).collection("comments");
        return commentsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

            }
        });
    }


}
