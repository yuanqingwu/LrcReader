package com.wyq.lrcreader.base;

/**
 * @author Uni.W
 * @date 2019/1/27 15:07
 */
public class NetworkState {
    public static NetworkState LOADED = new NetworkState(Status.SUCCESS, null);
    public static NetworkState LOADING = new NetworkState(Status.RUNNING, null);
    public Status status;
    public String msg;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static NetworkState error(String msg) {
        return new NetworkState(Status.FAILED, msg);
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }
}

