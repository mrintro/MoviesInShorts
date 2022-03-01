package com.example.moviesinshorts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

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
import com.example.moviesinshorts.fragments.HomeFragmentDirections;
import com.example.moviesinshorts.fragments.MovieDetailFragment;
import com.example.moviesinshorts.fragments.MovieDetailFragmentDirections;
import com.example.moviesinshorts.fragments.MovieFragment;
import com.example.moviesinshorts.fragments.SearchFragment;
import com.example.moviesinshorts.fragments.SearchFragmentDirections;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    @NotNull
    private ActivityMainBinding binding;
    @NotNull
    private Handler handler;

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
        setUpSearchButton();
//        setUpCategoryButton(fragmentManager);
//        setUpSearchButton();
////        binding.searchField.setOnFocusChangeListener(this);
//        initInstances();
//        initMovieFragment(fragmentManager, trendingInstance);
//        initTextChangeListener();

    }

//    public void navigate(MovieModel movie, Object action) {
//        NavHostFragment navHostFragment =  (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
//        NavController navController = navHostFragment.getNavController();
//        action instanceof  ? (() action) : null;
//    }

    public void navigateToDetailFragment(MovieModel movie ) {
        NavHostFragment navHostFragment =  (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        Log.d("Ayush", "Ayush " + movie.getTitle());
        HomeFragmentDirections.ActionHomeFragmentToMovieDetailFragment action = HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(movie);
        action.setMovieData(movie);
        navController.navigate(action);
    }

    public void navigateToDetails(MovieModel movie) {
        NavHostFragment navHostFragment =  (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        SearchFragmentDirections.ActionSearchFragmentToMovieDetailFragment action = SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(movie);
        action.setMovieData(movie);
        navController.navigate(action);
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

//
//    @Override
//    public void onBackPressed() {
//        binding.movieDetailView.setVisibility(View.VISIBLE);
//        binding.searchFragment.setVisibility(View.INVISIBLE);
//        binding.searchButton.setVisibility(View.VISIBLE);
//        fragmentManager.popBackStack();
//    }
//
    private void setUpSearchButton() {
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment navHostFragment =  (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                NavController navController = navHostFragment.getNavController();
               Log.d("Ayush", "Ayush " +  navController.getCurrentDestination().getDisplayName());
               if (navController.getCurrentDestination().getDisplayName().equals("com.example.moviesinshorts:id/homeFragment"))
                navController.navigate(R.id.action_homeFragment_to_searchFragment);
               else
                   navController.navigate(R.id.action_movieDetailFragment_to_searchFragment);

//                binding.searchField.setVisibility(View.VISIBLE);

            }
        });
    }



//    @SuppressLint("ResourceAsColor")
//    private void changeToInactive(Button trendingButton) {
//        trendingButton.setTextColor(R.color.colorTextHintDefault);
//        trendingButton.setText(R.string.active_trending);
//    }
//
//
//    //Change this to visible fragment change scenario

//
//    public void changeToDetailFragment(MovieDetailFragment movieDetailFragment) {
//        Log.d("Checking Nav", "got here");
//        binding.movieDetailView.setVisibility(View.INVISIBLE);
//        binding.detailScroll.setVisibility(View.VISIBLE);
//        binding.searchFragment.setVisibility(View.INVISIBLE);
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.movie_detail_fragment, movieDetailFragment, "Movie Detail Page");
//        fragmentTransaction.addToBackStack("DetailsPage");
//        fragmentTransaction.commit();
//    }
////
////    @Override
////    public void onFocusChange(View v, boolean hasFocus) {
////        if(binding.searchField.hasFocus()){
////            Log.d("Running here","focus check");
////            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
////        }
////    }




}