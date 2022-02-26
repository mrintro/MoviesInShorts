package com.example.moviesinshorts.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends ViewModel {

    private MutableLiveData<List<MovieModel>> nowPlayingLiveData;
    public MovieListViewModel(){
        nowPlayingLiveData = new MutableLiveData<List<MovieModel>>();
    }

    public MutableLiveData<List<MovieModel>> getNowPlayingLiveData() {
        return nowPlayingLiveData;
    }

    public void makeNowTrendingApiCall() {
        NowPlayingApi nowPlayingApi = RetroInstance.getRetroFitClient().create(NowPlayingApi.class);
        Call<List<MovieModel>> nowPlayingApiCall = nowPlayingApi.getMovieList();
        nowPlayingApiCall.enqueue(new Callback<List<MovieModel>>() {
            @Override
            public void onResponse(Call<List<MovieModel>> call, Response<List<MovieModel>> response) {
                nowPlayingLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<MovieModel>> call, Throwable t) {
                nowPlayingLiveData.postValue(null);
            }
        });
    }

}
