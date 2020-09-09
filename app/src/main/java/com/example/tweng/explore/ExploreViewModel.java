package com.example.tweng.explore;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tweng.Music;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExploreViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<Music>> exploreLiveData;
    private MutableLiveData<List<Music>> trendingLiveData;
    private MutableLiveData<List<Music>> latestLiveData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String Tag = "ExploreViewModel";

    public LiveData<List<Music>> getExploreLiveData(){
        if (exploreLiveData == null){
            Log.d(Tag, "called here");
            exploreLiveData = new MutableLiveData<>();
            getTopData();
        }
        return exploreLiveData;
    }

    public LiveData<List<Music>> getTrendingLiveData(){
        if (trendingLiveData == null){
            Log.d(Tag, "called here");

            trendingLiveData = new MutableLiveData<>();
            getTrending();
        }
        return trendingLiveData;
    }

    public LiveData<List<Music>> getLatestLiveData(){
        if (latestLiveData == null){
            Log.d(Tag, "called here");

            latestLiveData = new MutableLiveData<>();
            getLatest();
        }
        return latestLiveData;
    }

    public void getTopData(){

        final  List<Music> musicList = new ArrayList<>();
        CollectionReference  top_charts = db.collection("top_charts");
        top_charts.limit(25);
        Log.d(Tag, "function called here");

        //top_charts.orderBy("timestamp");
        top_charts.addSnapshotListener((queryDocumentSnapshots, e) -> {
            Log.d(Tag, "listener called here");

            if(e != null){
                Log.d(Tag,"Listener Failed", e);
                return;
            }

            assert queryDocumentSnapshots != null;
            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                switch (doc.getType()){
                    case ADDED:
                        Music music1 = doc.getDocument().toObject(Music.class);
                        musicList.add(music1);
                        exploreLiveData.setValue(musicList);

                        // music1.setCategory("top_charts");
                        //exploreLiveData.setValue(musicList);
                        break;

                    case MODIFIED:
                        music1 = doc.getDocument().toObject(Music.class);
                        //music1.setCategory("top_charts");
                        musicList.add(music1);
                        break;
                    case REMOVED:
                        music1 = doc.getDocument().toObject(Music.class);
                        musicList.remove(music1);
                        //exploreLiveData.setValue(musicList);

                        break;

                }
            }
        });


        Log.d(Tag, "end function  here");

    }

    public void getTrending(){
        Log.d(Tag, "function called here");

        final  List<Music> musicList = new ArrayList<>();

        CollectionReference trends = db.collection("Trends");

        trends.limit(25);
        //trends.orderBy("timestamp");
        trends.addSnapshotListener((queryDocumentSnapshots, e) -> {
            Log.d(Tag, "listener called here");

            if(e != null){
                Log.d(Tag,"Listener Failed", e);
                return;
            }

            assert queryDocumentSnapshots != null;

            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                switch (doc.getType()){
                    case ADDED:
                        Music music1 = doc.getDocument().toObject(Music.class);
                        //music1.setCategory("trending");
                        musicList.add(music1);

                        trendingLiveData.setValue(musicList);

                        //exploreLiveData.setValue(musicList);
                        break;
                    case MODIFIED:
                        music1 = doc.getDocument().toObject(Music.class);
                        // music1.setCategory("trending");

                        musicList.add(music1);
                        break;
                    case REMOVED:
                        music1 = doc.getDocument().toObject(Music.class);
                        musicList.remove(music1);
                        //exploreLiveData.setValue(musicList);
                        break;
                }
            }
        });

    }

    public void getLatest(){
        Log.d(Tag, "function called here");

        final  List<Music> musicList = new ArrayList<>();
        CollectionReference latest_uploads = db.collection("latest_uploads");

        latest_uploads.limit(25);
        //latest_uploads.orderBy("timestamp");
        latest_uploads.addSnapshotListener((queryDocumentSnapshots, e) -> {
            Log.d(Tag, "listener called here");

            if(e != null){
                Log.d(Tag,"Listener Failed", e);
                return;
            }

            assert queryDocumentSnapshots != null;
            for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                switch (doc.getType()){
                    case ADDED:
                        Music music1 = doc.getDocument().toObject(Music.class);
                        musicList.add(music1);
                        //music1.setCategory("latest");
                        latestLiveData.setValue(musicList);

                        break;
                    case MODIFIED:
                        music1 = doc.getDocument().toObject(Music.class);
                        musicList.add(music1);
                        break;
                    case REMOVED:
                        music1 = doc.getDocument().toObject(Music.class);
                        musicList.remove(music1);
                        //exploreLiveData.setValue(musicList);

                        break;
                }
            }
        });

    }

}
