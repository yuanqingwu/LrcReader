package com.wyq.lrcreader.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class Song implements Parcelable {
    private String artist;
    private String lrc;
    private Bitmap albumCover;

    public Song(){
        artist="artist";
        lrc="lrc";
        albumCover=Bitmap.createBitmap(74,64, Bitmap.Config.ARGB_8888);
    }

    protected Song(Parcel in) {
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
        dest.writeString(artist);
        dest.writeString(lrc);
        dest.writeParcelable(albumCover, flags);
    }

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", lrc='" + lrc + '\'' +
                ", albumCover=" + albumCover +
                '}';
    }
}
