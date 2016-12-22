package com.wyq.lrcreader.utils;

import android.util.Log;

import com.wyq.lrcreader.model.LrcInfo;
import com.wyq.lrcreader.model.Song;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uni.W on 2016/8/23.
 * <p>
 * 1.分行的歌词，每句有可能多个时间戳； 2.连续的歌词，每句一个时间戳；
 */
public class LrcParser {

    private LrcInfo lrcinfo = new LrcInfo();
    //存放临时时间
    private long currentTime = 0;
    //存放临时歌词
    private String currentContent = null;
    //用于保存时间点和歌词之间的对应关系
    //private Map<Long,String> maps =new HashMap<Long,String>();
    private Map<Long, String> maps = new TreeMap<Long, String>();

    /*
     * 根据文件路径，读取文件，返回一个输入流
     * @param   path    文件路径
     * @return InputStream 文件输入流
     * @throws FileNotFoundException
     * */
    private InputStream readLrcFile(String path) throws FileNotFoundException {
        File f = new File(path);
        InputStream ins = new FileInputStream(f);
        return ins;
    }

    public LrcInfo parser(String path) throws Exception {
        InputStream in = readLrcFile(path);
        lrcinfo = parser(in);
        return lrcinfo;
    }

    /**
     * @param inputStream 输入流
     * @return
     */
    public LrcInfo parser(InputStream inputStream) throws IOException {
        //包装对象
        InputStreamReader inr = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inr);
        //一行一行的读，每读一行解析一行
        String line = null;
        while ((line = reader.readLine()) != null) {
            parserLine(line);
        }
        //全部解析完后，设置info
        lrcinfo.setInfos(maps);
        Iterator<Map.Entry<Long, String>> iter = maps.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, String> entry = (Map.Entry<Long, String>) iter.next();
            Long key = entry.getKey();
            String val = entry.getValue();
            //        Log.e("---", "key="+key+"   val="+val);
        }
        return lrcinfo;
    }


    /**
     * 目前只测试过匹配网易云音乐的本地歌词
     *
     * @param songId
     * @param lrcText
     * @return
     */
    public Song parserAll(String songId, String lrcText) {
        Song song = new Song();

        //匹配歌名
        Pattern pattern1 = Pattern.compile("(?<=\\[ti:).*?(?=\\])");
        Matcher matcher1 = pattern1.matcher(lrcText);
        if (matcher1.find()) {
            song.setSongName(matcher1.group());
        } else {
            song.setSongName(songId);
        }

        //匹配歌手
        Pattern pattern2 = Pattern.compile("(?<=\\[ar:).*?(?=\\])");
        Matcher matcher2 = pattern2.matcher(lrcText);
        if (matcher2.find()) {
            song.setArtist(matcher2.group());
        }

        //匹配专辑
        Pattern pattern3 = Pattern.compile("(?<=\\[al:).*?(?=\\])");
        Matcher matcher3 = pattern3.matcher(lrcText);
        if (matcher3.find()) {
            song.setAlbum(matcher3.group());
        }

        String lrc = "";
        String reg = "(?<=\\])(?![\\[\\(\\\\n]).*?(?=[\\n\\\\])";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(lrcText);
        while (matcher.find()) {
            String line = matcher.group();
            lrc += (line + "\n\n");
        }
        song.setLrc(lrc);

        return song;
    }

    /**
     * 利用正则表达式解析每行具体语句
     * 并将解析完的信息保存到LrcInfo对象中
     *
     * @param line
     */
    private void parserLine(String line) {

        Pattern pattern1 = Pattern.compile("(?<=\\[ti:)([\\S\\s]+?)(?=\\])");
        Matcher matcher1 = pattern1.matcher(line);
        if (matcher1.find()) {
            lrcinfo.setTitle(matcher1.group().trim());
        }

        Pattern pattern2 = Pattern.compile("(?<=\\[ar:)([\\S\\s]+?)(?=\\])");
        Matcher matcher2 = pattern2.matcher(line);
        if (matcher2.find()) {
            lrcinfo.setArtist(matcher2.group().trim());
        }

        Pattern pattern3 = Pattern.compile("(?<=\\[al:)([\\S\\s]+?)(?=\\])");
        Matcher matcher3 = pattern3.matcher(line);
        if (matcher3.find()) {
            lrcinfo.setAlbum(matcher3.group().trim());
        }

        Pattern pattern4 = Pattern.compile("(?<=\\[by:)([\\S\\s]+?)(?=\\])");
        Matcher matcher4 = pattern4.matcher(line);
        if (matcher4.find()) {
            lrcinfo.setBySomeBody(matcher4.group().trim());
        }

        //通过正则表达式取得每句歌词信息
        // else {
        //设置正则表达式
        String reg = "\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,2})\\]|\\[(\\d{1,2}:\\d{1,2})\\]|\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,3})\\]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(line);
        //如果存在匹配项则执行如下操作
        while (matcher.find()) {
            //得到匹配的内容
            String msg = matcher.group();
            //LogUtil.i(msg);
            //得到这个匹配项开始的索引
            int start = matcher.start();
            //得到这个匹配项结束的索引
            int end = matcher.end();
            //得到这个匹配项中的数组
            int groupCount = matcher.groupCount();
            for (int index = 0; index < groupCount; index++) {
                String timeStr = matcher.group(index);
                //Log.i("","time["+index+"]="+timeStr);
                if (index == 0) {
                    //将第二组中的内容设置为当前的一个时间点
                    currentTime = str2Long(timeStr.substring(1, timeStr.length() - 1));
                }
            }
            //得到时间点后的内容
            //   Log.i("Test","line:"+line);
            String[] content = pattern.split(line);
//                for(int index =0; index<content.length; index++) {
//                    Log.i("", "content=" + content[content.length - 1]);
//                }
            //if(index==content.length-1){
            //将内容设置为当前内容
            if (content.length >= 1) {
                currentContent = content[content.length - 1].trim();
            } else {
                currentContent = " ";
            }
            // }
            // }
            //设置时间点和内容的映射
            maps.put(currentTime, currentContent);
            //           Log.i("","currentTime--->"+currentTime+"   currentContent--->"+currentContent);
            //遍历map
        }
        //}
    }

    private long str2Long(String timeStr) {
        //将时间格式为xx:xx.xx，返回的long要求以毫秒为单位
        //    Log.i("","timeStr="+timeStr);
        String[] s = timeStr.split("\\:");
        int min = Integer.parseInt(s[0]);
        int sec = 0;
        int mill = 0;
        if (s[1].contains(".")) {
            String[] ss = s[1].split("\\.");
            sec = Integer.parseInt(ss[0]);
            mill = Integer.parseInt(ss[1]);
            //        Log.i("","s[0]="+s[0]+"s[1]"+s[1]+"ss[0]="+ss[0]+"ss[1]="+ss[1]);
        } else {
            sec = Integer.parseInt(s[1]);
            //       Log.i("","s[0]="+s[0]+"s[1]"+s[1]);
        }
        return min * 60 * 1000 + sec * 1000 + mill * 10;
    }
}
