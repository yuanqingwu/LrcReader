package com.wyq.lrcreader.model;

import java.util.List;

/**
 * Created by Uni.W on 2016/8/19.
 */
public class ArtistResponse {
    private int count;
    private int code;
    private Artist result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Artist getResult() {
        return result;
    }

    public void setResult(Artist result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ArtistResponse{" +
                "count=" + count +
                ", code=" + code +
                ", result=" + result +
                '}';
    }
}
