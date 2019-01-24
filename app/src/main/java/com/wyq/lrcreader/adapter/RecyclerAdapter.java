package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private OnRecyclerItemLongClickListener onRecyclerItemLongClickListener;

    private Context context;
    private List<SearchResultEntity> list;

    public RecyclerAdapter(Context context, List<SearchResultEntity> list) {
        this.context = context;
        this.list = list;
    }

    public List<SearchResultEntity> getDataList() {
        return list;
    }

    public void refreshData(List<SearchResultEntity> newList) {
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

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener onRecyclerItemLongClickListener) {
        this.onRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_lrclist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (list == null) {
            return;
        }

        holder.nameText.setText(list.get(position).getSongName());
        holder.singerText.setText(list.get(position).getArtist());
//        holder.albumText.setText(list.get(position).getAlbum());
        //   holder.lrcText.setSelected(true);
        LogUtil.i(list.get(position).getAlbumCoverUri());
        String uri = list.get(position).getAlbumCoverUri().replace("/cover/", "/album-cover/");
        GlideApp.with(context).load(uri).placeholder(R.drawable.album).into(holder.headImage);

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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView headImage;
        TextView nameText;
        TextView singerText;
        TextView albumText;

        public MyViewHolder(View view) {
            super(view);
            headImage = view.findViewById(R.id.fragment_lrclist_item_head);
            nameText = view.findViewById(R.id.fragment_lrclist_item_name);
            singerText = view.findViewById(R.id.fragment_lrclist_item_singer);
            albumText = view.findViewById(R.id.fragment_lrclist_item_album);
        }
    }
}
