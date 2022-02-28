package com.example.moviesinshorts.network;

import com.example.moviesinshorts.response.MovieListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchQueryApi {

    @GET("search/movie?api_key=8d7f9feacb744041acb181031087eb6d")
    Call<MovieListResponse> getSearchedMovie(
            @Query("query") String query
    );

}
