package com.example.moviesinshorts.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.database.Database;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.model.ResponseModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.network.SearchQueryApi;
import com.example.moviesinshorts.network.TrendingApi;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.response.MovieResponse;
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.NetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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




    // New Method
    @SuppressLint("CheckResult")
    List<MovieModel> getDataFromDB() {
        getDatabaseInstance(application).dao().getAllNowPlayingMovies()
                .subscribe(movieModelListRes -> {
                    movieModelList.set(movieModelListRes);
                });
    }


    Observable<List<MovieModel>> getDataFromApi(){
        Observable<MovieResponse> movieFromApi = getNowPlayingApi().getMovieList()
                .subscribeOn(Schedulers.io())
                .doOnNext(movieListResponse -> {
                    getDatabaseInstance(application).dao().upsertNowPlaying(movieListResponse.getMovieList());
                }).subscribeOn(Schedulers.computation())
                .map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        return getDataFromDB();
                    }
                })
    }





























    @SuppressLint("CheckResult")
    public Observable<ResponseModel> getNowPlayingMovies(){
        Log.d("API Check","Checking if working1"+Constants.NOW_PLAYING_FRAGMENT);
        Observable<ResponseModel> dataFromDb= getDatabaseInstance(application).dao().getAllNowPlayingMovies()
                .map(new Function<List<MovieModel>, ResponseModel>() {
                    @Override
                    public ResponseModel apply(@NonNull List<MovieModel> movieModelList) throws Exception {
                        return new ResponseModel(true, movieModelList);
                    }
                })
                .subscribeOn(Schedulers.computation());

        if(NetworkHelper.checkNetwork(application)) {
            Observable<ResponseModel> dataFromApi = getNowPlayingApi().getMovieList()
                    .map(new Function<MovieListResponse, ResponseModel>() {
                @Override
                public ResponseModel apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                    Log.d("checking", "check map");
                    return new ResponseModel(false, movieListResponse.getMovieList());
                }
            })
            .map(responseModel -> {
                Observable.create(subscribe -> {
                    database.dao().upsertNowPlaying(responseModel.getMovieModels());
                    Log.d("Putting data into db","check");
                }).subscribeOn(Schedulers.computation()).subscribe();
                return responseModel;
            })
            .subscribeOn(Schedulers.io());
//            Log.d("check data", "check" + dataFromApi.toString());

//            Observable<List<MovieModel>> dataFromApi = getNowPlayingApi().getMovieList()
//                    .map(movieListResponse -> {
//                        return getDatabaseInstance(application).dao().setAndGetData(movieListResponse.getMovieList());
//                    });

            return Observable.merge(dataFromApi, dataFromDb).observeOn(AndroidSchedulers.mainThread());
        } else {
            return dataFromDb.observeOn(AndroidSchedulers.mainThread());
        }
    }

    public Observable<List<MovieModel>> getTrendingMovie(){
            Log.d("Check get db instance",getDatabaseInstance(application).toString());
            Log.d("API Check","Checking if working1"+Constants.TRENDING_FRAGMENT);

            Observable<List<MovieModel>> dataFromDb= getDatabaseInstance(application).dao().getAllTrendingMovies()
                    .filter(movieModelList -> movieModelList.size()>0)
                    .subscribeOn(Schedulers.computation());

        Log.d("checking for internet","No");
            if(NetworkHelper.checkNetwork(application)) {
                Log.d("checking for internet","yes");

                Observable<List<MovieModel>> dataFromApi = getTrendingApi().getMovieList().map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        Log.d("checking", "check map");
                        return movieListResponse.getMovieList();
                    }
                })
                        .map(movieModelList -> {
                            Observable.create(subscribe -> {
                                Log.d("Putting data into db","check");
                                database.dao().upsertTrending(movieModelList);
                                subscribe.onComplete();
                            }).subscribeOn(Schedulers.computation()).subscribe();
                            return movieModelList;
                        })
                        .subscribeOn(Schedulers.io());
                Log.d("check data", "check" + dataFromDb.toString());
                return Observable.concat(dataFromDb.observeOn(AndroidSchedulers.mainThread()), dataFromApi.observeOn(AndroidSchedulers.mainThread()));
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

    public void bookMarkMovies(ArrayList<Pair<Integer, Boolean>> bookmarkData){
        getDatabaseInstance(application).dao().bookMarkMultipleMovie(bookmarkData);
    }
    public void bookMarkMovie(int id, boolean flag){
        getDatabaseInstance(application).dao().bookMarkMovie(id, flag);
    }


    public Observable<List<MovieModel>> getBookmarkedMovies() {

        return getDatabaseInstance(application).dao().getBookmarkedMovies().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());

    }
}