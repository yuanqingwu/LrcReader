package com.wyq.lrcreader.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.wyq.lrcreader.utils.BitmapUtil;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class Song implements Parcelable {
    private String songName;
    private String artist;
    private String lrc;
    private String album;


    private Bitmap albumCover;

    public Song() {
        songName = "songName";
        artist = "artist";
        lrc = "lrc";
        album = "album";
        albumCover = Bitmap.createBitmap(74, 64, Bitmap.Config.ARGB_8888);
    }

    protected Song(Parcel in) {
        songName = in.readString();
        artist = in.readString();
        lrc = in.readString();
        albumCover = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(Bitmap albumCover) {
        this.albumCover = albumCover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(artist);
        dest.writeString(lrc);
        dest.writeString(album);
        dest.writeParcelable(albumCover, flags);
    }

    @Override
    public String toString() {
        //   return new Gson().toJson(this);
        String albumCoverStr = BitmapUtil.convertIconToString(albumCover);
        return "Song{" +
                "songName=(%" + songName + "%)" +
                ",artist=(%" + artist + "%)" +
                ",lrc=(%" + lrc + "%)" +
                ",album=(%" + album + "%)" +
                ",albumCover=(%" + albumCoverStr + "%)" +
                "}";
    }
}
