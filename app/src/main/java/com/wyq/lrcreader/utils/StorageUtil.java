package com.wyq.lrcreader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Uni.W
 * @date 2019/1/31 15:26
 */
public class StorageUtil {

    private static final String STORAGE_CACHE_FOLDER_SONG = "SONG";
    private static final String STORAGE_CACHE_FOLDER_ALBUM_COVER = "albumCover";
    private static volatile StorageUtil storageUtil;
    private String songAlbumCoverFolder;
    private Context context;

    private StorageUtil(Context context) {
        this.context = context.getApplicationContext();
        init();
    }

    public static StorageUtil getInstance(Context context) {
        if (storageUtil == null) {
            synchronized (StorageUtil.class) {
                if (storageUtil == null) {
                    storageUtil = new StorageUtil(context);
                }
            }
        }
        return storageUtil;
    }

    private void init() {
        if (externalStorageAvailable()) {
            songAlbumCoverFolder = context.getExternalCacheDir().getAbsolutePath() +
                    File.separator +
                    STORAGE_CACHE_FOLDER_ALBUM_COVER +
                    File.separator;
        } else {
            songAlbumCoverFolder = context.getCacheDir().getAbsolutePath() +
                    File.separator +
                    STORAGE_CACHE_FOLDER_ALBUM_COVER +
                    File.separator;
        }

        File albumCoverFoler = new File(songAlbumCoverFolder);
        if (!albumCoverFoler.exists()) {
            if (!albumCoverFoler.mkdir()) {
                LogUtil.i("专辑封面缓存文件夹创建失败");
            }
        }

    }

    public boolean externalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean saveToDiskCache() {

        return false;
    }

    public String saveImageToCacheFile(Bitmap bitmap) {
        byte[] bitmapBytes = BitmapUtil.bmpToByteArray(bitmap);
        String bitmapMD5 = EncryptUtils.encryptMD5ToString(bitmapBytes);
        String filePath = songAlbumCoverFolder + bitmapMD5;
        LogUtil.i("filePath:" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    LogUtil.i("专辑封面缓存文件创建失败");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream ops = null;
        try {
            ops = new BufferedOutputStream(new FileOutputStream(file));
            boolean res = bitmap.compress(Bitmap.CompressFormat.PNG, 100, ops);
            if (res) {
                return filePath;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(ops);
        }
        return null;
    }

    public Bitmap getImageFromCacheFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.canRead()) {
            byte[] res = FileUtils.read(file);

            if (res != null && res.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(res, 0, res.length);
                return bitmap;
            }
        }

        return null;
    }

}
