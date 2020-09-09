package com.example.tweng.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tweng.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Preferences extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_music), Context.MODE_PRIVATE);

        setSupportActionBar((Toolbar) findViewById(R.id.pref_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        String[] genre = getResources().getStringArray(R.array.music_genres);
        ArrayList<String> genres = new ArrayList<>();
        Collections.addAll(genres, genre);
        setCategoryChips(genres);

    }
    public void setCategoryChips(ArrayList<String> categories) {
        ChipGroup chipsPrograms = findViewById(R.id.chipsPrograms);
        for (String category : categories) {
            @SuppressLint("InflateParams")
            final Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_category, null, false);
            mChip.setText(category);
            if (Objects.equals(sharedPreferences.getString(category, "no"), "yes")){
                mChip.setChecked(true);
            }

            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            mChip.setPadding(paddingDp, 0, paddingDp, 0);
            mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    writeMusicPreference(compoundButton.getText().toString(), b);
                    writePreferencesToFireStore(compoundButton.getText().toString(), b);
                    if (b) {
                        Log.d(compoundButton.getText().toString(), "cliickeddedmfkfdkdfn");
                    }else {
                        Log.d(compoundButton.getText().toString(), "unclickedcliickeddedmfkfdkdfn");
                    }
                }
            });
            chipsPrograms.addView(mChip);
        }
    }

    private void writeMusicPreference(String preference, Boolean b){
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        if (b){
            editor.putString(preference, "yes");
            //Log.d(preference, "yes");
        }else {
            editor.putString(preference, "no");
            //Log.d(preference, "no");

        }
        editor.apply();
    }

    private void writePreferencesToFireStore(String preference, Boolean b){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       if (b) {
            db.collection("Users").document(user.getUid())
                    .update("preferences."+preference, "yes")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("firebaaaseee", "music Pref uodated");
                        }
                    });
        }else {
            db.collection("Users").document(user.getUid())
                    .update("preferences."+preference, "no")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("firebaaaseee", "music Pref uodated");
                        }
                    });
        }


    }

}
