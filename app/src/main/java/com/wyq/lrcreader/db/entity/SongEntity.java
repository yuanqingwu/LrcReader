package com.wyq.lrcreader.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.wyq.lrcreader.model.ISong;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * @author Uni.W
 * @date 2019/1/16 21:32
 */
@Entity(tableName = "song", indices = {@Index(value = {"aid"}, unique = true)})
public class SongEntity implements ISong, Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int aid;
    private String songName;
    private String artist;
    private String album;//专辑
    private String lrc;
    private int like;//是否收藏
    private String dataSource;//数据源
    private Date searchAt;//搜索时间，查看时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
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

    private String albumCover;//专辑封面地址

    public SongEntity() {

    }

    @Override
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    private SongEntity(Parcel parcel) {
        this.setAid(parcel.readInt());
        this.setSongName(parcel.readString());
        this.setArtist(parcel.readString());
        this.setLrc(parcel.readString());
        this.setAlbum(parcel.readString());
        this.setAlbumCover(parcel.readString());
        this.setLike(parcel.readInt());
        this.setDataSource(parcel.readString());
        this.setSearchAt(new Date(parcel.readLong()));
    }

    @Override
    public String getLrc() {
        return lrc;
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

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    @Override
    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }


    public static final Creator<SongEntity> CREATOR = new Creator<SongEntity>() {
        @Override
        public SongEntity createFromParcel(Parcel source) {
            return new SongEntity(source);
        }

        @Override
        public SongEntity[] newArray(int size) {
            return new SongEntity[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getAid());
        dest.writeString(getSongName());
        dest.writeString(getArtist());
        dest.writeString(getLrc());
        dest.writeString(getAlbum());
        dest.writeString(getAlbumCover());
        dest.writeInt(getLike());
        dest.writeString(getDataSource());
        dest.writeLong(getSearchAt().getTime());
    }

//    public SearchResultEntity toSearchResultEntity(){
//        SearchResultEntity entity = new SearchResultEntity();
//        entity.setAid(getAid());
//        entity.setDataSource(getDataSource());
//        entity.setAlbumCover(getAlbumCover());
//        entity.setLrc(getLrc());
//        entity.setArtist(getArtist());
//        entity.setSongName(getSongName());
//        return entity;
//    }
}
