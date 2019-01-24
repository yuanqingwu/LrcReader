package com.wyq.lrcreader.db.entity;

import com.wyq.lrcreader.model.ISong;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/16 21:32
 */
@Entity(tableName = "song")
public class SongEntity implements ISong {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String artist;
    private String lrc;
    private String album;//专辑
    private String albumCover;//专辑封面地址
    private int like;//是否收藏
    private String dataSource;//数据源
    private Date searchAt;//搜索时间，查看时间

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Date getSearchAt() {
        return searchAt;
    }

    public void setSearchAt(Date searchAt) {
        this.searchAt = searchAt;
    }

    @Override
    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    @Override
    public String getAlbumCover() {
        return albumCover;
    }

    @Override
    public boolean isLike() {
        return like > 0;
    }

    @Override
    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


}
