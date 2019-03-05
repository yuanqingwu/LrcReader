package com.wyq.lrcreader.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.base.GlideRequests;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.ui.activity.LrcActivity;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Uni.W
 * @date 2019/1/28 19:15
 */
public class LrcItemViewHolder extends RecyclerView.ViewHolder {
    private static GlideRequests glide;
    private ImageView headImage;
    private TextView nameText;
    private TextView artistText;
    private TextView albumText;

    public LrcItemViewHolder(View view, GlideRequests glideRequests) {
        super(view);
        headImage = view.findViewById(R.id.fragment_lrclist_item_head);
        nameText = view.findViewById(R.id.fragment_lrclist_item_name);
        artistText = view.findViewById(R.id.fragment_lrclist_item_singer);
        albumText = view.findViewById(R.id.fragment_lrclist_item_album);
        glide = glideRequests;
    }

    public static LrcItemViewHolder create(ViewGroup parent, GlideRequests glide) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_lrclist_item, parent, false);
        return new LrcItemViewHolder(view, glide);
    }

    public void bind(SearchResultEntity entity) {
        if (entity == null) {
            return;
        }
        nameText.setText(entity.getSongName());
        artistText.setText(entity.getArtist());
        loadImage(headImage, entity.getAlbumCoverUri());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LrcActivity.newInstance(itemView.getContext(), entity);
            }
        });
    }

    public void pauseRequests() {
        if (glide != null) {
            glide.pauseRequests();
        }
    }

    private void loadImage(ImageView imageView, String uri) {
        if (uri != null && uri.length() > 0) {
//            //防止复用之后图片错乱
//            if (imageView.getTag() != null && !imageView.getTag().equals(uri)) {
//                glide.clear(imageView);
//            }

            //FIXME 暂时修复api的异常
            String imageUri = uri.replace("/cover/", "/album-cover/");
            LogUtil.i("imageUri:" + imageUri);
            glide.load(imageUri)
                    .centerCrop()
                    .placeholder(R.drawable.album)
                    .into(headImage);

        }
    }

    /**
     * 适用于更新局部数据，比如只更新比分数据
     *
     * @param entity
     */
//    public void updateLrc(SearchResultEntity entity) {
////        resultEntity = entity;
//        nameText.setText(entity.getSongName());
//    }
    public void updateArtist(SearchResultEntity entity) {
        artistText.setText(entity.getArtist());
    }

    public void updateAlbumCover(SearchResultEntity entity) {
        loadImage(headImage, entity.getAlbumCoverUri());
    }


}
