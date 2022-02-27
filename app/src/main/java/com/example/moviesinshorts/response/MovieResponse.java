package com.example.moviesinshorts.response;

import com.example.moviesinshorts.model.MovieModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieResponse {

    @SerializedName("results")
    @Expose
    private MovieModel movieModel;

    MovieModel getMovie() {
        return movieModel;
    }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "movieModel=" + movieModel +
                '}';
    }
}
