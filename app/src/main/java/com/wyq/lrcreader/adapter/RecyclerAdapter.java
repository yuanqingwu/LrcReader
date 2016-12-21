package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.model.LrcInfo;
import com.wyq.lrcreader.model.Song;
import com.wyq.lrcreader.utils.LrcParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

        //解析出歌名，歌手，专辑
        LrcInfo lrcInfo = null;
        try {
            lrcInfo = new LrcParser().parser(new ByteArrayInputStream(list.get(position).getLrc().getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lrcStr = "", title = "";
        if (lrcInfo == null || lrcInfo.getInfos().size() == 0) {
            //解析失败
            title = list.get(position).getSongName();
            lrcStr="演唱："+list.get(position).getArtist()+"      "+"专辑：未知";
        } else {
            if (lrcInfo.getArtist() != null && lrcInfo.getArtist().length() > 0) {
                lrcStr = lrcStr + "演唱：" + lrcInfo.getArtist();
            }else{
                lrcStr = lrcStr + "演唱：未知";
            }
            if (lrcInfo.getAlbum() != null && lrcInfo.getAlbum().length() > 0) {
                lrcStr += ("    专辑：" + lrcInfo.getAlbum());
            }else{
                lrcStr += ("    专辑：未知");
            }
            if (lrcInfo.getTitle() != null && lrcInfo.getTitle().length() > 0) {
                title = lrcInfo.getTitle();
            }else{
                title="未知";
            }
        }
        holder.lrcText.setText(lrcStr);
        holder.nameText.setText(title);
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
