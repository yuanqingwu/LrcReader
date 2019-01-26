package com.wyq.lrcreader.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Uni.W
 * @date 2019/1/26 13:50
 */
public abstract class BaseRecyclerViewAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    protected OnRecyclerItemClickListener onRecyclerItemClickListener;
    protected OnRecyclerItemLongClickListener onRecyclerItemLongClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener onRecyclerItemLongClickListener) {
        this.onRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }


    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecyclerItemLongClickListener {
        void onItemLongClick(View view, int position);
    }


}
