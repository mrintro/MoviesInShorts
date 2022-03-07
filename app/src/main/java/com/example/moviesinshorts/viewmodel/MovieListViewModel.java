package com.example.moviesinshorts.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.repository.MovieListRepository;
import com.example.moviesinshorts.utils.NetworkHelper;

import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class MovieListViewModel extends ViewModel {

    private MovieListRepository movieListRepository;
    private MutableLiveData<List<MovieModel>> nowPlayingMovieList = new MutableLiveData<List<MovieModel>>();
    private MutableLiveData<List<MovieModel>> trendingMovieList = new MutableLiveData<List<MovieModel>>();
    private MutableLiveData<List<MovieModel>> searchMovieList = new MutableLiveData<List<MovieModel>>();
    private MutableLiveData<List<MovieModel>> bookMarkedMovie = new MutableLiveData<List<MovieModel>>();
    private Application application;
    private CompositeDisposable disposable = new CompositeDisposable();

    public void setNowPlayingMovieList() {
        this.nowPlayingMovieList = movieListRepository.getNowPlayingMovieList();
    }

    public void setTrendingMovieList() {
        this.trendingMovieList = movieListRepository.getTrendingMovieList();
    }

    public LiveData<List<MovieModel>> getNowPlayingMovieList() {
        setNowPlayingMovieList();
        return nowPlayingMovieList;
    }

    public LiveData<List<MovieModel>> getTrendingMovieList() {
        setTrendingMovieList();
        return trendingMovieList;
    }

    public MovieListViewModel(Application application){
        movieListRepository = MovieListRepository.getMovieListRepositoryInstance(application);
        this.application=application;
    }

    @SuppressLint("CheckResult")
    public void setTrendingMovies() {
        movieListRepository.getTrendingMovie();
    }


    public void setNowPlayingMovies() {
        movieListRepository.getNowPlayingMovie();
    }

    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getSearchMovie(String searchText){
        if(NetworkHelper.checkNetwork(application)) {
            movieListRepository.getSearchMovie(searchText).subscribeOn(AndroidSchedulers.mainThread()).subscribe(searchMovieList::postValue);
        }
        return searchMovieList;
    }

    public void bookMarkMovie(int id, boolean flag) {
        movieListRepository.bookMarkMovie(id, flag);
    }

    public void bookMarkMovie(MovieModel movieModel){
        movieListRepository.bookMarkMovie(movieModel);
    }


    @SuppressLint("CheckResult")
    public LiveData<List<MovieModel>> getBookMarkedMovies(){
        movieListRepository.getBookmarkedMovies().subscribe(movieModelList -> {
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
