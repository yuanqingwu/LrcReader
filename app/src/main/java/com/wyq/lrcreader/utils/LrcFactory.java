package com.wyq.lrcreader.utils;

import com.wyq.lrcreader.model.netmodel.gecimemodel.LrcInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Uni.W
 * @date 2019/1/18 20:39
 */
public class LrcFactory {

    private static LrcFactory lrcFactory;

    private LrcFactory() {

    }

    public static LrcFactory getInstance() {
        if (lrcFactory == null) {
            synchronized (LrcFactory.class) {
                if (lrcFactory == null) {
                    lrcFactory = new LrcFactory();
                }
            }
        }
        return lrcFactory;
    }


    public String reFormat(String lrcText) {
        LrcInfo lrcInfo = null;
        try {
            lrcInfo = LrcParser.getInstance().parser(new ByteArrayInputStream(lrcText.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lrcInfo == null || lrcInfo.getInfos().size() == 0) {
            lrcText.replace("\r\n", "\n\n");
            return lrcText;
        }

        StringBuilder lrcStr = new StringBuilder();
        if (lrcInfo.getTitle() != null && lrcInfo.getTitle().length() > 0) {
            lrcStr.append(lrcInfo.getTitle());
        }
        if (lrcInfo.getArtist() != null && lrcInfo.getArtist().length() > 0) {
            lrcStr.append("\n\n演唱：").append(lrcInfo.getArtist());
        }
        if (lrcInfo.getAlbum() != null && lrcInfo.getAlbum().length() > 0) {
            lrcStr.append("    专辑：").append(lrcInfo.getAlbum());
        }
        for (String s : lrcInfo.getInfos().values()) {
            lrcStr.append("\n\n").append(s);
        }

        return lrcStr.toString();
    }
}
