package com.example.moviesinshorts.fragments;

import static com.example.moviesinshorts.R.string.active_now_playing;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentHomeBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding fragmentHomeBinding;
    @NotNull
    private MovieFragment nowPlayingInstance;
    @NotNull
    private MovieFragment trendingInstance;
    private String currentFragment;
    @NotNull
    private FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = fragmentHomeBinding.getRoot();

        setUpCategoryButton();
        initInstances();
        initMovieFragment();
        
        return view;

    }



    private void initMovieFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.trending_movie_fragment, trendingInstance,trendingInstance.getFragmentName());
        fragmentTransaction.replace(R.id.now_playing_movie_fragment, nowPlayingInstance, nowPlayingInstance.getFragmentName());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        currentFragment=trendingInstance.getFragmentName();
    }


    private void initInstances() {
            nowPlayingInstance = new MovieFragment(Constants.NOW_PLAYING_FRAGMENT);
            Log.d(nowPlayingInstance.getFragmentName(), "checking");
            trendingInstance = new MovieFragment(Constants.TRENDING_FRAGMENT);
            Log.d(nowPlayingInstance.getFragmentName(), "checking");

    }

    private void setUpCategoryButton() {
        fragmentHomeBinding.trendingButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!currentFragment.equals(Constants.TRENDING_FRAGMENT)) {
                    fragmentHomeBinding.nowPlayingButton.setTextColor(R.color.colorTextHintDefault);
                    fragmentHomeBinding.nowPlayingButton.setText(active_now_playing);
                    fragmentHomeBinding.trendingButton.setTextColor(R.color.black);
                    fragmentHomeBinding.trendingButton.setText(R.string.trending);
                    fragmentHomeBinding.nowPlayingMovieFragment.setVisibility(View.INVISIBLE);
                    fragmentHomeBinding.trendingMovieFragment.setVisibility(View.VISIBLE);
                    currentFragment = Constants.TRENDING_FRAGMENT;
                }
            }
        });
        fragmentHomeBinding.nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!currentFragment.equals(Constants.NOW_PLAYING_FRAGMENT)) {
                    fragmentHomeBinding.trendingButton.setTextColor(R.color.colorTextHintDefault);
                    fragmentHomeBinding.trendingButton.setText(R.string.active_trending);
                    fragmentHomeBinding.nowPlayingButton.setTextColor(R.color.black);
                    fragmentHomeBinding.nowPlayingButton.setText(R.string.now_playing);
                    fragmentHomeBinding.trendingMovieFragment.setVisibility(View.INVISIBLE);
                    fragmentHomeBinding.nowPlayingMovieFragment.setVisibility(View.VISIBLE);
                    currentFragment = Constants.NOW_PLAYING_FRAGMENT;
                }
            }
        });

    }

}