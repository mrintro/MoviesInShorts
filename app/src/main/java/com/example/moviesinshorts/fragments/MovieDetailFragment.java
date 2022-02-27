package com.example.moviesinshorts.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.moviesinshorts.R;
import com.example.moviesinshorts.databinding.FragmentMovieDetailBinding;
import com.example.moviesinshorts.model.MovieModel;

public class MovieDetailFragment extends Fragment {

    private final MovieModel movie;
    private FragmentMovieDetailBinding fragmentMovieDetailBinding;
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

        Log.d("OncreateView","Called");

        fragmentMovieDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);
        View view = fragmentMovieDetailBinding.getRoot();

        setData();

        return view;
    }

    private void setData() {

        fragmentMovieDetailBinding.mainContainer.setVisibility(View.VISIBLE);
        Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500/"+movie.getBackdrop_path())
                .into(fragmentMovieDetailBinding.detailImage);
    }
}