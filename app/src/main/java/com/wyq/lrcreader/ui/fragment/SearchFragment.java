package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.LrcPagedAdapter;
import com.wyq.lrcreader.adapter.RecyclerListAdapter;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.model.viewmodel.SearchResultViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * @author Uni.W
 * @date 2019/1/20 14:40
 */
public class SearchFragment extends BaseFragment implements RecyclerListAdapter.OnRecyclerItemClickListener {

    @BindView(R.id.search_fragment_recycler_view)
    public RecyclerView recyclerView;

    //private RecyclerListAdapter adapter;
    private LrcPagedAdapter pagedAdapter;

    public static SearchFragment newInstance() {

        return new SearchFragment();
    }

    @Override
    int attachLayoutRes() {
        return R.layout.search_fragment;
    }

    @Override
    void initData() {

    }

    @Override
    void initView(View view) {

        initRecyclerView();
    }

    private void initRecyclerView() {
//        adapter = new RecyclerListAdapter(getContext(), null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//
//        adapter.setOnRecyclerItemClickListener(this);

        pagedAdapter = new LrcPagedAdapter(GlideApp.with(this));


        recyclerView.setAdapter(pagedAdapter);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        SearchResultViewModel searchResultViewModel = ViewModelProviders.of(this, factory)
                .get(SearchResultViewModel.class);

        LogUtil.i("" + searchResultViewModel == null ? "vm null" : "vm not null");

        searchResultViewModel.getSearchResults().observe(this, new Observer<PagedList<SearchResultEntity>>() {
            @Override
            public void onChanged(PagedList<SearchResultEntity> searchResultEntities) {
                if (searchResultEntities != null) {

                    LogUtil.i("LoadedCount:" + searchResultEntities.getLoadedCount() + searchResultEntities.get(0).getSongName());
                    pagedAdapter.submitList(searchResultEntities);
//                    pagedAdapter.setNetworkState(NetworkState.LOADING);
                } else {
                    LogUtil.i("searchResultEntities is null");
                }
            }
        });


    }

    @Override
    public void onItemClick(View view, int position) {
//        SearchResultEntity entity = adapter.getDataList().get(position);
//        LogUtil.i(entity.getSongName() + " :" + entity.getLrcUri());
//        LrcActivity.newInstance(getContext(), entity.getLrcUri(), entity.getAlbumCoverUri());
    }
}
