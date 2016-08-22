package com.wyq.lrcreader.model;

import java.util.List;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcResponse {
    private int count;
    private int code;
    private List<LrcResult> result;

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

    public List<LrcResult> getResult() {
        return result;
    }

    public void setResult(List<LrcResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "LrcResponse{" +
                "count=" + count +
                ", code=" + code +
                ", result=" + result +
                '}';
    }
}
