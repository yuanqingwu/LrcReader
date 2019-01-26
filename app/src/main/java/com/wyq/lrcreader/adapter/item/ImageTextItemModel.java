package com.wyq.lrcreader.adapter.item;

import android.graphics.drawable.Drawable;

/**
 * @author Uni.W
 * @date 2019/1/25 21:37
 */
public class ImageTextItemModel {

    private Drawable image;
    private String name;
    private String action;

    public ImageTextItemModel() {

    }

    public ImageTextItemModel(Drawable image, String name, String action) {
        this.image = image;
        this.name = name;
        this.action = action;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
