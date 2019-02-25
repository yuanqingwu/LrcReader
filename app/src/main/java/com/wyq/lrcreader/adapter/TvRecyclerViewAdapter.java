package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyq.lrcreader.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class TvRecyclerViewAdapter extends RecyclerView.Adapter<TvRecyclerViewAdapter.TvViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<String> list = new ArrayList<>();
    private Context context;
    private int selectPosition = -1;
    private int selectedTextColor = -1;
    private int padding;
    private int gravity = Gravity.CENTER;

    public TvRecyclerViewAdapter(List<String> oriList) {
        list.clear();
        if (oriList != null) {
            list.addAll(oriList);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void refreshData(List<String> oriList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return list.size();
            }

            @Override
            public int getNewListSize() {
                return oriList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return list.get(oldItemPosition).getClass().equals(oriList.get(newItemPosition).getClass());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return list.get(oldItemPosition).equals(oriList.get(newItemPosition));
            }
        }, true);
        list.clear();
        list.addAll(oriList);
        diffResult.dispatchUpdatesTo(this);
//        notifyDataSetChanged();
    }

    public void setSelectPosition(int position, @ColorInt int... selectedTextColor) {
        this.selectPosition = position;
        if (selectedTextColor != null && selectedTextColor.length == 1) {
            this.selectedTextColor = selectedTextColor[0];
        }
        notifyDataSetChanged();
    }

    public void setTvPadding(int padding) {
        this.padding = padding;
    }

    public void setTvGravity(int gravity) {
        this.gravity = gravity;
    }

    @NonNull
    @Override
    public TvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_tv_layout, parent, false);
        TvViewHolder holder = new TvViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TvViewHolder holder, final int position) {
        holder.textView.setText(list.get(position));
        holder.textView.setGravity(gravity);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.textView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class TvViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public TvViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerview_item_tv);
        }
    }

}
