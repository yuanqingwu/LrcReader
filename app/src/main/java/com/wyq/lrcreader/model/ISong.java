package com.wyq.lrcreader.model;


import java.util.Date;

/**
 * @author Uni.W
 * @date 2019/1/16 21:42
 */
public interface ISong extends IListSong {

    int getLike();

    Date getSearchAt();

}
