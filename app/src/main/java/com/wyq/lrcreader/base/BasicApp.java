package com.wyq.lrcreader.base;

import android.app.Application;

import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.db.AppDatabase;

/**
 * @author Uni.W
 * @date 2019/1/16 18:35
 */
public class BasicApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance();
    }
}
