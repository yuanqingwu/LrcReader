package com.wyq.lrcreader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.squareup.okhttp.internal.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Uni.W on 2016/8/24.
 */
public class DiskLruCacheUtil {

    private DiskLruCache mDiskCache;
    //指定磁盘缓存大小
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10;//10MB
    //IO缓存流大小
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    //缓存个数
    private static final int DISK_CACHE_INDEX = 0;

    /**
     * 创建缓存文件
     *
     * @param context  上下文对象
     * @param filePath 文件路径
     * @return 返回一个文件
     */
    public File getDiskCacheDir(Context context, String filePath) {
        boolean externalStorageAvailable = Environment
                .getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + filePath);
    }

    /**
     * 得到当前可用的空间大小
     *
     * @param path 文件的路径
     * @return
     */
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * 将URL转换成key
     *
     * @param url 图片的URL
     * @return
     */
    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    /**
     * 将Url的字节数组转换成哈希字符串
     *
     * @param bytes URL的字节数组
     * @return
     */
    private String bytesToHexString(byte[] bytes) {
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
     * 将Bitmap写入缓存
     *
     * @param url
     * @return
     * @throws IOException
     */
    private Bitmap addBitmapToDiskCache(String url) throws IOException {
        //如果当前线程是在主线程 则异常
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI Thread.");
        }
        if (mDiskCache == null) {
            return null;
        }

        //设置key，并根据URL保存输出流的返回值决定是否提交至缓存
        String key = hashKeyFormUrl(url);
        //得到Editor对象
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url, outputStream)) {
                //提交写入操作
                editor.commit();
            } else {
                //撤销写入操作
                editor.abort();
            }
            mDiskCache.flush();
        }
        return getBitmapFromDiskCache(url);
    }


    /**
     * 从缓存中取出Bitmap
     *
     * @param url 图片的URL
     * @return 返回Bitmap对象
     * @throws IOException
     */
    private Bitmap getBitmapFromDiskCache(String url) throws IOException {
        //如果当前线程是主线程 则异常
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w("DiskLruCache", "load bitmap from UI Thread, it's not recommended!");
        }
        //如果缓存中为空  直接返回为空
        if (mDiskCache == null) {
            return null;
        }

        //通过key值在缓存中找到对应的Bitmap
        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        //通过key得到Snapshot对象
        DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
        if (snapShot != null) {
            //得到文件输入流
            FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
            //得到文件描述符
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        }
        return bitmap;
    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
