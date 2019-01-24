package com.wyq.lrcreader.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/12 21:09
 */
@Entity(tableName = "search_result")
public class SearchResultEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String songName;
    private String artist;
    //    private String album;
    private String lrcUri;
    private String albumCoverUri;
    private String dataSource;//数据源

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLrcUri() {
        return lrcUri;
    }

    public void setLrcUri(String lrcUri) {
        this.lrcUri = lrcUri;
    }

    public String getAlbumCoverUri() {
        return albumCoverUri;
    }

    public void setAlbumCoverUri(String albumCoverUri) {
        this.albumCoverUri = albumCoverUri;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
