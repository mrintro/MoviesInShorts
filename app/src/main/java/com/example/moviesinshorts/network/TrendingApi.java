package com.example.moviesinshorts.network;

import com.example.moviesinshorts.response.MovieListResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrendingApi {

    @GET("movie/popular?api_key=8d7f9feacb744041acb181031087eb6d&language=en-US&page=1")
    Call<MovieListResponse> getMovieList();

}
