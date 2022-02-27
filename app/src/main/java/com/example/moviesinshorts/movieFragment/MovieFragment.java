package com.example.moviesinshorts.movieFragment;

import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.model.NowPlayingMovieResponse;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.ui.SliderAdapter;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    private FragmentMovieBinding fragmentMovieBinding;

    private NowPlayingMovieResponse movieList;
    private MovieListViewModel viewModel;
    private ViewPager2 viewPager2;
    private SliderAdapter sliderAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false);

        View movieFragmentView = fragmentMovieBinding.getRoot();

        setViewPagerAdapter();
        getRetrofitResponse();

        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        viewModel.getMovies().observe(getViewLifecycleOwner(), new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                Log.d("Api Response Movie Fragment",movieModels.toString());
                sliderAdapter.setMovieModels(movieModels);
            }
        });

        return movieFragmentView;
    }

    private void setViewPagerAdapter() {
        viewPager2 = fragmentMovieBinding.viewPagerSlider;
        sliderAdapter = new SliderAdapter(viewPager2);

        viewPager2.setAdapter(sliderAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
//        compositePageTransformer.addTransformer(new MarginPageTransformer(60));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
//                page.setScaleY(0.85f + r*0.15f);
                page.findViewById(R.id.imageSlide).setScaleY(0.85f + r*0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MovieModel movie = sliderAdapter.getMovieModel(position);
                fragmentMovieBinding.movieTitle.setText(movie.getTitle());

                Glide.with(getContext())
                        .load("https://image.tmdb.org/t/p/w500/"+movie.getBackdrop_path())
                        .into(fragmentMovieBinding.backgroundImage);

                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
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

    private void getRetrofitResponse() {

        NowPlayingApi nowPlayingApi = (NowPlayingApi) RetroInstance.buildApi(NowPlayingApi.class);
        Call<MovieListResponse> responseCall = nowPlayingApi.getMovieList();

        responseCall.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if(response.code() == 200){
                    Log.v("Response", response.body().toString());
                    List<MovieModel> movies = new ArrayList<>(response.body().getMovieList());
                    for(MovieModel movie: movies){
                        Log.v("Response",movie.getTitle());
                    }

                } else{
                    Log.e("Response", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {

            }
        });


    }

}