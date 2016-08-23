package com.wyq.lrcreader.model;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class Lrc implements Comparable<Lrc>{

    private int startTime;
    private long time;
    private String content;

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int compareTo(Lrc another) {
        return this.getStartTime()-another.getStartTime();
    }
}
