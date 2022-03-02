package com.example.moviesinshorts.database;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.moviesinshorts.database.tables.Movie;
import com.example.moviesinshorts.database.tables.NowPlayingMovie;
import com.example.moviesinshorts.database.tables.TrendingMovie;
import com.example.moviesinshorts.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

@androidx.room.Dao
public abstract class Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addMovie(MovieModel movie);

    @Query("SELECT * FROM MovieModel")
    public abstract Observable<List<MovieModel>> getAllMovies();


    @Query("SELECT * FROM MovieModel where MovieModel.trending=1")
    public abstract  Observable<List<MovieModel>> getAllTrendingMovies();

    @Query("SELECT * FROM MovieModel where MovieModel.nowPlaying=1")
    public abstract  Observable<List<MovieModel>> getAllNowPlayingMovies();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> addMultipleMovie(List<MovieModel> movieList);

    @Query("update MovieModel set trending=:flag WHERE MovieModel.id = :id")
    public abstract void updateTrending(int id, boolean flag);


    @Transaction
    public void upsertTrending(List<MovieModel> movieModelList){
        List<Long> insertResults = addMultipleMovie(movieModelList);
        for(int i=0;i<movieModelList.size();i++){
            if(insertResults.get(i)==-1) updateTrending(movieModelList.get(i).getId(), true);
        }
    }

    @Query("update MovieModel set nowPlaying=:flag WHERE MovieModel.id = :id")
    public abstract void updateNowPlaying(int id, boolean flag);

    @Transaction
    public void upsertNowPlaying(List<MovieModel> movieModelList){
        List<Long> insertResults = addMultipleMovie(movieModelList);
        for(int i=0;i<movieModelList.size();i++){
            if(insertResults.get(i)==-1) updateNowPlaying(movieModelList.get(i).getId(), true);
        }
    }



//    @Insert
//    public void addNowPlayingMovie(NowPlayingMovie movieID);
//
//    @Insert
//    public void addTrendingMovie(TrendingMovie movieID);

//    @Insert
//    public void addMultipleTrendingMovie(List<TrendingMovie> movieID);
//
//    @Insert
//    public void addMultipleNowPlayingMovie(List<NowPlayingMovie> movieID);

//    @Query("SELECT Movie.*, TrendingMovie.* FROM Movie INNER JOIN Movie ON TrendingMovie.id=Movie.id")

//    @Query("SELECT * FROM TrendingMovie")
//    public Flowable<List<TrendingMovie>> getTrendingMovie();
//
//    @Query("select MovieList.* from Movie inner join TrendingMovie on TrendingMovie.id = Movie.id")
//    public Flowable<List<Movie>> getTrendingMovie();
//
//    @Query("select Movie.* from Movie inner join NowPlayingMovie on NowPlayingMovie.id = Movie.id")
//    public Flowable<List<Movie>> getNowPlayingMovie();


}
