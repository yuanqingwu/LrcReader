package com.wyq.lrcreader.ui;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.ui.fragment.LocalFragment;
import com.wyq.lrcreader.ui.fragment.LrcLikeFragment;
import com.wyq.lrcreader.ui.fragment.SearchResultFragment;

import androidx.fragment.app.Fragment;

/**
 * @author Uni.W
 * @date 2019/1/20 14:54
 */
public enum EHomePageType {

    SEARCH_PAGE("搜索", 0, SearchResultFragment.newInstance(null), R.menu.home_appbar_search),
    LIKE_PAGE("喜欢", 1, LrcLikeFragment.newInstance(), R.menu.home_appbar_like),
    LOCAL_PAGE("本地", 2, LocalFragment.newInstance(), R.menu.home_appbar_local),
    ;


    private int position;
    private int menuId;
    private String pageName;
    private Fragment fragment;

    EHomePageType(String pageName, int position, Fragment fragment, int menuId) {
        this.pageName = pageName;
        this.position = position;
        this.fragment = fragment;
        this.menuId = menuId;
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

    public int getMenuId() {
        return menuId;
    }

}
