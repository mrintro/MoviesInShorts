package com.example.moviesinshorts.utils;

import com.example.moviesinshorts.database.tables.Movie;
import com.example.moviesinshorts.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class MovieModelToMovieConverter {
    public static List<Movie> movieModelToMovieDb(List<MovieModel> movieModelList){
        List<Movie> movieDb = new ArrayList<Movie>();
        for(MovieModel movie : movieModelList){
            movieDb.add(new Movie(movie));
        }
        return movieDb;
    }

}
