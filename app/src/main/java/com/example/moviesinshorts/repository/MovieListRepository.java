package com.example.moviesinshorts.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.network.SearchQueryApi;
import com.example.moviesinshorts.network.TrendingApi;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.response.MovieResponse;
import com.example.moviesinshorts.utils.ApiThreadExecutors;
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private final MutableLiveData<List<MovieModel>> movieList;
    private MutableLiveData<List<MovieModel>> searchedMovieList;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;

    private NowPlayingApi getNowPlayingApi() {
        if(nowPlayingApi==null) nowPlayingApi= (NowPlayingApi) RetroInstance.buildApi(NowPlayingApi.class);
        return nowPlayingApi;
    }
    private TrendingApi getTrendingApi() {
        if(trendingApi==null) trendingApi = (TrendingApi) RetroInstance.buildApi(TrendingApi.class);
        return trendingApi;
    }
    private SearchQueryApi getSearchQueryApi() {
        if(searchQueryApi==null) searchQueryApi = (SearchQueryApi) RetroInstance.buildApi(SearchQueryApi.class);
        return searchQueryApi;
    }

    public static MovieListRepository getMovieListRepositoryInstance() {
        if(movieListRepositoryInstance==null)
            return new MovieListRepository();
        return movieListRepositoryInstance;
    }

    private MovieListRepository() {
        searchedMovieList = new MutableLiveData<List<MovieModel>>();
        movieList = new MutableLiveData<List<MovieModel>>();
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getMovies(String fragmentName){

        Log.d("API Check","Checking if working1"+fragmentName+Constants.NOW_PLAYING_FRAGMENT);
        if(fragmentName.equals(Constants.NOW_PLAYING_FRAGMENT)) {
            try {
                getTrendingApi().getMovieList()
                        .subscribeOn(Schedulers.io())
                        .subscribe(movieListResponse -> {
                            movieList.postValue(movieListResponse.getMovieList());
                        });
            }catch (Exception e){
                Log.d("API FAILURE","Unable to fetch data from Trending API");
            }

            return movieList;

//
//            Call<MovieListResponse> responseCall = getNowPlayingApi().getMovieList();
//            responseCall.enqueue(new Callback<MovieListResponse>() {
//                @Override
//                public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
//                    if (response.code() == 200) {
//                        movieList.postValue(response.body().getMovieList());
//                    } else {
//                        Log.e("Response", response.errorBody().toString());
//                        movieList.postValue(null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MovieListResponse> call, Throwable t) {
//                    Log.e("Api Failed", t.toString());
//                    movieList.postValue(null);
//                }
//            });
        }
        else if(fragmentName == Constants.TRENDING_FRAGMENT){
            try {
                getTrendingApi().getMovieList()
                        .subscribeOn(Schedulers.io())
                        .subscribe(movieListResponse -> {
                            Log.d("Checking the data", movieListResponse.toString());
                            movieList.postValue(movieListResponse.getMovieList());
                        });
            }catch (Exception e){
                Log.d("API FAILURE","Unable to fetch data from Trending API");
            }

            return movieList;

        }
//            Call<MovieListResponse> responseCall = getTrendingApi().getMovieList();
//            responseCall.enqueue(new Callback<MovieListResponse>() {
//                @Override
//                public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
//                    if (response.code() == 200) {
//                        movieList.postValue(response.body().getMovieList());
//                    } else {
//                        Log.e("Response", response.errorBody().toString());
//                        movieList.postValue(null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MovieListResponse> call, Throwable t) {
//                    Log.e("Api Failed", t.toString());
//                    movieList.postValue(null);
//                }
//            });
//        }

        return movieList;
    }


    public LiveData<List<MovieModel>> getSearchMovie(String searchText) {

        Log.d("Search String",searchText);
        Call<MovieListResponse> responseCall = getSearchQueryApi().getSearchedMovie(
                searchText
        );

        responseCall.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if (response.code() == 200) {
                    Log.d("Response", response.message().toString());
                    searchedMovieList.postValue(response.body().getMovieList());
                } else {
                    Log.e("Response", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e("Api Failed", t.toString());
            }
        });

        return searchedMovieList;
    }

}