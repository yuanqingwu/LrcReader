package com.wyq.lrcreader.datasource;

import com.wyq.lrcreader.model.ISong;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Uni.W
 * @date 2019/1/14 21:37
 */
public interface SongDataSource {

    Flowable<List<ISong>> getSongList();

}
