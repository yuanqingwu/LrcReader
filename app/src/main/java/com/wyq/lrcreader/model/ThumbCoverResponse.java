package com.wyq.lrcreader.model;

/**
 * Created by Uni.W on 2016/8/19.
 */
public class ThumbCoverResponse {
    private int count;
    private int code;
    private ThumbCover result;

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

    public ThumbCover getResult() {
        return result;
    }

    public void setResult(ThumbCover result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ThumbCoverResponse{" +
                "count=" + count +
                ", code=" + code +
                ", result=" + result +
                '}';
    }
}
