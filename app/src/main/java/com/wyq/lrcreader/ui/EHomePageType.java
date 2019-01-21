package com.wyq.lrcreader.ui;

/**
 * @author Uni.W
 * @date 2019/1/20 14:54
 */
public enum EHomePageType {

    LIKE_PAGE("", 0),
    LOCAL_PAGE("", 1),
    SEARCH_PAGE("", 2);


    private int position;
    private String pageName;

    EHomePageType(String pageName, int position) {
        this.pageName = pageName;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getPageName() {
        return pageName;
    }
}
