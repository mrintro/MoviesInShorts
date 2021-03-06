package com.example.moviesinshorts.network;

import androidx.annotation.NonNull;

import com.example.moviesinshorts.model.MovieModel;
import com.example.moviesinshorts.response.MovieListResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NowPlayingApi {

    @GET("movie/now_playing?api_key=8d7f9feacb744041acb181031087eb6d&language=en-US&page=1")
    Observable<MovieListResponse> getMovieList();

}
