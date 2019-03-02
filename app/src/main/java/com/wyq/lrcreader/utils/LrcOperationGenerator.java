package com.wyq.lrcreader.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.item.ImageTextItemModel;
import com.wyq.lrcreader.ui.LikeGrade;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.res.ResourcesCompat;

import static com.wyq.lrcreader.ui.LikeGrade.LIKE_GRADE_MOST;
import static com.wyq.lrcreader.ui.LikeGrade.LIKE_GRADE_NOT;

/**
 * @author Uni.W
 * @date 2019/1/26 14:17
 */
public class LrcOperationGenerator {

    public static final String ACTION_LRC_MENU_SHARE_WECHAT = "ACTION_LRC_MENU_SHARE_WECHAT";
    public static final String ACTION_LRC_MENU_SHARE_MOMENTS = "ACTION_LRC_MENU_SHARE_MOMENTS";
    public static final String ACTION_LRC_MENU_SHARE_WEIBO = "ACTION_LRC_MENU_SHARE_WEIBO";
    public static final String ACTION_LRC_MENU_SHARE_NORMAL = "ACTION_LRC_MENU_SHARE_NORMAL";
    public static final String ACTION_LRC_MENU_DOWNLOAD = "ACTION_LRC_MENU_DOWNLOAD";
    public static final String ACTION_LRC_MENU_TEXT_RESIZE = "ACTION_LRC_MENU_TEXT_RESIZE";
    public static final String ACTION_LRC_MENU_LIKE = "ACTION_LRC_MENU_LIKE";
    public static final String ACTION_LRC_MENU_BACKGROUND_BLUR = "ACTION_LRC_MENU_BACKGROUND_BLUR";
    public static final String ACTION_LRC_MENU_CHOOSE_LRC_TEXT = "ACTION_LRC_MENU_CHOOSE_LRC_TEXT";
    public static final String ACTION_LRC_MENU_GEN_BITMAP = "ACTION_LRC_MENU_GEN_BITMAP";
    public static final String ACTION_LRC_MENU_GEN_VIDEO = "ACTION_LRC_MENU_GEN_VIDEO";

    private static LrcOperationGenerator generator;
    private Context context;
    private List<ImageTextItemModel> itemList;

    private LrcOperationGenerator(Context context) {
        this.context = context.getApplicationContext();
    }

    public static LrcOperationGenerator getInstance(Context context) {
        if (generator == null) {
            synchronized (LrcOperationGenerator.class) {
                if (generator == null) {
                    generator = new LrcOperationGenerator(context);
                }
            }
        }
        return generator;
    }


    public LrcOperationGenerator invalidate() {
        itemList = null;
        return this;
    }

    public List<ImageTextItemModel> genMenuList(int likeGrade) {
        if (itemList != null) {
            return itemList;
        }
        LikeGrade grade = getLikeGradeWithValue(likeGrade);
        itemList = new ArrayList<>();
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.wechat), "微信", ACTION_LRC_MENU_SHARE_WECHAT));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.moments), "朋友圈", ACTION_LRC_MENU_SHARE_MOMENTS));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.weibo), "微博", ACTION_LRC_MENU_SHARE_WEIBO));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_share_black_24dp), "分享", ACTION_LRC_MENU_SHARE_NORMAL));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_file_download_black_24dp), "下载", ACTION_LRC_MENU_DOWNLOAD));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_format_size_black_24dp), "调整字体", ACTION_LRC_MENU_TEXT_RESIZE));
        itemList.add(new ImageTextItemModel(getDrawable(grade.getDrawableId()), grade.getName(), ACTION_LRC_MENU_LIKE));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_blur_on_black_24dp), "背景模糊", ACTION_LRC_MENU_BACKGROUND_BLUR));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_content_copy_black_24dp), "选择歌词", ACTION_LRC_MENU_CHOOSE_LRC_TEXT));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_photo_library_black_24dp), "生成图片", ACTION_LRC_MENU_GEN_BITMAP));
        itemList.add(new ImageTextItemModel(getDrawable(R.drawable.ic_music_video_black_24dp), "生成短视频", ACTION_LRC_MENU_GEN_VIDEO));
        return itemList;
    }

    private Drawable getDrawable(int id) {
        return ResourcesCompat.getDrawable(context.getResources(), id, null);
    }

    public ImageTextItemModel addLikeGrade() {
        ImageTextItemModel model = itemList.get(6);

        LikeGrade grade = next(getLikeGradeWithName(model.getName()));

        model.setName(grade.getName());
        model.setImage(getDrawable(grade.getDrawableId()));
        return model;
    }

    public LikeGrade getLikeGradeWithValue(int value) {
        for (LikeGrade grade : LikeGrade.values()) {
            if (grade.getValue() == value) {
                return grade;
            }
        }
        return null;
    }

    public LikeGrade getLikeGradeWithName(String name) {
        for (LikeGrade grade : LikeGrade.values()) {
            if (grade.getName().equals(name)) {
                return grade;
            }
        }
        return null;
    }

    public LikeGrade next(LikeGrade likeGrade) {
        if (likeGrade.equals(LIKE_GRADE_MOST)) {
            return LIKE_GRADE_NOT;
        }
        return getLikeGradeWithValue(likeGrade.getValue() + 1);
    }
}
