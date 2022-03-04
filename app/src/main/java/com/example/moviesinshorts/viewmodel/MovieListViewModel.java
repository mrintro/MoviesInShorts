package com.example.moviesinshorts.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

    public MovieListViewModel(Application application){
        movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
        this.application=application;
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getTrendingMovies(){
        Log.d("Check","Check Result Api");
        Disposable s = movieListRepository.getTrendingMovie().subscribe(movieModelList -> {

            Log.d("checking where the data is comming from", "data");
            for(MovieModel movie : movieModelList){
                Log.d("check movie", movie.getTitle()+movie.isBookmark());
            }

            Log.d("Check","checking subscribe api1");
            trendingMovieList.postValue(movieModelList);
        },
        err->{
            Log.d("View Model", "Error Fetching trending movies");
        }, () -> {
          Log.d("View Model", "Fetched trending movies");
        }
        );
        return trendingMovieList;
    }

































    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getNowPlayingMovies(){
        Log.d("Check","Check Result Api");
        Disposable s = movieListRepository.getNowPlayingMovies().subscribe(responseModel -> {
            Log.d("View Model","checking subscribe api2 "+responseModel.isDb()+" "+responseModel.getMovieModels().size());
            if(responseModel.isDb() && responseModel.getMovieModels().size()>0) nowPlayingMovieList.postValue(responseModel.getMovieModels());
//            else if(responseModel.getMovieModels().size()==0){
//                getNowPlayingMovies();
//            }
        },onError -> {
            Log.d("View Model", "Error Fetching trending movies");
        }, () -> {
            Log.d("View Model", "Fetched trending movies");
        });
        return nowPlayingMovieList;
    }

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
