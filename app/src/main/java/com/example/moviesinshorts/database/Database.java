package com.example.moviesinshorts.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moviesinshorts.database.tables.Movie;
import com.example.moviesinshorts.database.tables.NowPlayingMovie;
import com.example.moviesinshorts.database.tables.TrendingMovie;
import com.example.moviesinshorts.model.MovieModel;

import java.util.List;

import kotlin.jvm.Volatile;

@androidx.room.Database(entities = {MovieModel.class, NowPlayingMovie.class, TrendingMovie.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract Dao dao();

    @Volatile
    private static Database databaseInstance;

    public static Database getDatabaseInstance(Context context) {
        if(databaseInstance==null) databaseInstance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "MovieDatabase")
                .allowMainThreadQueries().build();
        return databaseInstance;
    }

}
