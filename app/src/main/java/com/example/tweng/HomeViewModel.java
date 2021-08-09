package com.example.tweng;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Music>> musicPostLiveData;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "HomeViewModel Listener";

    //
    public LiveData<List<Music>> getMusicPost() {
        if (musicPostLiveData == null){
            Log.d(TAG, "called here");
            musicPostLiveData = new MutableLiveData<>();
            listenToPosts();
        }

        return musicPostLiveData;
    }

    public void listenToPosts() {
        final List<Music> musicList = new ArrayList<>();
        
        CollectionReference hiphop_posts = db.collection("posts").document("Hip-Hop").collection("music_posts");
        hiphop_posts.limit(25);
        hiphop_posts.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            Log.d(TAG, "not null query snapshots");
            assert queryDocumentSnapshots != null;
            //DocumentChange documentChange = new DocumentChange();
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Music music = dc.getDocument().toObject(Music.class);
                        music.setCategory("trending");
                        musicList.add(music);
                        musicPostLiveData.setValue(musicList);
                        Log.d(TAG, "New song: " + dc.getDocument().getData());
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                        break;
                }

            }
        });
    }
}