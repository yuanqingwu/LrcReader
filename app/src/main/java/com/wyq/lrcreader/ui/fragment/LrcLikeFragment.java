package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.RecyclerListAdapter;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.model.viewmodel.LrcLikeViewModel;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;
import com.wyq.lrcreader.ui.widget.FirePopupWindow;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * Created by Uni.W on 2016/8/31.
 */
public class LrcLikeFragment extends BaseFragment implements RecyclerListAdapter.OnRecyclerItemLongClickListener, RecyclerListAdapter.OnRecyclerItemClickListener {

    @BindView(R.id.fragment_lrc_like_list_recyclerview)
    public RecyclerView recyclerView;

    private RecyclerListAdapter adapter;
    private List<SongEntity> songList;

    public static LrcLikeFragment newInstance() {
        return new LrcLikeFragment();
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_lrc_like_list;
    }

    @Override
    public void initData() {
//        songList = getRepository().getDbGecimiRepository().getLikeSongList();

    }

    @Override
    public void initView(View view) {
        adapter = new RecyclerListAdapter(getActivity(), songList);
        LrcLikeViewModel.Factory factory = new LrcLikeViewModel.Factory(getRepository());
        LrcLikeViewModel lrcLikeViewModel = ViewModelProviders.of(this, factory)
                .get(LrcLikeViewModel.class);

        lrcLikeViewModel.getLikedSongs().observe(this, new Observer<List<SongEntity>>() {
            @Override
            public void onChanged(List<SongEntity> songEntities) {
                songList = songEntities;
                adapter.refreshData(songEntities);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerItemLongClickListener(this);
        adapter.setOnRecyclerItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            songList = savedInstanceState.getParcelableArrayList("songList");
//            if (songList != null && songList.size() > 0) {
//                Toast.makeText(getActivity(), "恢复", Toast.LENGTH_SHORT).show();
//                adapter.notifyDataSetChanged();
//            }
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (songList != null && songList.size() > 0) {
//            outState.putParcelableArrayList("songList", (ArrayList<? extends Parcelable>) songList);
//        }
    }

    @Override
    public void onItemClick(View view, int position) {

//        LrcActivity.newInstance(getContext(),songList.get(position));

    }

    @Override
    public void onItemLongClick(View view, final int position) {
        FirePopupWindow.text("删除", "取消").setOnClickListener(new FirePopupWindow.OnClickListener() {
            @Override
            public void onClick(String text) {
                if (text.equals("删除")) {
                    getRepository().getDbGecimiRepository().deleteSong(songList.get(position));

                    songList.remove(position);
                    adapter.notifyDataSetChanged();
                } else {

                }
            }
        }).showAsDropDown(view);
    }

}
