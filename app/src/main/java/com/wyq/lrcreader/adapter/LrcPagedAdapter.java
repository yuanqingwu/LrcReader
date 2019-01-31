package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.item.LrcItemViewHolder;
import com.wyq.lrcreader.adapter.item.NetworkStatusItemViewHolder;
import com.wyq.lrcreader.base.GlideRequests;
import com.wyq.lrcreader.base.NetworkState;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Uni.W
 * @date 2019/1/26 20:40
 */
public class LrcPagedAdapter extends PagedListAdapter<SearchResultEntity, RecyclerView.ViewHolder> {

    public static final DiffUtil.ItemCallback<SearchResultEntity> DIFF_ITEM_CALLBACK = new DiffUtil.ItemCallback<SearchResultEntity>() {

//        private Object PAYLOAD_SOURCE;

        @Override
        public boolean areItemsTheSame(@NonNull SearchResultEntity oldItem, @NonNull SearchResultEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchResultEntity oldItem, @NonNull SearchResultEntity newItem) {
            return oldItem.equals(newItem);
        }

//        @Nullable
//        @Override
//        public Object getChangePayload(@NonNull SearchResultEntity oldItem, @NonNull SearchResultEntity newItem) {
//            if (oldItem != null && newItem != null && sameExceptSource(oldItem, newItem)) {
//                return PAYLOAD_SOURCE;
//            }
//            return null;
//        }
//
//        public boolean sameExceptSource(@NonNull SearchResultEntity oldItem, @NonNull SearchResultEntity newItem) {
//            if (oldItem.getLrcUri().equals(newItem.getLrcUri()) && oldItem.getArtist().equals(newItem.getArtist())) {
//                if (!oldItem.getDataSource().equals(newItem.getDataSource())) {
//                    return true;
//                }
//            }
//            return false;
//        }
    };
    private Context context;
    private NetworkState networkState;
    private GlideRequests glide;
//
//    protected LrcPagedAdapter(@NonNull AsyncDifferConfig<SearchResultEntity> config) {
//        super(config);
//    }

    public LrcPagedAdapter(GlideRequests glide) {
        super(DIFF_ITEM_CALLBACK);
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.fragment_lrclist_item) {
            return LrcItemViewHolder.create(parent, glide);
        } else if (viewType == R.layout.network_state_item) {
            return NetworkStatusItemViewHolder.create(parent);
        } else {
            throw new IllegalArgumentException("unknown view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == R.layout.fragment_lrclist_item) {
            LogUtil.i("getItem(position):" + getItem(position).getSongName());
            ((LrcItemViewHolder) holder).bind(getItem(position));
        } else if (getItemViewType(position) == R.layout.network_state_item) {
            if (networkState != null) {
                ((NetworkStatusItemViewHolder) holder).bindTo(networkState);
            } else {
                LogUtil.i("networkState is null");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (!payloads.isEmpty()) {
            SearchResultEntity entity = getItem(position);
            ((LrcItemViewHolder) holder).updateLrc(entity);
        } else {
            onBindViewHolder(holder, position);
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        //todo
        return R.layout.fragment_lrclist_item;
//        if (hasExtraRow() && position == getItemCount() - 1) {
//            return R.layout.fragment_lrclist_item;
//        } else {
//            return R.layout.network_state_item;
//        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean hadExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean hasExtraRow = hadExtraRow;
        if (hadExtraRow != hasExtraRow()) {
            if (hadExtraRow != hasExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

}
