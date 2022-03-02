package com.example.moviesinshorts.database.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NowPlayingMovie {
    @PrimaryKey
    private int id;

    public NowPlayingMovie(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
