package com.wyq.lrcreader.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.wyq.lrcreader.model.IListSong;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/12 21:09
 */
@Entity(tableName = "search_result")
public class SearchResultEntity implements IListSong, Parcelable {

    public static final Creator<SearchResultEntity> CREATOR = new Creator<SearchResultEntity>() {
        @Override
        public SearchResultEntity createFromParcel(Parcel in) {
            return new SearchResultEntity(in);
        }

        @Override
        public SearchResultEntity[] newArray(int size) {
            return new SearchResultEntity[size];
        }
    };
    private String songName;
    private String artist;
    @PrimaryKey
    private int id;
    private String lrcUri;
    private String albumCoverUri;
    private String dataSource;//数据源


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String album;

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public SearchResultEntity() {

    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private SearchResultEntity(Parcel parcel) {
        this.setId(parcel.readInt());
        this.setSongName(parcel.readString());
        this.setArtist(parcel.readString());
        this.setAlbum(parcel.readString());
        this.setLrcUri(parcel.readString());
        this.setAlbumCoverUri(parcel.readString());
        this.setDataSource(parcel.readString());
    }

    public void setLrcUri(String lrcUri) {
        this.lrcUri = lrcUri;
    }

    @Override
    public String getSongName() {
        return songName;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getLrcUri() {
        return lrcUri;
    }


    public void setAlbumCoverUri(String albumCoverUri) {
        this.albumCoverUri = albumCoverUri;
    }

    @Override
    public String getAlbum() {
        return null;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String getAlbumCoverUri() {
        return albumCoverUri;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getSongName());
        dest.writeString(getArtist());
        dest.writeString(getAlbum());
        dest.writeString(getLrcUri());
        dest.writeString(getAlbumCoverUri());
        dest.writeString(getDataSource());
    }
}
