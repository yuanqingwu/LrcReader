package com.wyq.lrcreader.adapter.item;

import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.utils.PagingRequestHelper;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

/**
 * @author Uni.W
 * @date 2019/1/30 19:10
 */
public class LrcBoundaryCallback extends PagedList.BoundaryCallback<SearchResultEntity> {

    private Executor ioExecutor;

    private PagingRequestHelper helper;

    public LrcBoundaryCallback() {
        helper = new PagingRequestHelper(ioExecutor);
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL, new PagingRequestHelper.Request() {
            @Override
            public void run(Callback callback) {

            }
        });
    }

    @Override
    public void onItemAtEndLoaded(@NonNull SearchResultEntity itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull SearchResultEntity itemAtFront) {
        super.onItemAtFrontLoaded(itemAtFront);
    }
}
