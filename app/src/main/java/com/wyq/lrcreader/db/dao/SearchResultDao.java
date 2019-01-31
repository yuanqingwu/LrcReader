package com.wyq.lrcreader.db.dao;

import com.wyq.lrcreader.db.entity.SearchResultEntity;

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

//    @Query("SELECT * FROM search_result")
//    LiveData<List<SearchResultEntity>> getAll();

    @Query("SELECT * FROM search_result ORDER BY id DESC")
    DataSource.Factory<Integer, SearchResultEntity> getAll();
}
