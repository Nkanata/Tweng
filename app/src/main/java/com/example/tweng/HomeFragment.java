package com.example.tweng;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.Constants.Constants;
import com.example.tweng.Notifications.CreateNotification;
import com.example.tweng.Player.MusicPlayService;
import com.example.tweng.comments.MakeAComment;
import com.example.tweng.musicdetail.MusicDetailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel mViewModel;
    //
    private RecyclerView recyclerView;
    private List<Music> musicList1;
    private MusicPostAdapter musicAdapter;
    //
    MediaPlayer mediaPlayer;
    MusicPlayService musicPlayService;
    boolean mBound = false;
    boolean isPlaying = false;
    private int currentPlaying = -1;
    private int prevPlaying = -1;
    boolean isRegistered = false;
    private long download_id;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       /* OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);**/
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(requireActivity().getApplicationContext(), MusicPlayService.class);
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mBound = true;
        //  startService(intent);
        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        requireActivity().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        isRegistered = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton post = requireActivity().findViewById(R.id.post_button);
        post.setVisibility(View.VISIBLE);
    }

    PlayMusic playMusic = new PlayMusic() {
        @Override
        public void onPlay(List<Music> musicList, int adapter_position) {
            musicPlayService.playMusic(musicList, adapter_position);
        }

        @Override
        public void onPause(int adapter_position) {
            musicPlayService.pauseMusic();
        }

        @Override
        public void onResumePlay(int adapter_position) {
            musicPlayService.resumePlay();
        }
    };

    final MusicPostAdapter.OnPostListener onPostListener = new MusicPostAdapter.OnPostListener() {
        @Override
        public void onPostClickListener(int position) {
            final Fragment musicDetailFragment = new MusicDetailFragment(onPostListener, musicPlayService.getDuration());
            Music musicPassed = musicList1.get(position);
            Bundle music = new Bundle();
            music.putString("title",musicPassed.getTitle());
            music.putString("artist", musicPassed.getArtist());
            music.putInt("position", position);
            musicDetailFragment.setArguments(music);
            if (currentPlaying == position){
                music.putBoolean("isPlaying", true);
            }else {
                music.putBoolean("isPlaying", false);
            }

            loadFragment(musicDetailFragment);
        }

        @Override
        public void onPlayClickListener(int position) throws IOException {
            //playMusic(musicList1, position);
            if (position != currentPlaying) {
                prevPlaying = currentPlaying;
                musicAdapter.setPrevPlaying(prevPlaying);
                musicAdapter.notifyItemChanged(prevPlaying);
                currentPlaying = position;
                Log.d("not equivaaalent", String.valueOf(currentPlaying));
                Log.d("prev Play", String.valueOf(prevPlaying));
                playMusic.onPlay(musicList1, position);
                isPlaying = true;
            } else {
                Log.d("equivaaalent", String.valueOf(currentPlaying));
                if (isPlaying) {
                    playMusic.onPause(position);
                    isPlaying = false;
                } else {
                    playMusic.onResumePlay(position);
                    isPlaying = true;
                }
            }
            //musicAdapter.notifyItemChanged();
        }

        @Override
        public void onDownloadClickListener(int position) {
            Toast.makeText(requireActivity(), "Downloading", Toast.LENGTH_LONG).show();
            beginDownload(position);
        }

        @Override
        public void onCommentClickListener(int position) {
            Music music = musicList1.get(position);
            Intent intent = new Intent(requireActivity().getApplicationContext(), MakeAComment.class);
            intent.putExtra("title", music.getTitle());
            intent.putExtra("artist", music.getArtist());
            intent.putExtra("doc_id", music.getId());
            intent.putExtra(Constants.POSITION, position);
            requireActivity().startActivity(intent);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = view.findViewById(R.id.music_post_recycler);
        musicList1 = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(musicAdapter);
        //recyclerView.setNestedScrollingEnabled(false);
        //fethc();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mViewModel.getMusicPost().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                musicList1 = musicList;
                musicAdapter = new MusicPostAdapter(getActivity(), musicList1, onPostListener);
                recyclerView.setAdapter(musicAdapter);
            }
        });

        //mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        //transaction.add(R.id.scrollable_in_linear,fragment);

        transaction.replace(R.id.scrollable_in_linear, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered) {
            requireActivity().unregisterReceiver(broadcastReceiver);
            requireActivity().unregisterReceiver(downloadReceiver);
        }
        if (mBound) {
            requireActivity().unbindService(connection);
            mBound = false;
        }

    }


    public interface PlayMusic {
        void onPlay(List<Music> musicList, int adapter_position) throws IOException;

        void onPause(int adapter_position);

        void onResumePlay(int adapter_position);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            assert action != null;
            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    //onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying) {
                        musicAdapter.setSetPause(currentPlaying);
                        musicAdapter.notifyItemChanged(currentPlaying);
                        isPlaying = false;
                    } else {
                        musicAdapter.setSetPlay(currentPlaying);
                        musicAdapter.notifyItemChanged(currentPlaying);
                        isPlaying = true;
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    //onTrackNext();
                    break;
            }
        }
    };

    private void beginDownload(int position) {
        Music music = musicList1.get(position);

        File musicFile = new File(requireActivity().getExternalFilesDir(null), music.getTitle() + "-" + music.getArtist() + ".mp3");

        DownloadManager.Request request;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request = new DownloadManager.Request(Uri.parse(music.getUrl()))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(false)
                    .setAllowedOverMetered(false)
                    .setTitle("Tweng Downloading " + music.getTitle() + " by " + music.getArtist() + ".mp3")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, music.getTitle() + "-" + music.getArtist() + ".mp3")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setRequiresCharging(false);
        } else {
            request = new DownloadManager.Request(Uri.parse(music.getUrl()))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(false)
                    .setAllowedOverMetered(false)
                    .setTitle("Tweng Downloading " + music.getTitle() + " by " + music.getArtist() + ".mp3")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, music.getTitle() + "-" + music.getArtist() + ".mp3")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }

        DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        assert downloadManager != null;
        download_id = downloadManager.enqueue(request);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (download_id == id) {
                Toast.makeText(requireActivity(), "Download complete", Toast.LENGTH_SHORT).show();
            }
        }
    };


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


    //
    private void playMusic(List<Music> musicList, int adapter_position) throws IOException {
        Log.d("music list size", String.valueOf(musicList.size()));
        Log.d("position", String.valueOf(adapter_position));
        Music music = musicList.get(adapter_position);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(requireContext(), Uri.parse(music.getUrl()));
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

    }


}
