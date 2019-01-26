package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.item.ImageTextItemModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerGridAdapter extends BaseRecyclerViewAdapter<RecyclerGridAdapter.GridViewHolder> {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private List<ImageTextItemModel> list = new ArrayList<>();
    private Context context;
    private int orientation = VERTICAL;


    public RecyclerGridAdapter(Context context, List<ImageTextItemModel> oriList) {
        this.context = context;
        list = oriList;
    }

    public void refreshData(List oriList) {
        if (list == null) {
            list = oriList;
            notifyDataSetChanged();
        } else {

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

            list = oriList;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = orientation == VERTICAL ? R.layout.recyclerview_item_tv_img_layout_v : R.layout.recyclerview_item_tv_img_layout_h;
        GridViewHolder holder = new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, final int position) {

        holder.textView.setText(list.get(position).getName());
        holder.imageView.setImageDrawable(list.get(position).getImage());
//        GlideApp.with(context).load(list.get(position).getImage()).into(holder.imageView);

        if (onRecyclerItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerItemClickListener.onItemClick(v, position);
                }
            });
        }

        if (onRecyclerItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecyclerItemLongClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

//    private Bitmap getBitmapByName(String name) {
////        Logger.i(name);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
//        return BitmapFactory.decodeResource(context.getResources(), resID);
//    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public GridViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerview_item_tv_img_text);
            imageView = itemView.findViewById(R.id.recyclerview_item_tv_img_img);
        }
    }

}
