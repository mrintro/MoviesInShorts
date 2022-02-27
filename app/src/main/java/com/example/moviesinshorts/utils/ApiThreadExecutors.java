package com.example.moviesinshorts.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ApiThreadExecutors {

    private static ApiThreadExecutors apiThreadExecutorsInstance;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
    public static ApiThreadExecutors getApiThreadExecutorsInstance() {
        if(apiThreadExecutorsInstance==null) apiThreadExecutorsInstance = new ApiThreadExecutors();
        return apiThreadExecutorsInstance;
    }
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }


}
