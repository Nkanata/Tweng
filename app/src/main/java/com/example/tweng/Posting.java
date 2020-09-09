package com.example.tweng;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tweng.FirebaseOps.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Posting extends AppCompatActivity {
    Uri audioUri, imageUri;
    Uri downloadUri;
    TextView selectedFile;
    EditText songTitle, songArtist, songAlbum;
    Button post, selectSong, choose_art;
    ImageView song_art;
    Spinner genre;
    String genreSelected, selectedSongTitle, title, album, artist;
    Bitmap bitmap;
    private FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_posting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.music_details);
        actionBar.setDisplayHomeAsUpEnabled(true);


        songTitle = findViewById(R.id.song_title);
        songArtist = findViewById(R.id.song_artist_name);
        songAlbum = findViewById(R.id.posting_album);
        post = findViewById(R.id.post);
        selectSong = findViewById(R.id.choose_song);
        selectedFile = findViewById(R.id.selected_file);
        genre = findViewById(R.id.genres);
        choose_art = findViewById(R.id.choose_art);
        song_art = findViewById(R.id.song_art);


        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.music_genres, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        genre.setAdapter(arrayAdapter);


        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFile(v);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(album) && !TextUtils.isEmpty(artist)) {
                    Toast.makeText(getApplicationContext(), "Please fill All the Fields", Toast.LENGTH_LONG).show();
                    return;
                }
                if (imageUri == null){
                    Toast.makeText(getApplicationContext(), "Select Music Art ", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadAudioToFirebase(v);
            }
        });

        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genreSelected = parent.getItemAtPosition(position).toString();
                Log.d("Genreeeeeeee", genreSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Please select a genre", Toast.LENGTH_LONG).show();
            }
        });

        choose_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 110);
            }
        });
    }



    public void openAudioFile(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 101:
            if (resultCode == RESULT_OK && data.getData() != null) {
                audioUri = data.getData();
                selectedSongTitle = getFileName(audioUri);
                selectedFile.setText(selectedSongTitle);
            }else {
                return;
            }
            break;

            case 110:
                if (resultCode == RESULT_OK && data.getData() != null) {
                    imageUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        song_art.setImageBitmap(bitmap);
                        //new SaveProfilePic(getApplicationContext(), bitmap);
                        //new UploadMusicArt();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //imageView.setImageURI(imageUri);

                }
                break;
        }

    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadAudioToFirebase(View view) {
        if (selectedFile.getText().toString().equals("No Song Selected")) {
            Snackbar.make(view, "Please Select a file", Snackbar.LENGTH_LONG).show();
        } else {
            uploadFile();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void uploadFile() {
        StorageReference mStorageRef;
        StorageTask<UploadTask.TaskSnapshot> mUploadTask;
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        final String channel_id = "upload channel";
        final int notificationId = 9001;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }


      //  IsNetworkAvailable isNetworkAvailable = new IsNetworkAvailable();
        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "No Network Try again Later", Toast.LENGTH_LONG).show();
            return;
        }

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id);
        builder.setContentTitle(selectedSongTitle + " Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.upload)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        // Issue the initial notification with zero progress
        final int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(notificationId, builder.build());

        mAuth = FirebaseAuth.getInstance();
        if (audioUri != null) {
            String durationTxt;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("Posted By", mAuth.getUid())
                    .setCustomMetadata("Artist", artist)
                    .setCustomMetadata("Album", album)
                    .build();

            final StorageReference storageReference = mStorageRef.child(genreSelected).child(album + "/" + selectedSongTitle + "." + getFileExtension(audioUri));
            int durationInMillis = findSongDuration(audioUri);

            durationTxt = getDurationFromMilli(durationInMillis);

            if (durationInMillis == 0) {
                durationTxt = "NA";
            }

            final String finalDurationTxt = durationTxt;
            mUploadTask = storageReference.putFile(audioUri, metadata);
            Toast.makeText(getApplicationContext(), "Uploading Please Wait...", Toast.LENGTH_LONG).show();
            // Log.d("file Patrtttttttttttt", storageReference.toString());

            mUploadTask.addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When done, update the notification one more time to remove the progress bar
                            builder.setContentText("Upload complete")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(notificationId, builder.build());
                            ///
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUri = uri;
                                    Log.d("uriiiiiii", String.valueOf(uri));
                                    createUploadPost(uri);
                                    createUploadMusicData(uri);

                                }
                            });
                        }
                    }
            ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    // Do the job here that tracks the progress.
                    // Usually, this should be in a
                    // worker thread
                    // To show progress, update PROGRESS_CURRENT and update the notification with:
                    builder.setProgress(PROGRESS_MAX, progress, false);
                    notificationManager.notify(notificationId, builder.build());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Uploading Failed", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private int findSongDuration(Uri uri) {
        int timeInMillis;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMillis = Integer.parseInt(time);
            retriever.release();
            return timeInMillis;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getDurationFromMilli(int durationInMilli) {
        Date date = new Date(durationInMilli);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    private void createUploadPost(Uri uri) {
        title = songTitle.getText().toString();
        album = songAlbum.getText().toString();
        artist = songArtist.getText().toString();
        final String[] documentId = new String[1];
        //todo create post to firebase
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.username_preference_file), Context.MODE_PRIVATE);
        String username =preferences.getString( getString(R.string.username), getString(R.string.username) );
        Map<String, Object> post_data = new HashMap<>();
        post_data.put("url", String.valueOf(uri));
        post_data.put("posted_by", user.getUid());
        post_data.put("artist", artist);
        post_data.put("title", title);
        post_data.put("album", album);
        post_data.put("username", username);
        post_data.put("timestamp", FieldValue.serverTimestamp());
        post_data.put("genre", genreSelected);
        //
        if (bitmap == null){
            Toast.makeText(getApplicationContext(), "Select Music Art ", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUtils.createUploadPost(genreSelected, post_data, bitmap);
        /**db.collection("posts").document(genreSelected).collection("music posts")
                .add(post_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentId[0] = documentReference.getId();
                        new UploadMusicArt(bitmap, documentReference.getId(), genreSelected);
                        Log.d("New post Document", "DocumentSnapshot written with ID: " + documentReference.getId());

                    }
                });

        db.collection("hiphop_posts").document(documentId[0])
                .update("id", documentId[0]);**/

    }

    private void createUploadMusicData(Uri uri) {
        title = songTitle.getText().toString();
        album = songAlbum.getText().toString();
        artist = songArtist.getText().toString();
        Map<String, Object> music_data = new HashMap<>();
        music_data.put("title", title);
        music_data.put("artist", artist);
        music_data.put("album", album);
        music_data.put("genre", genreSelected);
        music_data.put("posted_by", user.getUid());
        music_data.put("url", String.valueOf(uri));
        music_data.put("timestamp", FieldValue.serverTimestamp());

        if (bitmap == null){
            Toast.makeText(getApplicationContext(), "Select Music Art ", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUtils.createUploadMusicData(music_data, bitmap, genreSelected);
        /**db.collection("Music")
                .add(music_data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        new UploadMusicArt(bitmap, documentReference.getId(), genreSelected);
                        Log.d("New music Document", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error Occurred", "Error adding document", e);
                    }
                });**/
    }

}
