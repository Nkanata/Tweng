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

public class UploadProfilePic {
    StorageReference mStorageRef;
    StorageTask<UploadTask.TaskSnapshot> mUploadTask;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    Bitmap bitmap;

    public UploadProfilePic(Bitmap bitmap) {
        this.bitmap = bitmap;
        uploadProfilePic(bitmap);
    }

    private void uploadProfilePic(Bitmap bitmap) {
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        String filename = "profile__pic.jpg";

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("User id", mAuth.getUid())
                .build();
        final StorageReference storageReference = mStorageRef.child("Profile_pics").child(mAuth.getUid() + "/" + filename);

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
                        toFirebase(uri);
                    }
                });
            }
        });

    }
    private void toFirebase(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user_data = new HashMap<>();
        user_data.put("profile_pic_url", uri.toString());
        assert user != null;
        db.collection("Users").document(user.getUid())
                .update(user_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Profile Piccccccc", "Profile Pic Uploaded Successfully");
                    }
                });
    }
}
