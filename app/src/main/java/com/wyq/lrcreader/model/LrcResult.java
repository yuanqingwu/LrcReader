package com.wyq.lrcreader.model;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcResult {
    private int aid;
    private String lrc;
    private String song;
    private int artist_id;
    private int sid;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "LrcResult{" +
                "aid=" + aid +
                ", lrc='" + lrc + '\'' +
                ", song='" + song + '\'' +
                ", artist_id=" + artist_id +
                ", sid=" + sid +
                '}';
    }
}
