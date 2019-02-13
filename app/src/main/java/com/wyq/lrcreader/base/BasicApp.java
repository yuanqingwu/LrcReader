package com.wyq.lrcreader.base;

import android.app.Application;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.AppDatabase;

/**
 * @author Uni.W
 * @date 2019/1/16 18:35
 */
public class BasicApp extends Application {

    private AppExecutors executors;

    @Override
    public void onCreate() {
        super.onCreate();
        executors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, getExecutors());
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public AppExecutors getExecutors() {
        return executors;
    }
}
