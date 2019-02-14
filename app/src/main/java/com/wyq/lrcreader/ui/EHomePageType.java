package com.wyq.lrcreader.ui;

import com.wyq.lrcreader.ui.fragment.LocalFragment;
import com.wyq.lrcreader.ui.fragment.LrcLikeFragment;
import com.wyq.lrcreader.ui.fragment.SearchResultFragment;

import androidx.fragment.app.Fragment;

/**
 * @author Uni.W
 * @date 2019/1/20 14:54
 */
public enum EHomePageType {

    SEARCH_PAGE("搜索", 0, SearchResultFragment.newInstance(null)),
    LIKE_PAGE("喜欢", 1, LrcLikeFragment.newInstance()),
    LOCAL_PAGE("本地", 2, LocalFragment.newInstance()),
    ;


    private int position;
    private String pageName;
    private Fragment fragment;

    EHomePageType(String pageName, int position, Fragment fragment) {
        this.pageName = pageName;
        this.position = position;
        this.fragment = fragment;
    }

    public int getPosition() {
        return position;
    }

    public String getPageName() {
        return pageName;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
