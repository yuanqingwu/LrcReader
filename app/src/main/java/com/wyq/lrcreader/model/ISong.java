package com.wyq.lrcreader.model;


import java.util.Date;

/**
 * @author Uni.W
 * @date 2019/1/16 21:42
 */
public interface ISong {
    int getAid();

    String getSongName();

    String getArtist();

    String getLrc();

    String getAlbum();

    String getAlbumCover();

    int getLike();

    String getDataSource();


    Date getSearchAt();

}
