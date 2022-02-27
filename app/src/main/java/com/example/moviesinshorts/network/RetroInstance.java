package com.example.moviesinshorts.network;

import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroInstance {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit retrofit;

    private static Retrofit getRetroFitClient() {
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Object buildApi(@NotNull Class api) {
        return getRetroFitClient().create(api);
    }

    public static NowPlayingApi getNowPlayingApi(){
        return getRetroFitClient().create(NowPlayingApi.class);
    }


}
