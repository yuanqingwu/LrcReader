package com.wyq.lrcreader.datasource.local.gecimi;

import com.tencent.mmkv.MMKV;
import com.wyq.lrcreader.constants.ParamsConstants;
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
                database.getSearchResultDao().getAll(MMKV.defaultMMKV().decodeInt(ParamsConstants.SEARCH_HISTORY_CACHE_MAX_NUMBER, 0)) :
                database.getSearchResultDao().getLocalSearchResult(filter);
        LogUtil.i("" + lrcSource == null ? "source is null" : "source is not null");
        return new LivePagedListBuilder(lrcSource,
                10)
                .build();
    }

    /**
     * 获取所有的歌名
     *
     * @return
     */
    public List<String> getAllSearchResultName() {
//        LogUtil.i(database.getSearchResultDao().getAllName() == null ? "db null" : "db not  null");
        return database.getSearchResultDao().getAllName();
    }

    /**
     * 获取所有的来源名称
     *
     * @return
     */
    public List<String> getAllSearchResultSource() {
        return database.getSearchResultDao().getAllSource();
    }

    public void insertSearchResult(SearchResultEntity entity) {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                database.getSearchResultDao().insert(entity);
                LogUtil.i("insert:" + entity.getSongName() + " " + entity.getDataSource());
            });
        });
    }

    public SearchResultEntity getEntityByAid(int aid) {
        return database.getSearchResultDao().getEntityByAid(aid);
    }

    public void updateSearchResult(SearchResultEntity entity) {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                int rows = database.getSearchResultDao().update(entity);
                LogUtil.i("update:" + rows + entity.getSongName() + " " + entity.getAlbumCoverUri());
            });
        });
    }

    public void deleteAllSearchResults() {
        database.getExecutors().diskIO().execute(() -> {
            database.runInTransaction(() -> {
                database.getSearchResultDao().deleteAll();
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

    public LiveData<List<SongEntity>> getLikeSongList(int likeGrade) {
        return database.getSongDao().getLikeSongList(likeGrade);
    }
}
