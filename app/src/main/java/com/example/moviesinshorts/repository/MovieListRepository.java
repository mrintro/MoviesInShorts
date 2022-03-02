package com.example.moviesinshorts.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.database.Database;
import com.example.moviesinshorts.database.tables.Movie;
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
import com.example.moviesinshorts.utils.MovieModelToMovieConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private final MutableLiveData<List<MovieModel>> nowPlayingMovieList;
    private final MutableLiveData<List<MovieModel>> trendingMovieList;
    private MutableLiveData<List<MovieModel>> searchedMovieList;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;

    private static Database database;

    private static void accept(Object data) {

    }

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

    public static MovieListRepository getMovieListRepositoryInstance(Application application) {
        if(movieListRepositoryInstance==null)
           movieListRepositoryInstance = new MovieListRepository(application);
        return movieListRepositoryInstance;
    }

//    public static getDatabaseInstance() {
//        if(database==null) database = Database.getDatabaseInstance();
//    }

    private MovieListRepository(Application application) {
        searchedMovieList = new MutableLiveData<List<MovieModel>>();
        trendingMovieList = new MutableLiveData<List<MovieModel>>();
        nowPlayingMovieList = new MutableLiveData<List<MovieModel>>();
        database = Database.getDatabaseInstance(application);
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getNowPlayingMovies(){

        Log.d("API Check","Checking if working1"+Constants.NOW_PLAYING_FRAGMENT);
        try {
            getTrendingApi().getMovieList()
                    .subscribeOn(Schedulers.io())
                    .subscribe(movieListResponse -> {
                        nowPlayingMovieList.postValue(movieListResponse.getMovieList());
                    });
        } catch (Exception e) {
            Log.d("API FAILURE", "Unable to fetch data from Trending API");
        }

        return nowPlayingMovieList;
    }

    public LiveData<List<MovieModel>> getTrendingMovie(){
        try {

            Observable<List<MovieModel>> movieListDb = database.dao().getAllTrendingMovies()
                    .filter(movieModelList -> movieModelList.size()>0)
                    .subscribeOn(Schedulers.computation());

            Observable<MovieListResponse> movieListApi = getTrendingApi().getMovieList()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(movieListResponse -> {
                        movieListResponse.getMovieList().forEach((movie) -> movie.setTrending(true));
                        database.dao().addMultipleMovie(movieListResponse.getMovieList());
                    })
                    .subscribeOn(Schedulers.computation());

            Observable.concat(movieListDb.observeOn(AndroidSchedulers.mainThread()) , movieListApi.observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(
                            MovieListRepository::accept
                    );


         }catch (Exception e){
            Log.d("API FAILURE","Unable to fetch data from Trending API");
        }

        return trendingMovieList;

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