package com.wyq.lrcreader.model.netmodel.gecimemodel;

/**
 * Created by Uni.W on 2016/8/19.
 */
public class AlbumCoverResponse extends BaseResponse {

    private AlbumCover result;

    public AlbumCover getResult() {
        return result;
    }

    public void setResult(AlbumCover result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "AlbumCoverResponse{" +
                "count=" + count +
                ", code=" + code +
                ", result=" + result +
                '}';
    }
}
