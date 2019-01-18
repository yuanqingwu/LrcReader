package com.wyq.lrcreader.model;

import java.sql.Date;

/**
 * @author Uni.W
 * @date 2019/1/16 21:42
 */
public interface ISong {

    int getId();

    String getName();

    String getArtist();

    String getLrc();

    String getAlbum();

    String getAlbumCover();

    boolean isLike();

    Date getSearchAt();

    String getDataSource();

}
