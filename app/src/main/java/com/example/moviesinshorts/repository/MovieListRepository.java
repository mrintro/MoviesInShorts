package com.example.moviesinshorts.repository;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesinshorts.database.Database;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.network.SearchQueryApi;
import com.example.moviesinshorts.network.TrendingApi;
import com.example.moviesinshorts.response.MovieListResponse;
import com.example.moviesinshorts.utils.Constants;
import com.example.moviesinshorts.utils.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;
    private final Application application;
    private final CompositeDisposable disposable = new CompositeDisposable();
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
        if(NetworkHelper.checkNetwork(application)) {
            getTrendingApi().getMovieList()
                    .map(new Function<MovieListResponse, List<MovieModel>>() {
                        @Override
                        public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                            for (MovieModel movieModel : movieListResponse.getMovieList())
                                movieModel.setTrending(true);
                            return movieListResponse.getMovieList();
                        }
                    }).doOnNext(movieModelList -> {
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
        } else{
            observeDataFromDB(Constants.TRENDING_FRAGMENT);
        }
    }

    public void getNowPlayingMovie() {
        if(NetworkHelper.checkNetwork(application)){
            getNowPlayingApi().getMovieList()
                    .map(new Function<MovieListResponse, List<MovieModel>>() {
                        @Override
                        public List<MovieModel> apply(@NonNull MovieListResponse movieListResponse) throws Exception {
                            for (MovieModel movieModel : movieListResponse.getMovieList())
                                movieModel.setNowPlaying(true);
                            return movieListResponse.getMovieList();
                        }
                    }).doOnNext(movieModelList -> {
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
        else{
            observeDataFromDB(Constants.NOW_PLAYING_FRAGMENT);
        }

    }

    private void observeDataFromDB(String fragmentName) {
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
                               response.forEach(res -> Log.d("Getting Response : ", "Movie : " + res.getTitle() + " " + res.isBookmark() + " " + res.isTrending() + " " + res.isNowPlaying()));

                           }
                           @Override
                           public void onError(@NonNull Throwable e) {
                                Log.d("Getting Response Error from DB", "response = " + e.toString());
                           }

                           @Override
                           public void onComplete() {
                               Log.d("Getting Response on complete from DB ", "on Complete");
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