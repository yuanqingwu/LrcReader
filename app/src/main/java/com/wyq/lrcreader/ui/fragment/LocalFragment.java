package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.BaseRecyclerViewAdapter;
import com.wyq.lrcreader.adapter.SongEntityListAdapter;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.model.viewmodel.LocalSongsViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.HomeAction;
import com.wyq.lrcreader.ui.activity.LrcActivity;
import com.wyq.lrcreader.ui.fragment.base.BaseLazyLoadFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * @author Uni.W
 * @date 2019/1/20 11:58
 */
public class LocalFragment extends BaseLazyLoadFragment implements BaseRecyclerViewAdapter.OnRecyclerItemClickListener {

    @BindView(R.id.local_fragment_recycler_view)
    public RecyclerView recyclerView;
    private SongEntityListAdapter adapter;
    private List<SongEntity> songList;

    public static LocalFragment newInstance() {
        return new LocalFragment();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());

        LocalSongsViewModel localSongsViewModel = ViewModelProviders.of(this, factory).get(LocalSongsViewModel.class);

        localSongsViewModel.getObservableLocalSong()
                .observe(this, new Observer<List<SongEntity>>() {
                    @Override
                    public void onChanged(List<SongEntity> songs) {
                        songList = songs;
                        adapter.refreshData(songs);
                    }
                });

        LiveEventBus.get().with(HomeAction.ACTION_LOCAL, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals(HomeAction.ACTION_LOCAL_LIST)) {
                    recyclerView.setVisibility(View.VISIBLE);
                    getChildFragmentManager().popBackStackImmediate();
                } else if (s.equals(HomeAction.ACTION_LOCAL_FOLDER)) {
                    recyclerView.setVisibility(View.GONE);
                    getChildFragmentManager().beginTransaction().add(R.id.local_fragment_holder_view, LocalLrcSearchFragment.newInstance()).addToBackStack("folder").commit();

                } else if (s.equals(HomeAction.ACTION_LOCAL_REFRESH)) {

                }
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void retry() {

    }

    @Override
    public int attachLayoutRes() {
        return R.layout.local_fragment_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {
        adapter = new SongEntityListAdapter(getActivity(), songList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        LrcActivity.newInstance(getActivity(), songList.get(position));
    }
}
