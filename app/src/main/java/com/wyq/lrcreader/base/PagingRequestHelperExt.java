package com.wyq.lrcreader.base;

import com.wyq.lrcreader.utils.PagingRequestHelper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * @author Uni.W
 * @date 2019/1/29 20:52
 */
public class PagingRequestHelperExt {

    public String getErrorMessage(PagingRequestHelper.StatusReport report) {
        for (PagingRequestHelper.RequestType type : PagingRequestHelper.RequestType.values()) {
            Throwable throwable = report.getErrorFor(type);
            if (throwable != null) {
                return throwable.getMessage();
            }
        }
        return null;
    }

    public LiveData<NetworkState> createStatusLiveData(PagingRequestHelper helper) {
        MutableLiveData liveData = new MutableLiveData();

        helper.addListener(new PagingRequestHelper.Listener() {
            @Override
            public void onStatusChange(@NonNull PagingRequestHelper.StatusReport report) {
                if (report.hasRunning()) {
                    liveData.postValue(NetworkState.LOADING);
                } else if (report.hasError()) {
                    liveData.postValue(NetworkState.error(getErrorMessage(report)));
                } else {
                    liveData.postValue(NetworkState.LOADED);
                }
            }
        });
        return liveData;
    }

}
