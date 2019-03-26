package com.wyq.lrcreader.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Base64;
import android.view.View;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Uni.W on 2016/8/29.
 */
public class BitmapUtil {

    /**
     * 将图片写入文件
     *
     * @param bitmap
     * @param fileName 文件名字
     * @param filePath 文件夹的路径
     */
    public static void saveBitMapToFile(Bitmap bitmap, String fileName, String filePath) {
        if (fileName == null || filePath == null) {
            LogUtil.i("param is null");
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }

            File albumFile = new File(file, fileName);
            FileOutputStream fos = new FileOutputStream(albumFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static Bitmap getBitmapFromFile(String filePath) {
        if (new File(filePath).exists())
            return BitmapFactory.decodeFile(filePath);
        return null;
    }

    public static boolean deleteBitmapFromFile(String filePath, String fileName) throws FileNotFoundException {
        if (fileName == null || filePath == null) {
            throw new NullPointerException("param can't be null");
        }
        File file = new File(filePath, fileName);
        if (file.exists()) {
            return file.delete();
        } else {
            throw new FileNotFoundException("No such file!");
        }
    }

    /**
     * 改变Bitmap透明度
     *
     * @param sourceImg 原始Bitmap
     * @param number    需要改变的透明度百分比
     * @return 目标Bitmap
     */
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
                sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            if (argb[i] != 0) {// 透明的颜色不作处理
                argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
            }
        }
        // 用处理好的数组建Bitmap
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
                sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }


    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对Bitmap 取MD5值
     *
     * @param bitmap
     * @param <T>    传入bitmap 或者String
     * @return
     * @throws Exception
     */
    public static <T> String getBitmapMD5Hex(T bitmap) throws Exception {
        if (bitmap == null) {
            return null;
        }
        if (bitmap instanceof Bitmap) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(BitmapUtil.convertIconToString((Bitmap) bitmap).getBytes());
                return bytesToHexString(md5.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else if (bitmap instanceof String) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(((String) bitmap).getBytes());
                return bytesToHexString(md5.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("only except String or Bitmap para");
        }

        return null;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    /**
     * 将view转化为bitmap
     *
     * @param view
     * @return
     */

    public static Bitmap convertViewToBitmap(View view) {
        int h = 0;
        if (view instanceof ScrollView) {
            for (int i = 0; i < ((ScrollView) view).getChildCount(); i++) {
                h += ((ScrollView) view).getChildAt(i).getHeight();
                LogUtil.i("scrollview child view height:" + h + " scrollview :" + view.getHeight());
            }
        } else {
            h = view.getHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 转换bitmap为数组
     *
     * @param bmp
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 建立Bitmap防止Out Of Memery Crash
     *
     * @param res       原始资源
     * @param resId     资源id
     * @param reqWidth  目标Bitmap宽度
     * @param reqHeight 目标Bitmap长度
     * @return 目标Bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        // BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        InputStream is = res.openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap getSuitaleBitmap(Bitmap image, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bitmapArray = bos.toByteArray();
        return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    //根据指定宽高计算图片适合的尺寸
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 图片模糊
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap blur(Bitmap bitmap, int radius) {
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int w = newBitmap.getWidth();
        int h = newBitmap.getHeight();
        int[] pix = new int[w * h];
        newBitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        newBitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return newBitmap;
    }


    /**
     * bitmap中的透明色用白色替换
     *
     * @param bitmap
     * @return
     */
    public static Bitmap changeColor(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] colorArray = new int[w * h];
        int n = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = getMixtureWhite(bitmap.getPixel(j, i));
                colorArray[n++] = color;
            }
        }
        return Bitmap.createBitmap(colorArray, w, h, Bitmap.Config.ARGB_8888);
    }

    /**
     * 获取和白色混合颜色
     *
     * @return
     */
    private static int getMixtureWhite(int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.rgb(getSingleMixtureWhite(red, alpha), getSingleMixtureWhite

                        (green, alpha),
                getSingleMixtureWhite(blue, alpha));
    }

    /**
     * 获取单色的混合值
     *
     * @param color
     * @param alpha
     * @return
     */
    private static int getSingleMixtureWhite(int color, int alpha) {
        int newColor = color * alpha / 255 + 255 - alpha;
        return newColor > 255 ? 255 : newColor;
    }

}
