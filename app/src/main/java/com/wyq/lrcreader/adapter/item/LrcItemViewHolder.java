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

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Uni.W
 * @date 2019/1/28 19:15
 */
public class LrcItemViewHolder extends RecyclerView.ViewHolder {
    private static GlideRequests glide;
    ImageView headImage;
    TextView nameText;
    TextView singerText;
    TextView albumText;
//    private SearchResultEntity resultEntity;

    public LrcItemViewHolder(View view, GlideRequests glideRequests) {
        super(view);
        headImage = view.findViewById(R.id.fragment_lrclist_item_head);
        nameText = view.findViewById(R.id.fragment_lrclist_item_name);
        singerText = view.findViewById(R.id.fragment_lrclist_item_singer);
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
//        resultEntity = entity;
        nameText.setText(entity.getSongName());
        singerText.setText(entity.getArtist());
        //FIXME 暂时修复api的异常
        String uri = entity.getAlbumCoverUri().replace("/cover/", "/album-cover/");
        glide.load(uri)
                .centerCrop()
                .placeholder(R.drawable.album)
                .into(headImage);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LrcActivity.newInstance(itemView.getContext(), entity);
            }
        });
    }

    /**
     * 适用于更新局部数据，比如只更新比分数据
     *
     * @param entity
     */
    public void updateLrc(SearchResultEntity entity) {
//        resultEntity = entity;
        nameText.setText(entity.getSongName());
    }
}
