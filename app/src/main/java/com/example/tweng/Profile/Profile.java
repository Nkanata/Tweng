package com.example.tweng.Profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tweng.About;
import com.example.tweng.R;
import com.example.tweng.Splash;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    String TAG = "Profile Activity";
    TextView acc_username ;
    FirebaseUser user;
    private FirebaseFirestore db;
    String filename = "profile__pic.jpg";
    //
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "profile_pic";
    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.account_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");

        _context = getApplicationContext();
        Log.d(TAG, "On profile");
        ImageView account_image = findViewById(R.id.account_image);
        TextView acc_email = findViewById(R.id.account_email);
        TextView acc_names = findViewById(R.id.account_names);
        TextView preference = findViewById(R.id.preference);
        TextView token = findViewById(R.id.account_token);
        token.setText(mAuth.getUid().substring(0, 7));

        acc_username = findViewById(R.id.account_username);
        //
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.username_preference_file), Context.MODE_PRIVATE);
        String username =preferences.getString( getString(R.string.username), getString(R.string.username) );
        if (!username.equals("")){
            acc_username.setText(username);
        }

        //
        TextView about = findViewById(R.id.about);
        TextView log_out = findViewById(R.id.log_out);
        //signOut.setText("Yes");
        //
        File directory = new File(getApplicationContext().getFilesDir(), filename);
        //file = new File(directory, "profile_pic" + ".jpg");
        if (directory.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(directory.getAbsolutePath());
            account_image.setImageBitmap(bitmap);
        }
        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
                //String token = profile.getToken();
                acc_names.setText(name);
                acc_email.setText(email);
               // account_image.setImageURI(photoUrl);

            }
        }
        if (log_out != null){
            log_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                    Log.d(TAG, "logged out");

                }
            });
        }
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
            }
        });
        preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Preferences.class);
                startActivity(intent);
            }
        });
        acc_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cliiicvked", "yoh yoh");
                showDialog(Profile.this);
            }
        });
        account_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewProfilePic.class);
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.editextview);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        final EditText et = dialog.findViewById(R.id.user_input);
        et.setText(acc_username.getText().toString());
        et.setSelectAllOnFocus(true);
        et.requestFocus();


        Button btnok = dialog.findViewById(R.id.save);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String username = et.getText().toString();
                acc_username.setText(username);
                updateUsername(username);
                writePreference(username);
            }
        });

        Button btncn = dialog.findViewById(R.id.cancel);
        btncn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        final Intent intent  = new Intent(this, Splash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Google sign out
        /**googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });**/
    }

    private void updateUsername(String username){
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("username", username);

        db.collection("Users").document(user.getUid())
                .update(userdata)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("udated", "updated username");
                    }
                });
    }

    private void writePreference(String username){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(getString(R.string.username_preference_file), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(getString(R.string.username), username);
        editor.apply();
    }


}