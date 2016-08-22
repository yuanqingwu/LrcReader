package com.wyq.lrcreader.constants;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class UrlConstant {
    /**
     * 实例：
     * 根据歌曲名获取歌词：'http://geci.me/api/lyric/海阔天空'
     * 根据歌曲名与歌手名获取歌词：'http://geci.me/api/lyric/海阔天空/Beyond'
     */

    public static final String NAME_LRC_URL_ROOT = "http://geci.me/api/lyric/";

    /**
     * 根据歌手编号获取歌手信息（暂时只有歌手名字）:'http://geci.me/api/artist/9208'
     */
    public static final String ARTIST_URL_ROOT = "http://geci.me/api/artist/";

    /**
     * 根据歌曲编号获取歌词URL：'http://geci.me/api/lrc/3861244'
     */
    public static final String SID_LRC_URL_ROOT = "http://geci.me/api/lrc/";

    /**
     * 根据专辑编号获取专辑封面URL：'http://geci.me/api/cover/1573814'
     */
    public static final String AID_ALBUM_COVER_URL_ROOT = "http://geci.me/api/cover/";
}
