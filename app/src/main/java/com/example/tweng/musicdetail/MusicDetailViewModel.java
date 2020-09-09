package com.example.tweng.musicdetail;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tweng.Music;
import com.example.tweng.comments.Comment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MusicDetailViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    // TODO: Retrieve comments

    private MutableLiveData<List<Comment>> commentsLiveData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "Music Detail View";

    public LiveData<List<Comment>> getComments(Music music) {
        if (commentsLiveData == null) {
            commentsLiveData = new MutableLiveData<>();
            listenToComments(music);
        }


        return commentsLiveData;
    }

    private void listenToComments(Music music) {
        final List<Comment> comments = new ArrayList<>();
        CollectionReference commentsRef = db.collection("Comments");
        commentsRef
                .whereEqualTo("post_id", music.getId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG + " comment listener", "Listen failed.", e);
                            return;
                        }

                        assert queryDocumentSnapshots != null;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            comments.add(doc.toObject(Comment.class));
                        }

                        commentsLiveData.setValue(comments);
                    }
                });

    }

}
