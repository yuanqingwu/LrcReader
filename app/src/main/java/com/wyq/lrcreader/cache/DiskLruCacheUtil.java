package com.wyq.lrcreader.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.internal.DiskLruCache;
import com.wyq.lrcreader.model.Song;
import com.wyq.lrcreader.utils.BitmapUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static DiskLruCacheUtil diskLruCacheUtil;

    private DiskLruCacheUtil(Context context, String fileName) {
        init(context, fileName);
    }

    public static DiskLruCacheUtil getInstance(Context context, String fileName) {
        if (diskLruCacheUtil == null) {
            diskLruCacheUtil = new DiskLruCacheUtil(context, fileName);
        }
        return diskLruCacheUtil;
    }


    private void init(Context context, String fileName) {
        try {
            File cacheDir = getDiskCacheDir(context, fileName);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建缓存文件
     *
     * @param context  上下文对象
     * @param filePath 文件路径
     * @return 返回一个文件
     */
    private File getDiskCacheDir(Context context, String filePath) {
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

    //获取应用程序版本号
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
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


    public boolean addToDiskCache(Song song) {
        //如果当前线程是在主线程 则异常
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            throw new RuntimeException("can not visit network from UI Thread.");
//        }
        if (mDiskCache == null || song == null) {
            return false;
        }
        String key = hashKeyFormUrl(song.toString());
        //得到Editor对象
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                outputStream.write(song.toString().getBytes());
                //提交写入操作
                editor.commit();
                mDiskCache.flush();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public String getFromDiskCache(String songString) throws IOException {
        //如果当前线程是主线程 则异常
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            Log.w("DiskLruCache", "load bitmap from UI Thread, it's not recommended!");
//        }
        //如果缓存中为空  直接返回为空
        if (mDiskCache == null) {
            return null;
        }

        //通过key值在缓存中找到对应的Bitmap
        Bitmap bitmap = null;
        String key = hashKeyFormUrl(songString);
        //通过key得到Snapshot对象
        DiskLruCache.Snapshot snapShot = mDiskCache.get(key);
        if (snapShot != null) {
//            //得到文件输入流
//            FileInputStream fileInputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
//            //得到文件描述符
//            FileDescriptor fileDescriptor = fileInputStream.getFD();
//            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            return snapShot.getString(DISK_CACHE_INDEX);
        }
        return null;
    }

    public boolean removeFromDiskCache(Song song) {
        if (mDiskCache == null || song == null) {
            return false;
        }
        String key = hashKeyFormUrl(song.toString());
        try {
            return mDiskCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Returns the number of bytes currently being used to store the values in this cache. This may be greater than the max size if a background deletion is pending.
     *
     * @return
     */
    public long getSize() {
        return mDiskCache.size();
    }

    /**
     * delete all cache data
     *
     * @return
     */
    public boolean deleteAll() {
        try {
            mDiskCache.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void getAllCacheSong(final Handler handler, final int what) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = mDiskCache.getDirectory().listFiles();
                BufferedReader bufferedReader = null;
                if (files.length > 0) {
                    for (File file : files) {
                        if (file.getName().equals("journal")) {
                            continue;
                        }
                        try {
                            bufferedReader = new BufferedReader(new FileReader(file));
                            String line, readStr = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                readStr += (line+"\n");
                            }
                            Song song = new Song();
                            Pattern pattern = Pattern.compile("(?<=\\(%)([\\S\\s]+?)(?=%\\))");
                            Matcher matcher = pattern.matcher(readStr);
                            String[] str = new String[4];
                            for (int i = 0; i < 4; i++) {
                                if (matcher.find()) {
                                    str[i] = matcher.group();
                                }
                            }
                            song.setSongName(str[0]);
                            song.setArtist(str[1]);
                            song.setLrc(str[2]);
                            song.setAlbumCover(BitmapUtil.convertStringToIcon(str[3]));
                            handler.obtainMessage(what, song).sendToTarget();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }

            }
        }).start();
    }

}
