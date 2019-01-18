package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author Uni.W
 * @date 2019/1/17 20:32
 */
public abstract class BaseLazyLoadFragment extends Fragment {

    protected boolean isViewCreated;
    protected boolean isVisibleToUser;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            lazyLoadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    void onLoadFailed() {

    }

}
