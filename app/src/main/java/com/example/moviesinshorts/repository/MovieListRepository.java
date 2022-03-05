package com.example.moviesinshorts.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.database.Database;
import com.example.moviesinshorts.database.tables.Movie;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.flow.Flow;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;
    private Application application;
    private CompositeDisposable disposable = new CompositeDisposable();
    private static Database database;

    // All Live Data Below
    private MutableLiveData<List<MovieModel>> searchedMovieList = new MutableLiveData<>();
    private MutableLiveData<List<MovieModel>> trendingMovieList = new MutableLiveData<>();
    private MutableLiveData<List<MovieModel>> nowPlayingMovieList = new MutableLiveData<>();

    public MutableLiveData<List<MovieModel>> getSearchedMovieList() {
        return searchedMovieList;
    }

    public MutableLiveData<List<MovieModel>> getTrendingMovieList() {
        return trendingMovieList;
    }

    public MutableLiveData<List<MovieModel>> getNowPlayingMovieList() {
        return nowPlayingMovieList;
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

    public static Database getDatabaseInstance(Application application) {
        if(database==null) database = Database.getDatabaseInstance(application);
        return database;
    }

    private MovieListRepository(Application application) {
        this.application = application;
    }


    public void getTrendingMovie() {
        getTrendingApi().getMovieList()
                .map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        for (MovieModel movieModel : movieListResponse.getMovieList())
                            movieModel.setTrending(true);
                        return  movieListResponse.getMovieList();
                    }
                }).doOnNext(movieModelList -> {
            Log.d("Receiving data here", "update call 3");
            getDatabaseInstance(application).dao().upsertTrending(movieModelList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<MovieModel>>() {
                    @Override
                    public void onNext(@NonNull List<MovieModel> movieModelList) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        observeDataFromDB(Constants.TRENDING_FRAGMENT);
                    }
                });

    }

    public void getNowPlayingMovie() {
        getNowPlayingApi().getMovieList()
                .map(new Function<MovieListResponse, List<MovieModel>>() {
                    @Override
                    public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                        for (MovieModel movieModel : movieListResponse.getMovieList())
                            movieModel.setNowPlaying(true);
                        return  movieListResponse.getMovieList();
                    }
                }).doOnNext(movieModelList -> {
            Log.d("Receiving data here", "update call 3");
            getDatabaseInstance(application).dao().upsertNowPlaying(movieModelList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<MovieModel>>() {
                    @Override
                    public void onNext(@NonNull List<MovieModel> movieModelList) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        observeDataFromDB(Constants.NOW_PLAYING_FRAGMENT);
                    }
                });

    }

    private void observeDataFromDB(String fragmentName) {
        Log.d("Getting Observe from data", "Funtion called");
        Observable<List<MovieModel>> movieModelFromDBObservable;
        if(fragmentName == Constants.TRENDING_FRAGMENT) movieModelFromDBObservable = getDatabaseInstance(application).dao().getAllTrendingMovies();
        else movieModelFromDBObservable = getDatabaseInstance(application).dao().getAllNowPlayingMovies();
            movieModelFromDBObservable
            .filter(movieModelList ->  movieModelList.size()>0)
            .subscribeOn(Schedulers.single())
            .subscribe(new DisposableObserver<List<MovieModel>>() {
                           @Override
                           public void onNext(@NonNull List<MovieModel> response) {
                                if(fragmentName == Constants.TRENDING_FRAGMENT) trendingMovieList.postValue(response);
                                else nowPlayingMovieList.postValue(response);
                               Log.d("Getting Response from db", "response size = " + response.size());
                               response.forEach(res -> Log.d("Getting Response : ", "Movie : " + res.getTitle() + " " + res.isBookmark() + " " + res.isTrending() + " " + res.isNowPlaying()));

                           }
                           @Override
                           public void onError(@NonNull Throwable e) {
                                Log.d("Getting Response Error from DB", "response size = " + e.toString());
                           }

                           @Override
                           public void onComplete() {
                               Log.d("Getting Response Message from DB", "on Complete");
                           }
                       }
            );
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

    public void bookMarkMovie(MovieModel movie){
        getDatabaseInstance(application).dao().bookMarkMovie(movie);
    }

    public Observable<List<MovieModel>> getBookmarkedMovies() {
        return getDatabaseInstance(application).dao().getBookmarkedMovies().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());
    }
}