package com.wyq.lrcreader.datasource.local.gecimi;

import com.wyq.lrcreader.db.AppDatabase;
import com.wyq.lrcreader.db.entity.SearchResultEntity;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author Uni.W
 * @date 2019/1/22 21:24
 */
public class DbGecimiRepository {

    private static DbGecimiRepository repository;

    private AppDatabase database;

    private DbGecimiRepository(AppDatabase appDatabase) {
        this.database = appDatabase;
    }

    public static DbGecimiRepository getInstance(AppDatabase appDatabase) {
        if (repository == null) {
            synchronized (DbGecimiRepository.class) {
                repository = new DbGecimiRepository(appDatabase);
            }
        }
        return repository;
    }


    public LiveData<List<SearchResultEntity>> getAllSearchResult() {
        return database.getSearchResultDao().getAll();
    }

    public void insertSearchResult(SearchResultEntity entity) {
        database.getSearchResultDao().insert(entity);
    }
}
