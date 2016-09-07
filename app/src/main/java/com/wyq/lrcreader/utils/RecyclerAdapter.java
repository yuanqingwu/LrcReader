package com.wyq.lrcreader.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.model.Song;

import java.util.List;

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
    private List<Song> list;

    public RecyclerAdapter(Context context, List<Song> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnItemLongClickListener(OnRecyclerItemLongClickListener onRecyclerItemLongClickListener) {
        this.onRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_lrclist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.headImage.setImageBitmap(list.get(position).getAlbumCover());
        holder.nameText.setText(list.get(position).getArtist());
        holder.lrcText.setText(list.get(position).getLrc());
        //   holder.lrcText.setSelected(true);

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
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView headImage;
        TextView nameText;
        TextView lrcText;

        public MyViewHolder(View view) {
            super(view);
            headImage = (ImageView) view.findViewById(R.id.fragment_lrclist_item_head);
            nameText = (TextView) view.findViewById(R.id.fragment_lrclist_item_name);
            lrcText = (TextView) view.findViewById(R.id.fragment_lrclist_item_lrc);
        }
    }
}
