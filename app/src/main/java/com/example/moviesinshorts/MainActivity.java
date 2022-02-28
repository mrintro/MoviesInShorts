package com.example.moviesinshorts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    @NotNull
    private Handler handler;

    private String currentFragement;
    private FragmentManager fragmentManager;


    private long lastEditTime;
    private final long delay = 1000;

    private final Runnable handleUserType = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis() > lastEditTime+delay-500){
                Log.d("Checking click", "searching");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        handler = new Handler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        setUpCatogaryButtons(fragmentManager);
        setUpSearchButton();
//        binding.searchField.setOnFocusChangeListener(this);
        initInstances();
        initMovieFragment(fragmentManager, trendingInstance);
//        initTextChangeListener();

    }

//    private void initTextChangeListener() {
//        binding.searchField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                handler.removeCallbacks(handleUserType);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.length() > 0){
//                    lastEditTime = System.currentTimeMillis();
//                    handler.postDelayed(handleUserType, delay);
//                }
//            }
//        });
//    }


    @Override
    public void onBackPressed() {
        binding.movieDetailView.setVisibility(View.VISIBLE);
        binding.searchFragment.setVisibility(View.INVISIBLE);
        binding.searchButton.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
    }

    private void setUpSearchButton() {
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchButton.setVisibility(View.INVISIBLE);
                binding.movieDetailView.setVisibility(View.INVISIBLE);
                binding.searchFragment.setVisibility(View.VISIBLE);
//                binding.searchField.setVisibility(View.VISIBLE);
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.search_fragment, searchFragment, "going to search fragment").addToBackStack("search from main");
                fragmentTransaction.commit();

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
                    binding.nowPlayingMovieFragment.setVisibility(View.INVISIBLE);
                    binding.trendingMovieFragment.setVisibility(View.VISIBLE);

//                    trendingInstance.changeMovieFragment(fragmentManager, trendingInstance);
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
                    binding.trendingMovieFragment.setVisibility(View.INVISIBLE);
                    binding.nowPlayingMovieFragment.setVisibility(View.VISIBLE);
//                    trendingInstance.changeMovieFragment(fragmentManager, nowPlayingInstance);
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


    //Change this to visible fragment change scenerio
    private void initMovieFragment(FragmentManager fragmentManager, MovieFragment movieFragment) {
        FragmentTransaction fragmentTransactaction = fragmentManager.beginTransaction();
        fragmentTransactaction.replace(R.id.trending_movie_fragment, movieFragment,movieFragment.getFragmentName());
        fragmentTransactaction.replace(R.id.now_playing_movie_fragment, nowPlayingInstance, nowPlayingInstance.getFragmentName());
        fragmentTransactaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransactaction.commit();
        currentFragement=movieFragment.getFragmentName();
    }

    public void changeToDetailFragment(MovieDetailFragment movieDetailFragment) {
        Log.d("Checking Nav", "got here");
        binding.movieDetailView.setVisibility(View.INVISIBLE);
        binding.detailScroll.setVisibility(View.VISIBLE);
        binding.searchFragment.setVisibility(View.INVISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_fragment, movieDetailFragment, "Movie Detail Page");
        fragmentTransaction.addToBackStack("DetailsPage");
        fragmentTransaction.commit();
    }
//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if(binding.searchField.hasFocus()){
//            Log.d("Running here","focus check");
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }




}