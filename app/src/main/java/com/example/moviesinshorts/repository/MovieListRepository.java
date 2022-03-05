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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.flow.Flow;

public class MovieListRepository {

    private static MovieListRepository movieListRepositoryInstance;
    private MutableLiveData<List<MovieModel>> searchedMovieList;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;
    private Application application;
    private CompositeDisposable disposable = new CompositeDisposable();


    private static Database database;

    public Observable<List<MovieModel>> setDataFromApi() {
        Log.d("Check","count 3");
        return getDatabaseInstance(application).dao().getAllNowPlayingMovies()
//                .flatMap(response -> {
//                     getDataFromApi();
//                    return Flowable.just(response);
//                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());



//        return getNowPlayingApi().getMovieList()
//                .  map(movieListResponse -> {
//                    return Observable.just(getDatabaseInstance(application).dao().upsertNowPlaying(movieListResponse.getMovieList()))
//                            .subscribe(new DisposableObserver<List<Long>>() {
//                                @Override
//                                public void onNext(@NonNull List<Long> longs) {
//                                    Log.d("Checking if updated", "data upsert going on");
//                                }
//
//                                @Override
//                                public void onError(@NonNull Throwable e) {
//                                    Log.d("Upsert Failure", "Error "+e.toString());
//                                }
//
//                                @Override
//                                public void onComplete() {
//
//                                }
//                            })

//                });

    }

