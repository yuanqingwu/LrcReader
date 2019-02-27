package com.wyq.lrcreader.db.dao;

import com.wyq.lrcreader.db.entity.SearchResultEntity;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author Uni.W
 * @date 2019/1/22 21:15
 */
@Dao
public interface SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchResultEntity entity);

    @Delete
    void delete(SearchResultEntity entity);

    @Update
    void update(SearchResultEntity entity);

    @Query("DELETE FROM search_result")
    void deleteAll();

//    @Query("SELECT * FROM search_result")
//    LiveData<List<SearchResultEntity>> getAll();

    @Query("SELECT * FROM search_result ORDER BY id DESC LIMIT :limit")
    DataSource.Factory<Integer, SearchResultEntity> getAll(int limit);

    @Query("SELECT distinct songName FROM search_result")
    List<String> getAllName();

    @Query("SELECT distinct dataSource FROM search_result")
    List<String> getAllSource();

    @Query("SELECT * FROM search_result WHERE songName LIKE :searchText or dataSource LIKE :searchText")
    DataSource.Factory<Integer, SearchResultEntity> getLocalSearchResult(String searchText);
}
