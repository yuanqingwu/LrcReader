package com.wyq.lrcreader.db.dao;

import com.wyq.lrcreader.db.entity.SearchHistoryEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @author Uni.W
 * @date 2019/2/13 16:38
 */
@Dao
public interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistoryEntity entity);

    @Delete
    void delete(SearchHistoryEntity entity);

    @Query("DELETE FROM search_history")
    void deleteAll();

    @Query("SELECT * FROM search_history")
    LiveData<List<SearchHistoryEntity>> getAll();
}
