package com.example.moviesinshorts.response;

import com.example.moviesinshorts.model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieListResponse {

    @SerializedName("total_results")
    @Expose()
    private int total_count;

    @SerializedName("results")
    @Expose()
    private List<MovieModel> movieList;

    public int getTotal_count() {
        return total_count;
    }

    public List<MovieModel> getMovieList() {
        return movieList;
    }

    @Override
    public String toString() {
        return "MovieListResponse{" +
                "total_count=" + total_count +
                ", movieList=" + movieList +
                '}';
    }
}