package com.wyq.lrcreader.utils;

import android.util.Log;

/**
 * Created by Uni.W on 2016/11/23.
 */

public class LogUtil {

    private static String TAG = "Test";

    public static void i(String s) {
        Log.i(TAG, s);
    }


    public static void d(String s) {
        Log.d(TAG, s);
    }

    public static void e(String s) {
        Log.e(TAG, s);
    }
}
