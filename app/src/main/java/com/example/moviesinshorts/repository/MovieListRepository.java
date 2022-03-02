package com.example.moviesinshorts.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.Credentials;
import com.example.moviesinshorts.utils.MovieModelToMovieConverter;
import com.example.moviesinshorts.utils.NetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private MutableLiveData<List<MovieModel>> searchedMovieList;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;
    private Application application;

    private static Database database;


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

    public static Database getDatabaseInstance(Application application) {
        if(database==null) database = Database.getDatabaseInstance(application);
        return database;
    }

    private MovieListRepository(Application application) {
        this.application = application;
    }

    @SuppressLint("CheckResult")
    public Observable<List<MovieModel>> getNowPlayingMovies(){
        Observable<List<MovieModel>> dataFromDb= getDatabaseInstance(application).dao().getAllNowPlayingMovies()
                .filter(movieModelList -> movieModelList.size()>0)
                .subscribeOn(Schedulers.computation());

        if(NetworkHelper.checkNetwork(application)) {
            Observable<List<MovieModel>> dataFromApi = getNowPlayingApi().getMovieList().map(new Function<MovieListResponse, List<MovieModel>>() {
                @Override
                public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                    Log.d("checking", "check map");
                    return movieListResponse.getMovieList();
                }
            })
            .map(movieModelList -> {
                Observable.create(subscribe -> {
                    database.dao().upsertNowPlaying(movieModelList);
                    subscribe.onComplete();
                }).subscribeOn(Schedulers.computation()).subscribe();
                return movieModelList;
            })
            .subscribeOn(Schedulers.io());
            Log.d("check data", "check" + dataFromApi.toString());
            return Observable.concat(dataFromApi, dataFromDb).observeOn(AndroidSchedulers.mainThread());
        } else {
            return dataFromDb.observeOn(AndroidSchedulers.mainThread());
        }
    }

    public Observable<List<MovieModel>> getTrendingMovie(){

        Log.d("API Check","Checking if working1"+Constants.TRENDING_FRAGMENT);

            Log.d("API Check","Checking if working1"+Constants.TRENDING_FRAGMENT);

            Observable<List<MovieModel>> dataFromDb= getDatabaseInstance(application).dao().getAllTrendingMovies()
                    .filter(movieModelList -> movieModelList.size()>0)
                    .subscribeOn(Schedulers.computation());

            if(NetworkHelper.checkNetwork(application)) {
                Observable<List<MovieModel>> dataFromApi = getTrendingApi().getMovieList().map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        Log.d("checking", "check map");
                        return movieListResponse.getMovieList();
                    }
                })
                        .map(movieModelList -> {
                            Observable.create(subscribe -> {
                                database.dao().upsertTrending(movieModelList);
                                subscribe.onComplete();
                            }).subscribeOn(Schedulers.computation()).subscribe();
                            return movieModelList;
                        })
                        .subscribeOn(Schedulers.io());
                Log.d("check data", "check" + dataFromApi.toString());
                return Observable.concat(dataFromApi, dataFromDb).observeOn(AndroidSchedulers.mainThread());
            } else {
                return dataFromDb.observeOn(AndroidSchedulers.mainThread());
            }
    }



    public Observable<List<MovieModel>> getSearchMovie(String searchText) {

        Observable<List<MovieModel>> searchFromApi = getSearchQueryApi().getSearchedMovie(searchText)
                .map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        return movieListResponse.getMovieList();
                    }
                }).subscribeOn(Schedulers.io());

        return searchFromApi;
    }

}