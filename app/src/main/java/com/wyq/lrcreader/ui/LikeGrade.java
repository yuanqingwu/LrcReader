package com.wyq.lrcreader.ui;

import com.wyq.lrcreader.R;

/**
 * @author Uni.W
 * @date 2019/2/26 17:27
 */
public enum LikeGrade {


    LIKE_GRADE_NOT(0, "不喜欢", R.drawable.ic_favorite_border_black_24dp),
    LIKE_GRADE_LITTER(1, "有点喜欢", R.drawable.ic_favorite_pink_24dp),
    LIKE_GRADE_NORMAL(2, "喜欢", R.drawable.ic_favorite_orange_24dp),
    LIKE_GRADE_MOST(3, "很喜欢", R.drawable.ic_favorite_read_24dp),
    ;


    private int value;
    private int drawableId;
    private String name;

    LikeGrade(int value, String name, int drawableId) {
        this.value = value;
        this.name = name;
        this.drawableId = drawableId;
    }

    public int getValue() {
        return value;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public String getName() {
        return name;
    }

}
