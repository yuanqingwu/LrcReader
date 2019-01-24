package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;

import com.wyq.lrcreader.ui.IRetryLoadCallback;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.annotation.Nullable;

/**
 * @author Uni.W
 * @date 2019/1/17 20:32
 */
public abstract class BaseLazyLoadFragment extends BaseFragment implements IRetryLoadCallback {

    protected boolean isViewCreated;
    protected boolean isVisibleToUser;

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i("lazyload onStart");
        isVisibleToUser = getUserVisibleHint();
        if (isVisibleToUser) {
            lazyLoadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        LogUtil.i("lazyload setUserVisibleHint" + isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i("lazyload onActivityCreated");
        isViewCreated = true;
        lazyLoadData();
    }

    /**
     * 当界面创建完成且可见的情况下才加载数据
     */
    private void lazyLoadData() {
        if (isViewCreated && isVisibleToUser) {
            loadData();
            isVisibleToUser = false;
            isViewCreated = false;
        }
    }


    abstract void loadData();

}
