package com.wyq.lrcreader.utils;

import android.content.Context;

/**
 * @author Uni.W
 * @date 2019/1/18 21:04
 */
public class BitmapFactory {

    private static BitmapFactory bitmapFactory;

    private BitmapFactory() {

    }

    public static BitmapFactory getInstance() {
        if (bitmapFactory == null) {
            synchronized (BitmapFactory.class) {
                if (bitmapFactory == null) {
                    bitmapFactory = new BitmapFactory();
                }
            }
        }
        return bitmapFactory;
    }


    private void buildGlide(Context context) {
    }

    public void toView() {

    }
}
