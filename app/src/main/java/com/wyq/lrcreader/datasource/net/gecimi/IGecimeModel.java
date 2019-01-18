package com.wyq.lrcreader.datasource.net.gecimi;

import com.wyq.lrcreader.model.netmodel.gecimemodel.AlbumCover;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Artist;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LyricResult;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Uni.W
 * @date 2019/1/12 16:53
 */
public interface IGecimeModel {

    Flowable<List<LyricResult>> getLrcResultList(String songName);

    Flowable<Artist> getArtist(long artistId);

    Flowable<AlbumCover> getAlbumCover(long albumId);

}
