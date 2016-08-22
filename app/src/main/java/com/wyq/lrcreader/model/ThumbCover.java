package com.wyq.lrcreader.model;

/**
 * Created by Uni.W on 2016/8/19.
 */
public class ThumbCover {
    private String cover;
    private String thumb;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public String toString() {
        return "ThumbCover{" +
                "cover='" + cover + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }
}
