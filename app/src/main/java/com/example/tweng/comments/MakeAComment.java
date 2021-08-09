package com.example.tweng.comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tweng.Constants.Constants;
import com.example.tweng.FirebaseOps.FirebaseUtils;
import com.example.tweng.HomeViewModel;
import com.example.tweng.Music;
import com.example.tweng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeAComment extends AppCompatActivity {
    TextView comment_box;
    EditText comment_Input;
    Button post_comment;
    ImageButton comment_cancel;
    String doc_id, artist, title, comment_added, genre;
    int position;
    HomeViewModel viewModel;
    List<Music> musicList1;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_a_comment);


        Bundle extras = getIntent().getExtras();
        assert extras != null;
       // doc_id = extras.getString("doc_id");
        artist = extras.getString("artist");
        title = extras.getString("title");
        position = extras.getInt(Constants.POSITION);

        comment_box = findViewById(R.id.comment_box);
        comment_Input = findViewById(R.id.comment_input);
        post_comment = findViewById(R.id.post_comment);
        post_comment.setEnabled(false);
        comment_cancel = findViewById(R.id.comment_cancel);

        comment_Input.requestFocus();
        comment_Input.selectAll();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        comment_box.setText(getText(R.string.comment_starter) + " " + title + " " + getText(R.string.by) + " " + artist);

        //comment_added = comment_Input.getText().toString();

        comment_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comment_box.setText("");
                comment_box.setText(s);
                post_comment.setEnabled(count != 0);
                //comment_added = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        comment_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(comment_Input.getText().toString().equals("Comment") || comment_Input.getText() == null) {
            post_comment.setEnabled(false);
        }
            post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    postAComment(doc_id);
            }
        });
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getMusicPost().observe(this, new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                Log.d("Music list size", String.valueOf(musicList.size()));
                Music music = musicList.get(position);
                genre = music.getGenre();
                doc_id = music.getId();
            }
        });


    }

    public void postAComment(String post_id) {
        comment_added = comment_Input.getText().toString();
        Map<String, Object> comment = new HashMap<>();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.username_preference_file), Context.MODE_PRIVATE);
        String username =preferences.getString( getString(R.string.username), getString(R.string.username) );
        comment.put("post_id", post_id);
        comment.put("comment", comment_added);
        comment.put("posted_by", mAuth.getUid());
        comment.put("username", username);
        comment.put("timestamp", FieldValue.serverTimestamp());
        final String[] id = new String[1];

        FirebaseUtils.postComment( comment);

       /* db.collection("hihop_posts").document(doc_id).collection("comments")
                .add(comment)
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

        db.collection("hihop_posts").document(doc_id).collection("comments")
                .document(id[0])
                .update("id", id[0])
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/

    }
}