package com.example.tweng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class Splash extends AppCompatActivity {
    static SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "TWeng-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "isFIrstTimeLaunch";
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this._context = Splash.this;
        preferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
        SignInButton googleSignInButton = findViewById(R.id.SignInButton);
        googleSignInButton.setVisibility(View.GONE);

       /* if (!isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }**/
        // Configure Google Sign In
       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        //
        mAuth = FirebaseAuth.getInstance();

        //googleSignInButton.setVisibility(VISIBLE);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

       /* ViewGroup rootView = (ViewGroup) findViewById(R.id.splashScreen);
        Fade mFade = new Fade(Fade.IN);

        TransitionManager.beginDelayedTransition(rootView, mFade);
        rootView.**/

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithGoogle:success");
                            //Snackbar snackbar = Snackbar.make(Splash.this, "Signed In Successfully.", Snackbar.LENGTH_SHORT);
                            //snackbar.show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            newUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithGoogle:failure", task.getException());
                            Snackbar.make(findViewById(R.layout.activity_splash), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if (user != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            SignInButton googleSignInButton = findViewById(R.id.SignInButton);
            googleSignInButton.setVisibility(VISIBLE);
        }
    }

    private void newUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> user_data = new HashMap<>();
        db = FirebaseFirestore.getInstance();
        assert user != null;
        for (UserInfo profile : user.getProviderData()) {
            // Id of the provider (ex: google.com)
            String providerId = profile.getProviderId();

            // UID specific to the provider
            String uid = profile.getUid();
            String user_id = user.getUid();

            // Name, email address, and profile photo Url
            String name = profile.getDisplayName();
            String email = profile.getEmail();
            Uri photoUrl = profile.getPhotoUrl();

            // account_image.setImageURI(photoUrl);
            user_data.put("Name",name );
            user_data.put("email", email);
            user_data.put("token", user_id.substring(0, 7));
            Map<String, Object> nested_pref = new HashMap<>();
            String no = "no";
            nested_pref.put("Hip-Hop",no );
            nested_pref.put("GengeTone",no );
            nested_pref.put("Gospel",no );
            nested_pref.put("R&amp;B",no );
            nested_pref.put("Alternative",no );
            nested_pref.put("Children Music",no );
            nested_pref.put("Classics",no );
            nested_pref.put("Country",no );
            nested_pref.put("Fitness Music",no );
            nested_pref.put("Dance",no );
            nested_pref.put("Any Other",no );
            user_data.put("preferences", nested_pref);
        }
        //DocumentReference documentReference = db.collection("Users").document(user.getUid());
        db.collection("Users").document(user.getUid())
                .set(user_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");

                    }

                });


    }


}
