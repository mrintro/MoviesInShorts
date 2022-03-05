package com.example.moviesinshorts.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesinshorts.database.tables.Movie;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.repository.MovieListRepository;
import com.example.moviesinshorts.response.MovieResponse;
import com.example.moviesinshorts.utils.NetworkHelper;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends ViewModel {

    private MovieListRepository movieListRepository;
    private final MutableLiveData<List<MovieModel>> nowPlayingMovieList = new MutableLiveData<List<MovieModel>>();
    private final MutableLiveData<List<MovieModel>> trendingMovieList = new MutableLiveData<List<MovieModel>>();
    private final MutableLiveData<List<MovieModel>> searchMovieList = new MutableLiveData<List<MovieModel>>();
    private final MutableLiveData<List<MovieModel>> bookMarkedMovie = new MutableLiveData<List<MovieModel>>();
    private Application application;

    private SharedPreferences sharedPreferences;
    private CompositeDisposable disposable = new CompositeDisposable();

    public MovieListViewModel(Application application){
        movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
        this.application=application;
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getTrendingMovies(){
//        Log.d("Check","Check Result Api");
//        disposable.add(movieListRepository.getTrendingMovie()
//                .timeout(100, TimeUnit.MILLISECONDS)
//                .onErrorResumeNext()
//                .subscribe(movieModelList -> {
//
//                Log.d("checking where the data is comming from", "data");
//                for(MovieModel movie : movieModelList){
//                    Log.d("check movie", movie.getTitle()+movie.isBookmark());
//                }
//
//                Log.d("Check","checking subscribe api1");
//                trendingMovieList.postValue(movieModelList);
//            },
//            err->{
//                Log.d("View Model", "Error Fetching trending movies");
//            }, () -> {
//              Log.d("View Model", "Fetched trending movies");
//            }
//            )
//        );
        movieListRepository.getTrendingMovie();
        return trendingMovieList;
    }


    public LiveData<List<MovieModel>> getNowPlayingMovies() {
        Log.d("Check","count 1");
//        setDataFromApi();
        Log.d("Check","count 4");
        return nowPlayingMovieList;
    }

    public void setDataFromApi() {
        movieListRepository.getDataFromApi();
        Log.d("Check","count 2");
        callRepositoryFunction();


    }

    private void callRepositoryFunction() {
        movieListRepository.setDataFromApi()
                .subscribe(new DisposableObserver<List<MovieModel>>() {
                    @Override
                    public void onNext(@NonNull List<MovieModel> movieModelList) {
                        Log.d("Check On Next Called","count "+movieModelList.size() );
                        if(movieModelList.size()==0) callRepositoryFunction();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Check Upsert Failure", "Error "+e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Check Upsert complete", "complete ");
                    }
                });


//
//                .subscribe(new DisposableSubscriber<List<MovieModel>>() {
//                    @Override
//                    public void onNext(List<MovieModel> movieModelList) {
//                        Log.d("Check On Next Called","count "+movieModelList.size() );
//                            callRepositoryFunction();
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        Log.d("Check Upsert Failure", "Error "+t.toString());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d("Check Upsert complete", "Error ");
//                    }
//                });
    }


//
//    @SuppressLint("CheckResult")
//    public LiveData<List<MovieModel>> getNowPlayingMovies(){
//        Log.d("Check","Check Result Api");
//        Disposable s = movieListRepository.getNowPlayingMovies().subscribe(responseModel -> {
//            Log.d("View Model","checking subscribe api2 "+responseModel.isDb()+" "+responseModel.getMovieModels().size());
//            if(responseModel.isDb() && responseModel.getMovieModels().size()>0) nowPlayingMovieList.postValue(responseModel.getMovieModels());
////            else if(responseModel.getMovieModels().size()==0){
////                getNowPlayingMovies();
////            }
//        },onError -> {
//            Log.d("View Model", "Error Fetching trending movies");
//        }, () -> {
//            Log.d("View Model", "Fetched trending movies");
//        });
//        return nowPlayingMovieList;
//    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getSearchMovie(String searchText){
        if(NetworkHelper.checkNetwork(application)) {
            movieListRepository.getSearchMovie(searchText).subscribeOn(AndroidSchedulers.mainThread()).subscribe(searchMovieList::postValue);
        }
        return searchMovieList;
    }



    public void bookMarkMovies(ArrayList<Pair<Integer, Boolean>> bookmarkData) {
        movieListRepository.bookMarkMovies(bookmarkData);
    }
    public void bookMarkMovie(int id, boolean flag) {
        movieListRepository.bookMarkMovie(id, flag);
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getBookMarkedMovies(){
        movieListRepository.getBookmarkedMovies().subscribe(movieModelList -> {
            Log.d("Checking bookmarked movies", movieModelList.toString());
            bookMarkedMovie.postValue(movieModelList);
        },
                onError ->{
            Log.d("Error", "Error Fetching Bookmarked Movies"+onError.toString());
                },
                () -> {
            Log.d("Complete", "Process Completed");
                });
        return bookMarkedMovie;
    }

}
