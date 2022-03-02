package com.example.moviesinshorts.database;

import com.example.moviesinshorts.network.NowPlayingApi;
import com.example.moviesinshorts.network.RetroInstance;
import com.example.moviesinshorts.network.SearchQueryApi;
import com.example.moviesinshorts.network.TrendingApi;

import io.reactivex.Observable;

public class DatabaseCalls {

    private DatabaseCalls databaseCallsInstance;
    private NowPlayingApi nowPlayingApi;
    private TrendingApi trendingApi;
    private SearchQueryApi searchQueryApi;

    private NowPlayingApi getNowPlayingApi() {
        if(nowPlayingApi==null) nowPlayingApi= (NowPlayingApi) RetroInstance.buildApi(NowPlayingApi.class);
        return nowPlayingApi;
    }
    private TrendingApi getTrendingApi() {
        if(trendingApi==null) trendingApi = (TrendingApi) RetroInstance.buildApi(TrendingApi.class);
        return trendingApi;
    }
    private SearchQueryApi getSearchQueryApi() {
        if(searchQueryApi==null) searchQueryApi = (SearchQueryApi) RetroInstance.buildApi(SearchQueryApi.class);
        return searchQueryApi;
    }


    public DatabaseCalls getDatabaseCallsInstance(){
        if(databaseCallsInstance==null) databaseCallsInstance = new DatabaseCalls();
        return databaseCallsInstance;
    }





}
