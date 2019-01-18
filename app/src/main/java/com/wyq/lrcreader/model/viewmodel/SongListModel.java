package com.wyq.lrcreader.model.viewmodel;

/**
 * @author Uni.W
 * @date 2019/1/12 21:09
 */
public class SongListModel {

    private String songName;
    private String artist;
    //    private String album;
    private String lrcUri;
    private String albumCoverUri;

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

    //    public String getAlbum() {
//        return album;
//    }
//
//    public void setAlbum(String album) {
//        this.album = album;
//    }


}
