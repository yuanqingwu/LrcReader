package com.wyq.lrcreader.constants;

import android.os.Environment;

/**
 * Created by Uni.W on 2016/12/12.
 */

public class LocalConstans {

    private static String SDRoot = Environment.getExternalStorageDirectory().toString();
    /**
     * 网易云音乐本地下载目录歌词
     */
    public static final String NETEASE_CLOUDMUSIC_DOWNLOAD_LYRIC = SDRoot + "/netease/cloudmusic/Download/Lyric/";

}
