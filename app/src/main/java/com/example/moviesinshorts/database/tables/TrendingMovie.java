package com.example.moviesinshorts.database.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrendingMovie {
    @PrimaryKey
    private int id;

    public TrendingMovie(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
