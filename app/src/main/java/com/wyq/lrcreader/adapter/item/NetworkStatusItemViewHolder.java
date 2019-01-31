package com.wyq.lrcreader.adapter.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.base.NetworkState;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Uni.W
 * @date 2019/1/27 16:42
 */
public class NetworkStatusItemViewHolder extends RecyclerView.ViewHolder {
    private ProgressBar progressBar;
    private TextView textView;
    private Button button;

    public NetworkStatusItemViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.network_state_progress_bar);
        textView = itemView.findViewById(R.id.network_state_tv);
        button = itemView.findViewById(R.id.network_state_retry_bt);
    }

    public static NetworkStatusItemViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_state_item, parent, false);
        return new NetworkStatusItemViewHolder(view);
    }

    public void bindTo(NetworkState networkState) {
        progressBar.setVisibility(networkState.status == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
        textView.setVisibility(networkState.msg != null ? View.VISIBLE : View.GONE);
        button.setVisibility(networkState.status == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
    }
}
