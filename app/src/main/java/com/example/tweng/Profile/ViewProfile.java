package com.example.tweng.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tweng.Constants.Constants;
import com.example.tweng.HomeViewModel;
import com.example.tweng.Music;
import com.example.tweng.R;
import com.example.tweng.chats.MessageList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewProfile extends Fragment {
    TextView username, token;
    RecyclerView influencer_post;
    ImageView profile_pic;
    FloatingActionButton dm;
    HomeViewModel homeViewModel;
    int position;
    String thisProfile;
    Map<String, Object> profile_data;
    Uri profile_pic_uri;

    String profile_username, id;

    List<Music> musicList1 = new ArrayList<>();

    private ViewProfileViewModel mViewModel;

    public static ViewProfile newInstance() {
        return new ViewProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_profile_fragment, container, false);
        username = view.findViewById(R.id.username);
        influencer_post = view.findViewById(R.id.influencerspost_recycler);
        profile_pic = view.findViewById(R.id.profile_pic);
        dm = view.findViewById(R.id.dm_influencer);
        token = view.findViewById(R.id.token);
        //
        assert getArguments() != null;
        if (getArguments() != null) {
            position = getArguments().getInt(Constants.POSITION);
        }

        //
        dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), MessageList.class);
                // Todo Create new Chat
                // Todo pass username and id as extras
                intent.putExtra(Constants.USERNAME, profile_username);
                intent.putExtra(Constants.USER_ID, id);
                requireActivity().startActivity(intent);
            }
        });


        return view;
    }

    public void setMusicList1(List<Music> musicList1) {
        this.musicList1 = musicList1;
    }

    public void setProfile_data(Map<String, Object> profile_data) {
        this.profile_data = profile_data;
        Log.d("Profile data", profile_data.toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewProfileViewModel.class);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getMusicPost().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> musicList) {
                final Music music = musicList.get(position);
               setMusicList1(musicList);
                thisProfile = music.getPosted_by();
                //profile_data = mViewModel.getProfile(music.getPosted_by());
                username.setText(music.getUsername());
                id = music.getPosted_by();

                // TODO description.setText(music.getdescription());
                mViewModel.getProfile(music.getPosted_by())
                        .observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
                            @Override
                            public void onChanged(Map<String, Object> stringObjectMap) {
                                String username1 = (String) stringObjectMap.get("username");
                                String id = (String) stringObjectMap.get("id");
                                profile_username = username1;

                                username.setText(username1);
                                Toast.makeText(getContext(), music.getId(), Toast.LENGTH_SHORT).show();
                                if (stringObjectMap.get("profile_pic_url") != null) {
                                    profile_pic_uri = Uri.parse((String) stringObjectMap.get("profile_pic_url"));
                                    Glide.with(ViewProfile.this).load(profile_pic_uri).into(profile_pic);
                                }
                                token.setText((String) stringObjectMap.get("token"));
                                setProfile_data(stringObjectMap);
                            }
                        });



            }
        });




        // TODO: Use the ViewModel
    }

}
