package com.example.moviesinshorts.database.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.moviesinshorts.model.MovieModel;

import java.util.ArrayList;

@Entity
public class Movie {


    public Movie(MovieModel movieModel) {
        this.id = movieModel.getId();
        this.popularity = movieModel.getPopularity();
        this.vote_average = movieModel.getVote_average();
        this.original_language = movieModel.getOriginal_language();
        this.title = movieModel.getTitle();
        this.poster_path = movieModel.getPoster_path();
        this.overview = movieModel.getOverview();
        this.backdrop_path = movieModel.getBackdrop_path();
    }

    private String backdrop_path;
    @PrimaryKey
    private int id;
    private double vote_average;
    private String original_language;
    private String overview;
    private double popularity;
    private String poster_path;
    private String title;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int getId() {
        return id;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }
}
