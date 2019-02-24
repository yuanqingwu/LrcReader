package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.LrcPagedAdapter;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.model.viewmodel.SearchResultViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;
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
public class SearchResultFragment extends BaseFragment {

    @BindView(R.id.search_fragment_recycler_view)
    public RecyclerView recyclerView;

    //private SearchResultListAdapter adapter;
    private LrcPagedAdapter pagedAdapter;

    private static final String FILTER = "FILTER";
    private volatile String filter = null;

    public static SearchResultFragment newInstance(String filter) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILTER, filter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.search_result_fragment;
    }

    @Override
    public void initData() {
        filter = getArguments().getString(FILTER);
    }

    @Override
    public void initView(View view) {

        initRecyclerView();
    }

    private void initRecyclerView() {
//        adapter = new SearchResultListAdapter(getContext(), null);
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

        searchResultViewModel.getSearchResults(filter).observe(this, new Observer<PagedList<SearchResultEntity>>() {
            @Override
            public void onChanged(PagedList<SearchResultEntity> searchResultEntities) {
                if (searchResultEntities != null) {

                    LogUtil.i("LoadedCount:" + searchResultEntities.getLoadedCount());
                    pagedAdapter.submitList(searchResultEntities);
//                    pagedAdapter.setNetworkState(NetworkState.LOADING);
                } else {
                    LogUtil.i("searchResultEntities is null");
                }
            }
        });


    }
}
