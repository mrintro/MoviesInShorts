package com.example.moviesinshorts.model;

import java.util.ArrayList;

public class NowPlayingMovieResponse {
    private Dates dates;
    private int page;
    private ArrayList<Result> results;
    private int total_pages;
    private int total_results;
}

class Dates{
    private String maximum;
    private String minimum;
}

class Result{
    private boolean adult;
    private String backdrop_path;
    private ArrayList<Integer> genre_ids;
    private int id;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;
}