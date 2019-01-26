package com.wyq.lrcreader.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Uni.W
 * @date 2019/1/25 21:29
 */
public class AssetsUtils {

    public static String readAssets2String(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
            if (is != null) {
                int len = is.available();
                byte[] buffer = new byte[len];
                is.read(buffer);
                return new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(is);
        }
        return null;
    }

    private Bitmap getDrawableBitmapByName(Context context, String name) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
