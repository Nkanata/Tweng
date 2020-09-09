package com.example.tweng.FirebaseOps;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadMusicArt {
    StorageReference mStorageRef;
    StorageTask<UploadTask.TaskSnapshot> mUploadTask;
    FirebaseStorage storage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Bitmap bitmap;
    String post_id;
    String genre;

    public UploadMusicArt(Bitmap bitmap, String post_id, String genre) {

        uploadArt(bitmap, post_id, genre);
    }

    private void uploadArt(Bitmap bitmap, final String post_id, final String genre){
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        String filename = "song_art.jpg";

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("User id", mAuth.getUid())
                .build();
        final StorageReference storageReference = mStorageRef.child("music art").child(post_id + "/" + filename);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        mUploadTask = storageReference.putBytes(data, metadata);
        mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        toFirebase(uri, genre, post_id);
                    }
                });
            }
        });

    }

    private void toFirebase(Uri uri, String genre, String post_id){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> song_data = new HashMap<>();
        song_data.put("music_art_url", uri.toString());
        assert user != null;
        db.collection("posts").document(genre).collection("music_posts").document(post_id)
                .update(song_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Song Art", "Song Art Uploaded Successfully");
                    }
                });
    }

}
