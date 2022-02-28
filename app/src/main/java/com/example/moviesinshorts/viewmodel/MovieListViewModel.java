package com.example.moviesinshorts.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.repository.MovieListRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends ViewModel {

    private MovieListRepository movieListRepository;

    public MovieListViewModel(){
        movieListRepository = MovieListRepository.getMovieListRepositoryInstance();
    }

    public LiveData<List<MovieModel>> getMovies(String fragmentName){
        return movieListRepository.getMovies(fragmentName);
    }

    public LiveData<List<MovieModel>> getSearchMovie(String searchText){
        return movieListRepository.getSearchMovie(searchText);
    }

    public void makeNowTrendingApiCall() {

    }

}
