package com.example.moviesinshorts.network;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroInstance {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    public static Retrofit retrofit;


    private static Retrofit getRetroFitClient() {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
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
