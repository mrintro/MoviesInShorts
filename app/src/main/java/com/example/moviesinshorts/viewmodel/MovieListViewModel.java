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

    public LiveData<List<MovieModel>> getMovies(){ return movieListRepository.getMovies(); }

    public void makeNowTrendingApiCall() {



//        NowPlayingApi nowPlayingApi = RetroInstance.getRetroFitClient().create(NowPlayingApi.class);
//        Call<List<MovieModel> nowPlayingApiCall = nowPlayingApi.getMovieList();
//        nowPlayingApiCall.enqueue(new Callback<List<MovieModel>() {
//            @Override
//            public void onResponse(Call<List<MovieModel> call, Response<List<MovieModel> response) {
//                Log.d("Movie", "Getting Response");
//                nowPlayingLiveData.postValue(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<MovieModel> call, Throwable t) {
//                nowPlayingLiveData.postValue(null);
//                Log.d("Movie", "API Failed"+t.toString());
//            }
//        });
    }

}
