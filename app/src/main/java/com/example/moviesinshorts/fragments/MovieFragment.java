package com.example.moviesinshorts.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.MainActivity;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.ui.SliderAdapter;
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.OnMovieOnClick;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.example.moviesinshorts.viewmodel.MyViewModelFactory;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding fragmentMovieBinding;
    private MovieListViewModel viewModel;
    private ViewPager2 viewPager2;
    private SliderAdapter sliderAdapter;
    private final String fragmentName;

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
        setViewPagerAdapter();
        setUpBookmarkButton();


        if(fragmentName.equals(Constants.TRENDING_FRAGMENT)) {
            viewModel.getTrendingMovieList().observe(getViewLifecycleOwner(), movieModels -> {
                sliderAdapter.setMovieModels(movieModels);
            });
        } else if(fragmentName.equals(Constants.NOW_PLAYING_FRAGMENT)){
            viewModel.getNowPlayingMovieList().observe(getViewLifecycleOwner(), movieModels -> {
                sliderAdapter.setMovieModels(movieModels);
            });
        }
        return movieFragmentView;
    }



    private void setUpBookmarkButton() {
        fragmentMovieBinding.bookmarkText.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToBookmarks());

        fragmentMovieBinding.bookmarkButton.setOnClickListener(v -> ((MainActivity) getActivity()).navigateToBookmarks());

    }



    private void setViewPagerAdapter() {

        OnMovieOnClick onMovieOnClick = (view, position) -> {
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment(sliderAdapter.getMovieAtPosition(position));
            ((MainActivity) getActivity()).navigateToDetailFragment(sliderAdapter.getMovieAtPosition(position));
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
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
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
                        .error(R.drawable.image_not_found)
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

}