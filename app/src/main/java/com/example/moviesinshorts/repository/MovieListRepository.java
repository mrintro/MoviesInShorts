package com.example.moviesinshorts.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.response.MovieResponse;
import com.example.moviesinshorts.utils.ApiThreadExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private MutableLiveData<List<MovieModel>> movieList;
    private NowPlayingApi nowPlayingApi;

    private NowPlayingApi getNowPlayingApi() {
        if(nowPlayingApi==null) nowPlayingApi= (NowPlayingApi) RetroInstance.buildApi(NowPlayingApi.class);
        return nowPlayingApi;
    }

    public static MovieListRepository getMovieListRepositoryInstance() {
        if(movieListRepositoryInstance==null)
            return new MovieListRepository();
        return movieListRepositoryInstance;
    }

    private MovieListRepository() {
        movieList = new MutableLiveData<List<MovieModel>>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        Call<MovieListResponse> responseCall = getNowPlayingApi().getMovieList();

        responseCall.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if(response.code() == 200){
                    movieList.postValue(response.body().getMovieList());
                } else{
                    Log.e("Response", response.errorBody().toString());
                    movieList.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e("Api Failed", t.toString());
                movieList.postValue(null);
            }
        });
        return movieList;
    }



}