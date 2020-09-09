package com.example.tweng.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tweng.HomeViewModel;
import com.example.tweng.Music;
import com.example.tweng.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExploreFragment extends Fragment {
    private PostAdapter latestPostAdapter;
    private PostAdapter trendingPostAdapter;
    private PostAdapter topChartsPostAdapter;
    private List<Music> musicList1;
    BottomNavigationView bottomNavigationView;

    private RecyclerView trending_recycler, chart_recycler, latest_recycler;

    private HomeViewModel mViewModel;
    private ExploreViewModel exploreViewModel;

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exploreViewModel = new ViewModelProvider(requireActivity()).get(ExploreViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore_fragment, container, false);
        trending_recycler = view.findViewById(R.id.trends_recycler);
        chart_recycler = view.findViewById(R.id.top_recycler);
        latest_recycler = view.findViewById(R.id.latest_recycler);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();

        trending_recycler.setLayoutManager(mLayoutManager);
        trending_recycler.setItemAnimator(defaultItemAnimator);

        latest_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        latest_recycler.setItemAnimator(defaultItemAnimator);

        chart_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        chart_recycler.setItemAnimator(defaultItemAnimator);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* mViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getMusicPost().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> music) {
                Log.d("Music list size", String.valueOf(music.size()));
                trendingPostAdapter = new PostAdapter(music, getContext(), "trending");
                trending_recycler.setAdapter(trendingPostAdapter);

                topChartsPostAdapter = new PostAdapter(music, getContext(), "top_charts");
                chart_recycler.setAdapter(topChartsPostAdapter);

                latestPostAdapter = new PostAdapter(music, getContext(), "latest");
                latest_recycler.setAdapter(latestPostAdapter);
            }
        });**/
        exploreViewModel.getExploreLiveData().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> music) {
                Log.d("Charts Music list size", String.valueOf(music.size()));
                if (musicList1 != null){
                    //musicList1.clear();
                }
                musicList1 = music;
                topChartsPostAdapter = new PostAdapter(musicList1, getContext(), "top_charts");
                chart_recycler.setAdapter(topChartsPostAdapter);
            }
        });

        exploreViewModel.getTrendingLiveData().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {

            @Override
            public void onChanged(List<Music> music) {
                Log.d("Trending Music listSize", String.valueOf(music.size()));

                if (musicList1 != null){
                   // musicList1.clear();
                }
                musicList1 = music;
                trendingPostAdapter = new PostAdapter(musicList1, getContext(), "trending");
                trending_recycler.setAdapter(trendingPostAdapter);
            }
        });

        exploreViewModel.getLatestLiveData().observe(getViewLifecycleOwner(), new Observer<List<Music>>() {
            @Override
            public void onChanged(List<Music> music) {
                Log.d("Latest Music list size", String.valueOf(music.size()));

                if (musicList1 != null){
                    //musicList1.clear();
                }
                musicList1 = music;
                latestPostAdapter = new PostAdapter(musicList1, getContext(), "latest");
                latest_recycler.setAdapter(latestPostAdapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton post = requireActivity().findViewById(R.id.post_button);
        post.setVisibility(View.GONE);
    }

}
