package com.example.moviesinshorts.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieDetailBinding;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.viewmodel.MovieListViewModel;
import com.example.moviesinshorts.viewmodel.MyViewModelFactory;

import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class MovieDetailFragment extends Fragment {

    private MovieModel movie;
    private FragmentMovieDetailBinding fragmentMovieDetailBinding;
    private MovieListViewModel movieListViewModel;

    public MovieDetailFragment() {

    }
    public MovieDetailFragment(MovieModel movie) {
        this.movie = movie;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMovieDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);
        View view = fragmentMovieDetailBinding.getRoot();
        movieListViewModel = new ViewModelProvider(this, new MyViewModelFactory(this.getActivity().getApplication())).get(MovieListViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movie = (MovieDetailFragmentArgs.fromBundle(getArguments()).getMovieData());
        setData();
        setBookmarkButton();

    }


    private void setBookmarkButton() {

        fragmentMovieDetailBinding.bookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movie.setBookmark(!movie.isBookmark());
                configureBookmarkImage(movie.isBookmark());
                movieListViewModel.bookMarkMovie(movie);
            }
        });

    }

    private void configureBookmarkImage(boolean bookmark) {
        if(bookmark){
            fragmentMovieDetailBinding.bookmarkImage.setImageResource(R.drawable.bookmark_red);
        }else {
            fragmentMovieDetailBinding.bookmarkImage.setImageResource(R.drawable.bookmark_white);
        }
    }

    private void setData() {
//        Blurry.with(requireContext())
//                .radius(50)
//                .sampling(10)
//                .color(Color.argb(80, 255, 255, 0))
//                .async()
//                .onto(fragmentMovieDetailBinding.titleContainer);

        fragmentMovieDetailBinding.title.setText(movie.getTitle());
        fragmentMovieDetailBinding.language.setText(movie.getOriginal_language());
        fragmentMovieDetailBinding.rating.setText("  |  "+ movie.getVote_average());
        configureBookmarkImage(movie.isBookmark());

        fragmentMovieDetailBinding.mainContainer.setVisibility(View.VISIBLE);
        Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500/"+movie.getPoster_path())
                .into(fragmentMovieDetailBinding.detailImage);
    }
}