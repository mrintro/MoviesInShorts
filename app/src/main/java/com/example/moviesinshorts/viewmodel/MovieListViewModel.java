package com.example.moviesinshorts.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesinshorts.database.tables.Movie;
import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.repository.MovieListRepository;
import com.example.moviesinshorts.utils.NetworkHelper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends ViewModel {

    private MovieListRepository movieListRepository;
    private final MutableLiveData<List<MovieModel>> nowPlayingMovieList = new MutableLiveData<List<MovieModel>>();
    private final MutableLiveData<List<MovieModel>> trendingMovieList = new MutableLiveData<List<MovieModel>>();
    private final MutableLiveData<List<MovieModel>> searchMovieList = new MutableLiveData<List<MovieModel>>();
    private Application application;
//
//    public LiveData<List<Movie>> getMovieLive() {
//        return movie;
//    }

    public MovieListViewModel(Application application){
        movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
        this.application=application;
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getTrendingMovies(){
        movieListRepository.getTrendingMovie().subscribe(movieModelList -> {
            Log.d("myapp", Log.getStackTraceString(new Exception()));
            Log.d("Check","checking subscribe api1");
            trendingMovieList.postValue(movieModelList);
        });
        return trendingMovieList;
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getNowPlayingMovies(){
        movieListRepository.getNowPlayingMovies().subscribe(movieModelList -> {
            Log.d("Check","checking subscribe api2");
            nowPlayingMovieList.postValue(movieModelList);
        });
        return nowPlayingMovieList;
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getSearchMovie(String searchText){
        Log.d("Check","Check Result Api");
        if(NetworkHelper.checkNetwork(application)) {
            movieListRepository.getSearchMovie(searchText).subscribeOn(AndroidSchedulers.mainThread()).subscribe(searchMovieList::postValue);
        }
        return searchMovieList;
    }

    public void bookMarkMovie(int id, boolean flag) {
        movieListRepository.bookMarkMovie(id, flag);
    }

}
