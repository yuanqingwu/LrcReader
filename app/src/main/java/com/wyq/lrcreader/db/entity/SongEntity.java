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

    @PrimaryKey
    private int id;
    private String songName;
    private String artist;
    private String lrcUri;
    private String album;//专辑
    private String albumCoverUri;//专辑封面地址
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
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String getLrcUri() {
        return lrcUri;
    }

    public void setLrcUri(String lrcUri) {
        this.lrcUri = lrcUri;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String getAlbumCoverUri() {
        return albumCoverUri;
    }

    public void setAlbumCoverUri(String albumCoverUri) {
        this.albumCoverUri = albumCoverUri;
    }

    @Override
    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Date getSearchAt() {
        return searchAt;
    }

    public void setSearchAt(Date searchAt) {
        this.searchAt = searchAt;
    }
}
