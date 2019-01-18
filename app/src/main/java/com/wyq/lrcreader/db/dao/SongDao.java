package com.wyq.lrcreader.db.dao;

import com.wyq.lrcreader.db.entity.SongEntity;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author Uni.W
 * @date 2019/1/16 21:54
 */
public interface SongDao {

    LiveData<List<SongEntity>> getSongList();
}
