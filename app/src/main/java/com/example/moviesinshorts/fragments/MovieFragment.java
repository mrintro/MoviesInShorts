package com.example.moviesinshorts.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.MainActivity;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.ui.SliderAdapter;
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.example.moviesinshorts.viewmodel.MyViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding fragmentMovieBinding;
    private OnMovieOnClick onMovieOnClick;
    private MovieListViewModel viewModel;
    private ViewPager2 viewPager2;
    private SliderAdapter sliderAdapter;
    private String currentFragment;
    private String fragmentName;
    private SharedPreferences sharedPreferences;

    public String getFragmentName() {
        return fragmentName;
    }

    public MovieFragment(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new MyViewModelFactory(this.getActivity().getApplication())).get(MovieListViewModel.class);
        if(fragmentName == Constants.TRENDING_FRAGMENT) viewModel.setTrendingMovies();
        else viewModel.setNowPlayingMovies();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false);
        View movieFragmentView = fragmentMovieBinding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("Bookmark", Context.MODE_PRIVATE);
        
        setViewPagerAdapter();
        setUpBookmarkButton();

        Log.d("Check","onCreatecalled");

        if(fragmentName.equals(Constants.TRENDING_FRAGMENT)) {
            viewModel.getTrendingMovieList().observe(getViewLifecycleOwner(), new Observer<List<MovieModel>>() {

                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    Log.d("Data changed found in trending", "check"+movieModels.toString());
                    sliderAdapter.setMovieModels(movieModels);
                }
            });
        } else if(fragmentName.equals(Constants.NOW_PLAYING_FRAGMENT)){
            Log.d("If fragment called", Constants.NOW_PLAYING_FRAGMENT);
            viewModel.getNowPlayingMovieList().observe(getViewLifecycleOwner(), new Observer<List<MovieModel>>() {

                @Override
                public void onChanged(List<MovieModel> movieModels) {
                    Log.d("Data changed found in now playing", "check");
                    sliderAdapter.setMovieModels(movieModels);
                }
            });
        }
        return movieFragmentView;
    }



    private void setUpBookmarkButton() {

        fragmentMovieBinding.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).navigateToBookmarks();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        db = Database.getDatabaseInstance(getActivity());
    }

    private void saveDataToDB(List<MovieModel> movieModels) {
//        db.dao().addMultipleMovie(movieModels);
    }


    private void setViewPagerAdapter() {

        onMovieOnClick = new OnMovieOnClick() {
            @Override
            public void onMovieOnClick(View view, int position) {
                MovieDetailFragment movieDetailFragment = new MovieDetailFragment(sliderAdapter.getMovieAtPosition(position));
//                ((MainActivity)getActivity()).changeToDetailFragment(movieDetailFragment);
                Log.d("Ayush", "MV" + sliderAdapter.getMovieAtPosition(position).toString());
                ((MainActivity) getActivity()).navigateToDetailFragment(sliderAdapter.getMovieAtPosition(position));
                Log.d("Listener check", "listening to "+ String.valueOf(position));
            }
        };

        viewPager2 = fragmentMovieBinding.viewPagerSlider;
        sliderAdapter = new SliderAdapter(viewPager2, onMovieOnClick, getActivity().getApplication(), fragmentMovieBinding, viewModel);

        viewPager2.setAdapter(sliderAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(60));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                Log.d("Check", "checking");
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
//                page.findViewById(R.id.imageSlide).setScaleY(0.85f + r*0.15f);
//                page.findViewById(R.id.bookmark_image).setScaleY(0.85f + r*0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                MovieModel movie = sliderAdapter.getMovieModel(position);
                fragmentMovieBinding.movieTitle.setText(movie.getTitle());

                Glide.with(requireContext())
                        .load("https://image.tmdb.org/t/p/w500/"+movie.getBackdrop_path())
                        .into(fragmentMovieBinding.backgroundImage);
                fragmentMovieBinding.imdbText.setText(String.valueOf(movie.getVote_average()));

                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    private void ObserveAnyChange() {

    }

    @Override
    public void onDestroy() {
        ArrayList<Pair<Integer, Boolean>> bookmarkData = new ArrayList<Pair <Integer, Boolean>>();
        Map<String,?> keys = sharedPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
        Log.d("check map", "onDestroy");
        
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
//        ArrayList<Pair<Integer, Boolean>> bookmarkData = new ArrayList<Pair <Integer, Boolean>>();
//        Map<String,?> keys = sharedPreferences.getAll();
//
//        for(Map.Entry<String,?> entry : keys.entrySet()){
//            bookmarkData.add(new Pair<Integer, Boolean>(Integer.parseInt(entry.getKey()), (Boolean) entry.getValue()));
//        }
//        viewModel.bookMarkMovie(bookmarkData);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();
        Log.d("check map", "onDestroyView");
        super.onDestroyView();
    }

//    @Override
//    public void onMovieOnClick(int position) {
//        Log.d("Slider", "Selected");
//
////        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
////        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
////        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.replace(fragmentMovieBinding.mainContainer.getId(), movieDetailFragment);
////        fragmentTransaction.addToBackStack("DetailsTransaction");
////        fragmentTransaction.commit();
//
//    }

}