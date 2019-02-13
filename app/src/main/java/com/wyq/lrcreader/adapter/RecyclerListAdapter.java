package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.model.IListSong;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class RecyclerListAdapter extends BaseRecyclerViewAdapter<RecyclerListAdapter.SongListViewHolder> {
    private Context context;
    private List<? extends IListSong> list;

    public RecyclerListAdapter(Context context, List<? extends IListSong> list) {
        this.context = context;
        this.list = list;
    }

    public List<? extends IListSong> getDataList() {
        return list;
    }

    public void refreshData(List<? extends IListSong> newList) {
        if (list == null) {
            list = newList;
            notifyItemRangeChanged(0, newList == null ? 0 : newList.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return list == null ? 0 : list.size();
                }

                @Override
                public int getNewListSize() {
                    return newList == null ? 0 : newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return list.get(oldItemPosition).getClass().equals(newList.get(newItemPosition).getClass());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return list.get(oldItemPosition).equals(newList.get(newItemPosition));
                }
            }, true);
            this.list = newList;
            diffResult.dispatchUpdatesTo(this);
        }

    }

    @Override
    public SongListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_lrclist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SongListViewHolder holder, int position) {
        if (list == null) {
            return;
        }

        holder.nameText.setText(list.get(position).getSongName());
        holder.singerText.setText(list.get(position).getArtist());
//        holder.albumText.setText(list.get(position).getAlbum());
        //   holder.lrcText.setSelected(true);

        String coverUri = list.get(position).getAlbumCoverUri();
        LogUtil.i("coverUri:" + coverUri);
        //fixme 如果是网址则加载，如果是本地图片则直接显示
        if (coverUri != null) {
            if (coverUri.startsWith("http")) {
                coverUri = list.get(position).getAlbumCoverUri().replace("/cover/", "/album-cover/");
            }
            GlideApp.with(context).load(coverUri).placeholder(R.drawable.album).into(holder.headImage);
        }

        if (onRecyclerItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

        if (onRecyclerItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecyclerItemLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {
        ImageView headImage;
        TextView nameText;
        TextView singerText;
        TextView albumText;

        public SongListViewHolder(View view) {
            super(view);
            headImage = view.findViewById(R.id.fragment_lrclist_item_head);
            nameText = view.findViewById(R.id.fragment_lrclist_item_name);
            singerText = view.findViewById(R.id.fragment_lrclist_item_singer);
            albumText = view.findViewById(R.id.fragment_lrclist_item_album);
        }
    }
}