    public void getDataFromApi() {
        getNowPlayingApi().getMovieList()
                .subscribeOn(Schedulers.io()).subscribe(new DisposableObserver<MovieListResponse>() {
            @Override
            public void onNext(@NonNull MovieListResponse movieListResponse) {
                Observable.just(getDatabaseInstance(application).dao().upsertNowPlaying(movieListResponse.getMovieList()))
                .subscribeOn(Schedulers.computation())
                .subscribe(new DisposableObserver<List<Long>>() {
                    @Override
                    public void onNext(@NonNull List<Long> longs) {
                        Log.d("Check Upsert Done", "Done ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Check Upsert Failure", "Error "+e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Check Upsert Complete", "Error ");
                    }
                });
                Log.d("Check Upsert Done", "Done ");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Check Upsert Failure", "Error "+e.toString());

            }

            @Override
            public void onComplete() {

            }
        });
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




    // New Method
//    @SuppressLint("CheckResult")
//    List<MovieModel> getDataFromDB() {
//        getDatabaseInstance(application).dao().getAllNowPlayingMovies()
//                .subscribe(movieModelListRes -> {
//                    movieModelList.set(movieModelListRes);
//                });
//    }


//    private Flowable<List<MovieModel>> getNowPlayingMovies(){
//        return getDatabaseInstance(application).dao().getAllNowPlayingMovies()
//                .flatMap(movieModelList -> {
//                    getNowPlayingApi().getMovieList()
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new DisposableObserver<MovieListResponse>() {
//                        @Override
//                        public void onNext(@NonNull MovieListResponse movieListResponse) {
//                            getDatabaseInstance(application).dao().upsertNowPlaying(movieListResponse.getMovieList());
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable e) {
//                            Log.e("Error Getting Data From API", e.toString());
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
//                    return Flowable.just()
//                });
//
//    }





























//    @SuppressLint("CheckResult")
//    public Observable<ResponseModel> getNowPlayingMovies(){
//        Log.d("API Check","Checking if working1"+Constants.NOW_PLAYING_FRAGMENT);
//        Observable<ResponseModel> dataFromDb= getDatabaseInstance(application).dao().getAllNowPlayingMovies()
//                .map(new Function<List<MovieModel>, ResponseModel>() {
//                    @Override
//                    public ResponseModel apply(@NonNull List<MovieModel> movieModelList) throws Exception {
//                        return new ResponseModel(true, movieModelList);
//                    }
//                })
//                .subscribeOn(Schedulers.computation());
//
//        if(NetworkHelper.checkNetwork(application)) {
//            Observable<ResponseModel> dataFromApi = getNowPlayingApi().getMovieList()
//                    .map(new Function<MovieListResponse, ResponseModel>() {
//                @Override
//                public ResponseModel apply(@NonNull MovieListResponse movieListResponse) throws Exception {
//                    Log.d("checking", "check map");
//                    return new ResponseModel(false, movieListResponse.getMovieList());
//                }
//            })
//            .map(responseModel -> {
//                Observable.create(subscribe -> {
//                    database.dao().upsertNowPlaying(responseModel.getMovieModels());
//                    Log.d("Putting data into db","check");
//                }).subscribeOn(Schedulers.computation()).subscribe();
//                return responseModel;
//            })
//            .subscribeOn(Schedulers.io());
////            Log.d("check data", "check" + dataFromApi.toString());
//
////            Observable<List<MovieModel>> dataFromApi = getNowPlayingApi().getMovieList()
////                    .map(movieListResponse -> {
////                        return getDatabaseInstance(application).dao().setAndGetData(movieListResponse.getMovieList());
////                    });
//
//            return Observable.merge(dataFromApi, dataFromDb).observeOn(AndroidSchedulers.mainThread());
//        } else {
//            return dataFromDb.observeOn(AndroidSchedulers.mainThread());
//        }
//    }

    public void getTrendingMovie(){
            Log.d("Check get db instance",getDatabaseInstance(application).toString());
            Log.d("API Check","Checking if working1"+Constants.TRENDING_FRAGMENT);

            Observable<List<MovieModel>> dataFromDb= getDatabaseInstance(application).dao().getAllTrendingMovies()
                    .filter(movieModelList -> movieModelList.size()>0)
                    .flatMap(movieModelList -> {
                        return Single.just(movieModelList).toObservable();
                    });

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
                .doOnNext(movieModelList -> {
                    database.dao().upsertTrending(movieModelList);
                });
//                .map(movieModelList -> {
//                    Observable.create(subscribe -> {
//                        Log.d("Putting data into db","check");
//                        database.dao().upsertTrending(movieModelList);
//                        subscribe.onComplete();
//                    }).subscribeOn(Schedulers.computation()).subscribe();
//                    return movieModelList;
//                })
                Log.d("check data", "check" + dataFromDb.toString());

                observeData(dataFromDb, dataFromApi);
            }
    }

    private void observeData(Observable<List<MovieModel>> dataFromDb, Observable<List<MovieModel>> dataFromApi) {
        disposable.add(
                dataFromApi
                .subscribeOn(Schedulers.io())
                .subscribe(
                        response ->{
                            Log.d("Getting Response", "response size = " + response.size());
                            response.forEach(res -> Log.d("Getting Response : ", "Movie : " + res.getTitle() + " " + res.isBookmark() + " " + res.isTrending() + " " + res.isNowPlaying()));
                        },
                        error -> {
                            Log.d("Getting Response Error", "response size = " + error.toString());
                            observeDataFromDB(dataFromDb);
                        },
                        () -> {
                            Log.d("Getting Response Message", "on Complete");
                            observeDataFromDB(dataFromDb);
                        }
                )
        );

    }

    private void observeDataFromDB(Observable<List<MovieModel>> dataFromDb) {
        disposable.add(
            dataFromDb
            .timeout(500, TimeUnit.MILLISECONDS)
            .onErrorResumeNext(dataFromDb)
            .subscribeOn(Schedulers.computation())
            .subscribe(response ->{
                        Log.d("Getting Response DB", "response size = " + response.size());
                        response.forEach(res -> Log.d("Getting Response : ", "Movie : " + res.getTitle() + " " + res.isBookmark() + " " + res.isTrending() + " " + res.isNowPlaying()));
                    },
                    error -> {
                        Log.d("Getting Response Error", "response size = " + error.toString());
                    },
                    () -> {
                        Log.d("Getting Response Message", "on Complete");
                    })
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


    public Observable<List<MovieModel>> getBookmarkedMovies() {

        return getDatabaseInstance(application).dao().getBookmarkedMovies().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread());

    }
}