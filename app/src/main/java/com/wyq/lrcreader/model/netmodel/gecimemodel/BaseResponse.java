package com.wyq.lrcreader.model.netmodel.gecimemodel;

/**
 * @author Uni.W
 * @date 2019/1/18 15:36
 */
public class BaseResponse {

    protected int count;
    protected int code;

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

    public boolean isSuccessful() {
        return code == 0;
    }

    public boolean isDataAvailable() {
        return code == 0 && count > 0;
    }
}
