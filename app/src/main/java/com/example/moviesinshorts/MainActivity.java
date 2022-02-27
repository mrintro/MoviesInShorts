package com.example.moviesinshorts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.moviesinshorts.databinding.ActivityMainBinding;
import com.example.moviesinshorts.fragments.MovieDetailFragment;
import com.example.moviesinshorts.fragments.MovieFragment;
import com.example.moviesinshorts.fragments.SearchFragment;
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
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        setUpCatogaryButtons(fragmentManager);
        setUpSearchButton();
        initInstances();
        initMovieFragment(fragmentManager, trendingInstance);


    }

    private void setUpSearchButton() {
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchButton.setVisibility(View.INVISIBLE);
                binding.movieDetailView.setVisibility(View.INVISIBLE);
                binding.searchFragment.setVisibility(View.VISIBLE);
                binding.searchField.setVisibility(View.VISIBLE);
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.search_fragment, searchFragment, "going to search fragment").addToBackStack("search from main");

            }
        });
    }

    private void initInstances() {
        nowPlayingInstance = new MovieFragment(Constants.NOW_PLAYING_FRAGMENT);
        Log.d(nowPlayingInstance.getFragmentName(),"checking");
        trendingInstance = new MovieFragment(Constants.TRENDING_FRAGMENT);
        Log.d(nowPlayingInstance.getFragmentName(),"checking");
    }

    private void setUpCatogaryButtons(FragmentManager fragmentManager) {
        binding.trendingButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!currentFragement.equals(Constants.TRENDING_FRAGMENT)) {
                    binding.nowPlayingButton.setTextColor(R.color.colorTextHintDefault);
                    binding.nowPlayingButton.setText(R.string.active_now_playing);
                    binding.trendingButton.setTextColor(R.color.black);
                    binding.trendingButton.setText(R.string.trending);
                    trendingInstance.changeMovieFragment(fragmentManager, trendingInstance);
                    currentFragement = Constants.TRENDING_FRAGMENT;
                }
            }
        });
        binding.nowPlayingButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!currentFragement.equals(Constants.NOW_PLAYING_FRAGMENT)) {
                    binding.trendingButton.setTextColor(R.color.colorTextHintDefault);
                    binding.trendingButton.setText(R.string.active_trending);
                    binding.nowPlayingButton.setTextColor(R.color.black);
                    binding.nowPlayingButton.setText(R.string.now_playing);
                    trendingInstance.changeMovieFragment(fragmentManager, nowPlayingInstance);
                    currentFragement = Constants.NOW_PLAYING_FRAGMENT;
                }
            }


        });

    }
    @SuppressLint("ResourceAsColor")
    private void changeToDisactive(Button trendingButton) {
        trendingButton.setTextColor(R.color.colorTextHintDefault);
        trendingButton.setText(R.string.active_trending);
    }

    private void initMovieFragment(FragmentManager fragmentManager, MovieFragment movieFragment) {
        FragmentTransaction fragmentTransactaction = fragmentManager.beginTransaction();
        fragmentTransactaction.replace(R.id.movie_fragment, movieFragment,movieFragment.getFragmentName());
        fragmentTransactaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransactaction.commit();
        currentFragement=movieFragment.getFragmentName();
    }

    public void changeToDetailFragment(MovieDetailFragment movieDetailFragment) {
        Log.d("Checking Nav", "got here");
        binding.movieDetailView.setVisibility(View.INVISIBLE);
        binding.detailScroll.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_fragment, movieDetailFragment, "Movie Detail Page").addToBackStack("DetailsPage");
        fragmentTransaction.commit();
    }
}