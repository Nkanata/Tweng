package com.example.tweng.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tweng.Music;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    MutableLiveData<List<Music>> search_data;
    final List<Music> resultList = new ArrayList<>();


    public LiveData<List<Music>> getResults(String query){
        if (search_data == null){
            search_data = new MutableLiveData<>();
        }
        getSearchResults(query);

        return search_data;
    }

    private void getSearchResults(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query ref = db.collectionGroup("music_posts");
        ref.limit(10);
        //ref.orderBy("timestamp");
        ref.whereEqualTo("title", query).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("searching the db vModel", String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                        for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments() ) {

                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                            search_data.setValue(resultList);
                        }
                    }
                });

        ref.whereEqualTo("artist", query).get()
                .addOnCompleteListener(task -> {
                   // Log.d("searching the db vModel", "firebase");
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                            search_data.setValue(resultList);
                        }
                    }
                });

        ref.whereEqualTo("username", query).get()
                .addOnCompleteListener(task -> {
                  //  Log.d("searching the db vModel", "firebase");

                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                            search_data.setValue(resultList);
                        }
                    }
                });

        ref.whereEqualTo("album", query).get()
                .addOnCompleteListener(task -> {
                   // Log.d("searching the db vModel", "firebase");

                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                            search_data.setValue(resultList);
                        }
                    }
                });

    }
}
