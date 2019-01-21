package com.wyq.lrcreader.db.dao;

import com.wyq.lrcreader.db.entity.SongEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author Uni.W
 * @date 2019/1/16 21:54
 */
@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(SongEntity songEntity);

    @Delete
    void delete(SongEntity songEntity);

    @Update
    void update(SongEntity songEntity);

    @Query("SELECT * FROM song")
    LiveData<List<SongEntity>> getLocalSongList();

    @Query("SELECT * FROM song WHERE `like` > 0 ")
    LiveData<List<SongEntity>> getLikeSongList();
}
