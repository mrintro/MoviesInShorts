package com.example.moviesinshorts.model;

import com.example.moviesinshorts.database.tables.Movie;

import java.util.List;

public class ResponseModel {

    private boolean isDb;
    private List<MovieModel> movieModels;

    public ResponseModel(boolean isDb, List<MovieModel> movieModels) {
        this.isDb = isDb;
        this.movieModels = movieModels;
    }

    public boolean isDb() {
        return isDb;
    }

    public void setDb(boolean db) {
        isDb = db;
    }

    public List<MovieModel> getMovieModels() {
        return movieModels;
    }

    public void setMovieModels(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
    }
}
