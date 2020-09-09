package com.example.tweng.musicdetail;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tweng.Constants.Constants;
import com.example.tweng.HomeViewModel;
import com.example.tweng.Music;
import com.example.tweng.MusicPostAdapter;
import com.example.tweng.Notifications.CreateNotification;
import com.example.tweng.Profile.ViewProfile;
import com.example.tweng.Profile.ViewProfileViewModel;
import com.example.tweng.R;
import com.example.tweng.comments.Comment;
import com.example.tweng.comments.CommentsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicDetailFragment extends Fragment{

    private MusicDetailViewModel mViewModel;
    FloatingActionButton comment, post;
    MusicPostAdapter.OnPostListener onPostListener;
    int position;
    LinearLayout download, detail_layout, commentB, play;
    ImageButton play_button, download_button;
    TextView play_text, posted_by, song_title, artist, description, album, counter, music_duration;
    ImageView song_art;
    RecyclerView comment_Recycler;
    SeekBar music_progress;
    boolean isPlaying = false;
    boolean isRegistered = false;
    HomeViewModel homeViewModel;
    List<Music> musicList1 = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    ViewProfileViewModel viewProfileViewModel;
    Map<String, Object> profile_data = new HashMap<>();

    long duration;


    public MusicDetailFragment(MusicPostAdapter.OnPostListener onPostListener, long duration) {
        this.onPostListener = onPostListener;
        this.duration = duration;
    }

    public MusicDetailFragment() {

    }
    public static MusicDetailFragment newInstance() {
        return new MusicDetailFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        isRegistered = true;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_detail_fragment, container, false);
        //


        assert getArguments() != null;
        position = getArguments().getInt("position");
        isPlaying = getArguments().getBoolean("isPlaying");
        post = requireActivity().findViewById(R.id.post_button);
        post.setVisibility(View.GONE);
        comment = view.findViewById(R.id.mDetailComment);
        play = view.findViewById(R.id.play);
        download = view.findViewById(R.id.download);
        play_button = view.findViewById(R.id.play_button);
        play_text = view.findViewById(R.id.play_text);
        posted_by = view.findViewById(R.id.posted_by);
        song_title = view.findViewById(R.id.song_title);
        artist = view.findViewById(R.id.detail_artist);
        album = view.findViewById(R.id.detail_album);
        description = view.findViewById(R.id.detail_description);
        counter = view.findViewById(R.id.counter);
        music_duration = view.findViewById(R.id.music_duration);
        song_art = view.findViewById(R.id.song_art);
        comment_Recycler = view.findViewById(R.id.comments_recycler);
        music_progress = view.findViewById(R.id.music_seekbar);
        //
        counter.setVisibility(View.GONE);
        music_duration.setVisibility(View.GONE);
        music_progress.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        comment_Recycler.setLayoutManager(mLayoutManager);
        comment_Recycler.setItemAnimator(new DefaultItemAnimator());
        //
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostListener.onCommentClickListener(position);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isPlaying){
                        onPostListener.onPlayClickListener(position);
                        play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
                        play_text.setText(R.string.pause);
                        isPlaying = true;
                    }else {
                        onPostListener.onPlayClickListener(position);
                        play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
                        play_text.setText(R.string.play);
                        isPlaying = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostListener.onDownloadClickListener(position);
            }
        });
        posted_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Music music = musicList1.get(position);
                final Fragment ViewProfile = new ViewProfile();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.POSITION, position);
                ViewProfile.setArguments(bundle);
                loadFragment(ViewProfile);
            }
        });
        //
        if (isPlaying){
            play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
            play_text.setText(R.string.pause);
            // Todo connect to player service and attach progress to progress bar
        }
        commentsAdapter = new CommentsAdapter(getContext(), comments);
        comment_Recycler.setAdapter(commentsAdapter);
        //fetch();

        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //final String[] music_doc_id = new String[1];
        final Music[] music1 = new Music[1];
        mViewModel = new ViewModelProvider(this).get(MusicDetailViewModel.class);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewProfileViewModel = new ViewModelProvider(this).get(ViewProfileViewModel.class);
        homeViewModel.getMusicPost().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<Music> musicList) {
                musicList1 = musicList;
                Music music = musicList.get(position);
                music1[0] = musicList.get(position);

                //music_doc_id[0] = music.getId();
                song_title.setText(music.getTitle() + " - " + music.getArtist());
                artist.setText(getResources().getString(R.string.artist) +":  "+music.getArtist());
                album.setText(getResources().getString(R.string.album) +":  "+music.getAlbum());
                Glide.with(requireContext()).load(music.getMusic_art_url()).into(song_art);


                viewProfileViewModel.getProfile(music.getPosted_by())
                        .observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
                            @Override
                            public void onChanged(Map<String, Object> stringObjectMap) {
                                String username = (String) stringObjectMap.get("username");
                                posted_by.setText(username);
                            }
                        });


                // TODO description.setText(music.getdescription());
                mViewModel.getComments(music).observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
                    @Override
                    public void onChanged(List<Comment> comments) {
                        commentsAdapter = new CommentsAdapter(getContext(), comments);
                        comment_Recycler.setAdapter(commentsAdapter);

                    }
                });

            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(duration);
        music_duration.setText(dateFormat.format(date));

    }

    private void fetch() {

        Comment comment = new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", "");
        comments.add(comment);

        comment = new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", "");
        comments.add(comment);
        comment = new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", "");
        comments.add(comment);
        comment = new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", "");
        comments.add(comment);
        comments.add(new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", ""));
        comments.add(new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", ""));
        comments.add(new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", ""));
        comments.add(new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", ""));
        comments.add(new Comment("code plus design makes Brian happy code plus design makes Brian happycode plus design makes Brian happycode plus design makes Brian happycode plus design ma" +
                "kes Brian happycode plus design makes Brian happy" +
                "\"\n", null, "nkanata", ""));
        commentsAdapter.notifyDataSetChanged();
    }


    //
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
                        play_button.setImageResource(R.drawable.outline_play_arrow_white_18dp);
                        play_text.setText(R.string.play);
                        isPlaying = false;
                    } else {
                        play_button.setImageResource(R.drawable.outline_pause_circle_outline_white_18dp);
                        play_text.setText(R.string.pause);
                        isPlaying = true;
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    //onTrackNext();
                    break;
            }
        }
    };
    //
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
        }
    }
}
