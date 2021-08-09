package com.example.tweng.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.Music;
import com.example.tweng.Profile.GuestProfile;
import com.example.tweng.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    SearchView searchView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Music> resultList = new ArrayList<>();
    List<GuestProfile> influence_rsList = new ArrayList<>();
    RecyclerView influence_rs;
    RecyclerView music_results;
    TextView search_guide, no_results, connection;
    ResultsAdapter resultsAdapter;
    Influence_rAdapter influence_rAdapter;
    RecyclerView.LayoutManager managerInf, managerMus;
    DefaultItemAnimator animator = new DefaultItemAnimator();
    SearchViewModel search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //
        toolbar = findViewById(R.id.toolbarS);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        search = new ViewModelProvider(this).get(SearchViewModel.class);

        no_results = findViewById(R.id.no_results);
        connection = findViewById(R.id.connection);
        connection.setVisibility(View.GONE);
        no_results.setVisibility(View.GONE);
        searchView = findViewById(R.id.search_view);
        searchView.requestFocus();
        searchView.onActionViewExpanded();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        handleIntent(getIntent());

        /*searchView.setSuggestionsAdapter(new CursorAdapter(){
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return null;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

            }
        });**/

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);
                searchView.setQuery(suggestion, true);
                return true;
            }
        });

        search_guide = findViewById(R.id.search_guide);
        influence_rs = findViewById(R.id.influence_rsRecycler);
        music_results = findViewById(R.id.music_resultsRecycler);

        managerInf = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        influence_rs.setLayoutManager(managerInf);
        influence_rs.setItemAnimator(animator);

        managerMus = new LinearLayoutManager(getApplicationContext());
        music_results.setLayoutManager(managerMus);
        music_results.setItemAnimator(animator);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            doMySearch(query);
            if (query != null) {
                Log.d("Query", query);
            }
        }
    }

    private void doMySearch(String query) {
        search.getResults(query).observe(this, music -> {

            if (!isNetworkAvailable()){
                connection.setVisibility(View.VISIBLE);
                return;
            }
            if (music.size() != 0){
                search_guide.setVisibility(View.GONE);
                resultsAdapter = new ResultsAdapter(music, getApplicationContext());
                music_results.setAdapter(resultsAdapter);
            }
            if (music.size() == 0){
                no_results.setVisibility(View.VISIBLE);
            }
        });
        //musicSearch(query);
       // influence_rsSearch(query);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void musicSearch(String query) {
        Query ref = db.collectionGroup("music_posts");
        ref.limit(10);
        ref.orderBy("timestamp");

        ref.whereEqualTo("artist", query).get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                        }
                    }
                });
        ref.whereEqualTo("title", query).get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                            Log.d("results", String.valueOf(resultList.size()));
                            resultsAdapter = new ResultsAdapter(resultList, getApplicationContext());
                            music_results.setAdapter(resultsAdapter);

                        }
                    }
                });
        ref.whereEqualTo("username", query).get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                        }
                    }
                });

        ref.whereEqualTo("album", query).get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Music music = doc.toObject(Music.class);
                            resultList.add(music);
                        }
                    }
                });
        if (resultList.size() > 0) {
            // Todo present results
            search_guide.setVisibility(View.GONE);
            resultsAdapter = new ResultsAdapter(resultList, getApplicationContext());
            music_results.setAdapter(resultsAdapter);
        } else {
            // couldn't find results
        }
    }

    private void influence_rsSearch(String query) {
        Query ref = db.collection("influence_rs");
        ref.limit(10);
        ref.orderBy("timestamp");

        ref.whereEqualTo("username", query).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        GuestProfile profile = doc.toObject(GuestProfile.class);
                        influence_rsList.add(profile);
                    }
                });

        if (influence_rsList.size() > 0) {
            search_guide.setVisibility(View.GONE);
            influence_rAdapter = new Influence_rAdapter(influence_rsList, getApplicationContext());
            influence_rs.setAdapter(influence_rAdapter);
        }
    }

    private void clearHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();

    }


}
