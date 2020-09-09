package com.example.tweng.Profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ViewProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    FirebaseFirestore db;

    MutableLiveData<Map<String, Object>> profile_data;


    public LiveData<Map<String, Object>> getProfile(String posted_by){
       if (profile_data == null){
           profile_data = new MutableLiveData<>();
           getProfileData(posted_by);
       }

         return profile_data;
    }

    private void getProfileData(String posted_by){
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(posted_by).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        Map<String, Object> profile;
                        assert doc != null;
                        if (doc.exists()){
                            profile = doc.getData();
                            profile_data.setValue(profile);
                        }else{
                            Log.d("doesn't existssssss", "document doesntexist");
                        }

                    }
                });
    }



}
