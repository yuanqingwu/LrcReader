package com.wyq.lrcreader.datasource.local.gecimi;

import com.wyq.lrcreader.db.AppDatabase;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

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

//    public LiveData<List<SearchResultEntity>> getAllSearchResult() {
//        return database.getSearchResultDao().getAll();
//    }

    public LiveData<PagedList<SearchResultEntity>> getAllSearchResult(String filter) {
        DataSource.Factory<Integer, SearchResultEntity> lrcSource = filter == null ?
                database.getSearchResultDao().getAll() :
                database.getSearchResultDao().getLocalSearchResult(filter);
        LogUtil.i("" + lrcSource == null ? "source is null" : "source is not null");
        return new LivePagedListBuilder(lrcSource,
                10)
                .build();
    }

    public void insertSearchResult(SearchResultEntity entity) {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                database.getSearchResultDao().insert(entity);
                LogUtil.i("insert:" + entity.getSongName() + " " + entity.getDataSource());
            });
        });
    }

    public void insertToSong(SongEntity songEntity) {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                database.getSongDao().insertSong(songEntity);
            });
        });
    }

    public void deleteSong(SongEntity entity) {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                database.getSongDao().delete(entity);
            });
        });
    }

    public LiveData<List<SongEntity>> getLikeSongList() {
        return database.getSongDao().getLikeSongList();
    }
}
