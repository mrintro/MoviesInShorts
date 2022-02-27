package com.example.moviesinshorts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.moviesinshorts.databinding.ActivityMainBinding;
import com.example.moviesinshorts.databinding.ActivityMainBindingImpl;
import com.example.moviesinshorts.movieFragment.MovieFragment;
import com.example.moviesinshorts.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    @NotNull
    private ActivityMainBinding binding;
    @NotNull
    private MovieFragment nowPlayingInstance;
    @NotNull
    private MovieFragment trendingInstance;
    private String currentFragement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        setUpButtons(fragmentManager);
        initInstances();
        initMovieFragment(fragmentManager, trendingInstance);


    }

    private void initInstances() {
        nowPlayingInstance = new MovieFragment(Constants.NOW_PLAYING_FRAGMENT);
        Log.d(nowPlayingInstance.getFragmentName(),"checking");
        trendingInstance = new MovieFragment(Constants.TRENDING_FRAGMENT);
        Log.d(nowPlayingInstance.getFragmentName(),"checking");
    }

    private void setUpButtons(FragmentManager fragmentManager) {
        binding.trendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentFragement.equals(Constants.TRENDING_FRAGMENT)) {
                    trendingInstance.changeMovieFragment(fragmentManager, trendingInstance);
                    currentFragement = Constants.TRENDING_FRAGMENT;
                }
            }
        });
        binding.nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentFragement.equals(Constants.NOW_PLAYING_FRAGMENT)) {
                    trendingInstance.changeMovieFragment(fragmentManager, nowPlayingInstance);
                    currentFragement = Constants.NOW_PLAYING_FRAGMENT;
                }
            }
        });

    }

    private void initMovieFragment(FragmentManager fragmentManager, MovieFragment movieFragment) {
        FragmentTransaction fragmentTransactaction = fragmentManager.beginTransaction();
        fragmentTransactaction.replace(R.id.movie_fragment, movieFragment,movieFragment.getFragmentName());
        fragmentTransactaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransactaction.commit();
        currentFragement=movieFragment.getFragmentName();
    }
}