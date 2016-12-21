package com.wyq.lrcreader.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.activity.LrcActivity;
import com.wyq.lrcreader.cache.DiskLruCacheUtil;
import com.wyq.lrcreader.model.Song;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uni.W on 2016/8/31.
 */
public class LrcLikeFragment extends Fragment implements RecyclerAdapter.OnRecyclerItemLongClickListener, RecyclerAdapter.OnRecyclerItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<Song> songList;

    private long cacheSize = 0;

    private static final int SHOW_LIKE_LRC = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LIKE_LRC:
                    Song song = (Song) msg.obj;
                    songList.add(song);
                    //  songList=(List<Song>) msg.obj;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lrclist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_lrclist_recyclerview);
        //       Log.i("Test", "oncreateView");
        songList = new ArrayList<>();
        adapter = new RecyclerAdapter(getActivity(), songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(LrcLikeFragment.this);
        adapter.setOnItemClickListener(LrcLikeFragment.this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DiskLruCacheUtil diskLruCacheUtil = DiskLruCacheUtil.getInstance(getActivity(), "song");
        if (cacheSize == 0 || cacheSize != diskLruCacheUtil.getSize()) {
            if (songList != null)
                songList.clear();
            diskLruCacheUtil.getAllCacheSong(handler, SHOW_LIKE_LRC);
            cacheSize = diskLruCacheUtil.getSize();
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            songList = savedInstanceState.getParcelableArrayList("songList");
            if (songList != null && songList.size() > 0) {
                Toast.makeText(getActivity(), "恢复", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (songList != null && songList.size() > 0) {
            outState.putParcelableArrayList("songList", (ArrayList<? extends Parcelable>) songList);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("songName", songList.get(position).getSongName().toString());
        bundle.putString("artist", songList.get(position).getArtist().toString());
        bundle.putString("lrcText", songList.get(position).getLrc().toString());
        bundle.putString("albumCover", BitmapUtil.convertIconToString(songList.get(position).getAlbumCover()));
        bundle.putBoolean("isLike", true);
//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        songList.get(position).getAlbumCover().compress(Bitmap.CompressFormat.PNG,100,bos);
//        bundle.putByteArray("albumCover",bos.toByteArray());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(getActivity(), LrcActivity.class);
        startActivity(intent, bundle);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确定删除嘛");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DiskLruCacheUtil.getInstance(getActivity(), "song").removeFromDiskCache(songList.get(position))) {
                    songList.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "未删除成功!", Toast.LENGTH_SHORT).show();
                }

            }
        }).create().show();
    }

}
