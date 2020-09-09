package com.example.tweng;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tweng.Player.MusicPlayService;
import com.example.tweng.Profile.Profile;
import com.example.tweng.search.SearchableActivity;
import com.example.tweng.explore.ExploreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton post;
    ActionBar actionBar;
    ActionBar.LayoutParams params;
    String filename = "profile__pic.jpg";

    MusicPlayService musicPlayService;
    public boolean mBound = false;
    Intent intent;

    BottomNavigationView bottomNavigationView;

    @Override
    public void onStart() {
        super.onStart();
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound){
            unbindService(connection);
            mBound = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(getApplicationContext(), MusicPlayService.class);
        setSupportActionBar((Toolbar) findViewById(R.id.top_toolbar));
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        File directory = new File(getApplicationContext().getFilesDir(), filename);
        if (directory.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(directory.getAbsolutePath());
            BitmapDrawable drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 120, 120, true));
            actionBar.setHomeAsUpIndicator(drawable);
        }else {
            actionBar.setHomeAsUpIndicator(R.drawable.outline_account_circle_white_18dp);
        }

        params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );



        final Fragment home = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.add(R.id.scrollable_in_linear,fragment);
        transaction.add(R.id.scrollable_in_linear, home);
        //transaction.addToBackStack(null);
        transaction.commit();

        post = findViewById(R.id.post_button);

       bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new
         BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
             ImageView logo = findViewById(R.id.logo);
             Toolbar topbar = findViewById(R.id.top_toolbar);

             switch (item.getItemId()) {
                 case R.id.search:
                     final Fragment fragment = new ExploreFragment();
                     actionBar.setDisplayShowTitleEnabled(false);
                     post.setVisibility(View.GONE);
                     loadFragment(fragment, "Explore Fragment");
                     break;

                 /*case R.id.inbox:
                   fragment1 = new InboxFragment();
                    logo.setVisibility(View.GONE);
                   post.setVisibility(View.GONE);
                    customView.setVisibility(View.GONE);
                    actionBar.setDisplayShowTitleEnabled(true);

                    actionBar.setTitle("Chats");
                     topbar.setVisibility(View.GONE);
                   loadFragment(fragment1);
                    break;**/

                 case R.id.home:
                     logo.setVisibility(View.VISIBLE);
                     topbar.setVisibility(View.VISIBLE);
                     post.setVisibility(View.VISIBLE);
                     actionBar.setDisplayShowTitleEnabled(false);

                     loadFragment(home, "Home Fragment");
                     break;
             }
             return true;
         }
     });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Posting.class);
                startActivity(intent);
            }
        });

        startService(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem myItem = menu.findItem(R.id.search_go_btn);
        myItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.topbarmenu, menu1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // User chose the "Settings" item, show the app settings UI...
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.search_go_btn){
            Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
            startActivity(intent);
            Log.d("search", "seafofof");
            //onSearchRequested();
        }

        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }


    private void loadFragment(final Fragment fragment, String name) {
        // load fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        int count = fragmentManager.getBackStackEntryCount();

        //transaction.add(R.id.scrollable_in_linear,fragment);
        transaction.replace(R.id.scrollable_in_linear, fragment);
       // transaction.addToBackStack(name);
        transaction.commit();

    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayService.LocalBinder binder = (MusicPlayService.LocalBinder) service;
            musicPlayService = binder.getService();
            //musicPlayService.setMusicList(m);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

}
