package com.wyq.lrcreader.model.netmodel.gecimemodel;

import java.util.List;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LyricResponse {
    private int count;
    private int code;
    private List<LyricResult> result;

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

    public List<LyricResult> getResult() {
        return result;
    }

    public void setResult(List<LyricResult> result) {
        this.result = result;
    }


    public boolean isDataAvailable() {
        return code == 0 && count > 0;
    }


    @Override
    public String toString() {
        return "LyricResponse{" +
                "count=" + count +
                ", code=" + code +
                ", result=" + result +
                '}';
    }
}
